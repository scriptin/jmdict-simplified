package org.edrdg.jmdict.simplified.conversion

import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Metadata

abstract class Converter<I : InputDictionaryEntry, O : OutputDictionaryEntry<O>, M : Metadata> {
    var metadata: M? = null

    abstract fun convert(xmlEntry: I): O

    abstract fun entity(value: String): String?

    fun entityToTag(text: String, entSeq: String) = CommonJsonElement.Tag(
        entity(text) ?: throw ConversionException(
            entSeq,
            "Following text doesn't match any entities from the original XML file: $text"
        )
    )

    private val centerDot = "・"

    fun xref(text: String, tagName: String, entSeq: String): CommonJsonElement.Xref {
        val parts = text.split(centerDot).map { it.trim() }
        return when (parts.size) {
            1 -> CommonJsonElement.Xref(parts[0], null, null)
            2 ->
                if (parts[1].toIntOrNull() != null)
                    CommonJsonElement.Xref(parts[0], null, parts[1].toInt())
                else
                    CommonJsonElement.Xref(parts[0], parts[1], null)
            // Abbreviations which use center-dot in violation of the rules:
            in 3..10 -> {
                // Special invalid cases in the source like this: <xref>ＯＢ・オー・ビー・1</xref>
                // The doc says, "The target keb or reb must not contain a centre-dot."
                // This rule is clearly broken by this example, as "オー・ビー" is
                // a Japanese spelling for "O" and "B" which uses a center-dot.
                // Such cases may or may not end with an index
                if (parts.last().toIntOrNull() != null) {
                    CommonJsonElement.Xref(
                        parts.first(),
                        parts.subList(1, parts.size).joinToString(centerDot),
                        parts.last().toInt(),
                    )
                } else {
                    CommonJsonElement.Xref(
                        parts.first(),
                        parts.drop(1).joinToString(centerDot),
                        null,
                    )
                }
            }
            else -> throw ConversionException(
                entSeq,
                "Unexpected number of parts (${parts.size}, expected 1-3) in <$tagName>: $text"
            )
        }
    }
}
