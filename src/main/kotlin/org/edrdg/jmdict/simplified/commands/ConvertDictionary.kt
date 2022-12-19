package org.edrdg.jmdict.simplified.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.path
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.swiftzer.semver.SemVer
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryWord
import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Metadata
import org.edrdg.jmdict.simplified.parsing.Parser
import java.lang.IllegalArgumentException
import java.nio.file.Path
import java.util.*

abstract class ConvertDictionary<E : InputDictionaryEntry, W : OutputDictionaryWord<W>>(
    val supportsCommon: Boolean,
    override val help: String = "Convert dictionary file into JSON",
    parser: Parser<E>,
) : AnalyzeDictionary<E>(
    help = help,
    parser = parser,
) {
    private var converter: Converter<E, W>? = null

    abstract fun buildConverter(metadata: Metadata): Converter<E, W>

    abstract fun getRelevantOutputsFor(word: W): List<Output>

    abstract fun serialize(word: W): String

    abstract val dictionaryName: String

    private val languages by option(
        "-l", "--languages",
        metavar = "LANGUAGES",
        help = "Comma-separated language IDs: ISO 639-2/B values, " +
            "optionally separated by dash (to have multiple languages in a same file), " +
            "or special 'all' value. " +
            (if (supportsCommon) "Can have '-common' suffix (e.g. 'eng-common') to include only common words. " else "") +
            "Examples: " +
            (if (supportsCommon) "'all,eng,eng-common' (will produce 3 files: all, English, English-common)" else "'all,eng' (will produce 2 files: all, English)") + ", " +
            "'ger,eng-ger' (2 files: German, English+German), 'fre' (French)",
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

    class Output(
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
                common = if (supportsCommon) language.endsWith("-common") else false,
            )
        }
    }

    private val version by option(
        "-v", "--version",
        metavar = "VERSION",
        help = "Version which will be used in output files",
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

    private fun convert(entry: E): W {
        require(converter != null) {
            "Converter has not been initialized"
        }
        return converter!!.convert(entry)
    }

    override fun processEntry(entry: E) {
        val word = convert(entry)
        getRelevantOutputsFor(word).forEach { output ->
            val json = serialize(word.onlyWithLanguages(output.languages))
            output.write("${if (output.acceptedAtLeastOneEntry) "," else ""}\n$json")
            output.acceptedAtLeastOneEntry = true
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
