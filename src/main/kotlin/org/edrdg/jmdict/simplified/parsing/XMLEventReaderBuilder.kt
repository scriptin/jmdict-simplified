package org.edrdg.jmdict.simplified.parsing

import java.io.File
import java.io.FileInputStream
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory

object XMLEventReaderBuilder {
    fun build(dictionaryXmlFile: File): XMLEventReader {
        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        return factory.createXMLEventReader(FileInputStream(dictionaryXmlFile))
    }
}
