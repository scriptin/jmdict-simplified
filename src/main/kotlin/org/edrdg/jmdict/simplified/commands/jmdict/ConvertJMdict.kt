package org.edrdg.jmdict.simplified.commands.jmdict

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.commands.ConvertDictionary
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictConverter
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictJsonElement
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement
import org.edrdg.jmdict.simplified.parsing.Metadata

class ConvertJMdict : ConvertDictionary<JMdictXmlElement.Entry, JMdictJsonElement.Word>(
    supportsCommonOnlyOutputs = true,
    help = "Convert JMdict.xml file into JSON",
    parser = JMdictParser,
) {
    override val dictionaryName = "jmdict"

    override val rootTagName = "JMdict"

    override fun buildConverter(metadata: Metadata) = JMdictConverter(metadata)

    override fun serialize(word: JMdictJsonElement.Word): String = Json.encodeToString(word)
}
