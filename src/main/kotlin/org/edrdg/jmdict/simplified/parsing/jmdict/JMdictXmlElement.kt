package org.edrdg.jmdict.simplified.parsing.jmdict

import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry

/**
 * Tags from the original XML files.
 *
 * Top level element is not included here because it's too simple:
 *
 * ~~~xml
 * <!ELEMENT JMdict (entry*)>
 * ~~~
 */
sealed class JMdictXmlElement(open val name: String) {
    /**
     * Dictionary entry
     *
     * ~~~xml
     * <!ELEMENT entry (ent_seq, k_ele*, r_ele+, sense+)>
     * ~~~
     *
     * Entries consist of kanji elements, reading elements,
     * general information and sense elements. Each entry must have at
     * least one reading element and one sense element. Others are optional.
     */
    data class Entry(
        val entSeq: EntSeq,
        val kEle: List<KEle>,
        val rEle: List<REle>,
        val sense: List<Sense>,
    ) : JMdictXmlElement("entry"), InputDictionaryEntry {
        override val allLanguages: Set<String>
            get() = sense
                .flatMap { sense -> sense.gloss.map { it.lang } }
                .toSet()
    }

    /**
     * Entry ID
     *
     * ~~~xml
     * <!ELEMENT ent_seq (#PCDATA)>
     * ~~~
     *
     * A unique numeric sequence number for each entry
     */
    data class EntSeq(val text: String) : JMdictXmlElement("ent_seq")

    /**
     * Kanji spelling (may contain kana)
     *
     * ~~~xml
     * <!ELEMENT k_ele (keb, ke_inf*, ke_pri*)>
     * ~~~
     *
     * The kanji element, or in its absence, the reading element, is
     * the defining component of each entry.
     * The overwhelming majority of entries will have a single kanji
     * element associated with a word in Japanese. Where there are
     * multiple kanji elements within an entry, they will be orthographical
     * variants of the same word, either using variations in okurigana, or
     * alternative and equivalent kanji. Common "mis-spellings" may be
     * included, provided they are associated with appropriate information
     * fields. Synonyms are not included; they may be indicated in the
     * cross-reference field associated with the sense element.
     */
    data class KEle(
        val keb: Keb,
        val keInf: List<KeInf>,
        val kePri: List<KePri>,
    ) : JMdictXmlElement("k_ele")

    /**
     * ~~~xml
     * <!ELEMENT keb (#PCDATA)>
     * ~~~
     *
     * This element will contain a word or short phrase in Japanese
     * which is written using at least one non-kana character (usually kanji,
     * but can be other characters). The valid characters are
     * kanji, kana, related characters such as chouon and kurikaeshi, and
     * in exceptional cases, letters from other alphabets.
     */
    data class Keb(val text: String) : JMdictXmlElement("keb")

    /**
     * ~~~xml
     * <!ELEMENT ke_inf (#PCDATA)>
     * ~~~
     *
     * This is a coded information field related specifically to the
     * orthography of the keb, and will typically indicate some unusual
     * aspect, such as okurigana irregularity.
     */
    data class KeInf(val text: String) : JMdictXmlElement("ke_inf")

    /**
     * ~~~xml
     * <!ELEMENT ke_pri (#PCDATA)>
     * ~~~
     *
     * This and the equivalent re_pri field are provided to record
     * information about the relative priority of the entry,  and consist
     * of codes indicating the word appears in various references which
     * can be taken as an indication of the frequency with which the word
     * is used. This field is intended for use either by applications which
     * want to concentrate on entries of  a particular priority, or to
     * generate subset files.
     *
     * The current values in this field are:
     *
     * - news1/2: appears in the "wordfreq" file compiled by Alexandre Girardi
     *   from the Mainichi Shimbun. (See the Monash ftp archive for a copy.)
     *   Words in the first 12,000 in that file are marked "news1" and words
     *   in the second 12,000 are marked "news2".
     * - ichi1/2: appears in the "Ichimango goi bunruishuu", Senmon Kyouiku
     *   Publishing, Tokyo, 1998.  (The entries marked "ichi2" were
     *   demoted from ichi1 because they were observed to have low
     *   frequencies in the WWW and newspapers.)
     * - spec1 and spec2: a small number of words use this marker when they
     *   are detected as being common, but are not included in other lists.
     * - gai1/2: common loanwords, based on the wordfreq file.
     * - nfxx: this is an indicator of frequency-of-use ranking in the
     *   wordfreq file. "xx" is the number of the set of 500 words in which
     *   the entry can be found, with "01" assigned to the first 500, "02"
     *   to the second, and so on. (The entries with news1, ichi1, spec1, spec2
     *   and gai1 values are marked with a "(P)" in the EDICT and EDICT2
     *   files.)
     *
     * The reason both the kanji and reading elements are tagged is because
     * on occasions a priority is only associated with a particular
     * kanji/reading pair.
     */
    data class KePri(val text: String) : JMdictXmlElement("ke_pri")

