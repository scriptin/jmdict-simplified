package org.edrdg.jmdict.simplified.processing

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.commands.DictionaryOutputWriter
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryWord
import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Metadata
import org.edrdg.jmdict.simplified.parsing.Parser
import java.io.File

/**
 * Parses, analyzes, and converts to JSON a dictionary XML file.
 * Can produce a report file.
 */
class Convert<E : InputDictionaryEntry, W : OutputDictionaryWord<W>>(
    override val dictionaryXmlFile: File,
    override val rootTagName: String,
    override val parser: Parser<E>,
    override val reportFile: File?,
    private val dictionaryName: String,
    private val version: String,
    private val languages: List<String>,
    private val outputs: List<DictionaryOutputWriter>,
    private val converter: Converter<E, W>,
) : DryRun<E>(
    dictionaryXmlFile = dictionaryXmlFile,
    rootTagName = rootTagName,
    parser = parser,
    reportFile = reportFile,
) {
    override fun reportFiles() {
        super.reportFiles()
        println("Output files:")
        languages.forEach {
            println(" - $dictionaryName-$it-$version.json")
        }
        println()
    }

    override fun beforeEntries(metadata: Metadata) {
        super.beforeEntries(metadata)
        converter.setMetadata(metadata)
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

    override fun processEntry(entry: E) {
        super.processEntry(entry)
        val word = converter.convert(entry)
        val relevantOutputs = outputs.filter { it.acceptsWord(word) }
        relevantOutputs.forEach { output ->
            val filteredWord = word.onlyWithLanguages(output.languages)
            val json = filteredWord.toJsonString()
            output.write("${if (output.acceptedAtLeastOneEntry) "," else ""}\n$json")
            output.acceptedAtLeastOneEntry = true
        }
    }

    override fun afterEntries(
        entryCount: Long,
        entriesByLanguage: Map<String, Long>,
    ) {
        super.afterEntries(entryCount, entriesByLanguage)
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
