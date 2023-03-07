package org.edrdg.jmdict.simplified.processing.kanjidic

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.edrdg.jmdict.simplified.commands.DictionaryOutputWriter
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.kanjidic.Kanjidic2JsonElement
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.Kanjidic2Metadata
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2XmlElement
import org.edrdg.jmdict.simplified.processing.ConvertingHandler
import java.nio.file.Path

class Kanjidic2ConvertingHandler(
    override val dictionaryName: String,
    override val version: String,
    override val languages: List<String>,
    override val outputDirectory: Path,
    override val outputs: List<DictionaryOutputWriter>,
    override val converter: Converter<Kanjidic2XmlElement.Character, Kanjidic2JsonElement.Character, Kanjidic2Metadata>,
) : ConvertingHandler<Kanjidic2XmlElement.Character, Kanjidic2JsonElement.Character, Kanjidic2Metadata>(
    dictionaryName,
    version,
    languages,
    outputDirectory,
    outputs,
    converter,
) {
    override fun beforeEntries(metadata: Kanjidic2Metadata) {
        converter.metadata = metadata
        outputs.forEach {
            it.write(
                """
                {
                "version": ${Json.encodeToString(version)},
                "languages": ${Json.encodeToString(it.languages.toList().sorted())},
                "dictDate": ${Json.encodeToString(metadata.date)},
                "fileVersion": ${Json.encodeToString(metadata.fileVersion)},
                "databaseVersion": ${Json.encodeToString(metadata.databaseVersion.toString())},
                "characters": [
                """.trimIndent().trimEnd('\n', ' ')
            )
        }
    }
}
