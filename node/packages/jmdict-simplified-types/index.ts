//////////////////////////////////////////////
// Shared custom types for all dictionaries //
//////////////////////////////////////////////

/**
 * Language code, ISO 639-1 standard.
 * 2 letters: "en", "es", "fr"
 * @see <https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes>
 * @see <https://en.wikipedia.org/wiki/ISO_639-1>
 */
export type Language2Letter = string;

/**
 * Language code, ISO 639-2 standard.
 * 3 letters: "eng", "spa", "fra"
 * @see <https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes>
 * @see <https://en.wikipedia.org/wiki/ISO_639-2>
 */
export type Language3Letter = string;

export type Language = Language2Letter | Language3Letter;

/**
 * Dictionary metadata: version, languages, creation date
 */
export interface DictionaryMetadata<L extends Language> {
  /**
   * Semantic version of this project (not the dictionary itself).
   * For the dictionary revisions, see `dictRevisions` field below
   * @see <https://semver.org/>
   */
  version: string;

  /**
   * List of languages in this files
   */
  languages: L[];

  /**
   * Creation date of JMdict file, as it appears in a comment
   * with format "JMdict created: YYYY-MM-DD" in the original XML file header
   */
  dictDate: string;
}

/////////////////////////////////////////////////
// Shared custom types for JMdict and JMnedict //
/////////////////////////////////////////////////

/**
 * xref - Full reference format: word (kanji+kana) + reading (kana-only) + sense index (starting from 1)
 */
export type XrefWordReadingIndex = [
  kanji: string,
  kana: string,
  senseIndex: number,
];

/**
 * xref - Shorter reference format: word + reading, without sense index
 */
export type XrefWordReading = [kanji: string, kana: string];

/**
 * xref - Shorter reference format: word (can be kana-only or contain kanji) + sense index
 */
export type XrefWordIndex = [kanjiOrKana: string, senseIndex: number];

/**
 * xref - The shortest reference format: just the word (can be kana-only or contain kanji)
 */
export type XrefWord = [kanjiOrKana: string];

/**
 * xref - Cross-reference
 *
 * Examples:
 * - `["丸", "まる", 1]` - refers to the word "丸", read as "まる" ("maru"),
 *   specifically the 1st sense element
 * - `["○", "まる", 1]` - same as previous, but "○" is a special character
 *    for the word "丸"
 * - `["二重丸", "にじゅうまる"]` - refers to the word "二重丸",
 *   read as "にじゅうまる" ("nijoumaru")
 * - `["漢数字"]` - refers to the word "漢数字", with any reading
 */
export type Xref =
  | XrefWordReadingIndex
  | XrefWordReading
  | XrefWordIndex
  | XrefWord;

/**
 * tag - All tags are listed in a separate section of the file.
 * See the descriptions of the root JSON objects of each dictionary.
 *
 * Examples:
 * - `"v5uru"` - "Godan verb - Uru old class verb (old form of Eru)"
 * - `"n"` - "noun (common) (futsuumeishi)",
 * - `"tv"` - "television"
 */
export type Tag = string;

/**
 * Dictionary metadata, such as revisions and tags.
 */
export interface JMdictDictionaryMetadata
  extends DictionaryMetadata<Language3Letter> {
  /**
   * `true` if this file contains only common kana/kanji versions
   */
  commonOnly: boolean;

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
}

//////////////////
// JMdict types //
//////////////////

/**
 * JMdict root object
 *
 * Important concepts:
 *
 * - "Kanji" and "kana" versions of words are not always equivalent
 *   to "spellings" and "readings" correspondingly. Some words are kana-only.
 *   You should treat "kanji" and "kana" as different ways of spelling,
 *   although when kanji versions are present, kana versions are indeed "readings" for those
 * - Some kana versions only apply to particular kanji versions, i.e. different spellings
 *   of the same word can be read in different ways. You'll see the `appliesToKanji` field
 *   being filled with a particular version in such cases
 * - "Sense" in JMdict refers to translations along with some other information.
 *   Sometimes, some "senses" only apply to some particular kanji/kana versions of a word,
 *   that's why you'll see fields `appliesToKanji` and `appliesToKana`.
 *   In {@link JMnedict}, translations are simply called "translations," there are no "senses"
 */
export interface JMdict extends JMdictDictionaryMetadata {
  /**
   * List of dictionary entries/words
   */
  words: JMdictWord[];
}

