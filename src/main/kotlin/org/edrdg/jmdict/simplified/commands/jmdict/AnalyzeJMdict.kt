package org.edrdg.jmdict.simplified.commands.jmdict

import org.edrdg.jmdict.simplified.commands.AnalyzeDictionary
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement

open class AnalyzeJMdict(
    override val help: String = "Analyze JMdict.xml file contents",
) : AnalyzeDictionary<JMdictXmlElement.Entry>(
    parser = JMdictParser,
    help = help,
) {
    override val rootTagName = "JMdict"
}
