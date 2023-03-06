package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.XMLEventReaderBuilder
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement
import org.edrdg.jmdict.simplified.processing.EventLoop
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictReportingHandler

class AnalyzeJMnedict : AnalyzeCommand<JMnedictXmlElement.Entry, JMdictMetadata>(
    help = "Analyze JMnedict.xml file contents",
    parser = JMnedictParser,
    rootTagName = "JMnedict",
) {
    override fun run() {
        EventLoop(
            parser = parser,
            eventReader = XMLEventReaderBuilder.build(dictionaryXmlFile),
            rootTagName = rootTagName,
            skipOpeningRootTag = true,
        ).addHandlers(
            JMdictReportingHandler(
                dictionaryXmlFile = dictionaryXmlFile,
                reportFile = reportFile,
            ),
        ).run()
    }
}
