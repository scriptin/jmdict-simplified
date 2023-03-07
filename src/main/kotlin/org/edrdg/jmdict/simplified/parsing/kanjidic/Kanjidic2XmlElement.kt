package org.edrdg.jmdict.simplified.parsing.kanjidic

import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Tags from the original XML files.
 *
 * ~~~xml
 * <!ELEMENT kanjidic2 (header,character*)>
 * ~~~
 */
sealed class Kanjidic2XmlElement(open val name: String) {
    /**
     * Header
     *
     * ~~~xml
     * <!ELEMENT header (file_version,database_version,date_of_creation)>
     * <!--
     *         The single header element will contain identification information
     *         about the version of the file
     *         -->
     * ~~~
     */
    data class Header(
        val fileVersion: FileVersion,
        val databaseVersion: DatabaseVersion,
        val dateOfCreation: DateOfCreation,
    ) : Kanjidic2XmlElement("header")

    /**
     * ~~~xml
     * <!ELEMENT file_version (#PCDATA)>
     * <!--
     *         This field denotes the version of kanjidic2 structure, as more
     *         than one version may exist.
     *         -->
     * ~~~
     */
    data class FileVersion(val value: Int) : Kanjidic2XmlElement("file_version")

    /**
     * ~~~xml
     * <!ELEMENT database_version (#PCDATA)>
     * <!--
     *         The version of the file, in the format YYYY-NN, where NN will be
     *         a number starting with 01 for the first version released in a
     *         calendar year, then increasing for each version in that year.
     *         -->
     * ~~~
     */
    data class DatabaseVersion(val text: String) : Kanjidic2XmlElement("database_version") {
        val year: Int
            get() = text.split("-")[0].toInt()

        val ordinalVersion: Int
            get() = text.split("-")[1].toInt()
    }

    /**
     * ~~~xml
     * <!ELEMENT date_of_creation (#PCDATA)>
     * <!--
     *         The date the file was created in international format (YYYY-MM-DD).
     *         -->
     * ~~~
     */
    data class DateOfCreation(val text: String) : Kanjidic2XmlElement("date_of_creation") {
        val asDate: LocalDate
            get() = LocalDate.parse(text, DateTimeFormatter.ISO_DATE)
    }

    /**
     * ~~~xml
     * <!ELEMENT character (literal, codepoint, radical, misc, dic_number?, query_code?, reading_meaning?)*>
     * ~~~
     */
    data class Character(
        val literal: Literal,
        val codepoint: Codepoint,
        val radical: Radical,
        val misc: Misc,
        val dicNumber: DicNumber?,
        val queryCode: QueryCode?,
        val readingMeaning: ReadingMeaning?,
    ) : Kanjidic2XmlElement("character"), InputDictionaryEntry {
        override val allLanguages: Set<String>
            get() = readingMeaning
                ?.rmGroups
                ?.flatMap { rm -> rm.meanings.map { it.mLang } }
                ?.toSet()
                .orEmpty()
    }

    /**
     * ~~~xml
     * <!ELEMENT literal (#PCDATA)>
     * <!--
     *         The character itself in UTF8 coding.
     *         -->
     * ~~~
     */
    data class Literal(val text: String) : Kanjidic2XmlElement("literal")

    /**
     * ~~~xml
     * <!ELEMENT codepoint (cp_value+)>
     *         <!--
     *         The codepoint element states the code of the character in the various
     *         character set standards.
     *         -->
     * ~~~
     */
    data class Codepoint(val cpValues: List<CpValue>) : Kanjidic2XmlElement("codepoint")

    enum class CpType(val value: String) {
        JIS208("jis208"),
        JIS212("jis212"),
        JIS213("jis213"),
        UCS("ucs");

        companion object {
            fun fromString(s: String): CpType {
                return CpType.values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${CpType.values()}")
            }
        }
    }

