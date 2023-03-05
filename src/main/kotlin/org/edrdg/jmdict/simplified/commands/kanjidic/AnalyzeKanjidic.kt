package org.edrdg.jmdict.simplified.commands.kanjidic

import org.edrdg.jmdict.simplified.commands.AnalyzeCommand
import org.edrdg.jmdict.simplified.parsing.Kanjidic2Metadata
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2Parser
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2XmlElement
import org.edrdg.jmdict.simplified.processing.Kanjidic2DryRun

class AnalyzeKanjidic : AnalyzeCommand<Kanjidic2XmlElement.Character, Kanjidic2Metadata>(
    help = "Analyze kanjidic2.xml file contents",
    parser = Kanjidic2Parser,
    rootTagName = "kanjidic2",
) {
    override fun run() {
        Kanjidic2DryRun(
            dictionaryXmlFile = dictionaryXmlFile,
            rootTagName = rootTagName,
            parser = parser,
            reportFile = reportFile,
        ).run()
    }
}
