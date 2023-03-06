package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.ConvertCommand
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictConverter
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictJsonElement
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.XMLEventReaderBuilder
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement
import org.edrdg.jmdict.simplified.processing.EventLoop
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictConvertingHandler
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictReportingHandler

class ConvertJMnedict : ConvertCommand<JMnedictXmlElement.Entry, JMnedictJsonElement.Word, JMdictMetadata>(
    supportsCommonOnlyOutputs = false,
    help = "Convert JMnedict.xml file into JSON",
    parser = JMnedictParser,
    rootTagName = "JMnedict",
    dictionaryName = "jmnedict",
    converter = JMnedictConverter(),
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
            JMdictConvertingHandler(
                dictionaryName = dictionaryName,
                version = version,
                languages = languages,
                outputDirectory = outputDirectory,
                outputs = outputs,
                converter = converter,
            ),
        ).run()
    }
}
