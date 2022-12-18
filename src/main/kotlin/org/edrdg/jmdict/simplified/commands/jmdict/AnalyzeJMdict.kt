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
    override fun getLanguagesOfXmlEntry(entry: JMdictXmlElement.Entry): Set<String> =
        entry.sense
            .flatMap { sense -> sense.gloss.map { it.lang } }
            .toSet()

    override val rootTagName = "JMdict"
}
