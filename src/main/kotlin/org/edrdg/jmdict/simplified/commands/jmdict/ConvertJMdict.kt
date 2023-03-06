package org.edrdg.jmdict.simplified.commands.jmdict

import org.edrdg.jmdict.simplified.commands.ConvertCommand
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictConverter
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictJsonElement
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictConvertingProcessor
import java.io.FileInputStream
import javax.xml.stream.XMLInputFactory

class ConvertJMdict : ConvertCommand<JMdictXmlElement.Entry, JMdictJsonElement.Word, JMdictMetadata>(
    supportsCommonOnlyOutputs = true,
    help = "Convert JMdict.xml file into JSON",
    parser = JMdictParser,
    rootTagName = "JMdict",
    dictionaryName = "jmdict",
    converter = JMdictConverter(),
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
