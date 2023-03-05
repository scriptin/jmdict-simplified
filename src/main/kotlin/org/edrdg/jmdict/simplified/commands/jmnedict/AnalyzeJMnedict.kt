package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement
import org.edrdg.jmdict.simplified.processing.JMdictDryRun

class AnalyzeJMnedict : AnalyzeCommand<JMnedictXmlElement.Entry, JMdictMetadata>(
    help = "Analyze JMnedict.xml file contents",
    parser = JMnedictParser,
    rootTagName = "JMnedict",
) {
    override fun run() {
        JMdictDryRun(
            dictionaryXmlFile = dictionaryXmlFile,
            rootTagName = rootTagName,
            parser = parser,
            reportFile = reportFile,
        ).run()
    }
}
