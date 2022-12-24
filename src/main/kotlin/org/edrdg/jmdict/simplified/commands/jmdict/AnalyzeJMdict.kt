package org.edrdg.jmdict.simplified.commands.jmdict

import org.edrdg.jmdict.simplified.commands.AnalyzeDictionary
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement

class AnalyzeJMdict : AnalyzeDictionary<JMdictXmlElement.Entry>(
    help = "Analyze JMdict.xml file contents",
    parser = JMdictParser,
    rootTagName = "JMdict",
)
