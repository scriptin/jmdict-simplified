package org.edrdg.jmdict.simplified.commands.kanjidic

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.Kanjidic2Metadata
import org.edrdg.jmdict.simplified.parsing.XMLEventReaderBuilder
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2Parser
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2XmlElement
import org.edrdg.jmdict.simplified.processing.kanjidic.Kanjidic2ReportingProcessor

class AnalyzeKanjidic : AnalyzeCommand<Kanjidic2XmlElement.Character, Kanjidic2Metadata>(
    help = "Analyze kanjidic2.xml file contents",
    parser = Kanjidic2Parser,
    rootTagName = "kanjidic2",
) {
    override fun run() {
        Kanjidic2ReportingProcessor(
            rootTagName = rootTagName,
            parser = parser,
            eventReader = XMLEventReaderBuilder.build(dictionaryXmlFile),
            dictionaryXmlFile = dictionaryXmlFile,
            reportFile = reportFile,
        ).run()
    }
}
