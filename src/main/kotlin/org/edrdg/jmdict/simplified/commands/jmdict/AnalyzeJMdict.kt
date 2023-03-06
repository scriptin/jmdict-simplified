package org.edrdg.jmdict.simplified.commands.jmdict

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.XMLEventReaderBuilder
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictReportingProcessor

class AnalyzeJMdict : AnalyzeCommand<JMdictXmlElement.Entry, JMdictMetadata>(
    help = "Analyze JMdict.xml file contents",
    parser = JMdictParser,
    rootTagName = "JMdict",
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