    /**
     * Kana/reading
     *
     * ~~~xml
     * <!ELEMENT r_ele (reb, re_nokanji?, re_restr*, re_inf*, re_pri*)>
     * ~~~
     *
     * The reading element typically contains the valid readings
     * of the word(s) in the kanji element using modern kanadzukai.
     * Where there are multiple reading elements, they will typically be
     * alternative readings of the kanji element. In the absence of a
     * kanji element, i.e. in the case of a word or phrase written
     * entirely in kana, these elements will define the entry.
     */
    data class REle(
        val reb: Reb,
        val reNokanji: ReNokanji?,
        val reRestr: List<ReRestr>,
        val reInf: List<ReInf>,
        val rePri: List<RePri>,
    ) : JMdictXmlElement("r_ele")

    /**
     * ~~~xml
     * <!ELEMENT reb (#PCDATA)>
     * ~~~
     *
     * this element content is restricted to kana and related
     * characters such as chouon and kurikaeshi. Kana usage will be
     * consistent between the keb and reb elements; e.g. if the keb
     * contains katakana, so too will the reb.
     */
    data class Reb(val text: String) : JMdictXmlElement("reb")

    /**
     * ~~~xml
     * <!ELEMENT re_nokanji (#PCDATA)>
     * ~~~
     *
     * This element, which will usually have a null value, indicates
     * that the reb, while associated with the keb, cannot be regarded
     * as a true reading of the kanji. It is typically used for words
     * such as foreign place names, gairaigo which can be in kanji or
     * katakana, etc.
     */
    data class ReNokanji(val text: String?) : JMdictXmlElement("re_nokanji")

    /**
     * ~~~xml
     * <!ELEMENT re_restr (#PCDATA)>
     * ~~~
     *
     * This element is used to indicate when the reading only applies
     * to a subset of the keb elements in the entry. In its absence, all
     * readings apply to all kanji elements. The contents of this element
     * must exactly match those of one of the keb elements.
     */
    data class ReRestr(val text: String) : JMdictXmlElement("re_restr")

    /**
     * ~~~xml
     * <!ELEMENT re_inf (#PCDATA)>
     * ~~~
     *
     * General coded information pertaining to the specific reading.
     * Typically it will be used to indicate some unusual aspect of
     * the reading.
     */
    data class ReInf(val text: String) : JMdictXmlElement("re_inf")

    /**
     * ~~~xml
     * <!ELEMENT re_pri (#PCDATA)>
     * ~~~
     *
     * See the comment on ke_pri above.
     */
    data class RePri(val text: String) : JMdictXmlElement("re_pri")

    /**
     * Sense/translation
     *
     * ~~~xml
     * <!ELEMENT sense (stagk*, stagr*, pos*, xref*, ant*, field*, misc*, s_inf*, lsource*, dial*, gloss*)>
     * ~~~
     *
     * The sense element will record the translational equivalent
     * of the Japanese word, plus other related information. Where there
     * are several distinctly different meanings of the word, multiple
     * sense elements will be employed.
     */
    data class Sense(
        val stagk: List<Stagk>,
        val stagr: List<Stagr>,
        val pos: List<Pos>,
        val xref: List<Xref>,
        val ant: List<Ant>,
        val field: List<Field>,
        val misc: List<Misc>,
        val sInf: List<SInf>,
        val lsource: List<Lsource>,
        val dial: List<Dial>,
        val gloss: List<Gloss>,
        val example: List<Example>,
    ) : JMdictXmlElement("sense")

    /**
     * ~~~xml
     * <!ELEMENT stagk (#PCDATA)>
     * ~~~
     *
     * These elements, if present, indicate that the sense is restricted
     * to the lexeme represented by the keb and/or reb.
     *
     * @see Stagr
     */
    data class Stagk(val text: String) : JMdictXmlElement("stagk")

    /**
     * ~~~xml
     * <!ELEMENT stagr (#PCDATA)>
     * ~~~
     *
     * @see Stagk
     */
    data class Stagr(val text: String) : JMdictXmlElement("stagr")

