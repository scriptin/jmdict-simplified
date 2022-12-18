package org.edrdg.jmdict.simplified.conversion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class JMdictJsonElement : CommonJsonElement() {
    @Serializable
    data class Word(
        val id: String,
        val kanji: List<Kanji>,
        val kana: List<Kana>,
        val sense: List<Sense>,
    )

    @Serializable
    data class Kanji(
        val common: Boolean,
        val text: String,
        val tags: List<Tag>,
    )

    @Serializable
    data class Kana(
        val common: Boolean,
        val text: String,
        val tags: List<Tag>,
        val appliesToKanji: List<String>,
    )

    @Serializable
    data class Sense(
        val partOfSpeech: List<Tag>,
        val appliesToKanji: List<String>,
        val appliesToKana: List<String>,
        val related: List<Xref>,
        val antonym: List<Xref>,
        val field: List<Tag>,
        val dialect: List<Tag>,
        val info: List<String>,
        val languageSource: List<LanguageSource>,
        val gloss: List<Gloss>,
    )

    @Serializable
    data class LanguageSource(
        val lang: String,
        val full: Boolean,
        val wasei: Boolean,
        val text: String?,
    )

    @Serializable
    data class Gloss(
        val lang: String,
        val gender: Gender?,
        val type: GlossType?,
        val text: String,
    )

    @Serializable
    enum class Gender(val value: String) {
        @SerialName("masculine") MASCULINE("masculine"),
        @SerialName("feminine") FEMININE("feminine"),
        @SerialName("neuter") NEUTER("neuter");
    }

    @Serializable
    enum class GlossType(val value: String) {
        @SerialName("literal") LITERAL("literal"),
        @SerialName("figurative") FIGURATIVE("figurative"),
        @SerialName("explanation") EXPLANATION("explanation"),
        @SerialName("trademark") TRADEMARK("trademark");
    }
}
