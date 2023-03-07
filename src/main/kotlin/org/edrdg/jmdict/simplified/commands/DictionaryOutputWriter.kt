package org.edrdg.jmdict.simplified.commands

import org.edrdg.jmdict.simplified.conversion.OutputDictionaryEntry
import java.nio.file.Path

class DictionaryOutputWriter(
    path: Path,
    val languages: Set<String>,
    val commonOnly: Boolean,
) {
    private val fileWriter = path.toFile().writer()

    fun write(text: String) {
        fileWriter.write(text)
    }

    fun close() {
        fileWriter.close()
    }

    private var acceptedEntry = false

    var acceptedAtLeastOneEntry
        get() = acceptedEntry
        set(value) {
            if (!acceptedEntry && value) acceptedEntry = true
        }

    fun <O : OutputDictionaryEntry<O>> acceptsEntry(word: O): Boolean {
        val shareSomeLanguages = languages.intersect(word.allLanguages).isNotEmpty()
        // For non-only-common outputs, all words must be accepted
        // Otherwise, for only-common outputs, allow only common words
        val matchesByCommon = !commonOnly || word.isCommon
        return (shareSomeLanguages || languages.contains("all")) && matchesByCommon
    }
}
