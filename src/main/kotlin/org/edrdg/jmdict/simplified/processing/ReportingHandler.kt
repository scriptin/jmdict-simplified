package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.parsing.*
import java.io.File

/**
 * Parses and analyzes a dictionary XML file without conversion to JSON.
 * Can produce a report file.
 */
open class ReportingHandler<E : InputDictionaryEntry, M : Metadata>(
    /** Source file name for reporting */
    open val dictionaryXmlFile: File,
    /** Set if you want to write report to a file instead of printing to terminal */
    open val reportFile: File?,
) : EventHandler<E, M> {
    private val reportFileWriter by lazy { reportFile?.writer() }

    internal fun writeln(text: String = "") {
        if (reportFile != null) {
            reportFileWriter?.write("$text\n")
        } else {
            println(text)
        }
    }

    override fun onStart() {
        println("Input file: $dictionaryXmlFile")
        println()

        if (reportFile != null) {
            println("Report file: $reportFile")
            println()
        }
    }

    override fun beforeEntries(metadata: M) {
        writeln(MarkdownUtils.heading("Dictionary metadata", level = 3))
        writeln()
    }

    private var entryCount: Long = 0L
    private val entriesByLanguage = mutableMapOf<String, Long>()

    override fun onEntry(entry: E) {
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

    override fun afterEntries() {
        writeln(MarkdownUtils.heading("Entries by language", level = 3))
        writeln()
        writeln(getEntriesSummaryTable(entryCount, entriesByLanguage))
        writeln()
        println("Entries processed: $entryCount")
    }

    override fun onFinish() {
        reportFileWriter?.close()
    }
}