    /**
     * ~~~xml
     * <!ELEMENT cp_value (#PCDATA)>
     *         <!--
     *         The cp_value contains the codepoint of the character in a particular
     *         standard. The standard will be identified in the cp_type attribute.
     *         -->
     * ~~~
     */
    data class CpValue(
        /**
         * ~~~xml
         * <!ATTLIST cp_value cp_type CDATA #REQUIRED>
         *         <!--
         *         The cp_type attribute states the coding standard applying to the
         *         element. The values assigned so far are:
         *                 jis208 - JIS X 0208-1997 - kuten coding (nn-nn)
         *                 jis212 - JIS X 0212-1990 - kuten coding (nn-nn)
         *                 jis213 - JIS X 0213-2000 - kuten coding (p-nn-nn)
         *                 ucs - Unicode 4.0 - hex coding (4 or 5 hexadecimal digits)
         *         -->
         * ~~~
         */
        val cpType: CpType,
        val text: String,
    ) : Kanjidic2XmlElement("cp_value")

    /**
     * ~~~xml
     * <!ELEMENT radical (rad_value+)>
     * ~~~
     */
    data class Radical(val radValues: List<RadValue>) : Kanjidic2XmlElement("radical")

    enum class RadType(val value: String) {
        CLASSICAL("classical"),
        NELSON_C("nelson_c");

        companion object {
            fun fromString(s: String): RadType {
                return RadType.values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${RadType.values()}")
            }
        }
    }

    /**
     * ~~~xml
     * <!ELEMENT rad_value (#PCDATA)>
     *         <!--
     *         The radical number, in the range 1 to 214. The particular
     *         classification type is stated in the rad_type attribute.
     *         -->
     * ~~~
     */
    data class RadValue(
        /**
         * ~~~xml
         * <!ATTLIST rad_value rad_type CDATA #REQUIRED>
         *         <!--
         *         The rad_type attribute states the type of radical classification.
         *                 classical - based on the system first used in the KangXi Zidian.
         *                     The Shibano "JIS Kanwa Jiten" is used as the reference source.
         *                 nelson_c - as used in the Nelson "Modern Japanese-English
         *                     Character Dictionary" (i.e. the Classic, not the New Nelson).
         *                     This will only be used where Nelson reclassified the kanji.
         *         -->
         * ~~~
         */
        val radType: RadType,
        val value: Int,
    ) : Kanjidic2XmlElement("rad_value")

    /**
     * ~~~xml
     * <!ELEMENT misc (grade?, stroke_count+, variant*, freq?, rad_name*, jlpt?)>
     * ~~~
     */
    data class Misc(
        val grade: Grade?,
        val strokeCounts: List<StrokeCount>,
        val variants: List<Variant>,
        val freq: Freq?,
        val radNames: List<RadName>,
        val jlpt: Jlpt?
    ) : Kanjidic2XmlElement("misc")

    /**
     * ~~~xml
     * <!ELEMENT grade (#PCDATA)>
     *         <!--
     *         The kanji grade level. 1 through 6 indicates a Kyouiku kanji
     *         and the grade in which the kanji is taught in Japanese schools.
     *         8 indicates it is one of the remaining Jouyou Kanji to be learned
     *         in junior high school. 9 indicates it is a Jinmeiyou (for use
     *         in names) kanji which in addition  to the Jouyou kanji are approved
     *         for use in family name registers and other official documents. 10
     *         also indicates a Jinmeiyou kanji which is a variant of a
     *         Jouyou kanji. [G]
     *         -->
     * ~~~
     */
    data class Grade(val value: Int) : Kanjidic2XmlElement("grade")

    /**
     * ~~~xml
     * <!ELEMENT stroke_count (#PCDATA)>
     *         <!--
     *         The stroke count of the kanji, including the radical. If more than
     *         one, the first is considered the accepted count, while subsequent ones
     *         are common miscounts. (See Appendix E. of the KANJIDIC documentation
     *         for some of the rules applied when counting strokes in some of the
     *         radicals.) [S]
     *         -->
     * ~~~
     */
    data class StrokeCount(val value: Int) : Kanjidic2XmlElement("stroke_count")

    enum class VarType(val value: String) {
        JIS208("jis208"),
        JIS212("jis212"),
        JIS213("jis213"),
        DEROO("deroo"),
        NJECD("njecd"),
        S_H("s_h"),
        NELSON_C("nelson_c"),
        ONEILL("oneill"),
        UCS("ucs");

        companion object {
            fun fromString(s: String): VarType {
                return VarType.values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${VarType.values()}")
            }
        }
    }

