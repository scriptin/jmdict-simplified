package org.edrdg.jmdict.simplified.conversion

interface Converter<E, W> {
    fun convertWord(xmlEntry: E): W
}
