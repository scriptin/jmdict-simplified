package org.edrdg.jmdict.simplified.processing.kanjidic

import org.edrdg.jmdict.simplified.parsing.*
import org.edrdg.jmdict.simplified.processing.MarkdownUtils
import org.edrdg.jmdict.simplified.processing.ReportingHandler
import java.io.File
import kotlin.math.round

/**
 * Parses and analyzes a dictionary XML file without conversion to JSON.
 * Can produce a report file.
 */
class Kanjidic2ReportingHandler<E : InputDictionaryEntry>(
    override val dictionaryXmlFile: File,
    override val reportFile: File?,
) : ReportingHandler<E, Kanjidic2Metadata>(dictionaryXmlFile, reportFile) {
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
        super.onStart()
        super.writeln(
            getDictionaryMetadataMarkdownTable(dictionaryXmlFile, metadata),
        )
        super.writeln("")
    }
}
