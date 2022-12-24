package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.AnalyzeDictionary
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement

class AnalyzeJMnedict : AnalyzeDictionary<JMnedictXmlElement.Entry>(
    help = "Analyze JMnedict.xml file contents",
    parser = JMnedictParser,
    rootTagName = "JMnedict",
)
