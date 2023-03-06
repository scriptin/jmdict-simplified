package org.edrdg.jmdict.simplified.processing.jmdict

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.commands.DictionaryOutputWriter
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryWord
import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.Parser
import org.edrdg.jmdict.simplified.processing.ConvertingProcessor
import org.edrdg.jmdict.simplified.processing.DictionaryProcessor
import java.io.File
import java.nio.file.Path
import javax.xml.stream.XMLEventReader

/**
 * Parses, analyzes, and converts to JSON a dictionary XML file.
 * Can produce a report file.
 */
class JMdictConvertingProcessor<E : InputDictionaryEntry, W : OutputDictionaryWord<W>>(
    override val parser: Parser<E, JMdictMetadata>,
    override val rootTagName: String,
    override val eventReader: XMLEventReader,
    val dictionaryXmlFile: File,
    val reportFile: File?,
    dictionaryName: String,
    private val version: String,
    languages: List<String>,
    outputDirectory: Path,
    private val outputs: List<DictionaryOutputWriter>,
    private val converter: Converter<E, W, JMdictMetadata>,
) : DictionaryProcessor<E, JMdictMetadata> {
    private val reportingProcessor = JMdictReportingProcessor(
        parser,
        rootTagName,
        eventReader,
        dictionaryXmlFile,
        reportFile,
    )

    override val skipOpeningRootTag = true

    private val convertingProcessor = ConvertingProcessor(
        parser,
        rootTagName,
        eventReader,
        dictionaryName,
        version,
        languages,
        outputDirectory,
        outputs,
        converter,
        skipOpeningRootTag,
    )

    override fun onStart() {
        reportingProcessor.onStart()
        convertingProcessor.onStart()
    }

    override fun beforeEntries(metadata: JMdictMetadata) {
        reportingProcessor.beforeEntries(metadata)
        convertingProcessor.beforeEntries(metadata)
        converter.metadata = metadata
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
        reportingProcessor.processEntry(entry)
        convertingProcessor.processEntry(entry)
    }

    override fun afterEntries() {
        reportingProcessor.afterEntries()
        reportingProcessor.afterEntries()
    }

    override fun onFinish() {
        reportingProcessor.onFinish()
        convertingProcessor.onFinish()
    }
}
