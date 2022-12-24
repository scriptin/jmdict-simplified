package org.edrdg.jmdict.simplified.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import org.edrdg.jmdict.simplified.processing.AnalyzingDictionaryProcessor
import org.edrdg.jmdict.simplified.parsing.*

open class AnalyzeDictionary<E : InputDictionaryEntry>(
    help: String = "Analyze dictionary file contents",
    private val parser: Parser<E>,
    private val rootTagName: String,
) : CliktCommand(help = help) {
    init {
        context {
            helpFormatter = CliktHelpFormatter(showRequiredTag = true)
        }
    }

    internal val reportFile by option(
        "-r", "--report",
        metavar = "REPORT",
        help = "Output file for an analysis report",
    ).file(canBeDir = false)

    internal val dictionaryXmlFile by argument().file(mustExist = true, canBeDir = false)

    override fun run() {
        val processor = AnalyzingDictionaryProcessor(
            dictionaryXmlFile = dictionaryXmlFile,
            reportFile = reportFile,
            parser = parser,
            rootTagName = rootTagName,
        )
        processor.run()
    }
}
