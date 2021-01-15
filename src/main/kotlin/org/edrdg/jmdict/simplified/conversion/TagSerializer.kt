package org.edrdg.jmdict.simplified.conversion

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TagSerializer : KSerializer<JMdictSimplified.Tag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Tag", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: JMdictSimplified.Tag) = encoder.encodeString(value.abbreviation)

    override fun deserialize(decoder: Decoder) = JMdictSimplified.Tag(decoder.decodeString())
}
