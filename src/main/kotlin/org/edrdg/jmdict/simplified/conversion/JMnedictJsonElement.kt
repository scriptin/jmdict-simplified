package org.edrdg.jmdict.simplified.conversion

import kotlinx.serialization.Serializable

sealed class JMnedictJsonElement : CommonJsonElement() {
    @Serializable
    data class Word(
        val id: String,
        val kanji: List<Kanji>,
        val kana: List<Kana>,
        val translation: List<Translation>,
    )

    @Serializable
    data class Kanji(
        val text: String,
        val tags: List<Tag>,
    )

    @Serializable
    data class Kana(
        val text: String,
        val tags: List<Tag>,
        val appliesToKanji: List<String>,
    )

    @Serializable
    data class Translation(
        val type: List<Tag>,
        val related: List<Xref>,
        val translation: List<TranslationText>,
    )

    @Serializable
    data class TranslationText(
        val lang: String,
        val text: String,
    )
}
