# JMdict and JMnedict, but in JSON

> TL;DR: [JMdict](http://www.edrdg.org/jmdict/j_jmdict.html) and [JMnedict](http://www.edrdg.org/enamdict/enamdict_doc.html) in JSON format with more comprehensible structure and (hopefully) better documentation.

**[LATEST RELEASE](https://github.com/scriptin/jmdict-simplified/releases/latest) - INCLUDES PRE-BUILT JSON FILES**, you don't have to run the conversion yourself.

## Why?

Original XML files are less than ideal. This project provides the following changes and improvements:

1. JSON format.
2. Regular structure for every item in every collection, no "same as in previous" implicit values. This is a problem with original XML files because your code has to track parts of state while traversing collections.
3. Human-readable names for fields. No more cryptic abbreviations with no explainations.
4. [Array fields are never `null` or missing, always empty arrays.](http://thecodelesscode.com/case/6)

## Full and "common" versions

There are two versions of the JMdict dictionary: full and "common"-only. Dictionary entries are considered "common" if `/k_ele/ke_pri` or `/r_ele/re_pri` elements in original file contain one of these markers: "news1", "ichi1", "spec1", "spec2", "gai1". Common-only distributions are much smaller.

JMnedict has only one version.

## Requirements for running the scripts

- Java 8+ (not JDK, just Java itself)

You don't need to have Gradle installed, just use the Gradle wrapper provided in this repository: `gradlew` (for Linux/Mac) or `gradlew.bat` (for Windows)

## Building dictionaries

NOTE: You can grab the pre-built JSON files in the [latest release](https://github.com/scriptin/jmdict-simplified/releases/latest).

Use included scripts: `gradlew` for Linux/Mac OS, `gradlew.bat` for Windows.

Tasks to convert dictionary files and create distribution archives:

- `./gradlew clean` - clean all build artifacts to start a fresh build, in cases when you need to re-download and convert from scratch
- `./gradlew download` - download and extract source dictionary XML files
- `./gradlew tags` - (after re-downloading source XML files) regenerate `src/jmdict/tag.xq` and `src/jmnedict/tag.xq`. Without these tags the `convert` command will not work
- `./gradlew convert` - convert all dictionaries to JSON
- `./gradlew dist` - create distribution archives

Utility tasks (for CI/CD workflows):

- `./gradlew --quiet jmdictHasChanged` and `./gradlew --quiet jmnedictHasChanged` - check if JMdict or JMnedict has changed
  by comparing checksums of downloaded files (run after `download` task!) with those stored in checksum files.
  Outputs `YES` or `NO`. The `--quiet` is needed to put values into shell variables without extra output from Gradle.
- `./gradlew updateChecksums` - update checksum files in `checksums/` directory.
  Run after creating distribution archives and commit checksum files into the repository,
  so that next time CI/CD workflow knows if it needs to rebuild anything.

There are also more specific tasks, run `./gradlew tasks` for details

## Troubleshooting

- Make sure `java` is available on your `$PATH` environment variable
- Make sure you run `tags` command before running `convert` when you first close this repository
- In cases when conversion fails, it may be due to JVM memory limitations.
  You can set a value for `-Xmx` variable in `gradle.properties` (e.g. `-Xmx6g` for 6 GB).
  Note that [RAM limit for GitHub actions is 7 GB][gh-actions-hardware].
- Run Gradle with `--stacktrace`, `--info`, or `--debug` arguments to see more details

[gh-actions-hardware]: https://docs.github.com/en/actions/reference/virtual-environments-for-github-hosted-runners#supported-runners-and-hardware-resources

## Format of JMdict

### Custom types

- `xref` (array of numbers/strings) := one of the following structures:

    - [kanji, kana, sense_index] - full format, which lists both kanji (or other non-kana characters) and kana writings which this sense applies to, as well as index of a sense (counting from 1)
    - [kanji/kana, sense_index] - just one writing (kanji or kana) and sense index
    - [kanji/kana] - just one writing (kanji or kana)

    Examples: `["丸", "まる", 1]`, `["○", "まる", 1]`, `["二重丸", "にじゅうまる"]`, `["漢数字"]`, etc.

- `tag` (string) := all tags are listed in a separate section of the file, see the description of root JSON object

### Root JSON object

- `version` (string) := [Semantic version](http://semver.org/) of this project (not the dictionary itself)
- `dictDate` (string) := Creation date of JMdict file, as it appears in a comment with format "JMdict created: YYYY-MM-DD" in the original XML file header
- `dictRevisions` (array of string) := Revisions of JMdict file, as they appear in comments before DOCTYPE in the original XML file header. These only contain actual version (e.g., "1.08"), not a full comment. Original comments also mention changes made, but this is omitted in the resulting JSON files
- `tags` (object) := all tags: parts of speech, names of dialects, fields of application, etc. All those things are expressed as XML entities in the original file. Keys of this objects are tags per se, values are descriptions, slightly modified from the original file
- `words` (array of objects) :=
    - `id` (string) := unique identifier
    - `kanji` (array of objects) := kanji (and other non-kana) writings
        - `common` (boolean) := `true` if this particular spelling is common. This field combines all the `*_pri` fields from original files in a same way as [jisho.org][] and other on-line dictionaries do ("common" word markers). It gets rid of bunch of `*_pri` fields which are not typically used. Words marked with "news1", "ichi1", "spec1", "spec2", "gai1" in the original file are treated as common, which may or may not be true according other sources.
        - `text` (string) := any non-kana-only writing, may contain kanji, kana, and some other characters
        - `tags` (array of tags) := tags applied to this writing
    - `kana` (array of objects) := kana-only (with few exceptions) writings, typically considered as "readings", but these can be a word writings by themselves
        - `common` (boolean) := same as for kanji elements
        - `text` (string) := kana-only writing, may only accidentally contain middle-dot and other punctuation-like characters
        - `tags` (array of tags) := same as for kanji
        - `appliesToKanji` (array of strings) := list of kanji writings within this word which this kana version applies to. `"*"` means "all", empty array means "none"
    - `sense` (array of objects) := senses (translations + some related data) for this words
        - `partOfSpeech` (array of tags) := all parts of speech for this sense. Unlike the original dictionary file, this field is never empty/missing. In the original file, part-of-speech from earlier sense elements may apply to following elements, in which case latter don't have defined part-of-speech
        - `appliesToKanji` (array of strings) := list of kanji writings within this word which this sense applies to. `"*"` means "all", empty array means "none"
        - `appliesToKana` (array of strings) := list of kana writings within this word which this sense applies to. `"*"` means "all", empty array means "none"
        - `related` (array of xrefs) := xrefs to related words
        - `antonym` (array of xrefs) := xrefs to antonyms of this word
        - `field` (array of tags) := fields of application
        - `dialect` (array of tags) := dialects
        - `misc` (array of tags) := other related tags
        - `info` (array of strings) := other info
        - `languageSource` (array of objects) := source language info for borrowed words and wasei-eigo
            - `lang` (string) := language code from the ISO 639-2 standard
            - `full` (boolean) := indicates whether the sense element fully or partially describes the source word or phrase of the loanword
            - `wasei` (boolean) := indicates that the Japanese word has been constructed from words in the source language, and not from an actual phrase in that language. See [Wasei-eigo](https://en.wikipedia.org/wiki/Wasei-eigo)
            - `text` (string or null) := text in the language defined by a `lang` element, or `null`
        - `gloss` (array of objects) := translations
            - `type` (string or null) := type of translation, one of `"literal"`, `"figurative"`, `"explanation"`, or `null`. Most words have `null` values, meaning this attribute was absent in the original XML entry. The original documents do not describe the meaning of this attribute being absent.
            - `lang` (string) := language code from the ISO 639-2 standard
            - `text` (string) := a word or phrase

Notes:

1. All fields in all objects are always present, none ever omitted
2. Array fields are never `null` or missing, only empty
3. The only places which allow `null` values are `sense->languageSource->text` and `sense->gloss->type` in word element

## Format of JMnedict

### Custom types

Same as for JMdict

### Root JSON object

- `version` (string) := [Semantic version](http://semver.org/) of this project (not the dictionary itself)
- `dictDate` (string) := Creation date of JMnedict file, as it appears in a comment with format "JMnedict created: YYYY-MM-DD" in the original XML file header
- `dictRevisions` (array of string) := Revisions of JMnedict file, as they appear in comments before DOCTYPE in the original XML file header. These only contain actual version (e.g., "1.08"), not a full comment. Original comments also mention changes made, but this is omitted in the resulting JSON files
- `tags` (object) := all tags: parts of speech, names of dialects, fields of application, etc. All those things are expressed as XML entities in the original file. Keys of this objects are tags per se, values are descriptions, slightly modified from the original file
- `words` (array of objects) :=
    - `id` (string) := unique identifier
    - `kanji` (array of objects) := kanji (and other non-kana) writings
        - `text` (string) := any non-kana-only writing, may contain kanji, kana, and some other characters
        - `tags` (array of tags) := tags applied to this writing
    - `kana` (array of objects) := kana-only (with few exceptions) writings, typically considered as "readings", but these can be a word writings by themselves
        - `text` (string) := kana-only writing, may only accidentally contain middle-dot and other punctuation-like characters
        - `tags` (array of tags) := same as for kanji
        - `appliesToKanji` (array of strings) := list of kanji writings within this word which this kana version applies to. `"*"` means "all", empty array means "none"
    - `translation` (array of objects) := translations + some related data for this words
        - `type` (array of tags) := name types, as specified in tags
        - `related` (array of xrefs) := xrefs to related words
        - `translation` (array of objects) := actual translations
            - `lang` (string) := language code from the ISO 639-2 standard
            - `text` (string) := a word or phrase

Differences from JMdict format:

1. `kanji` and `kana` have no `common` flag because in this dictionary priority data is missing (`ke_pri` and `re_pri` fields)
2. `translation` instead of `gloss`
3. `translation->translation->lang` seems to be always empty because the original XML file has no data in corresponding attributes, even though documentation says otherwise

## License

Original XML files, **JMdict_e.xml** and **JMnedict.xml** are property of the Electronic Dictionary Research and Development Group, and are used in conformance with the Group's [licence](http://www.edrdg.org/edrdg/licence.html). Project started in 1991 by [Jim Breen](http://www.csse.monash.edu.au/~jwb/).

All derived files are distributed under the same license, as the original license requires it.

Source files of this project (excluding distribution archives containing JSON files) are available under [Creative Commons Attribution-ShareAlike License v4.0](http://creativecommons.org/licenses/by-sa/4.0/). See [LICENSE.txt](LICENSE.txt)
