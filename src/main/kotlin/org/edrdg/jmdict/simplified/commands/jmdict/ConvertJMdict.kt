package org.edrdg.jmdict.simplified.commands.jmdict

import org.edrdg.jmdict.simplified.commands.ConvertDictionary
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictConverter
import org.edrdg.jmdict.simplified.conversion.jmdict.JMdictJsonElement
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictParser
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement

class ConvertJMdict : ConvertDictionary<JMdictXmlElement.Entry, JMdictJsonElement.Word>(
    supportsCommonOnlyOutputs = true,
    help = "Convert JMdict.xml file into JSON",
    parser = JMdictParser,
    rootTagName = "JMdict",
    dictionaryName = "jmdict",
    converter = JMdictConverter(),
)
