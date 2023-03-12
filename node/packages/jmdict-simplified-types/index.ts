//////////////////////////////////////////////
// Shared custom types for all dictionaries //
//////////////////////////////////////////////

/**
 * Language code, ISO 639-1 standard.
 * 2 letters: "en", "es", "fr"
 * See <https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes>
 * See <https://en.wikipedia.org/wiki/ISO_639-1>
 */
export type Language2Letter = string;

/**
 * Language code, ISO 639-2 standard.
 * 3 letters: "eng", "spa", "fra"
 * See <https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes>
 * See <https://en.wikipedia.org/wiki/ISO_639-2>
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
   * See <https://semver.org/>
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
 * - `["○", "まる", 1]` - same as prevous, but "○" is a special character
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

export type JMdictKana = {
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

export type JMdictSense = {
  /**
   * Parts of speech for this sense.
   *
   * In the original files, part-of-speech from the previous sense elements
   * may apply to the sunsequent elements: e.g. if the 1st and 2nd elements
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
   * `"*"` means "all". This is never empty, unlike {@link JMdictKana#appliesToKanji}.
   * See also {@link JMdictKana#appliesToKanji}
   */
  appliesToKanji: string[];

  /**
   * List of kana writings within this word which this sense applies to.
   * Works in conjunction with the previous `appliesToKanji` field.
   * "*"` means "all". This is never empty, unlike {@link JMdictKana#appliesToKanji}.
   * See also `appliesToKanji` field and {@link JMdictKana#appliesToKanji}.
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
   * Miscellania - list of other tags which don't fit into other tag fields
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
   * See <https://en.wikipedia.org/wiki/Wasei-eigo>
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
   * Typically for a noun in the target language.
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
   * See {@link JMdictKanji#text}
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

export type JMnedictTranslation = {
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
  fileVersion: number;
  databaseRevision: string;
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
  literal: string;
  codepoints: Kanjidic2Codepoint[];
  radicals: Kanjidic2Radical[];
  misc: Kanjidic2Misc;
  dictionaryReferences: Kanjidic2DictionaryReference[];
  queryCodes: Kanjidic2QueryCode[];
  readingMeaning: Kanjidic2ReadingMeaning | null;
};

export type Kanjidic2Codepoint = {
  type: 'jis208' | 'jis212' | 'jis213' | 'ucs';
  value: string;
};

export type Kanjidic2Radical = {
  type: 'classical' | 'nelson_c';
  value: number;
};

export type Kanjidic2Misc = {
  grade: number | null;
  strokeCounts: number[];
  variants: Kanjidic2Variant[];
  frequency: number | null;
  radicalNames: string[];
  jlptLevel: number | null;
};

export type Kanjidic2Variant = {
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
 * @see {Kanjidic2DictionaryReferenceNotMorohashi} for non-Morohashi types
 */
export type Kanjidic2DictionaryReferenceMorohashi = {
  type: 'moro';
  moroshashi: {
    volume: number;
    page: number;
  };
  value: string;
};

export type Kanjidic2DictionaryReferenceNotMorohashi = {
  /**
   * 'moro' type is excluded on purpose
   * @see {Kanjidic2DictionaryReferenceMorohashi} for Morohashi ('moro') type
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
  moroshashi: null;
  value: string;
};

export type Kanjidic2DictionaryReference =
  | Kanjidic2DictionaryReferenceMorohashi
  | Kanjidic2DictionaryReferenceNotMorohashi;

/**
 * Special case for query code: skip
 * @see {Kanjidic2QueryCodeNotSkip} for non-'skip' types
 */
export type Kanjidic2QueryCodeSkip = {
  type: 'skip';
  skipMisclassification:
    | 'posn'
    | 'stroke_count'
    | 'stroke_and_posn'
    | 'stroke_diff';
  value: string;
};

export type Kanjidic2QueryCodeNotSkip = {
  /**
   * 'skip' type is excluded on purpose
   * @see {Kanjidic2QueryCodeSkip} for 'skip' type
   */
  type: 'sh_desc' | 'four_corner' | 'deroo' | 'misclass';
  skipMisclassification: null;
  value: string;
};

export type Kanjidic2QueryCode =
  | Kanjidic2QueryCodeSkip
  | Kanjidic2QueryCodeNotSkip;

export type Kanjidic2ReadingMeaning = {
  groups: Kanjidic2ReadingMeaningGroup[];
  nanori: string[];
};

export type Kanjidic2ReadingMeaningGroup = {
  readings: Kanjidic2Reading[];
  meanings: Kanjidic2Meaning[];
};

export type Kanjidic2Reading = {
  type: 'pinyin' | 'korean_r' | 'korean_h' | 'vietnam' | 'ja_on' | 'ja_kun';
  onType: string | null;
  status: string | null;
  value: string;
};

export type Kanjidic2Meaning = {
  lang: Language2Letter;
  value: string;
};
