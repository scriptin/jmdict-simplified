package org.edrdg.jmdict.simplified.conversion

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.lang.NumberFormatException

object XrefSerializer : KSerializer<CommonJsonElement.Xref> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Xref")

    override fun serialize(encoder: Encoder, value: CommonJsonElement.Xref) {
        require(encoder is JsonEncoder)
        val array = mutableListOf<JsonPrimitive>()
        array.add(JsonPrimitive(value.part1))
        if (value.part2 != null) {
            array.add(JsonPrimitive(value.part2))
        }
        if (value.index != null) {
            array.add(JsonPrimitive(value.index))
        }
        encoder.encodeJsonElement(JsonArray(array))
    }

    override fun deserialize(decoder: Decoder): CommonJsonElement.Xref {
        require(decoder is JsonDecoder)
        val array = decoder.decodeJsonElement()
        val size = array.jsonArray.size
        val part1: String = array.jsonArray[0].jsonPrimitive.content
        return if (size == 1) {
            CommonJsonElement.Xref(part1, null, null)
        } else if (size == 2) {
            if (array.jsonArray[1].jsonPrimitive.isString) {
                CommonJsonElement.Xref(part1, array.jsonArray[1].jsonPrimitive.content, null)
            } else {
                try {
                    CommonJsonElement.Xref(part1, null, array.jsonArray[1].jsonPrimitive.content.toInt())
                } catch (nfe: NumberFormatException) {
                    throw Exception(
                        "Expected to find a string or a number at index 1 in xref, " +
                            "found ${array.jsonArray[1]}; json: $array",
                        nfe
                    )
                }
            }
        } else if (size == 3) {
            val part2: String = array.jsonArray[1].jsonPrimitive.content
            val index = array.jsonArray[2].jsonPrimitive.content.toInt()
            CommonJsonElement.Xref(part1, part2, index)
        } else {
            throw Exception("Unexpected xref size of $size, expected 1-3")
        }
    }
}
