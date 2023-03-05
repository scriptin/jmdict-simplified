package org.edrdg.jmdict.simplified.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import org.edrdg.jmdict.simplified.parsing.*

abstract class AnalyzeCommand<E : InputDictionaryEntry, M : Metadata>(
    help: String = "Analyze dictionary file contents",
    open val parser: Parser<E, M>,
    open val rootTagName: String,
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

    val dictionaryXmlFile by argument().file(mustExist = true, canBeDir = false)
}
