package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.parsing.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader

interface DictionaryProcessor<E : InputDictionaryEntry, M : Metadata> {
    val parser: Parser<E, M>
    val eventReader: XMLEventReader
    val rootTagName: String

    fun onStart()

    fun beforeEntries(metadata: M)

    fun processEntry(entry: E)

    fun afterEntries()

    fun onFinish()

    /**
     * In some cases we want to parse the root tag, e.g. to read attributes.
     * Return true if you want to automatically skip the opening tag.
     */
    val skipOpeningRootTag: Boolean

    fun run() {
        try {
            onStart()
            val metadata = parser.parseMetadata(eventReader)
            beforeEntries(metadata)
            if (skipOpeningRootTag) {
                eventReader.openTag(QName(rootTagName), "root opening tag")
            }
            while (parser.hasNextEntry(eventReader)) {
                val entry = parser.parseEntry(eventReader)
                processEntry(entry)
            }
            eventReader.closeTag(QName(rootTagName), "root closing tag")
            afterEntries()
        } finally {
            onFinish()
        }
    }
}
