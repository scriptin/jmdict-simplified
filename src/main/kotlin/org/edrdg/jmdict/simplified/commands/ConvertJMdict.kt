package org.edrdg.jmdict.simplified.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.swiftzer.semver.SemVer
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.parsing.Parser
import org.edrdg.jmdict.simplified.parsing.openTag
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory

class ConvertJMdict : CliktCommand(
    help = "Convert JMdict.xml file into JSON"
) {
    init {
        context {
            helpFormatter = CliktHelpFormatter(showRequiredTag = true)
        }
    }

    private val languages by option(
        "-l", "--lang", "--langs", "--language", "--languages",
        metavar = "LANGUAGES",
        help = "Comma-separated language IDs: ISO 639-2 values, possibly separated by dash, " +
            "or special 'all' value, can have '-common' suffix. " +
            "Examples: 'all,eng,eng-common' (all + English + English-common), " +
            "'ger,eng-ger' (German + English/German), 'fre' (French only)",
    ).split(",").required().check(
        "language must be wither ISO 639-2 values ('eng', 'ger', 'fre', etc.), " +
            "a special value 'all', and can have '-common' suffix"
    ) {
        it.all { it.matches("^\\w{3}(-common)?$".toRegex()) }
    }

    private val version by option(
        "--version",
        metavar = "VERSION",
        help = "Version of the jmdict-simplified project, used in output file names",
    ).required().check("must be a valid semantic version <https://semver.org/spec/v2.0.0.html> without build metadata") {
        try {
            val v = SemVer.parse(it)
            v.buildMetadata == null
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private val jmdictFile by argument().file(mustExist = true, canBeDir = false)

    private val outputDir by argument().path(canBeFile = false, mustBeWritable = true)

    override fun run() {
        println("Input file:")
        println(" - $jmdictFile")
        println()

        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        val eventReader = factory.createXMLEventReader(FileInputStream(jmdictFile))

        println("Output directory:")
        println(" - $outputDir")
        println()

        println("Output files:")
        languages.forEach {
            println(" - jmdict-$it-$version.json")
        }
        println()

        try {
            val metadata = Parser.parseMetadata(eventReader)
            println("Collected metadata")

            eventReader.openTag(QName("JMdict"), "root opening tag")

            val converter = Converter(metadata)

            var count: Long = 0
            while (Parser.hasNextEntry(eventReader)) {
                val entry = Parser.parseEntry(eventReader)
                val word = converter.convertWord(entry)
                val json = Json.encodeToString(word)
                // println("\n$json")
                count += 1
            }
            println("\nConverted $count entries")
        } finally {
            eventReader.close()
        }
    }
}
