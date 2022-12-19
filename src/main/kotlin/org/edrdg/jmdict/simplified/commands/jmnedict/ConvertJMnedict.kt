package org.edrdg.jmdict.simplified.commands.jmnedict

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.commands.ConvertDictionary
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictConverter
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictJsonElement
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement
import org.edrdg.jmdict.simplified.parsing.Metadata

class ConvertJMnedict : ConvertDictionary<JMnedictXmlElement.Entry, JMnedictJsonElement.Word>(
    supportsCommon = false,
    help = "Convert JMnedict.xml file into JSON",
    parser = JMnedictParser,
) {
    override val dictionaryName = "jmnedict"

    override val rootTagName = "JMnedict"

    override fun buildConverter(metadata: Metadata) = JMnedictConverter(metadata)

    override fun filterWordByLanguages(word: JMnedictJsonElement.Word, output: Output) =
        word.copy(
            translation = word.translation.map { t ->
                t.copy(
                    translation = t.translation.filter {
                        output.languages.contains(it.lang) || output.languages.contains("all")
                    }
                )
            }.filter { it.translation.isNotEmpty() }
        )

    override fun filterOutputsFor(word: JMnedictJsonElement.Word, languages: Set<String>): List<Output> =
        outputs.filter { output ->
            val haveCommonLanguages = output.languages.intersect(languages).isNotEmpty()
            haveCommonLanguages || output.languages.contains("all")
        }

    override fun serialize(word: JMnedictJsonElement.Word): String = Json.encodeToString(word)
}
