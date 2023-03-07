package org.edrdg.jmdict.simplified.conversion.jmnedict

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.conversion.CommonJsonElement
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryEntry

sealed class JMnedictJsonElement : CommonJsonElement() {
    @Serializable
    data class Word(
        val id: String,
        val kanji: List<Kanji>,
        val kana: List<Kana>,
        val translation: List<Translation>,
    ) : OutputDictionaryEntry<Word> {
        override val allLanguages: Set<String>
            get() = translation
                .flatMap { translation -> translation.translation.map { it.lang } }
                .toSet()

        override fun onlyWithLanguages(languages: Set<String>): Word =
            copy(
                translation = translation.map { t ->
                    t.copy(
                        translation = t.translation.filter {
                            languages.contains(it.lang) || languages.contains("all")
                        }
                    )
                }.filter { it.translation.isNotEmpty() }
            )

        override fun toJsonString() = Json.encodeToString(this)
    }

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
