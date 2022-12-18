package org.edrdg.jmdict.simplified.commands.jmdict

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.commands.ConvertDictionary
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictConverter
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictJsonElement
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement
import org.edrdg.jmdict.simplified.parsing.Metadata

class ConvertJMdict : ConvertDictionary<JMdictXmlElement.Entry, JMdictJsonElement.Word>(
    supportsCommon = true,
    help = "Convert JMdict.xml file into JSON",
    parser = JMdictParser,
) {
    override val dictionaryName = "jmdict"

    override val rootTagName = "JMdict"

    override fun getLanguagesOfXmlEntry(entry: JMdictXmlElement.Entry): Set<String> =
        entry.sense
            .flatMap { sense -> sense.gloss.map { it.lang } }
            .toSet()

    override fun getLanguagesOfJsonWord(word: JMdictJsonElement.Word): Set<String> =
        word.sense
            .flatMap { sense -> sense.gloss.map { it.lang } }
            .toSet()

    override fun buildConverter(metadata: Metadata) = JMdictConverter(metadata)

    override fun filterWordByLanguages(word: JMdictJsonElement.Word, output: Output) =
        word.copy(
            sense = word.sense.map { s ->
                s.copy(
                    gloss = s.gloss.filter {
                        output.languages.contains(it.lang) || output.languages.contains("all")
                    }
                )
            }.filter { it.gloss.isNotEmpty() }
        )

    override fun filterOutputsFor(word: JMdictJsonElement.Word, languages: Set<String>): List<Output> {
        val entryIsCommon = word.kanji.any { it.common } || word.kana.any { it.common }
        return outputs.filter { output ->
            val haveCommonLanguages = output.languages.intersect(languages).isNotEmpty()
            (haveCommonLanguages || output.languages.contains("all")) && output.common == entryIsCommon
        }
    }

    override fun serialize(word: JMdictJsonElement.Word): String = Json.encodeToString(word)
}
