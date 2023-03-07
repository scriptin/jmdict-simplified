package org.edrdg.jmdict.simplified.processing.jmdict

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.commands.DictionaryOutputWriter
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.processing.ConvertingHandler
import java.nio.file.Path

/**
 * Parses, analyzes, and converts to JSON a dictionary XML file.
 * Can produce a report file.
 */
class JMdictConvertingHandler<I : InputDictionaryEntry, O : OutputDictionaryEntry<O>>(
    override val dictionaryName: String,
    override val version: String,
    override val languages: List<String>,
    override val outputDirectory: Path,
    override val outputs: List<DictionaryOutputWriter>,
    override val converter: Converter<I, O, JMdictMetadata>,
) : ConvertingHandler<I, O, JMdictMetadata>(
    dictionaryName,
    version,
    languages,
    outputDirectory,
    outputs,
    converter,
) {
    override fun beforeEntries(metadata: JMdictMetadata) {
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
}
