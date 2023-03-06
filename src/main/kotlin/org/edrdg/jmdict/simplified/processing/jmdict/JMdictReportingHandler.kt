package org.edrdg.jmdict.simplified.processing.jmdict

import org.edrdg.jmdict.simplified.parsing.*
import org.edrdg.jmdict.simplified.processing.MarkdownUtils
import org.edrdg.jmdict.simplified.processing.ReportingHandler
import java.io.File
import kotlin.math.round

/**
 * Parses and analyzes a dictionary XML file without conversion to JSON.
 * Can produce a report file.
 */
class JMdictReportingHandler<E : InputDictionaryEntry>(
    override val dictionaryXmlFile: File,
    override val reportFile: File?,
) : ReportingHandler<E, JMdictMetadata>(dictionaryXmlFile, reportFile) {
    private fun getDictionaryMetadataMarkdownTable(dictionaryXmlFile: File, metadata: JMdictMetadata): String {
        val fileSize = dictionaryXmlFile.length()
        val fileSizeMB = round(fileSize.toDouble() / 1024.0 / 1024.0).toInt()
        return MarkdownUtils.table(
            listOf("Attribute", "Value"),
            listOf(
                listOf("File size", "${fileSizeMB}MB ($fileSize bytes)"),
                listOf("Date", metadata.date),
                listOf("Revisions", metadata.revisions.joinToString(", ")),
                listOf("# of XML entities", metadata.entities.size),
            )
        )
    }

    override fun beforeEntries(metadata: JMdictMetadata) {
        super.onStart()
        super.writeln(
            getDictionaryMetadataMarkdownTable(dictionaryXmlFile, metadata),
        )
        super.writeln("")
    }
}
