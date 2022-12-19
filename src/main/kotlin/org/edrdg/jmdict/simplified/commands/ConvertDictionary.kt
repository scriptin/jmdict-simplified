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
import java.util.*
import kotlin.IllegalArgumentException

abstract class ConvertDictionary<E : InputDictionaryEntry, W : OutputDictionaryWord<W>>(
    val supportsCommonOnlyOutputs: Boolean,
    override val help: String = "Convert dictionary file into JSON",
    parser: Parser<E>,
) : AnalyzeDictionary<E>(
    help = help,
    parser = parser,
) {
    private var converter: Converter<E, W>? = null

    abstract fun buildConverter(metadata: Metadata): Converter<E, W>

    abstract fun serialize(word: W): String

    abstract val dictionaryName: String

    private fun checkCommonOnly(language: String) {
        require(supportsCommonOnlyOutputs || !language.endsWith("-common")) {
            "This dictionary type does not support common-only version, but you have " +
                "provided a language specification '$language'"
        }
    }

    private fun checkLanguageCode(code: String, language: String) {
        try {
            Locale(code)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Unrecognized language code '$code' in the language specification '$language'",
                e,
            )
        }
    }

    private fun checkLanguageCodesUnique(codes: List<String>, language: String) {
        val partsCount = codes.groupingBy { it }.eachCount()
        require(partsCount.values.all { it == 1 }) {
            val duplicates = partsCount.toList().filter { it.second > 1 }.map { it.first }
            val duplicatesString = duplicates.joinToString("', '")
            "Language code${if (duplicates.size > 1) "s" else ""} '$duplicatesString' " +
                "appear${if (duplicates.size > 1) "" else "s"} more than once " +
                "in the language specification '$language'"
        }
    }

    private val languages by option(
        "-l", "--languages",
        metavar = "LANGUAGES",
        help = "Comma-separated language IDs: ISO 639-2/B values, " +
            "optionally separated by dash (to have multiple languages in a same file), " +
            "or special 'all' value. " +
            (if (supportsCommonOnlyOutputs)
                "Can have '-common' suffix (e.g. 'eng-common') to include only common words. "
            else
                "") +
            "Examples: " +
            (if (supportsCommonOnlyOutputs)
                "'all,eng,eng-common' (will produce 3 files: all, English, English-common)"
            else
                "'all,eng' (will produce 2 files: all, English)") + ", " +
            "'ger,eng-ger' (2 files: German, English+German), 'fre' (French)",
    ).split(",").required().validate { languages ->
        languages.forEach { language ->
            checkCommonOnly(language)
            val withoutCommon = language.replace("-common$".toRegex(), "")
            if (withoutCommon == "all") return@forEach
            val parts = withoutCommon.split("-")
            parts.forEach { checkLanguageCode(it, language) }
            checkLanguageCodesUnique(parts, language)
        }
    }

    private val outputs by lazy {
        languages.map { language ->
            val fileName = "$dictionaryName-$language-$version.json"
            DictionaryOutputWriter(
                path = outputDir.resolve(fileName),
                languages = language
                    .replace("-common$".toRegex(), "")
                    .split("-")
                    .toSet(),
                commonOnly = if (supportsCommonOnlyOutputs) language.endsWith("-common") else false,
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
                "commonOnly": ${Json.encodeToString(it.commonOnly)},
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
        super.processEntry(entry)
        val word = convert(entry)
        val relevantOutputs = outputs.filter { it.acceptsWord(word) }
        relevantOutputs.forEach { output ->
            val filteredWord = word.onlyWithLanguages(output.languages)
            val json = serialize(filteredWord)
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
