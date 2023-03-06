package org.edrdg.jmdict.simplified.commands.jmdict

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.XMLEventReaderBuilder
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement
import org.edrdg.jmdict.simplified.processing.EventLoop
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictReportingHandler

class AnalyzeJMdict : AnalyzeCommand<JMdictXmlElement.Entry, JMdictMetadata>(
    help = "Analyze JMdict.xml file contents",
    parser = JMdictParser,
    rootTagName = "JMdict",
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
