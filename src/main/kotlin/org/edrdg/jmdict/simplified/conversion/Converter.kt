package org.edrdg.jmdict.simplified.conversion

import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Metadata

abstract class Converter<E : InputDictionaryEntry, W : OutputDictionaryWord<W>> {
    private var _metadata: Metadata? = null

    fun setMetadata(m: Metadata) {
        this._metadata = m
    }

    abstract fun convert(xmlEntry: E): W

    private val entities by lazy {
        require(_metadata != null) {
            "Metadata must be set"
        }
        _metadata!!.entities.entries.associate { (k, v) -> v to k }
    }

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
