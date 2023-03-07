package org.edrdg.jmdict.simplified.conversion

interface OutputDictionaryEntry<E : OutputDictionaryEntry<E>> {
    val allLanguages: Set<String>

    fun onlyWithLanguages(languages: Set<String>): E

    /**
     * `false` is default because we don't expect to have
     * common-only outputs if dictionary doesn't support it
     */
    val isCommon: Boolean
        get() = false

    fun toJsonString(): String
}
