package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.ConvertCommand
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictConverter
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictJsonElement
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictConvertingProcessor
import java.io.FileInputStream
import javax.xml.stream.XMLInputFactory

class ConvertJMnedict : ConvertCommand<JMnedictXmlElement.Entry, JMnedictJsonElement.Word, JMdictMetadata>(
    supportsCommonOnlyOutputs = false,
    help = "Convert JMnedict.xml file into JSON",
    parser = JMnedictParser,
    rootTagName = "JMnedict",
    dictionaryName = "jmnedict",
    converter = JMnedictConverter(),
) {
    override fun run() {
        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        val eventReader = factory.createXMLEventReader(FileInputStream(dictionaryXmlFile))
        JMdictConvertingProcessor(
            rootTagName = rootTagName,
            parser = parser,
            eventReader = eventReader,
            dictionaryXmlFile = dictionaryXmlFile,
            reportFile = reportFile,
            dictionaryName = dictionaryName,
            version = version,
            languages = languages,
            outputDirectory = outputDirectory,
            outputs = outputs,
            converter = converter,
        ).run()
    }
}
