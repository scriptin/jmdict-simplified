package org.edrdg.jmdict.simplified.processing

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.commands.DictionaryOutputWriter
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryWord
import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.Parser
import java.io.File
import java.nio.file.Path

/**
 * Parses, analyzes, and converts to JSON a dictionary XML file.
 * Can produce a report file.
 */
class JMdictConvert<E : InputDictionaryEntry, W : OutputDictionaryWord<W>>(
    override val dictionaryXmlFile: File,
    override val rootTagName: String,
    override val parser: Parser<E, JMdictMetadata>,
    override val reportFile: File?,
    dictionaryName: String,
    private val version: String,
    languages: List<String>,
    outputDirectory: Path,
    private val outputs: List<DictionaryOutputWriter>,
    private val converter: Converter<E, W, JMdictMetadata>,
) : Convert<E, W, JMdictMetadata>(
    dictionaryXmlFile,
    rootTagName,
    parser,
    reportFile,
    dictionaryName,
    version,
    languages,
    outputDirectory,
    outputs,
    converter,
) {
    override fun beforeEntries(metadata: JMdictMetadata) {
        super.beforeEntries(metadata)
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

    override fun getDictionaryMetadataTable(metadata: JMdictMetadata): String {
        return JMdictDryRun.getDictionaryMetadataMarkdownTable(dictionaryXmlFile, metadata)
    }
}
