package org.edrdg.jmdict.simplified.conversion.jmdict

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.conversion.CommonJsonElement
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryEntry

sealed class JMdictJsonElement : CommonJsonElement() {
    @Serializable
    data class Word(
        val id: String,
        val kanji: List<Kanji>,
        val kana: List<Kana>,
        val sense: List<Sense>,
    ) : OutputDictionaryEntry<Word> {
        override val allLanguages: Set<String>
            get() = sense
                .flatMap { sense -> sense.gloss.map { it.lang } }
                .toSet()

        override fun onlyWithLanguages(languages: Set<String>): Word =
            copy(
                sense = sense.map { s ->
                    s.copy(
                        gloss = s.gloss.filter {
                            languages.contains(it.lang) || languages.contains("all")
                        }
                    )
                }.filter { it.gloss.isNotEmpty() }
            )

        override val isCommon: Boolean
            get() = kanji.any { it.common } || kana.any { it.common }

        override fun toJsonString() = Json.encodeToString(this)
    }

    @Serializable
    data class WordWithExamples(
        val id: String,
        val kanji: List<Kanji>,
        val kana: List<Kana>,
        val sense: List<SenseWithExamples>,
    ) : OutputDictionaryEntry<WordWithExamples> {
        override val allLanguages: Set<String>
            get() = setOf("eng") // Only English version with examples

        // Only English version with examples, so a simple copy() is enough
        override fun onlyWithLanguages(languages: Set<String>): WordWithExamples = copy()

        override val isCommon: Boolean
            get() = kanji.any { it.common } || kana.any { it.common }

        override fun toJsonString() = Json.encodeToString(this)
    }

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
        val misc: List<Tag>,
        val info: List<String>,
        val languageSource: List<LanguageSource>,
        val gloss: List<Gloss>,
    )

    @Serializable
    data class SenseWithExamples(
        val partOfSpeech: List<Tag>,
        val appliesToKanji: List<String>,
        val appliesToKana: List<String>,
        val related: List<Xref>,
        val antonym: List<Xref>,
        val field: List<Tag>,
        val dialect: List<Tag>,
        val misc: List<Tag>,
        val info: List<String>,
        val languageSource: List<LanguageSource>,
        val gloss: List<Gloss>,
        val examples: List<Example>,
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

    @Serializable
    data class Example(
        val source: ExampleSource,
        val text: String,
        val sentences: List<ExampleSentence>,
    )

    @Serializable
    data class ExampleSource(
        val type: ExampleSourceType,
        val value: String,
    )

    @Serializable
    enum class ExampleSourceType(val value: String) {
        @SerialName("tatoeba") TATOEBA("tatoeba"),
    }

    @Serializable
    data class ExampleSentence(
        val land: String,
        val text: String,
    )
}
