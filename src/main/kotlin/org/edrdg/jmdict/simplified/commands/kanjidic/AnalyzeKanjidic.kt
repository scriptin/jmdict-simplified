package org.edrdg.jmdict.simplified.commands.kanjidic

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.Kanjidic2Metadata
import org.edrdg.jmdict.simplified.parsing.XMLEventReaderBuilder
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2Parser
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2XmlElement
import org.edrdg.jmdict.simplified.processing.EventLoop
import org.edrdg.jmdict.simplified.processing.kanjidic.Kanjidic2ReportingHandler

class AnalyzeKanjidic : AnalyzeCommand<Kanjidic2XmlElement.Character, Kanjidic2Metadata>(
    help = "Analyze kanjidic2.xml file contents",
    parser = Kanjidic2Parser,
    rootTagName = "kanjidic2",
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
        ).run()
    }
}
