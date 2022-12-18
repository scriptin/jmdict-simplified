package org.edrdg.jmdict.simplified.commands

import org.edrdg.jmdict.simplified.parsing.JMdictParser
import org.edrdg.jmdict.simplified.parsing.JMdictXmlElement

open class AnalyzeJMdict(
    override val help: String = "Analyze JMdict.xml file contents",
) : AnalyzeDictionary<JMdictXmlElement.Entry>(
    parser = JMdictParser,
    help = help,
) {
    override fun getLanguagesOf(entry: JMdictXmlElement.Entry): Set<String> =
        entry.sense
            .flatMap { sense -> sense.gloss.map { it.lang } }
            .toSet()

    override val rootTagName: String = "JMdict"
}
