package org.edrdg.jmdict.simplified.parsing

import javax.xml.namespace.QName
import javax.xml.stream.Location
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent

/**
 * Human-readable exceptions for XML parser
 */
sealed class ParsingException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)

    class UnexpectedEndOfDocument(cause: Throwable) : ParsingException("Unexpected end of document", cause)

    companion object {
        private fun showLocation(l: Location): String = "${l.lineNumber}:${l.columnNumber}"

        private fun replaceWS(s: String): String {
            return s.replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
        }

        protected fun show(event: XMLEvent): String {
            return when (event.eventType) {
                XMLEvent.SPACE -> "[whitespace]"
                XMLEvent.CHARACTERS -> "[characters:'${replaceWS(event.asCharacters().data)}']"
                else -> event.toString()
            }
        }
    }

    class UnexpectedEvent : ParsingException {
        constructor(expectedEventType: Class<*>, unexpectedEvent: XMLEvent, description: String) :
            super(
                "Expected event of type ${expectedEventType.simpleName} ($description), " +
                    "got ${show(unexpectedEvent)} - at ${showLocation(unexpectedEvent.location)}"
            )

        constructor(expectedEventTypes: List<Class<*>>, unexpectedEvent: XMLEvent, description: String) :
            super(
                "Expected one of these event types: ${expectedEventTypes.map { it.simpleName }} ($description), " +
                    "got ${show(unexpectedEvent)} - at ${showLocation(unexpectedEvent.location)}"
            )
    }

    class UnexpectedOpeningTag : ParsingException {
        constructor(expectedTag: QName, description: String, unexpectedTag: StartElement) :
            super(
                "Expected opening tag <$expectedTag> ($description), " +
                    "got $unexpectedTag - at ${showLocation(unexpectedTag.location)}"
            )

        constructor(expectedTags: List<QName>, description: String, unexpectedTag: StartElement) :
            super(
                "Expected opening tag ${expectedTags.map { "<$it>" }} ($description), " +
                    "got $unexpectedTag - at ${showLocation(unexpectedTag.location)}"
            )
    }

    class UnexpectedClosingTag(expectedTag: QName, description: String, unexpectedTag: EndElement) :
        ParsingException(
            "Expected closing tag </$expectedTag> ($description), " +
                "got $unexpectedTag - at ${showLocation(unexpectedTag.location)}"
        )

    class InvalidAttributeFormat(
        tag: StartElement,
        attribute: Attribute,
        details: String,
        cause: Throwable? = null
    ) : ParsingException(
        "Invalid format of an attribute [$attribute] in tag $tag: $details - at ${showLocation(tag.location)}",
        cause
    )

    class MissingRequiredAttribute(tag: StartElement, attributeName: QName) :
        ParsingException(
            "Missing required attribute [$attributeName] in tag $tag - at ${showLocation(tag.location)}"
        )

    class ProhibitedAttributes(tag: StartElement, attributeNames: List<String>, allowedAttributes: List<String>) :
        ParsingException(
            "Prohibited attributes $attributeNames in tag $tag, " +
                "allowed attributes are $allowedAttributes - at ${showLocation(tag.location)}"
        )

    class EmptyChildrenList(parentTag: StartElement, expectedChildTag: QName) :
        ParsingException(
            "Expected at least one child tag <$expectedChildTag> " +
                "in tag $parentTag - at ${showLocation(parentTag.location)}"
        )

    class MissingCharacters(parentTag: StartElement, unexpectedEvent: XMLEvent) :
        ParsingException(
            "Expected characters inside $parentTag, " +
                "got $unexpectedEvent - at ${showLocation(unexpectedEvent.location)}"
        )

    class MissingText(parentTag: StartElement, unexpectedEvent: XMLEvent) :
        ParsingException(
            "Expected characters and/or entity references inside $parentTag, " +
                "got $unexpectedEvent - at ${showLocation(unexpectedEvent.location)}"
        )

    class InvalidCharactersFormat(
        parentTag: StartElement,
        text: String,
        details: String,
        cause: Throwable? = null
    ) : ParsingException(
        "Invalid format of a text '$text' in tag $parentTag: $details - at ${showLocation(parentTag.location)}",
        cause
    )

    class InvalidDoctypeFormat(
        dtd: String,
        details: String,
    ) : ParsingException(
        "Invalid format of DTD string: $details\n$dtd"
    )
}
