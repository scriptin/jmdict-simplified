package org.edrdg.jmdict.simplified.commands.jmdict

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictReportingProcessor
import java.io.FileInputStream
import javax.xml.stream.XMLInputFactory

class AnalyzeJMdict : AnalyzeCommand<JMdictXmlElement.Entry, JMdictMetadata>(
    help = "Analyze JMdict.xml file contents",
    parser = JMdictParser,
    rootTagName = "JMdict",
) {
    override fun run() {
        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        val eventReader = factory.createXMLEventReader(FileInputStream(dictionaryXmlFile))
        JMdictReportingProcessor(
            dictionaryXmlFile = dictionaryXmlFile,
            rootTagName = rootTagName,
            eventReader = eventReader,
            parser = parser,
            reportFile = reportFile,
        ).run()
    }
}
