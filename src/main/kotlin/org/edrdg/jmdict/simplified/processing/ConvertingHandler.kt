package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.commands.DictionaryOutputWriter
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryWord
import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Metadata
import java.nio.file.Path

/**
 * Parses, analyzes, and converts to JSON a dictionary XML file.
 * Can produce a report file.
 */
open class ConvertingHandler<E : InputDictionaryEntry, W : OutputDictionaryWord<W>, M : Metadata>(
    open val dictionaryName: String,
    open val version: String,
    open val languages: List<String>,
    open val outputDirectory: Path,
    open val outputs: List<DictionaryOutputWriter>,
    open val converter: Converter<E, W, M>,
) : EventHandler<E, M> {
    override fun onStart() {
        println("Output directory: $outputDirectory")
        println()
        println("Output files:")
        languages.forEach {
            println(" - $dictionaryName-$it-$version.json")
        }
        println()
    }

    override fun beforeEntries(metadata: M) {
    }

    override fun onEntry(entry: E) {
        val word = converter.convert(entry)
        val relevantOutputs = outputs.filter { it.acceptsWord(word) }
        relevantOutputs.forEach { output ->
            val filteredWord = word.onlyWithLanguages(output.languages)
            val json = filteredWord.toJsonString()
            output.write("${if (output.acceptedAtLeastOneEntry) "," else ""}\n$json")
            output.acceptedAtLeastOneEntry = true
        }
    }

    override fun afterEntries() {
        outputs.forEach {
            it.write(
                """
                ]
                }
                """.trimIndent()
            )
        }
    }

    override fun onFinish() {
        outputs.forEach { it.close() }
    }
}