    /**
     * ~~~xml
     * <!ELEMENT pos (#PCDATA)>
     * ~~~
     *
     * Part-of-speech information about the entry/sense. Should use
     * appropriate entity codes. In general where there are multiple senses
     * in an entry, the part-of-speech of an earlier sense will apply to
     * later senses unless there is a new part-of-speech indicated.
     */
    data class Pos(val text: String) : JMdictXmlElement("pos")

    /**
     * ~~~xml
     * <!ELEMENT xref (#PCDATA)*>
     * ~~~
     *
     * This element is used to indicate a cross-reference to another
     * entry with a similar or related meaning or sense. The content of
     * this element is typically a keb or reb element in another entry. In some
     * cases a keb will be followed by a reb and/or a sense number to provide
     * a precise target for the cross-reference. Where this happens, a JIS
     * "centre-dot" (0x2126) is placed between the components of the
     * cross-reference. The target keb or reb must not contain a centre-dot.
     */
    data class Xref(val text: String) : JMdictXmlElement("xref")

    /**
     * ~~~xml
     * <!ELEMENT ant (#PCDATA)*>
     * ~~~
     *
     * This element is used to indicate another entry which is an
     * antonym of the current entry/sense. The content of this element
     * must exactly match that of a keb or reb element in another entry.
     */
    data class Ant(val text: String) : JMdictXmlElement("ant")

    /**
     * ~~~xml
     * <!ELEMENT field (#PCDATA)>
     * ~~~
     *
     * Information about the field of application of the entry/sense.
     * When absent, general application is implied. Entity coding for
     * specific fields of application.
     */
    data class Field(val text: String) : JMdictXmlElement("field")

    /**
     * ~~~xml
     * <!ELEMENT misc (#PCDATA)>
     * ~~~
     *
     * This element is used for other relevant information about
     * the entry/sense. As with part-of-speech, information will usually
     * apply to several senses.
     */
    data class Misc(val text: String) : JMdictXmlElement("misc")

    /**
     * ~~~xml
     * <!ELEMENT s_inf (#PCDATA)>
     * ~~~
     *
     * The sense-information elements provided for additional
     * information to be recorded about a sense. Typical usage would
     * be to indicate such things as level of currency of a sense, the
     * regional variations, etc.
     */
    data class SInf(val text: String) : JMdictXmlElement("s_inf")

    enum class LsType(val value: String) {
        FULL("full"), PART("part");

        companion object {
            fun fromString(s: String): LsType {
                return values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${values()}")
            }
        }
    }

    /**
     * ~~~xml
     * <!ELEMENT lsource (#PCDATA)>
     * ~~~
     *
     * This element records the information about the source
     * language(s) of a loan-word/gairaigo. If the source language is other
     * than English, the language is indicated by the xml:lang attribute.
     * The element value (if any) is the source word or phrase.
     */
    data class Lsource(
        /**
         * ~~~xml
         * <!ATTLIST lsource xml:lang CDATA "eng">
         * ~~~
         *
         * The xml:lang attribute defines the language(s) from which
         * a loanword is drawn.  It will be coded using the three-letter language
         * code from the ISO 639-2 standard. When absent, the value "eng" (i.e.
         * English) is the default value. The bibliographic (B) codes are used.
         */
        val lang: String,
        /**
         * ~~~xml
         * <!ATTLIST lsource ls_type CDATA #IMPLIED>
         * ~~~
         *
         * The ls_type attribute indicates whether the lsource element
         * fully or partially describes the source word or phrase of the
         * loanword. If absent, it will have the implied value of "full".
         * Otherwise it will contain "part".
         */
        val lsType: LsType,
        /**
         * ~~~xml
         * <!ATTLIST lsource ls_wasei CDATA #IMPLIED>
         * ~~~
         *
         * The ls_wasei attribute indicates that the Japanese word
         * has been constructed from words in the source language, and
         * not from an actual phrase in that language. Most commonly used to
         * indicate "waseieigo".
         */
        val lsWasei: Boolean,
        val text: String?,
    ) : JMdictXmlElement("lsource")

    /**
     * ~~~xml
     * <!ELEMENT dial (#PCDATA)>
     * ~~~
     *
     * For words specifically associated with regional dialects in
     * Japanese, the entity code for that dialect, e.g. ksb for Kansaiben.
     */
    data class Dial(val text: String) : JMdictXmlElement("dial")

    enum class GType(val value: String) {
        LIT("lit"),
        FIG("fig"),
        EXPL("expl"),
        TM("tm");

        companion object {
            fun fromString(s: String): GType {
                return values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${values()}")
            }
        }
    }

