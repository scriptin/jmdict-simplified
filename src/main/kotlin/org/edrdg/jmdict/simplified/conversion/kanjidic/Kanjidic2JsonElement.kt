package org.edrdg.jmdict.simplified.conversion.kanjidic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryEntry

sealed class Kanjidic2JsonElement {
    @Serializable
    data class Character(
        val literal: String,
        val codepoints: List<Codepoint>,
        val radicals: List<Radical>,
        val misc: Misc,
        val dictionaryReferences: List<DictionaryReference>,
        val queryCodes: List<QueryCode>,
        val readingMeaning: ReadingMeaning?,
    ) : OutputDictionaryEntry<Character> {
        override val allLanguages: Set<String>
            get() = readingMeaning
                ?.groups
                ?.flatMap { group -> group.meanings.map { it.lang } }
                ?.toSet()
                .orEmpty()

        override fun onlyWithLanguages(languages: Set<String>): Character =
            copy(
                readingMeaning = readingMeaning?.copy(
                    groups = readingMeaning.groups.map { group ->
                        group.copy(
                            meanings = group.meanings.filter {
                                languages.contains(it.lang) || languages.contains("all")
                            }
                        )
                    }
                )
            )

        override fun toJsonString(): String = Json.encodeToString(this)
    }

    @Serializable
    data class Codepoint(
        val type: CodepointType,
        val value: String,
    )

    @Serializable
    enum class CodepointType(val value: String) {
        @SerialName("jis208") JIS208("jis208"),
        @SerialName("jis212") JIS212("jis212"),
        @SerialName("jis213") JIS213("jis213"),
        @SerialName("ucs") UCS("ucs");
    }

    @Serializable
    data class Radical(
        val type: RadicalType,
        val value: Int,
    )

    @Serializable
    enum class RadicalType(val value: String) {
        @SerialName("classical") CLASSICAL("classical"),
        @SerialName("nelson_c") NELSON_C("nelson_c");
    }

    @Serializable
    data class Misc(
        val grade: Int?,
        val strokeCounts: List<Int>,
        val variants: List<Variant>,
        val frequency: Int?,
        val radicalNames: List<String>,
        val jlptLevel: Int?,
    )

    @Serializable
    data class Variant(
        val type: VariantType,
        val value: String,
    )

    @Serializable
    enum class VariantType(val value: String) {
        @SerialName("jis208") JIS208("jis208"),
        @SerialName("jis212") JIS212("jis212"),
        @SerialName("jis213") JIS213("jis213"),
        @SerialName("deroo") DEROO("deroo"),
        @SerialName("njecd") NJECD("njecd"),
        @SerialName("s_h") S_H("s_h"),
        @SerialName("nelson_c") NELSON_C("nelson_c"),
        @SerialName("oneill") ONEILL("oneill"),
        @SerialName("ucs") UCS("ucs");
    }

    @Serializable
    data class DictionaryReference(
        val type: DictionaryReferenceType,
        val morohashi: Morohashi?,
        val value: String,
    )

    @Serializable
    enum class DictionaryReferenceType(val value: String) {
        @SerialName("nelson_c") NELSON_C("nelson_c"),
        @SerialName("nelson_n") NELSON_N("nelson_n"),
        @SerialName("halpern_njecd") HALPERN_NJECD("halpern_njecd"),
        @SerialName("halpern_kkd") HALPERN_KKD("halpern_kkd"),
        @SerialName("halpern_kkld") HALPERN_KKLD("halpern_kkld"),
        @SerialName("halpern_kkld_2ed") HALPERN_KKLD_2ED("halpern_kkld_2ed"),
        @SerialName("heisig") HEISIG("heisig"),
        @SerialName("heisig6") HEISIG6("heisig6"),
        @SerialName("gakken") GAKKEN("gakken"),
        @SerialName("oneill_names") ONEILL_NAMES("oneill_names"),
        @SerialName("oneill_kk") ONEILL_KK("oneill_kk"),
        @SerialName("moro") MORO("moro"),
        @SerialName("henshall") HENSHALL("henshall"),
        @SerialName("sh_kk") SH_KK("sh_kk"),
        @SerialName("sh_kk2") SH_KK2("sh_kk2"),
        @SerialName("sakade") SAKADE("sakade"),
        @SerialName("jf_cards") JF_CARDS("jf_cards"),
        @SerialName("henshall3") HENSHALL3("henshall3"),
        @SerialName("tutt_cards") TUTT_CARDS("tutt_cards"),
        @SerialName("crowley") CROWLEY("crowley"),
        @SerialName("kanji_in_context") KANJI_IN_CONTEXT("kanji_in_context"),
        @SerialName("busy_people") BUSY_PEOPLE("busy_people"),
        @SerialName("kodansha_compact") KODANSHA_COMPACT("kodansha_compact"),
        @SerialName("maniette") MANIETTE("maniette");
    }

    @Serializable
    data class Morohashi(
        val volume: Int,
        val page: Int,
    )

    @Serializable
    data class QueryCode(
        val type: QueryCodeType,
        val skipMisclassification: SkipMisclassification?,
        val value: String,
    )

    @Serializable
    enum class QueryCodeType(val value: String) {
        @SerialName("skip") SKIP("skip"),
        @SerialName("sh_desc") SH_DESC("sh_desc"),
        @SerialName("four_corner") FOUR_CORNER("four_corner"),
        @SerialName("deroo") DEROO("deroo"),
        @SerialName("misclass") MISCLASS("misclass");
    }

    @Serializable
    enum class SkipMisclassification(val value: String) {
        @SerialName("posn") POSN("posn"),
        @SerialName("stroke_count") STROKE_COUNT("stroke_count"),
        @SerialName("stroke_and_posn") STROKE_AND_POSN("stroke_and_posn"),
        @SerialName("stroke_diff") STROKE_DIFF("stroke_diff");
    }

    @Serializable
    data class ReadingMeaning(
        val groups: List<ReadingMeaningGroup>,
        val nanori: List<String>,
    )

    @Serializable
    data class ReadingMeaningGroup(
        val readings: List<Reading>,
        val meanings: List<Meaning>,
    )

    @Serializable
    data class Reading(
        val type: ReadingType,
        val onType: String?,
        val status: String?,
        val value: String,
    )

    @Serializable
    enum class ReadingType(val value: String) {
        @SerialName("pinyin") PINYIN("pinyin"),
        @SerialName("korean_r") KOREAN_R("korean_r"),
        @SerialName("korean_h") KOREAN_H("korean_h"),
        @SerialName("vietnam") VIETNAM("vietnam"),
        @SerialName("ja_on") JA_ON("ja_on"),
        @SerialName("ja_kun") JA_KUN("ja_kun");
    }

    @Serializable
    data class Meaning(
        val lang: String,
        val value: String,
    )
}