    /**
     * ~~~xml
     * <!ELEMENT variant (#PCDATA)>
     *         <!--
     *         Either a cross-reference code to another kanji, usually regarded as a
     *         variant, or an alternative indexing code for the current kanji.
     *         The type of variant is given in the var_type attribute.
     *         -->
     * ~~~
     */
    data class Variant(
        /**
         * ~~~xml
         * <!ATTLIST variant var_type CDATA #REQUIRED>
         *         <!--
         *         The var_type attribute indicates the type of variant code. The current
         *         values are:
         *                 jis208 - in JIS X 0208 - kuten coding
         *                 jis212 - in JIS X 0212 - kuten coding
         *                 jis213 - in JIS X 0213 - kuten coding
         *                   (most of the above relate to "shinjitai/kyuujitai"
         *                   alternative character glyphs)
         *                 deroo - De Roo number - numeric
         *                 njecd - Halpern NJECD index number - numeric
         *                 s_h - The Kanji Dictionary (Spahn & Hadamitzky) - descriptor
         *                 nelson_c - "Classic" Nelson - numeric
         *                 oneill - Japanese Names (O'Neill) - numeric
         *                 ucs - Unicode codepoint- hex
         *         -->
         * ~~~
         */
        val varType: VarType,
        val text: String,
    ) : Kanjidic2XmlElement("variant")

    /**
     * ~~~xml
     * <!ELEMENT freq (#PCDATA)>
     *         <!--
     *         A frequency-of-use ranking. The 2,500 most-used characters have a
     *         ranking; those characters that lack this field are not ranked. The
     *         frequency is a number from 1 to 2,500 that expresses the relative
     *         frequency of occurrence of a character in modern Japanese. This is
     *         based on a survey in newspapers, so it is biassed towards kanji
     *         used in newspaper articles. The discrimination between the less
     *         frequently used kanji is not strong. (Actually there are 2,501
     *         kanji ranked as there was a tie.)
     *         -->
     * ~~~
     */
    data class Freq(val value: Int) : Kanjidic2XmlElement("freq")

    /**
     * ~~~xml
     * <!ELEMENT rad_name (#PCDATA)>
     *         <!--
     *         When the kanji is itself a radical and has a name, this element
     *         contains the name (in hiragana.) [T2]
     *         -->
     * ~~~
     */
    data class RadName(val text: String) : Kanjidic2XmlElement("rad_name")

    /**
     * ~~~xml
     * <!ELEMENT jlpt (#PCDATA)>
     *         <!--
     *         The (former) Japanese Language Proficiency test level for this kanji.
     *         Values range from 1 (most advanced) to 4 (most elementary). This field
     *         does not appear for kanji that were not required for any JLPT level.
     *         Note that the JLPT test levels changed in 2010, with a new 5-level
     *         system (N1 to N5) being introduced. No official kanji lists are
     *         available for the new levels. The new levels are regarded as
     *         being similar to the old levels except that the old level 2 is
     *         now divided between N2 and N3.
     *         -->
     * ~~~
     */
    data class Jlpt(val value: Int) : Kanjidic2XmlElement("jlpt")

    /**
     * ~~~xml
     * <!ELEMENT dic_number (dic_ref+)>
     *         <!--
     *         This element contains the index numbers and similar unstructured
     *         information such as page numbers in a number of published dictionaries,
     *         and instructional books on kanji.
     *         -->
     * ~~~
     */
    data class DicNumber(val dicRefs: List<DicRef>) : Kanjidic2XmlElement("dic_number")

    enum class DRType(val value: String) {
        NELSON_C("nelson_c"),
        NELSON_N("nelson_n"),
        HALPERN_NJECD("halpern_njecd"),
        HALPERN_KKD("halpern_kkd"),
        HALPERN_KKLD("halpern_kkld"),
        HALPERN_KKLD_2ED("halpern_kkld_2ed"),
        HEISIG("heisig"),
        HEISIG6("heisig6"),
        GAKKEN("gakken"),
        ONEILL_NAMES("oneill_names"),
        ONEILL_KK("oneill_kk"),
        MORO("moro"),
        HENSHALL("henshall"),
        SH_KK("sh_kk"),
        SH_KK2("sh_kk2"),
        SAKADE("sakade"),
        JF_CARDS("jf_cards"),
        HENSHALL3("henshall3"),
        TUTT_CARDS("tutt_cards"),
        CROWLEY("crowley"),
        KANJI_IN_CONTEXT("kanji_in_context"),
        BUSY_PEOPLE("busy_people"),
        KODANSHA_COMPACT("kodansha_compact"),
        MANIETTE("maniette");

