package org.edrdg.jmdict.simplified.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.path
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.swiftzer.semver.SemVer
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.JMdictConverter
import org.edrdg.jmdict.simplified.parsing.JMdictXmlElement
import org.edrdg.jmdict.simplified.parsing.Metadata
import org.edrdg.jmdict.simplified.parsing.Parser
import java.lang.IllegalArgumentException
import java.nio.file.Path
import java.util.*

abstract class ConvertDictionary<E, W>(
    override val help: String = "Convert dictionary file into JSON",
    parser: Parser<E>,
) : AnalyzeDictionary<E>(
    help = help,
    parser = parser,
) {
    internal var converter: Converter<E, W>? = null

    abstract fun buildConverter(metadata: Metadata): Converter<E, W>

    abstract val dictionaryName: String

    private val languages by option(
        "-l", "--lang", "--langs", "--language", "--languages",
        metavar = "LANGUAGES",
        help = "Comma-separated language IDs: ISO 639-2/B values, possibly separated by dash, " +
            "or special 'all' value, can have '-common' suffix. " +
            "Examples: 'all,eng,eng-common' (all, English, English-common), " +
            "'ger,eng-ger' (German, English/German), 'fre' (French)",
    ).split(",").required().validate { languages ->
        languages.forEach { language ->
            val withoutCommon = language.replace("-common$".toRegex(), "")
            if (withoutCommon == "all") return@forEach
            val parts = withoutCommon.split("-")
            parts.forEach {
                try {
                    Locale(it)
                } catch (e: Exception) {
                    throw IllegalArgumentException(
                        "Unrecognized language code '$it' in the language specification '$language'",
                        e,
                    )
                }
            }
            val partsCount = parts.groupingBy { it }.eachCount()
            require(partsCount.values.all { it == 1 }) {
                val duplicates = partsCount.toList().filter { it.second > 1 }.map { it.first }
                "Language code${if (duplicates.size > 1) "s" else ""} " +
                    "${duplicates.joinToString("', '", prefix = "'", postfix = "'")} " +
                    "appear${if (duplicates.size > 1) "" else "s"} more than once " +
                    "in the language specification '$language'"
            }
        }
    }

    internal class Output(
        path: Path,
        val languages: Set<String>,
        val common: Boolean,
    ) {
        private val fileWriter = path.toFile().writer()

        fun write(text: String) {
            fileWriter.write(text)
        }

        fun close() {
            fileWriter.close()
        }

        private var acceptedEntry = false

        var acceptedAtLeastOneEntry
            get() = acceptedEntry
            set(value) {
                if (!acceptedEntry && value) acceptedEntry = true
            }
    }

    internal val outputs by lazy {
        languages.map { language ->
            val fileName = "$dictionaryName-$language-$version.json"
            Output(
                path = outputDir.resolve(fileName),
                languages = language
                    .replace("-common$".toRegex(), "")
                    .split("-")
                    .toSet(),
                common = language.endsWith("-common"),
            )
        }
    }

    private val version by option(
        "--version",
        metavar = "VERSION",
        help = "Version of the jmdict-simplified project, used in output file names",
    ).required().check("must be a valid semantic version <https://semver.org/spec/v2.0.0.html>") {
        try {
            SemVer.parse(it)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private val outputDir by argument().path(canBeFile = false, mustBeWritable = true)

    override fun printMoreInfo() {
        println("Output directory:")
        println(" - $outputDir")
        println()

        println("Output files:")
        languages.forEach {
            println(" - $dictionaryName-$it-$version.json")
        }
        println()
    }

    override fun beforeEntries(metadata: Metadata) {
        super.beforeEntries(metadata)
        converter = buildConverter(metadata)
        outputs.forEach {
            it.write(
                """
                {
                "version": ${Json.encodeToString(version)},
                "languages": ${Json.encodeToString(it.languages.toList().sorted())},
                "commonOnly": ${Json.encodeToString(it.common)},
                "dictDate": ${Json.encodeToString(metadata.date)},
                "dictRevisions": ${Json.encodeToString(metadata.revisions)},
                "tags": ${Json.encodeToString(metadata.entities)},
                "words": [
                """.trimIndent().trimEnd('\n', ' ')
            )
        }
    }

    override fun afterEntries() {
        super.afterEntries()
        outputs.forEach {
            it.write(
                """
                ]
                }
                """.trimIndent()
            )
        }
    }

    override fun finish() {
        super.finish()
        outputs.forEach { it.close() }
    }
}
