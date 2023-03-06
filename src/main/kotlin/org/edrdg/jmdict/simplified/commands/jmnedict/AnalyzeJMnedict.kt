package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.XMLEventReaderBuilder
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictReportingProcessor

class AnalyzeJMnedict : AnalyzeCommand<JMnedictXmlElement.Entry, JMdictMetadata>(
    help = "Analyze JMnedict.xml file contents",
    parser = JMnedictParser,
    rootTagName = "JMnedict",
) {
    override fun run() {
        JMdictReportingProcessor(
            dictionaryXmlFile = dictionaryXmlFile,
            rootTagName = rootTagName,
            eventReader = XMLEventReaderBuilder.build(dictionaryXmlFile),
            parser = parser,
            reportFile = reportFile,
        ).run()
    }
}