        companion object {
            fun fromString(s: String): DRType {
                return DRType.values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${DRType.values()}")
            }
        }
    }

    /**
     * ~~~xml
     * <!ELEMENT dic_ref (#PCDATA)>
     *         <!--
     *         Each dic_ref contains an index number. The particular dictionary,
     *         etc. is defined by the dr_type attribute.
     *         -->
     * ~~~
     */
    data class DicRef(
        /**
         * ~~~xml
         * <!ATTLIST dic_ref dr_type CDATA #REQUIRED>
         *         <!--
         *         The dr_type defines the dictionary or reference book, etc. to which
         *         dic_ref element applies. The initial allocation is:
         *           nelson_c - "Modern Reader's Japanese-English Character Dictionary",
         *                 edited by Andrew Nelson (now published as the "Classic"
         *                 Nelson).
         *           nelson_n - "The New Nelson Japanese-English Character Dictionary",
         *                 edited by John Haig.
         *           halpern_njecd - "New Japanese-English Character Dictionary",
         *                 edited by Jack Halpern.
         *           halpern_kkd - "Kodansha Kanji Dictionary", (2nd Ed. of the NJECD)
         *                 edited by Jack Halpern.
         *           halpern_kkld - "Kanji Learners Dictionary" (Kodansha) edited by
         *                 Jack Halpern.
         *           halpern_kkld_2ed - "Kanji Learners Dictionary" (Kodansha), 2nd edition
         *             (2013) edited by Jack Halpern.
         *           heisig - "Remembering The  Kanji"  by  James Heisig.
         *           heisig6 - "Remembering The  Kanji, Sixth Ed."  by  James Heisig.
         *           gakken - "A  New Dictionary of Kanji Usage" (Gakken)
         *           oneill_names - "Japanese Names", by P.G. O'Neill.
         *           oneill_kk - "Essential Kanji" by P.G. O'Neill.
         *           moro - "Daikanwajiten" compiled by Morohashi. For some kanji two
         *                 additional attributes are used: m_vol:  the volume of the
         *                 dictionary in which the kanji is found, and m_page: the page
         *                 number in the volume.
         *           henshall - "A Guide To Remembering Japanese Characters" by
         *                 Kenneth G.  Henshall.
         *           sh_kk - "Kanji and Kana" by Spahn and Hadamitzky.
         *           sh_kk2 - "Kanji and Kana" by Spahn and Hadamitzky (2011 edition).
         *           sakade - "A Guide To Reading and Writing Japanese" edited by
         *                 Florence Sakade.
         *           jf_cards - Japanese Kanji Flashcards, by Max Hodges and
         *                 Tomoko Okazaki. (Series 1)
         *           henshall3 - "A Guide To Reading and Writing Japanese" 3rd
         *                 edition, edited by Henshall, Seeley and De Groot.
         *           tutt_cards - Tuttle Kanji Cards, compiled by Alexander Kask.
         *           crowley - "The Kanji Way to Japanese Language Power" by
         *                 Dale Crowley.
         *           kanji_in_context - "Kanji in Context" by Nishiguchi and Kono.
         *           busy_people - "Japanese For Busy People" vols I-III, published
         *                 by the AJLT. The codes are the volume.chapter.
         *           kodansha_compact - the "Kodansha Compact Kanji Guide".
         *           maniette - codes from Yves Maniette's "Les Kanjis dans la tete" French adaptation of Heisig.
         *         -->
         * ~~~
         */
        val drType: DRType,
        /**
         * ~~~xml
         * <!ATTLIST dic_ref m_vol CDATA #IMPLIED>
         *         <!--
         *         See above under "moro".
         *         -->
         * ~~~
         */
        val mVol: Int?,
        /**
         * ~~~xml
         * <!ATTLIST dic_ref m_page CDATA #IMPLIED>
         *         <!--
         *         See above under "moro".
         *         -->
         * ~~~
         *
         * In the XML, this is a 4-digit decimal number with zero padding.
         * Leading zeroes are dropped here
         */
        val mPage: Int?,
        /**
         * Usually an integer, but there are exception with "X" as a last character,
         * e.g. "21371X"
         */
        val value: String,
    ) : Kanjidic2XmlElement("dic_ref")

