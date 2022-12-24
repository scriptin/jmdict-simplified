package org.edrdg.jmdict.simplified.commands.processor

import org.edrdg.jmdict.simplified.parsing.InputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.Metadata
import org.edrdg.jmdict.simplified.parsing.Parser
import java.io.File

open class AnalyzingDictionaryProcessor<E : InputDictionaryEntry>(
    override val dictionaryXmlFile: File,
    override val reportFile: File?,
    override val parser: Parser<E>,
    override val rootTagName: String,
) : DictionaryProcessor<E> {
    override fun printMoreInfo() {}

    override fun beforeEntries(metadata: Metadata) {}

    override fun processEntry(entry: E) {}

    override fun afterEntries() {}

    override fun finish() {}

    private val reportFileWriter by lazy { reportFile?.writer() }

    override fun writeln(text: String) {
        if (reportFile != null) {
            reportFileWriter?.write("$text\n")
        } else {
            println(text)
        }
    }
}