/**
 * JMdict entry/word
 */
export type JMdictWord = {
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

export type JMdictKanji = {
  /**
   * `true` if this particular word is considered common.
   * This field combines all the `*_pri` fields
   * from original files in a same way as <https://jisho.org>
   * and other on-line dictionaries do (typically, some words have
   * "common" markers/tags). It gets rid of a bunch of `*_pri` fields
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

export type JMdictKana = {
  /**
   * Same as {@link JMdictKanji}.common.
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
   * Same as {@link JMdictKanji}.tags
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

export type JMdictSense = {
  /**
   * Parts of speech for this sense.
   *
   * In the original files, part-of-speech from the previous sense elements
   * may apply to the subsequent elements: e.g. if the 1st and 2nd elements
   * are both nouns, then only the 1st will state that explicitly.
   * This requires users to check the whole list of senses to correctly
   * determine part of speech for any particular sense.
   *
   * Unlike the original XML files, this field is never empty/missing.
   * Here, this field is "normalized" - parts of speech are present
   * in every element, even if they are all the same.
   */
  partOfSpeech: Tag[];

  /**
   * List of kanji writings within this word which this sense applies to.
   * Works in conjunction with the next `appliesToKana` field.
   * `"*"` means "all". This is never empty, unlike {@link JMdictKana}.appliesToKanji.
   */
  appliesToKanji: string[];

  /**
   * List of kana writings within this word which this sense applies to.
   * Works in conjunction with the previous `appliesToKanji` field.
   * "*" means "all". This is never empty, unlike {@link JMdictKana}.appliesToKanji.
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
   * Miscellanea - list of other tags which don't fit into other tag fields
   */
  misc: Tag[];

  /**
   * Other information about this word
   */
  info: string[];

  /**
   * Source language information for borrowed words and wasei-eigo.
   * Will be empty for words with Japanese origin (most of JMdict entries)
   */
  languageSource: JMdictLanguageSource[];

  /**
   * Translations of this word
   */
  gloss: JMdictGloss[];
};

/**
 * Source language information for borrowed words and wasei-eigo.
 * For borrowed words this will contain the original word/phrase,
 * in the source language
 */
export type JMdictLanguageSource = {
  /**
   * Language of this translation
   */
  lang: Language3Letter;

  /**
   * Indicates whether the sense element fully or partially
   * describes the source word or phrase of the loanword
   */
  full: boolean;

  /**
   * Indicates that the word is wasei-eigo.
   *
   * @see <https://en.wikipedia.org/wiki/Wasei-eigo>
   */
  wasei: boolean;

  /**
   * Text in the language defined by a `lang` field, or `null`
   */
  text: string | null;
};

/**
 * Gender
 */
export type JMdictGender = 'masculine' | 'feminine' | 'neuter';

/**
 * export type of translation
 */
export type JMdictGlossType =
  | 'literal'
  | 'figurative'
  | 'explanation'
  | 'trademark'; // e.g. name of a company or a product

/**
 * Translation of a word
 */
export type JMdictGloss = {
  /**
   * Language of this translation
   */
  lang: Language3Letter;

  /**
   * Gender.
   * Typically, for a noun in the target language.
   * When `null`, the gender is either not relevant or hasn't been provided.
   */
  gender: JMdictGender | null;

  /**
   * export type of translation.
   * Most words have `null` values, meaning this attribute was absent in the original XML entry.
   * Jmdict documentation does not describe the meaning of this attribute being absent.
   */
  type: JMdictGlossType | null;

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
 * Differences from {@link JMdict} format (in {@link JMdictWord}):
 *
 * 1. `kanji` and `kana` have no `common` flag because in this dictionary
 *    priority data is missing (`ke_pri` and `re_pri` fields in JMdict,
 *    see {@link JMdictKanji}.common and {@link JMdictKana}.common)
 * 2. `translation` instead of `gloss`
 * 3. `translation->translation->lang` seems to be always empty because
 *    the original XML files have no data in corresponding attributes,
 *    even though documentation says otherwise. In this JSON version,
 *    `"eng"` (English) is always present as a default
 */
export interface JMnedict extends JMdictDictionaryMetadata {
  /**
   * List of dictionary entries/words
   */
  words: JMnedictWord[];
}

/**
 * JMdict entry/word
 */
export type JMnedictWord = {
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

export type JMnedictKanji = {
  /**
   * The word itself, as spelled with any non-kana-only writing.
   *
   * @see {@link JMdictKanji}.text
   */
  text: string;

  /**
   * Tags applicable to this writing
   */
  tags: Tag[];
};

export type JMnedictKana = {
  /**
   * Kana-only writing, may only accidentally contain middle-dot
   * and other punctuation-like characters.
   */
  text: string;

  /**
   * Same as {@link JMnedictKanji}.tags
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

export type JMnedictTranslation = {
  /**
   * Name types, as specified in {@link JMnedict}.tags
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

export type JMnedictTranslationTranslation = {
  /**
   * Language of this translation
   */
  lang: Language3Letter;

  /**
   * A translation word/phrase
   */
  text: string;
};

////////////////////
// Kanjidic types //
////////////////////

export interface Kanjidic2DictionaryMetadata
  extends DictionaryMetadata<Language2Letter> {
  /**
   * Version of the file, ordinal number.
   * The original XML file doesn't specify the meaning of this field.
   */
  fileVersion: number;
  /**
   * Format: YYYY-NN, where YYYY is a year, and NN is a zero-padded ordinal number (01, 02, ..., 99)
   */
  databaseVersion: string;
}

/**
 * Kanjidic root object
 */
export interface Kanjidic2 extends Kanjidic2DictionaryMetadata {
  /**
   * List of dictionary entries/characters
   */
  characters: Kanjidic2Character[];
}

export type Kanjidic2Character = {
  /**
   * Kanji itself
   */
  literal: string;

  /**
   * Kanji code in various encoding systems, such as Unicode or JIS
   */
  codepoints: Kanjidic2Codepoint[];

  /**
   * Radicals (i.e. "components" used for looking up kanji in dictionaries) of this kanji
   * Note that radicals don't necessarily represent all the component parts of a kanji,
   * but instead only describe some more distinctive parts. Radicals are used
   * to create indexes of kanji in dictionaries.
   */
  radicals: Kanjidic2Radical[];

  /**
   * Miscellanea data, such as school grade, JLPT level, usage frequency, etc.
   */
  misc: Kanjidic2Misc;

  /**
   * References to find this kanji in various dictionaries
   */
  dictionaryReferences: Kanjidic2DictionaryReference[];

  /**
   * Query codes to find this kanji in various (typically electronic) dictionaries.
   * Query code is typically a unique sequence of numbers and/or letters which
   * describes the shape of a kanji w/o relying on the knowledge of its
   * reading or meaning.
   */
  queryCodes: Kanjidic2QueryCode[];

  /**
   * Reading and meaning of a kanji, split into groups because different
   * readings can have different meanings.
   */
  readingMeaning: Kanjidic2ReadingMeaning | null;
};

export type Kanjidic2Codepoint = {
  /**
   * - jis208 - JIS X 0208-1997 - kuten coding, value format: nn-nn
   * - jis212 - JIS X 0212-1990 - kuten coding, value format: nn-nn
   * - jis213 - JIS X 0213-2000 - kuten coding, value format: p-nn-nn
   * - ucs - Unicode 4.0 - hex coding, value format: 4 or 5 hexadecimal digits
   */
  type: 'jis208' | 'jis212' | 'jis213' | 'ucs';
  value: string;
};

export type Kanjidic2Radical = {
  /**
   * - classical - based on the system first used in the KangXi Zidian.
   *   The Shibano "JIS Kanwa Jiten" is used as the reference source.
   * - nelson_c - as used in the Nelson "Modern Japanese-English
   *   Character Dictionary" (i.e. the Classic, not the New Nelson).
   *   This will only be used where Nelson reclassified the kanji.
   */
  type: 'classical' | 'nelson_c';
  value: number;
};

export type Kanjidic2Misc = {
  grade: number | null;

  /**
   * First value is the right count, the rest are common miscounts
   */
  strokeCounts: number[];

  /**
   * List of variants of this kanji. "Variants" typically kanji with the same
   * meaning but different shape, e.g. language-specific or simplified versions.
   */
  variants: Kanjidic2Variant[];

  /**
   * The rank of the character based on its frequency.
   * Only first 2,500 most used kanji, based on data of Japanese newspapers.
   */
  frequency: number | null;

  /**
   * Human-readable names of radical, if this kanji is also known as a radical
   * for other kanji. Most of the time this list is empty.
   */
  radicalNames: string[];

  /**
   * The (former) Japanese Language Proficiency Test (JLPT) level for this kanji.
   * 1 (most advanced) to 4 (most elementary).
   * Some kanji are not listed in JLPT.
   *
   * "Note that the JLPT test levels changed in 2010, with a new 5-level
   * system (N1 to N5) being introduced. No official kanji lists are
   * available for the new levels. The new levels are regarded as
   * being similar to the old levels except that the old level 2 is
   * now divided between N2 and N3."
   */
  jlptLevel: number | null;
};

export type Kanjidic2Variant = {
  /**
   * - jis208 - in JIS X 0208 - kuten coding
   * - jis212 - in JIS X 0212 - kuten coding
   * - jis213 - in JIS X 0213 - kuten coding
   * - deroo - De Roo number - numeric
   * - njecd - Halpern NJECD index number - numeric
   * - s_h - The Kanji Dictionary (Spahn & Hadamitzky) - descriptor
   * - nelson_c - "Classic" Nelson - numeric
   * - oneill - Japanese Names (O'Neill) - numeric
   * - ucs - Unicode codepoint - hexadecimal
   */
  type:
    | 'jis208'
    | 'jis212'
    | 'jis213'
    | 'deroo'
    | 'njecd'
    | 's_h'
    | 'nelson_c'
    | 'oneill'
    | 'ucs';
  value: string;
};

/**
 * Special case for reference: Morohashi
 *
 * @see {@link Kanjidic2DictionaryReferenceNotMorohashi} for non-Morohashi types
 */
export type Kanjidic2DictionaryReferenceMorohashi = {
  /**
   * - moro - "Daikanwajiten" compiled by Morohashi. For some kanji two
   *   additional attributes are used: m_vol:  the volume of the
   *   dictionary in which the kanji is found, and m_page: the page
   *   number in the volume.
   *
   * @see {@link Kanjidic2DictionaryReferenceNotMorohashi} for non-Morohashi types
   */
  type: 'moro';
  morohashi: {
    volume: number;
    page: number;
  } | null;
  value: string;
};

export type Kanjidic2DictionaryReferenceNotMorohashi = {
  /**
   * - nelson_c - "Modern Reader's Japanese-English Character Dictionary",
   *   edited by Andrew Nelson (now published as the "Classic" Nelson).
   * - nelson_n - "The New Nelson Japanese-English Character Dictionary", edited by John Haig.
   * - halpern_njecd - "New Japanese-English Character Dictionary", edited by Jack Halpern.
   * - halpern_kkd - "Kodansha Kanji Dictionary", (2nd Ed. of the NJECD) edited by Jack Halpern.
   * - halpern_kkld - "Kanji Learners Dictionary" (Kodansha) edited by Jack Halpern.
   * - halpern_kkld_2ed - "Kanji Learners Dictionary" (Kodansha), 2nd edition
   *   (2013) edited by Jack Halpern.
   * - heisig - "Remembering The Kanji"  by  James Heisig.
   * - heisig6 - "Remembering The Kanji, Sixth Ed." by James Heisig.
   * - gakken - "A New Dictionary of Kanji Usage" (Gakken)
   * - oneill_names - "Japanese Names", by P.G. O'Neill.
   * - oneill_kk - "Essential Kanji" by P.G. O'Neill.
   * - moro - See {@link Kanjidic2DictionaryReferenceMorohashi}
   * - henshall - "A Guide To Remembering Japanese Characters" by Kenneth G. Henshall.
   * - sh_kk - "Kanji and Kana" by Spahn and Hadamitzky.
   * - sh_kk2 - "Kanji and Kana" by Spahn and Hadamitzky (2011 edition).
   * - sakade - "A Guide To Reading and Writing Japanese" edited by Florence Sakade.
   * - jf_cards - Japanese Kanji Flashcards, by Max Hodges and Tomoko Okazaki. (Series 1)
   * - henshall3 - "A Guide To Reading and Writing Japanese" 3rd
   *   edition, edited by Henshall, Seeley and De Groot.
   * - tutt_cards - Tuttle Kanji Cards, compiled by Alexander Kask.
   * - crowley - "The Kanji Way to Japanese Language Power" by Dale Crowley.
   * - kanji_in_context - "Kanji in Context" by Nishiguchi and Kono.
   * - busy_people - "Japanese For Busy People" vols I-III, published
   *   by the AJLT. The codes are the volume.chapter.
   * - kodansha_compact - the "Kodansha Compact Kanji Guide".
   * - maniette - codes from Yves Maniette's "Les Kanjis dans la tete" French adaptation of Heisig.
   *
   * 'moro' type is excluded on purpose
   *
   * @see {@link Kanjidic2DictionaryReferenceMorohashi} for Morohashi ('moro') type
   */
  type:
    | 'nelson_c'
    | 'nelson_n'
    | 'halpern_njecd'
    | 'halpern_kkd'
    | 'halpern_kkld'
    | 'halpern_kkld_2ed'
    | 'heisig'
    | 'heisig6'
    | 'gakken'
    | 'oneill_names'
    | 'oneill_kk'
    | 'henshall'
    | 'sh_kk'
    | 'sh_kk2'
    | 'sakade'
    | 'jf_cards'
    | 'henshall3'
    | 'tutt_cards'
    | 'crowley'
    | 'kanji_in_context'
    | 'busy_people'
    | 'kodansha_compact'
    | 'maniette';
  morohashi: null;
  value: string;
};

/**
 * Dictionary references.
 *
 * This type is split into multiple cases for better type checking:
 *
 * - {@link Kanjidic2DictionaryReferenceMorohashi} - "Morohashi", has an optional additional field
 * - {@link Kanjidic2DictionaryReferenceNotMorohashi} - everything else
 */
export type Kanjidic2DictionaryReference =
  | Kanjidic2DictionaryReferenceMorohashi
  | Kanjidic2DictionaryReferenceNotMorohashi;

/**
 * Special case for query code: skip
 *
 * @see {@link Kanjidic2QueryCodeNotSkip} for non-skip types
 */
export type Kanjidic2QueryCodeSkip = {
  /**
   * - skip -  Halpern's SKIP (System of Kanji Indexing by Patterns)
   *   code. The format is n-nn-nn. See the KANJIDIC documentation
   *   for a description of the code and restrictions on the
   *   commercial use of this data. [P] There are also
   *   a number of misclassification codes, indicated by the
   *   "skip_misclass" attribute.
   *
   * @see {@link Kanjidic2QueryCodeNotSkip} for non-skip types
   */
  type: 'skip';
  /**
   * - posn - a mistake in the division of the kanji
   * - stroke_count - a mistake in the number of strokes
   * - stroke_and_posn - mistakes in both division and strokes
   * - stroke_diff - ambiguous stroke counts depending on glyph
   */
  skipMisclassification:
    | 'posn'
    | 'stroke_count'
    | 'stroke_and_posn'
    | 'stroke_diff'
    | null;
  value: string;
};

export type Kanjidic2QueryCodeNotSkip = {
  /**
   * - skip - See {@link Kanjidic2QueryCodeSkip}
   * - sh_desc - the descriptor codes for The Kanji Dictionary (Tuttle 1996)
   *   by Spahn and Hadamitzky. They are in the form nxnn.n,
   *   e.g. 3k11.2, where the kanji has 3 strokes in the
   *   identifying radical, it is radical "k" in the SH
   *   classification system, there are 11 other strokes, and it is
   *   the 2nd kanji in the 3k11 sequence. [I]
   * - four_corner - the "Four Corner" code for the kanji. This is a code
   *   invented by Wang Chen in 1928. See the KANJIDIC documentation
   *   for an overview of  the Four Corner System. [Q]
   * - deroo - the codes developed by the late Father Joseph De Roo, and
   *   published in  his book "2001 Kanji" (Bonjinsha). Fr De Roo
   *   gave his permission for these codes to be included. [DR]
   * - misclass - a possible misclassification of the kanji according
   *   to one of the code types. (See the "Z" codes in the KANJIDIC
   *   documentation for more details.)
   *
   * 'skip' type is excluded on purpose
   *
   * @see {@link Kanjidic2QueryCodeSkip} for 'skip' type
   */
  type: 'sh_desc' | 'four_corner' | 'deroo' | 'misclass';
  skipMisclassification: null;
  value: string;
};

/**
 * Query codes.
 *
 * This type is split into multiple cases for better type checking:
 *
 * - {@link Kanjidic2QueryCodeSkip} - "skip" code, has an optional additional field
 * - {@link Kanjidic2QueryCodeNotSkip} - everything else
 */
export type Kanjidic2QueryCode =
  | Kanjidic2QueryCodeSkip
  | Kanjidic2QueryCodeNotSkip;

/**
 * Readings and meanings of kanji, split by groups
 */
export type Kanjidic2ReadingMeaning = {
  /**
   * Groups are required because different readings can have
   * different meanings.
   */
  groups: Kanjidic2ReadingMeaningGroup[];

  /**
   * Japanese readings that are now only associated with names.
   * (from jap. "名乗り", "to say or give one's own name")
   */
  nanori: string[];
};

/**
 * Reading/meaning group.
 *
 * Groups are required because different readings can have
 * different meanings.
 */
export type Kanjidic2ReadingMeaningGroup = {
  readings: Kanjidic2Reading[];
  meanings: Kanjidic2Meaning[];
};

export type Kanjidic2Reading = {
  /**
   * - pinyin - the modern PinYin romanization of the Chinese reading
   *   of the kanji. The tones are represented by a concluding digit. [Y]
   * - korean_r - the romanized form of the Korean reading(s) of the
   *   kanji. The readings are in the (Republic of Korea) Ministry
   *   of Education style of romanization. [W]
   * - korean_h - the Korean reading(s) of the kanji in hangul.
   * - vietnam - the Vietnamese readings supplied by Minh Chau Pham.
   * - ja_on - the "on" Japanese reading of the kanji, in katakana.
   *   Another attribute r_status, if present, will indicate with
   *   a value of "jy" whether the reading is approved for a
   *   "Jouyou kanji". (The r_status attribute is not currently used.)
   *   A further attribute on_type, if present, will indicate with
   *   a value of kan, go, tou or kan'you the type of on-reading.
   *   (The on_type attribute is not currently used.)
   * - ja_kun - the "kun" Japanese reading of the kanji, usually in hiragana.
   *   Where relevant the okurigana is also included separated by a
   *   "." (dot). Readings associated with prefixes and suffixes are
   *   marked with a "-" (minus/hyphen). A second attribute r_status, if present,
   *   will indicate with a value of "jy" whether the reading is
   *   approved for a "Jouyou kanji". (The r_status attribute is not currently used.)
   */
  type: 'pinyin' | 'korean_r' | 'korean_h' | 'vietnam' | 'ja_on' | 'ja_kun';

  /**
   * Indicates the type of on-reading: "kan", "go", "tou" or "kan'you".
   * Currently not used.
   */
  onType: string | null;

  /**
   * "jy" indicates the reading is approved for a "Jouyou kanji"
   * Currently not used.
   */
  status: string | null;

  value: string;
};

/**
 * Meaning usually refers to a historical usage of a kanji.
 * This sometimes doesn't represent the current usage.
 * For example, some kanji are not used as standalone words anymore,
 * or used in multiple words with unrelated meanings.
 */
export type Kanjidic2Meaning = {
  lang: Language2Letter;
  value: string;
};

//////////////////////////////
// KRADFILE+KRADFILE2 types //
//////////////////////////////

/**
 * KRADFILE and KRADFILE2 are combined into a single file.
 * This is the only type you'll need.
 */
export interface Kradfile {
  /**
   * Version of jmdict-simplified project
   */
  version: string;

  /**
   * Map of: Kanji -> list of radicals/components
   */
  kanji: {
    [kanji: string]: string[];
  };
}

//////////////////////////////
// RADKFILE+RADKFILE2 types //
//////////////////////////////

/**
 * RADKFILE and RADKFILE2 are combined into a single file.
 * (The "radkfilex" file from the source archive is used.)
 */
export interface Radkfile {
  /**
   * Version of jmdict-simplified project
   */
  version: string;

  /**
   * Map of: radical -> radical info, see {@link RadkfileRadicalInfo}
   */
  radicals: {
    // Radical -> info
    [radical: string]: RadkfileRadicalInfo;
  };
}

/**
 * Radical info
 */
export type RadkfileRadicalInfo = {
  /**
   * Stroke count, integer > 0
   */
  strokeCount: number;

  /**
   * One of:
   *
   * - the JIS X 0212 code of the kanji whose glyph better depicts the element in question
   * - the name of an image file (used by the WWWJDIC server)
   */
  code: string | null;

  /**
   * Kanji which use this radical.
   */
  kanji: string[];
};
