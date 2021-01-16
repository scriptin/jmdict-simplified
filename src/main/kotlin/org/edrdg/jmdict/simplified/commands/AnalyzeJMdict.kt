package org.edrdg.jmdict.simplified.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import org.edrdg.jmdict.simplified.parsing.*
import java.io.FileInputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory
import kotlin.math.round

open class AnalyzeJMdict(
    open val help: String = "Analyze JMdict.xml file contents"
) : CliktCommand(help = help) {
    init {
        context {
            helpFormatter = CliktHelpFormatter(showRequiredTag = true)
        }
    }

    val reportFile by option(
        "-r", "--report",
        metavar = "REPORT",
        help = "Output file for an analysis report",
    ).file(canBeDir = false)

    val reportFileWriter by lazy { reportFile?.writer() }

    val jmdictFile by argument().file(mustExist = true, canBeDir = false)

    internal open fun printMoreInfo() {
        // Nothing here
    }

    /**
     * Write to an analysis output file (if provided), or to stdout
     */
    private fun writeln(text: String = "") {
        if (reportFile != null) {
            reportFileWriter?.write("$text\n")
        } else {
            println(text)
        }
    }

    internal open fun beforeEntries(metadata: Metadata) {
        // Nothing here
    }

    internal open fun processEntry(entry: JMdictTag.Entry) {
        // Nothing here
    }

    internal open fun afterEntries() {
        // Nothing here
    }

    internal open fun finish() {
        reportFileWriter?.close()
    }

    override fun run() {
        println("Input file:")
        println(" - $jmdictFile")
        println()

        if (reportFile != null) {
            println("Report file:")
            println(" - $reportFile")
            println()
        }

        printMoreInfo()

        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        val eventReader = factory.createXMLEventReader(FileInputStream(jmdictFile))

        try {
            val metadata = Parser.parseMetadata(eventReader)

            writeln(MarkdownUtils.heading("Dictionary metadata", level = 3))
            writeln()

            val fileSize = jmdictFile.length()
            val fileSizeMB = round(fileSize.toDouble() / 1024.0 / 1024.0).toInt()
            writeln(MarkdownUtils.table(
                listOf("Attribute", "Value"),
                listOf(
                    listOf("File size", "${fileSizeMB}MB ($fileSize bytes)"),
                    listOf("Date", metadata.date),
                    listOf("Revisions", metadata.revisions.joinToString(", ")),
                    listOf("# of XML entities", metadata.entities.size),
                )
            ))
            writeln()

            eventReader.openTag(QName("JMdict"), "root opening tag")

            beforeEntries(metadata)

            var entryCount = 0L
            val entriesByLanguage = mutableMapOf<String, Long>()
            while (Parser.hasNextEntry(eventReader)) {
                val entry = Parser.parseEntry(eventReader)
                processEntry(entry)
                entryCount += 1
                entry.sense
                    .flatMap { sense -> sense.gloss.map { it.lang } }
                    .toSet()
                    .forEach { lang ->
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
            writeln(MarkdownUtils.table(
                listOf("Language", "# of entries"),
                listOf(listOf("all", entryCount)) + entriesByLanguageRows,
                alignRight = listOf(1),
            ))
            writeln()

            println("Entries processed: $entryCount")
        } finally {
            eventReader.close()
            finish()
        }
    }
}
