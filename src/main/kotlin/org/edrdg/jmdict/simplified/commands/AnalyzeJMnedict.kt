package org.edrdg.jmdict.simplified.commands

import org.edrdg.jmdict.simplified.parsing.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.JMnedictXmlElement

open class AnalyzeJMnedict(
    override val help: String = "Analyze JMnedict.xml file contents",
) : AnalyzeDictionary<JMnedictXmlElement.Entry>(
    parser = JMnedictParser,
    help = help,
) {
    override fun getLanguagesOf(entry: JMnedictXmlElement.Entry): Set<String> =
        entry.trans
            .flatMap { trans -> trans.transDet.map { it.lang } }
            .toSet()

    override val rootTagName: String = "JMnedict"
}
