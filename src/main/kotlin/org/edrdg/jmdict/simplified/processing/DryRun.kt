package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.parsing.*
import org.edrdg.jmdict.simplified.parsing.openTag
import java.io.File
import java.io.FileInputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory

/**
 * Parses and analyzes a dictionary XML file without conversion to JSON.
 * Can produce a report file.
 */
abstract class DryRun<E : InputDictionaryEntry, M : Metadata>(
    open val dictionaryXmlFile: File,
    open val rootTagName: String,
    open val parser: Parser<E, M>,
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
        println("Input file: $dictionaryXmlFile")
        println()

        if (reportFile != null) {
            println("Report file: $reportFile")
            println()
        }
    }

    abstract fun getDictionaryMetadataTable(metadata: M): String

    open fun beforeEntries(metadata: M) {
        writeln(MarkdownUtils.heading("Dictionary metadata", level = 3))
        writeln()
        writeln(getDictionaryMetadataTable(metadata))
        writeln()
    }

    private var entryCount: Long = 0L
    private val entriesByLanguage = mutableMapOf<String, Long>()

    open fun processEntry(entry: E) {
        entryCount += 1
        entry.allLanguages.forEach { lang ->
            entriesByLanguage.putIfAbsent(lang, 0L)
            entriesByLanguage.computeIfPresent(lang) { _, n -> n + 1L }
        }
    }

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

    open fun afterEntries() {
        writeln(MarkdownUtils.heading("Entries by language", level = 3))
        writeln()
        writeln(getEntriesSummaryTable(entryCount, entriesByLanguage))
        writeln()
        println("Entries processed: $entryCount")
    }

    open fun finish() {
        reportFileWriter?.close()
        eventReader.close()
    }

    fun run() {
        try {
            reportFiles()
            val metadata = parser.parseMetadata(eventReader)
            beforeEntries(metadata)
            eventReader.openTag(QName(rootTagName), "root opening tag")
            while (parser.hasNextEntry(eventReader)) {
                val entry = parser.parseEntry(eventReader)
                processEntry(entry)
            }
            eventReader.closeTag(QName(rootTagName), "root closing tag")
            afterEntries()
        } finally {
            finish()
        }
    }
}
