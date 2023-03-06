package org.edrdg.jmdict.simplified.processing

import org.edrdg.jmdict.simplified.parsing.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader

class EventLoop<E : InputDictionaryEntry, M : Metadata>(
    val parser: Parser<E, M>,
    val eventReader: XMLEventReader,
    val rootTagName: String,
    /**
     * In some cases we want to parse the root tag, e.g. to read attributes.
     * Return true if you want to automatically skip the opening tag.
     */
    private val skipOpeningRootTag: Boolean,
) {
    private val _handlers: MutableList<EventHandler<E, M>> = mutableListOf()

    fun addHandlers(vararg handlers: EventHandler<E, M>): EventLoop<E, M> {
        _handlers.addAll(handlers)
        return this
    }

    fun run() {
        try {
            _handlers.forEach { it.onStart() }
            val metadata = parser.parseMetadata(eventReader)
            _handlers.forEach { it.beforeEntries(metadata) }
            if (skipOpeningRootTag) {
                eventReader.openTag(QName(rootTagName), "root opening tag")
            }
            while (parser.hasNextEntry(eventReader)) {
                val entry = parser.parseEntry(eventReader)
                _handlers.forEach { it.onEntry(entry) }
            }
            eventReader.closeTag(QName(rootTagName), "root closing tag")
            _handlers.forEach { it.afterEntries() }
        } finally {
            _handlers.forEach { it.onFinish() }
            eventReader.close()
        }
    }
}
