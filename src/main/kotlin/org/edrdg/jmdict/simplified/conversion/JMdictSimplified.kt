package org.edrdg.jmdict.simplified.conversion

sealed class JMdictSimplified {
    data class Xref(val part1: String, val part2: String?, val index: Int?)

    data class Tag(val abbreviation: String)

    data class Word(
        val id: String,
        val kanji: List<Kanji>,
        val kana: List<Kana>,
        val sense: List<Sense>,
    )

    data class Kanji(
        val common: Boolean,
        val text: String,
        val tags: List<Tag>,
    )

    data class Kana(
        val common: Boolean,
        val text: String,
        val tags: List<Tag>,
        val appliesToKanji: List<String>,
    )

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

    data class LanguageSource(
        val lang: String,
        val full: Boolean,
        val wasei: Boolean,
        val text: String?,
    )

    data class Gloss(
        val lang: String,
        val gender: Gender?,
        val type: GlossType?,
        val text: String,
    )

    enum class Gender(val value: String) {
        MASCULINE("masculine"),
        FEMININE("feminine"),
        NEUTER("neuter"),
    }

    enum class GlossType(val value: String) {
        LITERAL("literal"),
        FIGURATIVE("figurative"),
        EXPLANATION("explanation");
    }
}
