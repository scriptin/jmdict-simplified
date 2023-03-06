package org.edrdg.jmdict.simplified.commands.kanjidic

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.Kanjidic2Metadata
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2Parser
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2XmlElement
import org.edrdg.jmdict.simplified.processing.kanjidic.Kanjidic2ReportingProcessor
import java.io.FileInputStream
import javax.xml.stream.XMLInputFactory

class AnalyzeKanjidic : AnalyzeCommand<Kanjidic2XmlElement.Character, Kanjidic2Metadata>(
    help = "Analyze kanjidic2.xml file contents",
    parser = Kanjidic2Parser,
    rootTagName = "kanjidic2",
) {
    override fun run() {
        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        val eventReader = factory.createXMLEventReader(FileInputStream(dictionaryXmlFile))
        Kanjidic2ReportingProcessor(
            rootTagName = rootTagName,
            parser = parser,
            eventReader = eventReader,
            dictionaryXmlFile = dictionaryXmlFile,
            reportFile = reportFile,
        ).run()
    }
}
