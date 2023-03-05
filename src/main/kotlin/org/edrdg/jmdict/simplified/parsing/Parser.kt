package org.edrdg.jmdict.simplified.parsing

import javax.xml.stream.XMLEventReader

interface Parser<E : InputDictionaryEntry, M : Metadata> {
    fun parseMetadata(eventReader: XMLEventReader): M
    fun hasNextEntry(eventReader: XMLEventReader): Boolean
    fun parseEntry(eventReader: XMLEventReader): E
}
