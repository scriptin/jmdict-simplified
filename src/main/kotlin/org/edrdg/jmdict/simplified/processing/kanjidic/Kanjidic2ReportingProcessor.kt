package org.edrdg.jmdict.simplified.processing.kanjidic

import org.edrdg.jmdict.simplified.parsing.*
import org.edrdg.jmdict.simplified.processing.DictionaryProcessor
import org.edrdg.jmdict.simplified.processing.MarkdownUtils
import org.edrdg.jmdict.simplified.processing.ReportingProcessor
import java.io.File
import kotlin.math.round
import javax.xml.stream.XMLEventReader

/**
 * Parses and analyzes a dictionary XML file without conversion to JSON.
 * Can produce a report file.
 */
class Kanjidic2ReportingProcessor<E : InputDictionaryEntry>(
    override val parser: Parser<E, Kanjidic2Metadata>,
    override val rootTagName: String,
    override val eventReader: XMLEventReader,
    val dictionaryXmlFile: File,
    val reportFile: File?,
) : DictionaryProcessor<E, Kanjidic2Metadata> {
    override val skipOpeningRootTag = false

    private val reportingProcessor = ReportingProcessor(
        parser,
        rootTagName,
        eventReader,
        skipOpeningRootTag,
        dictionaryXmlFile,
        reportFile,
    )

    override fun onStart() {
        reportingProcessor.onStart()
    }

    private fun getDictionaryMetadataMarkdownTable(dictionaryXmlFile: File, metadata: Kanjidic2Metadata): String {
        val fileSize = dictionaryXmlFile.length()
        val fileSizeMB = round(fileSize.toDouble() / 1024.0 / 1024.0).toInt()
        return MarkdownUtils.table(
            listOf("Attribute", "Value"),
            listOf(
                listOf("File size", "${fileSizeMB}MB ($fileSize bytes)"),
                listOf("Date", metadata.date),
                listOf("File version", metadata.fileVersion),
                listOf("Database version", metadata.databaseVersion),
            )
        )
    }

    override fun beforeEntries(metadata: Kanjidic2Metadata) {
        reportingProcessor.onStart()
        reportingProcessor.writeln(
            getDictionaryMetadataMarkdownTable(dictionaryXmlFile, metadata),
        )
        reportingProcessor.writeln()
    }

    override fun processEntry(entry: E) {
        reportingProcessor.processEntry(entry)
    }

    override fun afterEntries() {
        reportingProcessor.afterEntries()
    }

    override fun onFinish() {
        reportingProcessor.onFinish()
    }
}
