package org.edrdg.jmdict.simplified.commands.jmnedict

import org.edrdg.jmdict.simplified.commands.ConvertDictionary
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictConverter
import org.edrdg.jmdict.simplified.conversion.jmnedict.JMnedictJsonElement
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictParser
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement

class ConvertJMnedict : ConvertDictionary<JMnedictXmlElement.Entry, JMnedictJsonElement.Word>(
    supportsCommonOnlyOutputs = false,
    help = "Convert JMnedict.xml file into JSON",
    parser = JMnedictParser,
    rootTagName = "JMnedict",
    dictionaryName = "jmnedict",
    converter = JMnedictConverter(),
)
