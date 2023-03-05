package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.parsing.*
import java.io.File
import kotlin.math.round

fun getDictionaryMetadataMarkdownTable(dictionaryXmlFile: File, metadata: JMdictMetadata): String {
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

/**
 * Parses and analyzes a dictionary XML file without conversion to JSON.
 * Can produce a report file.
 */
open class JMdictDryRun<E : InputDictionaryEntry>(
    override val dictionaryXmlFile: File,
    override val rootTagName: String,
    override val parser: Parser<E, JMdictMetadata>,
    override val reportFile: File?,
) : DryRun<E, JMdictMetadata>(
    dictionaryXmlFile,
    rootTagName,
    parser,
    reportFile,
) {
    override fun getDictionaryMetadataTable(metadata: JMdictMetadata): String {
        return getDictionaryMetadataMarkdownTable(dictionaryXmlFile, metadata)
    }
}
