package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.parsing.*
import java.io.File
import kotlin.math.round

/**
 * Parses and analyzes a dictionary XML file without conversion to JSON.
 * Can produce a report file.
 */
open class Kanjidic2DryRun<E : InputDictionaryEntry>(
    override val dictionaryXmlFile: File,
    override val rootTagName: String,
    override val parser: Parser<E, Kanjidic2Metadata>,
    override val reportFile: File?,
) : DryRun<E, Kanjidic2Metadata>(
    dictionaryXmlFile,
    rootTagName,
    parser,
    reportFile,
) {
    override fun getDictionaryMetadataTable(metadata: Kanjidic2Metadata): String {
        return getDictionaryMetadataMarkdownTable(dictionaryXmlFile, metadata)
    }

    companion object {
        fun getDictionaryMetadataMarkdownTable(dictionaryXmlFile: File, metadata: Kanjidic2Metadata): String {
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
    }
}
