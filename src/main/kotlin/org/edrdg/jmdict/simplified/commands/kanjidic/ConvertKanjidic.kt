package org.edrdg.jmdict.simplified.commands.kanjidic

import org.edrdg.jmdict.simplified.commands.ConvertCommand
import org.edrdg.jmdict.simplified.conversion.kanjidic.Kanjidic2Converter
import org.edrdg.jmdict.simplified.conversion.kanjidic.Kanjidic2JsonElement
import org.edrdg.jmdict.simplified.parsing.Kanjidic2Metadata
import org.edrdg.jmdict.simplified.parsing.XMLEventReaderBuilder
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2Parser
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2XmlElement
import org.edrdg.jmdict.simplified.processing.EventLoop
import org.edrdg.jmdict.simplified.processing.kanjidic.Kanjidic2ConvertingHandler
import org.edrdg.jmdict.simplified.processing.kanjidic.Kanjidic2ReportingHandler

class ConvertKanjidic : ConvertCommand<Kanjidic2XmlElement.Character, Kanjidic2JsonElement.Character, Kanjidic2Metadata>(
    supportsCommonOnlyOutputs = false,
    help = "Convert kanjidic2.xml file into JSON",
    parser = Kanjidic2Parser,
    rootTagName = "kanjidic2",
    dictionaryName = "kanjidic2",
    converter = Kanjidic2Converter()
) {
    override fun run() {
        EventLoop(
            parser = parser,
            eventReader = XMLEventReaderBuilder.build(dictionaryXmlFile),
            rootTagName = rootTagName,
            skipOpeningRootTag = false,
        ).addHandlers(
            Kanjidic2ReportingHandler(
                dictionaryXmlFile = dictionaryXmlFile,
                reportFile = reportFile,
            ),
            Kanjidic2ConvertingHandler(
                dictionaryName = dictionaryName,
                version = version,
                languages = languages,
                outputDirectory = outputDirectory,
                outputs = outputs,
                converter = converter,
            )
        ).run()
    }
}