    /**
     * ~~~xml
     * <!ELEMENT query_code (q_code+)>
     *         <!--
     *         These codes contain information relating to the glyph, and can be used
     *         for finding a required kanji. The type of code is defined by the
     *         qc_type attribute.
     *         -->
     * ~~~
     */
    data class QueryCode(val qCodes: List<QCode>) : Kanjidic2XmlElement("query_code")

    enum class QCType(val value: String) {
        SKIP("skip"),
        SH_DESC("sh_desc"),
        FOUR_CORNER("four_corner"),
        DEROO("deroo"),
        MISCLASS("misclass");

        companion object {
            fun fromString(s: String): QCType {
                return QCType.values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${QCType.values()}")
            }
        }
    }

    enum class SkipMisclass(val value: String) {
        POSN("posn"),
        STROKE_COUNT("stroke_count"),
        STROKE_AND_POSN("stroke_and_posn"),
        STROKE_DIFF("stroke_diff");

        companion object {
            fun fromString(s: String): SkipMisclass {
                return SkipMisclass.values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${SkipMisclass.values()}")
            }
        }
    }

    /**
     * ~~~xml
     * <!ELEMENT q_code (#PCDATA)>
     *         <!--
     *         The q_code contains the actual query-code value, according to the
     *         qc_type attribute.
     *         -->
     * ~~~
     */
    data class QCode(
        /**
         * ~~~xml
         * <!ATTLIST q_code qc_type CDATA #REQUIRED>
         *         <!--
         *         The qc_type attribute defines the type of query code. The current values
         *         are:
         *           skip -  Halpern's SKIP (System  of  Kanji  Indexing  by  Patterns)
         *                 code. The  format is n-nn-nn.  See the KANJIDIC  documentation
         *                 for  a description of the code and restrictions on  the
         *                 commercial  use  of this data. [P]  There are also
         *                 a number of misclassification codes, indicated by the
         *                 "skip_misclass" attribute.
         *           sh_desc - the descriptor codes for The Kanji Dictionary (Tuttle
         *                 1996) by Spahn and Hadamitzky. They are in the form nxnn.n,
         *                 e.g.  3k11.2, where the  kanji has 3 strokes in the
         *                 identifying radical, it is radical "k" in the SH
         *                 classification system, there are 11 other strokes, and it is
         *                 the 2nd kanji in the 3k11 sequence. (I am very grateful to
         *                 Mark Spahn for providing the list of these descriptor codes
         *                 for the kanji in this file.) [I]
         *           four_corner - the "Four Corner" code for the kanji. This is a code
         *                 invented by Wang Chen in 1928. See the KANJIDIC documentation
         *                 for  an overview of  the Four Corner System. [Q]
         *
         *           deroo - the codes developed by the late Father Joseph De Roo, and
         *                 published in  his book "2001 Kanji" (Bonjinsha). Fr De Roo
         *                 gave his permission for these codes to be included. [DR]
         *           misclass - a possible misclassification of the kanji according
         *                 to one of the code types. (See the "Z" codes in the KANJIDIC
         *                 documentation for more details.)
         *
         *         -->
         * ~~~
         */
        val qcType: QCType,
        /**
         * ~~~xml
         * <!ATTLIST q_code skip_misclass CDATA #IMPLIED>
         *         <!--
         *         The values of this attribute indicate the type if
         *         misclassification:
         *         - posn - a mistake in the division of the kanji
         *         - stroke_count - a mistake in the number of strokes
         *         - stroke_and_posn - mistakes in both division and strokes
         *         - stroke_diff - ambiguous stroke counts depending on glyph
         *         -->
         * ~~~
         */
        val skipMisclass: SkipMisclass?,
        val text: String,
    ) : Kanjidic2XmlElement("q_code")

    /**
     * ~~~xml
     * <!ELEMENT reading_meaning (rmgroup*, nanori*)>
     *         <!--
     *         The readings for the kanji in several languages, and the meanings, also
     *         in several languages. The readings and meanings are grouped to enable
     *         the handling of the situation where the meaning is differentiated by
     *         reading. [T1]
     *         -->
     * ~~~
     */
    data class ReadingMeaning(
        val rmGroups: List<RmGroup>,
        val nanori: List<Nanori>,
    ) : Kanjidic2XmlElement("reading_meaning")

