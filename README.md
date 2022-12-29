# JMdict and JMnedict, but in JSON

> TL;DR: [JMdict][] and [JMnedict][] in JSON format
> with more comprehensible structure
> and (hopefully) better documentation.
>
> **[LATEST RELEASE of JSON files][latest-release]** -
> you don't have to run the conversion script yourself!

## Why?

Original XML files are less than ideal in terms of format.
(My opinion only, the JMdict/Jmnedict project in general is absolutely awesome!)
This project provides the following changes and improvements:

1. JSON format instead of XML
2. Regular structure for every item in every collection, no "same as in previous" implicit values.
   This is a problem with original XML files because users' code has to keep track
   of various parts of state while traversing collections
3. Human-readable names for fields instead of cryptic abbreviations with no explanations
4. Avoiding `null` (with few exceptions) and missing fields, preferring empty arrays.
   See <http://thecodelesscode.com/case/6> for the inspiration for this

## Full and "common" versions

There are two versions of the JMdict dictionary: full and "common"-only. Dictionary entries are considered "common" if `/k_ele/ke_pri` or `/r_ele/re_pri` elements in original file contain one of these markers: "news1", "ichi1", "spec1", "spec2", "gai1". Common-only distributions are much smaller.

JMnedict has only one version.

## Requirements for running the scripts

