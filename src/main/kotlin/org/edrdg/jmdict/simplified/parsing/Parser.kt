package org.edrdg.jmdict.simplified.parsing

import javax.xml.stream.XMLEventReader

interface Parser<E> {
    fun parseMetadata(eventReader: XMLEventReader): Metadata
    fun hasNextEntry(eventReader: XMLEventReader): Boolean
    fun parseEntry(eventReader: XMLEventReader): E
}
