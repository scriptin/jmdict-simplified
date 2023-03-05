package org.edrdg.jmdict.simplified.commands.jmdict

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement
import org.edrdg.jmdict.simplified.processing.JMdictDryRun

class AnalyzeJMdict : AnalyzeCommand<JMdictXmlElement.Entry, JMdictMetadata>(
    help = "Analyze JMdict.xml file contents",
    parser = JMdictParser,
    rootTagName = "JMdict",
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
