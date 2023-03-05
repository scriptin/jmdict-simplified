package org.edrdg.jmdict.simplified.parsing.kanjidic

import org.edrdg.jmdict.simplified.parsing.*
import org.edrdg.jmdict.simplified.parsing.skip
import org.edrdg.jmdict.simplified.parsing.skipSpace
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.XMLEvent

object Kanjidic2Parser : Parser<Kanjidic2XmlElement.Character, Kanjidic2Metadata> {
    override fun parseMetadata(eventReader: XMLEventReader): Kanjidic2Metadata {
        eventReader.skip(
            setOf(
                XMLEvent.START_DOCUMENT,
                XMLEvent.SPACE,
                XMLEvent.DTD,
            )
        )

        eventReader.openTag(QName("kanjidic2"), "Kanjidic opening tag")

        val header = eventReader.tag(QName("header"), "Kanjidic header") {
            eventReader.skip(setOf(XMLEvent.COMMENT))
            Kanjidic2XmlElement.Header(
                fileVersion = eventReader.tag(QName("file_version"), "File version") {
                    Kanjidic2XmlElement.FileVersion(eventReader.text(it).toInt())
                },
                databaseVersion = eventReader.tag(QName("database_version"), "Database version") {
                    Kanjidic2XmlElement.DatabaseVersion(eventReader.text(it))
                },
                dateOfCreation = eventReader.tag(QName("date_of_creation"), "Date of creation") {
                    Kanjidic2XmlElement.DateOfCreation(eventReader.text(it))
                },
            )
        }

        return Kanjidic2Metadata(
            header.fileVersion.value,
            Kanjidic2DatabaseVersion(
                header.databaseVersion.year,
                header.databaseVersion.ordinalVersion,
            ),
            header.dateOfCreation.text,
        )
    }

    override fun hasNextEntry(eventReader: XMLEventReader): Boolean {
        eventReader.skipSpace()
        val next = eventReader.peek()
        return next.isStartElement && next.asStartElement().name.localPart == "character"
    }

    override fun parseEntry(eventReader: XMLEventReader): Kanjidic2XmlElement.Character {
        TODO("Not yet implemented")
    }
}
