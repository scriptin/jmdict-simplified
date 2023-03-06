package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.commands.DictionaryOutputWriter
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryWord
import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Metadata
import org.edrdg.jmdict.simplified.parsing.Parser
import java.nio.file.Path
import javax.xml.stream.XMLEventReader

/**
 * Parses, analyzes, and converts to JSON a dictionary XML file.
 * Can produce a report file.
 */
class ConvertingProcessor<E : InputDictionaryEntry, W : OutputDictionaryWord<W>, M : Metadata>(
    override val parser: Parser<E, M>,
    override val rootTagName: String,
    override val eventReader: XMLEventReader,
    private val dictionaryName: String,
    private val version: String,
    private val languages: List<String>,
    private val outputDirectory: Path,
    private val outputs: List<DictionaryOutputWriter>,
    private val converter: Converter<E, W, M>,
    override val skipOpeningRootTag: Boolean,
) : DictionaryProcessor<E, M> {
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

    override fun processEntry(entry: E) {
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
