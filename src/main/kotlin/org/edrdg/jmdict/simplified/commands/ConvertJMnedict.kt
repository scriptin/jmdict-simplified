package org.edrdg.jmdict.simplified.commands

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.conversion.JMnedictConverter
import org.edrdg.jmdict.simplified.conversion.JMnedictJsonElement
import org.edrdg.jmdict.simplified.parsing.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.JMnedictXmlElement
import org.edrdg.jmdict.simplified.parsing.Metadata

class ConvertJMnedict : ConvertDictionary<JMnedictXmlElement.Entry, JMnedictJsonElement.Word>(
    hasCommon = false,
    help = "Convert JMdict.xml file into JSON",
    parser = JMnedictParser,
) {
    override val dictionaryName = "jmnedict"

    override val rootTagName = "JMnedict"

    override fun getLanguagesOfXmlEntry(entry: JMnedictXmlElement.Entry): Set<String> =
        entry.trans
            .flatMap { trans -> trans.transDet.map { it.lang } }
            .toSet()

    override fun getLanguagesOfJsonWord(word: JMnedictJsonElement.Word): Set<String> =
        word.translation
            .flatMap { translation -> translation.translation.map { it.lang } }
            .toSet()

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
