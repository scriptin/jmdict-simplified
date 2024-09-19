package org.edrdg.jmdict.simplified.commands.jmdict

import org.edrdg.jmdict.simplified.commands.ConvertCommand
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictExamplesConverter
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictJsonElement
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.XMLEventReaderBuilder
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement
import org.edrdg.jmdict.simplified.processing.EventLoop
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictConvertingHandler
import org.edrdg.jmdict.simplified.processing.jmdict.JMdictReportingHandler

class ConvertJMdictExamples : ConvertCommand<JMdictXmlElement.Entry, JMdictJsonElement.WordWithExamples, JMdictMetadata>(
    supportsCommonOnlyOutputs = true,
    help = "Convert JMdict_e_examp.xml file into JSON",
    parser = JMdictParser,
    rootTagName = "JMdict",
    dictionaryName = "jmdict-examples",
    converter = JMdictExamplesConverter(),
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
