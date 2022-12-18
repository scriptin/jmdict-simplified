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
    override fun getLanguagesOfXmlEntry(entry: JMnedictXmlElement.Entry): Set<String> =
        entry.trans
            .flatMap { trans -> trans.transDet.map { it.lang } }
            .toSet()

    override val rootTagName = "JMnedict"
}
