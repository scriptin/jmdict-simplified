package org.edrdg.jmdict.simplified.commands

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.JMdictConverter
import org.edrdg.jmdict.simplified.conversion.JMdictJsonElement
import org.edrdg.jmdict.simplified.parsing.JMdictParser
import org.edrdg.jmdict.simplified.parsing.JMdictXmlElement
import org.edrdg.jmdict.simplified.parsing.Metadata

class ConvertJMdict : ConvertDictionary<JMdictXmlElement.Entry, JMdictJsonElement.Word>(
    help = "Convert JMdict.xml file into JSON",
    parser = JMdictParser,
) {
    override fun getLanguagesOf(entry: JMdictXmlElement.Entry): Set<String> =
        entry.sense
            .flatMap { sense -> sense.gloss.map { it.lang } }
            .toSet()

    override val rootTagName: String = "JMdict"

    override fun buildConverter(metadata: Metadata): Converter<JMdictXmlElement.Entry, JMdictJsonElement.Word> =
        JMdictConverter(metadata)

    override val dictionaryName: String = "jmdict"

    override fun processEntry(entry: JMdictXmlElement.Entry) {
        super.processEntry(entry)
        require(converter != null) {
            "Converter has not been initialized"
        }
        val word = converter!!.convertWord(entry)
        val entryLanguages = word.sense
            .flatMap { sense -> sense.gloss.map { it.lang } }
            .toSet()
        val entryIsCommon = word.kanji.any { it.common } || word.kana.any { it.common }
        outputs.filter { output ->
            val haveCommonLanguages = output.languages.intersect(entryLanguages).isNotEmpty()
            (haveCommonLanguages || output.languages.contains("all")) && output.common == entryIsCommon
        }.forEach { output ->
            val filteredWord = word.copy(
                sense = word.sense.map { s ->
                    s.copy(
                        gloss = s.gloss.filter {
                            output.languages.contains(it.lang) || output.languages.contains("all")
                        }
                    )
                }.filter { it.gloss.isNotEmpty() }
            )
            val json = Json.encodeToString(filteredWord)
            output.write("${if (output.acceptedAtLeastOneEntry) "," else ""}\n$json")
            output.acceptedAtLeastOneEntry = true
        }
    }
}
