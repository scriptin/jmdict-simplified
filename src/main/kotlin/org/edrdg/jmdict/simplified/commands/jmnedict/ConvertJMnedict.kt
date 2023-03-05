package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.ConvertCommand
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictConverter
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictJsonElement
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement
import org.edrdg.jmdict.simplified.processing.JMdictConvert

class ConvertJMnedict : ConvertCommand<JMnedictXmlElement.Entry, JMnedictJsonElement.Word, JMdictMetadata>(
    supportsCommonOnlyOutputs = false,
    help = "Convert JMnedict.xml file into JSON",
    parser = JMnedictParser,
    rootTagName = "JMnedict",
    dictionaryName = "jmnedict",
    converter = JMnedictConverter(),
) {
    override fun run() {
        JMdictConvert(
            dictionaryXmlFile = dictionaryXmlFile,
            rootTagName = rootTagName,
            parser = parser,
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
