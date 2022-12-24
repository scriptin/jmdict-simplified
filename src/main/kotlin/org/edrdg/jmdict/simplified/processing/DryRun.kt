package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Metadata
import org.edrdg.jmdict.simplified.parsing.Parser
import org.edrdg.jmdict.simplified.parsing.openTag
import java.io.File
import java.io.FileInputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory
import kotlin.math.round

/**
 * Parses and analyzes a dictionary XML file without conversion to JSON.
 * Can produce a report file.
 */
open class DryRun<E : InputDictionaryEntry>(
    open val dictionaryXmlFile: File,
    open val rootTagName: String,
    open val parser: Parser<E>,
    open val reportFile: File?,
) {
    private val eventReader by lazy {
        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        factory.createXMLEventReader(FileInputStream(dictionaryXmlFile))
    }

    private val reportFileWriter by lazy { reportFile?.writer() }

    private fun writeln(text: String = "") {
        if (reportFile != null) {
            reportFileWriter?.write("$text\n")
        } else {
            println(text)
        }
    }

    open fun reportFiles() {
        println("Input file:")
        println(" - $dictionaryXmlFile")
        println()

        if (reportFile != null) {
            println("Report file:")
            println(" - $reportFile")
            println()
        }
    }

    private fun getDictionaryMetadataTable(metadata: Metadata): String {
        val fileSize = dictionaryXmlFile.length()
        val fileSizeMB = round(fileSize.toDouble() / 1024.0 / 1024.0).toInt()
        return MarkdownUtils.table(
            listOf("Attribute", "Value"),
            listOf(
                listOf("File size", "${fileSizeMB}MB ($fileSize bytes)"),
                listOf("Date", metadata.date),
                listOf("Revisions", metadata.revisions.joinToString(", ")),
                listOf("# of XML entities", metadata.entities.size),
            )
        )
    }

    open fun beforeEntries(metadata: Metadata) {
        writeln(MarkdownUtils.heading("Dictionary metadata", level = 3))
        writeln()
        writeln(getDictionaryMetadataTable(metadata))
        writeln()
    }

    open fun processEntry(entry: E) {}

    private fun getEntriesSummaryTable(
        entryCount: Long,
        entriesByLanguage: Map<String, Long>,
    ): String {
        val entriesByLanguageRows = entriesByLanguage.toList()
            .sortedByDescending { it.second }
            .map { listOf(it.first, it.second) }
        return MarkdownUtils.table(
            listOf("Language", "# of entries"),
            listOf(listOf("all", entryCount)) + entriesByLanguageRows,
            alignRight = listOf(1),
        )
    }

    open fun afterEntries(
        entryCount: Long,
        entriesByLanguage: Map<String, Long>,
    ) {
        writeln(MarkdownUtils.heading("Entries by language", level = 3))
        writeln()
        writeln(getEntriesSummaryTable(entryCount, entriesByLanguage))
        writeln()
        println("Entries processed: $entryCount")
    }

    open fun finish() {
        eventReader.close()
    }

    fun run() {
        try {
            reportFiles()

            val metadata = parser.parseMetadata(eventReader)
            beforeEntries(metadata)

            eventReader.openTag(QName(rootTagName), "root opening tag")

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

            afterEntries(entryCount, entriesByLanguage)
        } finally {
            finish()
        }
    }
}