- Java 17 (JRE only, JDK is not necessary) - you can use [Azul Zulu OpenJDK](https://www.azul.com/downloads/?version=java-17-lts&package=jre)

You don't need to have Gradle installed, just use the Gradle wrapper provided in this repository: `gradlew` (for Linux/Mac) or `gradlew.bat` (for Windows)

## Building dictionaries

NOTE: You can grab the pre-built JSON files in the [latest release][latest-release]

Use included scripts: `gradlew` for Linux/Mac OS, `gradlew.bat` for Windows.

Tasks to convert dictionary files and create distribution archives:

- `./gradlew clean` - clean all build artifacts to start a fresh build, in cases when you need to re-download and convert from scratch
- `./gradlew download` - download and extract source dictionary XML files into `build/dict-xml`
- `./gradlew convert` - convert all dictionaries to JSON in `build/dict-json`
- `./gradlew archive` - create distribution archives (zip, tar+gzip) in `build/distributions`

Utility tasks (for CI/CD workflows):

- `./gradlew --quiet jmdictHasChanged` and `./gradlew --quiet jmnedictHasChanged` - check if JMdict or JMnedict has changed
  by comparing checksums of downloaded files (run after `download` task!) with those stored in checksum files.
  Outputs `YES` or `NO`. The `--quiet` is needed to put values into shell variables without extra output from Gradle.
- `./gradlew updateChecksums` - update checksum files in `checksums/` directory.
  Run after creating distribution archives and commit checksum files into the repository,
  so that next time CI/CD workflow knows if it needs to rebuild anything.
- `./gradlew userJar` - create an Uber JAR for standalone use (w/o Gradle).
  The JAR program shows help messages and should be intuitive to use if you know how to run it.

There are also more specific tasks, run `./gradlew tasks` for details

## Troubleshooting

- Make sure to run `convert` task before running `acrhive`
- If running Gradle fails, make sure `java` is available on your `$PATH` environment variable
- Run Gradle with `--stacktrace`, `--info`, or `--debug` arguments to see more details

## Format

The following are [TypeScript types](https://www.typescriptlang.org/) for JSON files produced by this project.
You can also find Kotlin types in [JMdictJsonElement.kt](src/main/kotlin/org/edrdg/jmdict/simplified/conversion/jmdict/JMdictJsonElement.kt)
and [JMnedictJsonElement.kt](src/main/kotlin/org/edrdg/jmdict/simplified/conversion/jmnedict/JMnedictJsonElement.kt),
although they contain some stuff you might not need.

```typescript
/////////////////////////////////////////////////
// Shared custom types for JMdict and JMnedict //
/////////////////////////////////////////////////

/**
 * xref - Full format, which lists both kanji (or other non-kana characters)
 * and kana writings which this sense applies to,
 * as well as index of a sense (counting from 1)
 */
type XrefFull = [kanji: string, kana: string, senseIndex: number];

/**
 * xref - Just one writing (kanji or kana) and sense index
 */
type XrefShortWithIndex = [kanjiOrKana: string, senseIndex: number];

/**
 * xref - Just one writing (kanji or kana)
 */
type XrefShortWithoutIndex = [kanjiOrKana: string];

/**
 * xref - Cross-reference
 * Examples:
 * - `["丸", "まる", 1]` - refers to the word "丸", read as "まる" ("maru"), specifically the 1st sense element
 * - `["○", "まる", 1]` - same, but "○" is a special character for the word "丸"
 * - `["二重丸", "にじゅうまる"]` - refers to the word "二重丸", read as "にじゅうまる" ("nijoumaru")
 * - `["漢数字"]` - refers to the word "漢数字", with any reading
 */
type Xref = XrefFull | XrefShortWithIndex | XrefShortWithoutIndex;

/**
 * tag - All tags are listed in a separate section of the file.
 * See the descriptions of the root JSON objects of each dictionary.
 *
 * Examples:
 * - `"v5uru"` - "Godan verb - Uru old class verb (old form of Eru)"
 * - `"n"` - "noun (common) (futsuumeishi)",
 * - `"tv"` - "television"
 */
type Tag = string;

/**
 * Language code, ISO 639-2 standard.
 * See <https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes>
 * See <https://en.wikipedia.org/wiki/ISO_639-2>
 */
type Language = string;


//////////////////
// JMdict types //
//////////////////

/**
 * JMdict root object
 */
type JMdict = {
    /**
     * Semantic version of this project (not the dictionary itself).
     * For the dictionary revisions, see `dictRevisions` field below
     * See <https://semver.org/>
     */
    version: string;

    /**
     * Creation date of JMdict file, as it appears in a comment
     * with format "JMdict created: YYYY-MM-DD" in the original XML file header
     */
    dictDate: string;

    /**
     * Revisions of JMdict file, as they appear in comments
     * in the original XML file header. These only contain
     * actual version (e.g., "1.08"), not a full comment.
     * Original comments also mention changes made,
     * but this is omitted in the resulting JSON files
     */
    dictRevisions: string[];

    /**
     * Tags: parts of speech, names of dialects, fields of application, etc.
     * All those things are expressed as XML entities in the original file.
     * Keys of this object are tags per se, and values are descriptions,
     * slightly modified from the original file
     */
    tags: {
        [tag: Tag]: string;
    };

    /**
     * List of dictionary entries/words
     */
    words: JMdictWord[];
};

/**
 * JMdict entry/word
 */
type JMdictWord = {
    /**
     * Unique identifier of an entry
     */
    id: string;

    /**
     * Kanji (and other non-kana) writings.
     * Note that some words are only spelled with kana, so this may be empty.
     */
    kanji: JMdictKanji[];

    /**
     * Kana-only writings of words.
     * If a kanji is also present, these can be considered as "readings",
     * but there are words written with kana only.
     */
    kana: JMdictKana[];

    /**
     * Senses = translations + some related data
     */
    sense: JMdictSense[];
};

type JMdictKanji = {
    /**
     * `true` if this particular word is considered common.
     * This field combines all the `*_pri` fields
     * from original files in a same way as <https://jisho.org>
     * and other on-line dictionaries do (typically, some words have
     * "common" markers/tags). It gets rid of bunch of `*_pri` fields
     * which are not typically used. Words marked with "news1", "ichi1",
     * "spec1", "spec2", "gai1" in the original file are treated as common,
     * which may or may not be true according to other sources.
     */
    common: boolean;

    /**
     * The word itself, as spelled with any non-kana-only writing.
     * May contain kanji, kana (but not only kana!), and some other characters.
     * Example: "ＣＤプレイヤー" - none of these symbols are kanji,
     * but "ＣＤ" is not kana, so it will be in this field. The corresponding
     * kana text will be "シーディープレイヤー", where "シーディー" is how the "ＣＤ"
     * is spelled in Japanese kana.
     */
    text: string;

    /**
     * Tags applicable to this writing
     */
    tags: Tag[];
};

type JMdictKana = {
    /**
     * Same as {@link JMdictKanji#common}.
     * In this case, it shows that this particular kana transcription of a word
     * is considered common. For example, when a word can be read in multiple ways,
     * some of them may be more common than others.
     */
    common: boolean;

    /**
     * Kana-only writing, may only accidentally contain middle-dot
     * and other punctuation-like characters.
     */
    text: string;

    /**
     * Same as {@link JMdictKanji#tags}
     */
    tags: Tag[];

    /**
     * List of kanji spellings of this word which this particular kana version applies to.
     * `"*"` means "all", an empty array means "none".
     * This field is useful for words will multiple kanji variants - some of them may be read
     * differently than others.
     */
    appliesToKanji: string[];
};

type JMdictSense = {
    /**
     * All parts of speech for this sense.
     * Unlike the original dictionary XML files, this field is never empty/missing.
     * In the original files, part-of-speech from the previous sense elements may apply
     * to the sunsequent elements: e.g. in the 1st and 2nd elements are both nouns,
     * then only the 1st will state that explicitly. This requires users to check the whole
     * list of senses to correctly determine part of speech for any particular sense.
     * In this project, this field is "normalized" - parts of speech are present in every element,
     * even if they are all the same.
     */
    partOfSpeech: Tag[];

    /**
     * List of kanji writings within this word which this sense applies to.
     * `"*"` means "all", empty array means "none".
     * See also {@link JMdictKana#appliesToKanji}
     */
    appliesToKanji: string[];

    /**
     * List of kana writings within this word which this sense applies to.
     * "*"` means "all", empty array means "none".
     * See also `appliesToKanji` field above and {@link JMdictKana#appliesToKanji}.
     */
    appliesToKana: string[];

    /**
     * References to related words
     */
    related: Xref[];

    /**
     * References to antonyms of this word
     */
    antonym: Xref[];

    /**
     * List of fields of application of this word.
     * E.g. `"math"` means that this word is related to or used in Mathematics.
     */
    field: Tag[];

    /**
     * List of dialects where this word is used
     */
    dialect: Tag[];

    /**
     * Miscellania - list of other tags, anything which doesn't fit into other tag fields above
     */
    misc: Tag[];

    /**
     * Other information about this word
     */
    info: string[];

    /**
     * Source language information for borrowed words and wasei-eigo
     */
    languageSource: JMdictLanguageSource[];

    /**
     * Translations of this word
     */
    gloss: JMdictGloss[];
};

/**
 * Source language information for borrowed words and wasei-eigo
 */
type JMdictLanguageSource = {
    /**
     * Language of this translation
     */
    lang: Language;

    /**
     * Indicates whether the sense element fully or partially
     * describes the source word or phrase of the loanword
     */
    full: boolean;

    /**
     * Indicates that the word is wasei-eigo.
     * See <https://en.wikipedia.org/wiki/Wasei-eigo>
     */
    wasei: boolean;

    /**
     * Text in the language defined by a `lang` field, or `null`
     */
    text: string | null;
};

/**
 * Type of translation
 */
enum JMdictGlossType {
    literal = "literal",
    figurative = "figurative",
    explanation = "explanation",
    trademark = "trademark", // e.g. name of a company or product
}

/**
 * Translation of a word
 */
type JMdictGloss = {
    /**
     * Type of translation.
     * Most words have `null` values, meaning this attribute was absent in the original XML entry.
     * Jmdict documentation does not describe the meaning of this attribute being absent.
     */
    type: JMdictGlossType | null;

    /**
     * Language code, ISO 639-2 standard
     * See also {@link JMdictLanguageSource#lang}
     */
    lang: string;

    /**
     * A translation word/phrase
     */
    text: string;
};


////////////////////
// Jmnedict types //
////////////////////

/**
 * JMnedict root object
 *
 * Differences from JMdict format:
 *
 * 1. `kanji` and `kana` have no `common` flag because in this dictionary
 *    priority data is missing (`ke_pri` and `re_pri` fields)
 * 2. `translation` instead of `gloss`
 * 3. `translation->translation->lang` seems to be always empty because
 *    the original XML files have no data in corresponding attributes,
 *    even though documentation says otherwise. In this JSON version,
 *    `"eng"` (English) is always present as a default
 */
type JMnedict = {
    /**
     * Semantic version of this project (not the dictionary itself).
     * See <https://semver.org/>
     */
    version: string;

    /**
     * Creation date of JMnedict file, as it appears in a comment
     * with format "JMnedict created: YYYY-MM-DD" in the original XML file header
     */
    dictDate: string;

    /**
     * Revisions of JMnedict file, as they appear in comments
     * in the original XML file header. These only contain
     * actual version (e.g., "1.08"), not a full comment.
     * Original comments also mention changes made,
     * but this is omitted in the resulting JSON files
     */
    dictRevisions: string[];

    /**
     * Tags: parts of speech, names of dialects, fields of application, etc.
     * All those things are expressed as XML entities in the original file.
     * Keys of this object are the tags per se, and values are descriptions,
     * slightly modified from the original file.
     */
    tags: {
        [tag: Tag]: string;
    };

    /**
     * List of dictionary entries/words
     */
    words: JMnedictWord[];
};

/**
 * JMdict entry/word
 */
type JMnedictWord = {
    /**
     * Unique identifier of an entry
     */
    id: string;

    /**
     * Kanji (and other non-kana) writings.
     * Note that some words are only spelled with kana, so this may be empty.
     */
    kanji: JMnedictKanji[];

    /**
     * Kana-only writings of words.
     * If a kanji is also present, these can be considered as "readings",
     * but there are words written with kana only.
     */
    kana: JMnedictKana[];

    /**
     * Translations + some related data
     */
    translation: JMnedictTranslation[];
};

type JMnedictKanji = {
    /**
     * The word itself, as spelled with any non-kana-only writing.
     * See {@link JMdictKanji#text}
     */
    text: string;

    /**
     * Tags applicable to this writing
     */
    tags: Tag[];
};

type JMnedictKana = {
    /**
     * Kana-only writing, may only accidentally contain middle-dot
     * and other punctuation-like characters.
     */
    text: string;

    /**
     * Same as {@link JMnedictKanji#tags}
     */
    tags: Tag[];

    /**
     * List of kanji spellings of this word which this particular kana version applies to.
     * `"*"` means "all", an empty array means "none".
     * This field is useful for words will multiple kanji variants - some of them may be read
     * differently than others.
     */
    appliesToKanji: string[];
};

type JMnedictTranslation = {
    /**
     * Name types, as specified in {@link JMnedict#tags}
     */
    type: Tag[];

    /**
     * References to related words
     */
    related: Xref[];

    /**
     * Translations
     */
    translation: JMnedictTranslationTranslation[];
};

type JMnedictTranslationTranslation = {
    /**
     * Language of this translation
     */
    lang: Language;

    /**
     * A translation word/phrase
     */
    text: string;
}

```

## License

Original XML files, **JMdict.xml**, **JMdict_e.xml**, and **JMnedict.xml**
are property of the Electronic Dictionary Research and Development Group,
and are used in conformance with the Group's [licence][EDRDG-license].
Project started in 1991 by Jim Breen.

All derived files are distributed under the same license, as the original license requires it.

Source files of this project (excluding distribution archives containing JSON files)
are available under [Creative Commons Attribution-ShareAlike License v4.0][CC-BY-SA-4].
See [LICENSE.txt](LICENSE.txt)

[JMdict]: http://www.edrdg.org/jmdict/j_jmdict.html
[JMnedict]: http://www.edrdg.org/enamdict/enamdict_doc.html
[latest-release]: https://github.com/scriptin/jmdict-simplified/releases/latest
[EDRDG-license]: http://www.edrdg.org/edrdg/licence.html
[CC-BY-SA-4]: http://creativecommons.org/licenses/by-sa/4.0/
