package org.edrdg.jmdict.simplified.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.path
import net.swiftzer.semver.SemVer
import org.edrdg.jmdict.simplified.commands.processor.ConvertingDictionaryProcessor
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryWord
import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Parser
import java.util.*
import kotlin.IllegalArgumentException

abstract class ConvertDictionary<E : InputDictionaryEntry, W : OutputDictionaryWord<W>>(
    help: String = "Convert dictionary file into JSON",
    private val supportsCommonOnlyOutputs: Boolean,
    private val parser: Parser<E>,
    private val rootTagName: String,
    private val dictionaryName: String,
    private val converter: Converter<E, W>,
) : AnalyzeDictionary<E>(
    help = help,
    parser = parser,
    rootTagName = rootTagName,
) {
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

    override fun run() {
        val processor = ConvertingDictionaryProcessor(
            dictionaryXmlFile = dictionaryXmlFile,
            reportFile = reportFile,
            parser = parser,
            rootTagName = rootTagName,
            dictionaryName = dictionaryName,
            version = version,
            outputDir = outputDir,
            languages = languages,
            outputs = outputs,
            converter = converter,
        )
        processor.run()
    }
}
