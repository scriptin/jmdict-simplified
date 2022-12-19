package org.edrdg.jmdict.simplified.parsing.jmnedict

import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry

/**
 * Tags from the original XML files.
 *
 * Top level element is not included here because it's too simple:
 *
 * ~~~xml
 * <!ELEMENT JMnedict (entry*)>
 * ~~~
 */
sealed class JMnedictXmlElement(open val name: String) {
    /**
     * Dictionary entry
     *
     * ~~~xml
     * <!ELEMENT entry (ent_seq, k_ele*, r_ele+, trans+)>
     * ~~~
     *
     * Entries consist of kanji elements, reading elements
     * name translation elements. Each entry must have at
     * least one reading element and one sense element. Others are optional.
     *
     * Note: probably "and one translation element", not "and one sense element"
     */
    data class Entry(
        val entSeq: EntSeq,
        val kEle: List<KEle>,
        val rEle: List<REle>,
        val trans: List<Trans>,
    ) : JMnedictXmlElement("entry"), InputDictionaryEntry {
        override val allLanguages: Set<String>
            get() = trans
                .flatMap { trans -> trans.transDet.map { it.lang } }
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
    data class EntSeq(val text: String) : JMnedictXmlElement("ent_seq")

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
     * element associated with an entity name in Japanese. Where there are
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
    ) : JMnedictXmlElement("k_ele")

    /**
     * ~~~xml
     * <!ELEMENT keb (#PCDATA)>
     * ~~~
     *
     * This element will contain an entity name in Japanese
     * which is written using at least one non-kana character (usually
     * kanji, but can be other characters). The valid
     * characters are kanji, kana, related characters such as chouon and
     * kurikaeshi, and in exceptional cases, letters from other alphabets.
     */
    data class Keb(val text: String) : JMnedictXmlElement("keb")

    /**
     * ~~~xml
     * <!ELEMENT ke_inf (#PCDATA)>
     * ~~~
     *
     * This is a coded information field related specifically to the
     * orthography of the keb, and will typically indicate some unusual
     * aspect, such as okurigana irregularity.
     */
    data class KeInf(val text: String) : JMnedictXmlElement("ke_inf")

    /**
     * ~~~xml
     * <!ELEMENT ke_pri (#PCDATA)>
     * ~~~
     *
     * This and the equivalent re_pri field are provided to record
     * information about the relative priority of the entry, and are for
     * use either by applications which want to concentrate on entries of
     * a particular priority, or to generate subset files. The reason
     * both the kanji and reading elements are tagged is because on
     * occasions a priority is only associated with a particular
     * kanji/reading pair.
     */
    data class KePri(val text: String) : JMnedictXmlElement("ke_pri")

    /**
     * ~~~xml
     * <!ELEMENT r_ele (reb, re_restr*, re_inf*, re_pri*)>
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
        val reRestr: List<ReRestr>,
        val reInf: List<ReInf>,
        val rePri: List<RePri>,
    ) : JMnedictXmlElement("r_ele")

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
    data class Reb(val text: String) : JMnedictXmlElement("reb")

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
    data class ReRestr(val text: String) : JMnedictXmlElement("re_restr")

    /**
     * ~~~xml
     * <!ELEMENT re_inf (#PCDATA)>
     * ~~~
     *
     * General coded information pertaining to the specific reading.
     * Typically it will be used to indicate some unusual aspect of
     * the reading.
     */
    data class ReInf(val text: String) : JMnedictXmlElement("re_inf")

    /**
     * ~~~xml
     * <!ELEMENT re_pri (#PCDATA)>
     * ~~~
     *
     * See the comment on ke_pri above.
     */
    data class RePri(val text: String) : JMnedictXmlElement("re_pri")

    /**
     * ~~~xml
     * <!ELEMENT trans (name_type*, xref*, trans_det*)>
     * ~~~
     *
     * The trans element will record the translational equivalent
     * of the Japanese name, plus other related information.
     */
    data class Trans(
        val nameType: List<NameType>,
        val xref: List<Xref>,
        val transDet: List<TransDet>,
    ) : JMnedictXmlElement("trans")

    /**
     * ~~~xml
     * <!ELEMENT name_type (#PCDATA)>
     * ~~~
     *
     * The type of name, recorded in the appropriate entity codes.
     */
    data class NameType(val text: String) : JMnedictXmlElement("name_type")

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
     * cross-reference.
     */
    data class Xref(val text: String) : JMnedictXmlElement("xref")

    /**
     * ~~~xml
     * <!ELEMENT trans_det (#PCDATA)>
     * ~~~
     *
     * The actual translations of the name, usually as a transcription
     * into the target language.
     */
    data class TransDet(
        /**
         * ~~~xml
         * <!ATTLIST trans_det xml:lang CDATA #IMPLIED>
         * ~~~
         *
         * The xml:lang attribute defines the target language of the
         * translated name. It will be coded using the three-letter language
         * code from the ISO 639-2 standard. When absent, the value "eng"
         * (i.e. English) is the default value. The bibliographic (B) codes
         * are used.
         */
        val lang: String,
        val text: String,
    ) : JMnedictXmlElement("trans_det")
}
