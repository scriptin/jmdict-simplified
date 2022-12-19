package org.edrdg.jmdict.simplified.conversion

import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry

abstract class Converter<E : InputDictionaryEntry, W : OutputDictionaryWord<W>> {
    abstract fun convert(xmlEntry: E): W

    abstract val entities: Map<String, String>

    fun entityToTag(text: String, entSeq: String) = CommonJsonElement.Tag(
        entities[text] ?: throw ConversionException(
            entSeq,
            "Following text doesn't match any entities from the original XML file: $text"
        )
    )

    fun xref(text: String, tagName: String, entSeq: String): CommonJsonElement.Xref {
        val parts = text.split("・").map { it.trim() }
        return when (parts.size) {
            1 -> CommonJsonElement.Xref(parts[0], null, null)
            2 ->
                if (parts[1].matches("\\d+".toRegex()))
                    CommonJsonElement.Xref(parts[0], null, parts[1].toInt())
                else
                    CommonJsonElement.Xref(parts[0], parts[1], null)
            3 -> CommonJsonElement.Xref(parts[0], parts[1], parts[2].toInt())
            else -> throw ConversionException(
                entSeq,
                "Unexpected number of parts (${parts.size}, expected 1-3) in <$tagName>: $text"
            )
        }
    }
}
