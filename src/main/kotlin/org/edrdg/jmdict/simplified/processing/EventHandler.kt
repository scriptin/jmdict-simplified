package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.parsing.*

interface EventHandler<E : InputDictionaryEntry, M : Metadata> {
    fun onStart()

    fun beforeEntries(metadata: M)

    fun onEntry(entry: E)

    fun afterEntries()

    fun onFinish()
}