    /**
     * ~~~xml
     * <!ELEMENT rmgroup (reading*, meaning*)>
     * ~~~
     */
    data class RmGroup(
        val readings: List<Reading>,
        val meanings: List<Meaning>,
    ) : Kanjidic2XmlElement("rmgroup")

    enum class RType(val value: String) {
        PINYIN("pinyin"),
        KOREAN_R("korean_r"),
        KOREAN_H("korean_h"),
        VIETNAM("vietnam"),
        JA_ON("ja_on"),
        JA_KUN("ja_kun");

        companion object {
            fun fromString(s: String): RType {
                return RType.values().find { it.value == s } ?:
                throw IllegalArgumentException("Value $s is not one of: ${RType.values()}")
            }
        }
    }

    /**
     * ~~~xml
     * <!ELEMENT reading (#PCDATA)>
     *         <!--
     *         The reading element contains the reading or pronunciation
     *         of the kanji.
     *         -->
     * ~~~
     */
    data class Reading(
        /**
         * ~~~xml
         * <!ATTLIST reading r_type CDATA #REQUIRED>
         *         <!--
         *         The r_type attribute defines the type of reading in the reading
         *         element. The current values are:
         *           pinyin - the modern PinYin romanization of the Chinese reading
         *                 of the kanji. The tones are represented by a concluding
         *                 digit. [Y]
         *           korean_r - the romanized form of the Korean reading(s) of the
         *                 kanji.  The readings are in the (Republic of Korea) Ministry
         *                 of Education style of romanization. [W]
         *           korean_h - the Korean reading(s) of the kanji in hangul.
         *           vietnam - the Vietnamese readings supplied by Minh Chau Pham.
         *           ja_on - the "on" Japanese reading of the kanji, in katakana.
         *                 Another attribute r_status, if present, will indicate with
         *                 a value of "jy" whether the reading is approved for a
         *                 "Jouyou kanji". (The r_status attribute is not currently used.)
         *                 A further attribute on_type, if present,  will indicate with
         *                 a value of kan, go, tou or kan'you the type of on-reading.
         *                 (The on_type attribute is not currently used.)
         *           ja_kun - the "kun" Japanese reading of the kanji, usually in
         *                 hiragana.
         *                 Where relevant the okurigana is also included separated by a
         *                 ".". Readings associated with prefixes and suffixes are
         *                 marked with a "-". A second attribute r_status, if present,
         *                 will indicate with a value of "jy" whether the reading is
         *                 approved for a "Jouyou kanji". (The r_status attribute is
         *                 not currently used.)
         *         -->
         * ~~~
         */
        val rType: RType,
        /**
         * ~~~xml
         * <!ATTLIST reading on_type CDATA #IMPLIED>
         *         <!--
         *         See under ja_on above.
         *         -->
         * ~~~
         */
        val onType: String?,
        /**
         * ~~~xml
         * <!ATTLIST reading r_status CDATA #IMPLIED>
         *         <!--
         *         See under ja_on and ja_kun above.
         *         -->
         * ~~~
         */
        val rStatus: String?,
        val text: String,
    ) : Kanjidic2XmlElement("reading")

    /**
     * ~~~xml
     * <!ELEMENT meaning (#PCDATA)>
     *         <!--
     *         The meaning associated with the kanji.
     *         -->
     * ~~~
     */
    data class Meaning(
        /**
         * ~~~xml
         * <!ATTLIST meaning m_lang CDATA #IMPLIED>
         *         <!--
         *         The m_lang attribute defines the target language of the meaning. It
         *         will be coded using the two-letter language code from the ISO 639-1
         *         standard. When absent, the value "en" (i.e. English) is implied. [{}]
         *         -->
         * ~~~
         */
        val mLang: String,
        val text: String,
    ) : Kanjidic2XmlElement("meaning")

    /**
     * ~~~xml
     * <!ELEMENT nanori (#PCDATA)>
     *         <!--
     *         Japanese readings that are now only associated with names.
     *         -->
     * ~~~
     */
    data class Nanori(val text: String) : Kanjidic2XmlElement("nanori")
}
