package org.edrdg.jmdict.simplified.commands.processor

import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Metadata
import org.edrdg.jmdict.simplified.parsing.Parser
import org.edrdg.jmdict.simplified.parsing.openTag
import java.io.File
import java.io.FileInputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory
import kotlin.math.round

interface DictionaryProcessor<E : InputDictionaryEntry> {
    fun printMoreInfo()

    fun beforeEntries(metadata: Metadata)

    fun processEntry(entry: E)

    fun afterEntries()

    fun finish()

    fun writeln(text: String = "")

    val dictionaryXmlFile: File
    val reportFile: File?
    val parser: Parser<E>
    val rootTagName: String

    fun run() {
        println("Input file:")
        println(" - $dictionaryXmlFile")
        println()

        if (reportFile != null) {
            println("Report file:")
            println(" - $reportFile")
            println()
        }

        printMoreInfo()

        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        val eventReader = factory.createXMLEventReader(FileInputStream(dictionaryXmlFile))

        try {
            val metadata = parser.parseMetadata(eventReader)

            writeln(MarkdownUtils.heading("Dictionary metadata", level = 3))
            writeln()

            val fileSize = dictionaryXmlFile.length()
            val fileSizeMB = round(fileSize.toDouble() / 1024.0 / 1024.0).toInt()
            writeln(
                MarkdownUtils.table(
                    listOf("Attribute", "Value"),
                    listOf(
                        listOf("File size", "${fileSizeMB}MB ($fileSize bytes)"),
                        listOf("Date", metadata.date),
                        listOf("Revisions", metadata.revisions.joinToString(", ")),
                        listOf("# of XML entities", metadata.entities.size),
                    )
                )
            )
            writeln()

            eventReader.openTag(QName(rootTagName), "root opening tag")

            beforeEntries(metadata)

            var entryCount = 0L
            val entriesByLanguage = mutableMapOf<String, Long>()
            while (parser.hasNextEntry(eventReader)) {
                val entry = parser.parseEntry(eventReader)
                processEntry(entry)
                entryCount += 1
                entry.allLanguages.forEach { lang ->
                    entriesByLanguage.putIfAbsent(lang, 0L)
                    entriesByLanguage.computeIfPresent(lang) { _, n -> n + 1L }
                }
            }

            afterEntries()

            writeln(MarkdownUtils.heading("Entries by language", level = 3))
            writeln()

            val entriesByLanguageRows = entriesByLanguage.toList()
                .sortedByDescending { it.second }
                .map { listOf(it.first, it.second) }
            writeln(
                MarkdownUtils.table(
                    listOf("Language", "# of entries"),
                    listOf(listOf("all", entryCount)) + entriesByLanguageRows,
                    alignRight = listOf(1),
                )
            )
            writeln()

            println("Entries processed: $entryCount")
        } finally {
            eventReader.close()
            finish()
        }
    }
}
