package org.edrdg.jmdict.simplified.conversion

interface OutputDictionaryWord<W : OutputDictionaryWord<W>> {
    val allLanguages: Set<String>

    fun onlyWithLanguages(languages: Set<String>): W
}