    /**
     * ~~~xml
     * <!ELEMENT gloss (#PCDATA | pri)*>
     * ~~~
     *
     * Within each sense will be one or more "glosses", i.e.
     * target-language words or phrases which are equivalents to the
     * Japanese word. This element would normally be present, however it
     * may be omitted in entries which are purely for a cross-reference.
     */
    data class Gloss(
        /**
         * ~~~xml
         * <!ATTLIST gloss xml:lang CDATA "eng">
         * ~~~
         *
         * The xml:lang attribute defines the target language of the
         * gloss. It will be coded using the three-letter language code from
         * the ISO 639 standard. When absent, the value "eng" (i.e. English)
         * is the default value.
         */
        val lang: String,
        /**
         * ~~~xml
         * <!ATTLIST gloss g_gend CDATA #IMPLIED>
         * ~~~
         *
         * The g_gend attribute defines the gender of the gloss (typically
         * a noun in the target language. When absent, the gender is either
         * not relevant or has yet to be provided.
         */
        val gGend: String?,
        /**
         * ~~~xml
         * <!ATTLIST gloss g_type CDATA #IMPLIED>
         * ~~~
         *
         * The g_type attribute specifies that the gloss is of a particular
         * type, e.g. "lit" (literal), "fig" (figurative), "expl" (explanation).
         */
        val gType: GType?,
        val text: String?,
    ) : JMdictXmlElement("gloss")

    /**
     * ~~~xml
     * <!ATTLIST ex_srce exsrc_type CDATA #IMPLIED>
     * ~~~
     *
     * Possible values:
     *
     * - 'tat' - Tatoeba.org
     */
    enum class ExampleSourceType(val value: String) {
        TAT("tat"); // Tatoeba.org™

        companion object {
            fun fromString(s: String): ExampleSourceType {
                return ExampleSourceType.values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${ExampleSourceType.values()}")
            }
        }
    }

    /**
     * ~~~xml
     * <!ATTLIST ex_srce exsrc_type CDATA #IMPLIED>
     * ~~~
     *
     * Example source information: type and identifier.
     */
    data class ExampleSource(
        /**
         * Source type of this example. Currently, the only possible value is "tat" (Tatoeba.org).
         */
        val type: ExampleSourceType,
        /**
         * Value is some kind an identifier within the source.
         */
        val value: String,
    ) : JMdictXmlElement("ex_srce")

    /**
     * ~~~xml
     * <!ELEMENT ex_sent (#PCDATA)>
     * ~~~
     *
     * Example sentence, i.e. a translation of the example.
     */
    data class ExampleSentence(
        /**
         * ~~~xml
         * <!ATTLIST ex_sent xml:lang CDATA "eng">
         * ~~~
         *
         * The xml:lang attribute defines the target language of the
         * gloss. It will be coded using the three-letter language code from
         * the ISO 639 standard. When absent, the value "eng" (i.e. English)
         * is the default value.
         */
        val lang: String,
        val text: String,
    ) : JMdictXmlElement("ex_sent")

    /**
     * ~~~xml
     * <!ELEMENT example (ex_srce,ex_text,ex_sent+)>
     *     <!-- The example elements contain a Japanese sentence using the term
     *     associated with the entry, and one or more translations of that sentence.
     *     Within the element, the ex_srce element will indicate the source of the
     *     sentences (typically the sequence number in the Tatoeba Project), the
     *     ex_text element will contain the form of the term in the Japanese
     *     sentence, and the ex_sent elements contain the example sentences.
     *     -->
     * ~~~
     *
     * Examples are provided only in one specific version of JMdict_e_examp.xml
     */
    data class Example(
        /**
         * ~~~xml
         * <!ELEMENT ex_srce (#PCDATA)>
         * ~~~
         *
         * Source type of this example. Currently, the only possible value is "tat" (Tatoeba.org).
         */
        val source: ExampleSource,
        /**
         * ~~~xml
         * <!ELEMENT ex_text (#PCDATA)>
         * ~~~
         *
         * A text version of this entry, which can be found in the example sentences.
         * This text is given exactly as it is in the Japanese example,
         * and is meant to indicate which exact spelling of a given entry appears in its example.
         * It is intended for purposes such as text highlighting.
         *
         * For example, the entry for the pronoun "you" contains several ways to write the word: 貴方, あなた, etc.
         * If this example element contains a sentence where this pronoun is spelled as "あなた",
         * then this field will also contain "あなた."
         */
        val text: String,
        val sentences: List<ExampleSentence>,
    ) : JMdictXmlElement("example")
}
