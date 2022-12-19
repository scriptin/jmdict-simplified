package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.AnalyzeDictionary
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement

open class AnalyzeJMnedict(
    override val help: String = "Analyze JMnedict.xml file contents",
) : AnalyzeDictionary<JMnedictXmlElement.Entry>(
    parser = JMnedictParser,
    help = help,
) {
    override val rootTagName = "JMnedict"
}
