package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictReportingProcessor
import java.io.FileInputStream
import javax.xml.stream.XMLInputFactory

class AnalyzeJMnedict : AnalyzeCommand<JMnedictXmlElement.Entry, JMdictMetadata>(
    help = "Analyze JMnedict.xml file contents",
    parser = JMnedictParser,
    rootTagName = "JMnedict",
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
