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
        eventReader.skip(setOf(XMLEvent.COMMENT))
        eventReader.skipSpace()
        val next = eventReader.peek()
        return next.isStartElement && next.asStartElement().name.localPart == "character"
    }

    override fun parseEntry(eventReader: XMLEventReader): Kanjidic2XmlElement.Character {
        return eventReader.tag(QName("character"), "character") {
            Kanjidic2XmlElement.Character(
                literal = liberal(eventReader),
                codepoint = codepoint(eventReader),
                radical = radical(eventReader),
                misc = misc(eventReader),
                dicNumber = dicNumber(eventReader),
                queryCode = queryCode(eventReader),
                readingMeaning = readingMeaning(eventReader),
            )
        }
    }

    private fun liberal(eventReader: XMLEventReader) = eventReader.tag(QName("literal"), "literal") {
        Kanjidic2XmlElement.Literal(eventReader.text(it))
    }

    private fun codepoint(eventReader: XMLEventReader) = eventReader.tag(QName("codepoint"), "codepoint") {
        Kanjidic2XmlElement.Codepoint(
            cpValues = eventReader.nonEmptyTagList(it, QName("cp_value"), "cp_value") { cpValue ->
                Kanjidic2XmlElement.CpValue(
                    cpType = cpValue.attrEnum(
                        QName("cp_type"),
                        Kanjidic2XmlElement.CpType.values(),
                        Kanjidic2XmlElement.CpType::fromString,
                    ) ?: throw ParsingException.MissingRequiredAttribute(cpValue, QName("cp_type")),
                    text = eventReader.text(cpValue),
                )
            },
        )
    }

    private fun radical(eventReader: XMLEventReader) = eventReader.tag(QName("radical"), "radical") {
        Kanjidic2XmlElement.Radical(
            radValues = eventReader.nonEmptyTagList(it, QName("rad_value"), "rad_value") { radValue ->
                Kanjidic2XmlElement.RadValue(
                    radType = radValue.attrEnum(
                        QName("rad_type"),
                        Kanjidic2XmlElement.RadType.values(),
                        Kanjidic2XmlElement.RadType::fromString,
                    ) ?: throw ParsingException.MissingRequiredAttribute(radValue, QName("cp_type")),
                    value = eventReader.text(radValue).toInt(),
                )
            },
        )
    }

    private fun misc(eventReader: XMLEventReader) = eventReader.tag(QName("misc"), "misc") {
        Kanjidic2XmlElement.Misc(
            grade = eventReader.maybeTag(QName("grade"), "grade") { grade ->
                Kanjidic2XmlElement.Grade(
                    value = eventReader.text(grade).toInt(),
                )
            },
            strokeCounts = eventReader.nonEmptyTagList(it, QName("stroke_count"), "stroke_count") { strokeCount ->
                Kanjidic2XmlElement.StrokeCount(
                    value = eventReader.text(strokeCount).toInt(),
                )
            },
            variants = eventReader.simpleTagList(QName("variant"), "variant") { variant ->
                Kanjidic2XmlElement.Variant(
                    varType = variant.attrEnum(
                        QName("var_type"),
                        Kanjidic2XmlElement.VarType.values(),
                        Kanjidic2XmlElement.VarType::fromString,
                    ) ?: throw ParsingException.MissingRequiredAttribute(variant, QName("var_type")),
                    text = eventReader.text(variant),
                )
            },
            freq = eventReader.maybeTag(QName("freq"), "freq") { freq ->
                Kanjidic2XmlElement.Freq(
                    value = eventReader.text(freq).toInt(),
                )
            },
            radNames = eventReader.simpleTagList(QName("rad_name"), "rad_name") { radName ->
                Kanjidic2XmlElement.RadName(
                    text = eventReader.text(radName),
                )
            },
            jlpt = eventReader.maybeTag(QName("jlpt"), "jlpt") { jlpt ->
                Kanjidic2XmlElement.Jlpt(
                    value = eventReader.text(jlpt).toInt(),
                )
            },
        )
    }

    private fun dicNumber(eventReader: XMLEventReader) = eventReader.maybeTag(QName("dic_number"), "dic_number") {
        Kanjidic2XmlElement.DicNumber(
            dicRefs = eventReader.nonEmptyTagList(it, QName("dic_ref"), "dic_ref") { dicRef ->
                Kanjidic2XmlElement.DicRef(
                    drType = dicRef.attrEnum(
                        QName("dr_type"),
                        Kanjidic2XmlElement.DRType.values(),
                        Kanjidic2XmlElement.DRType::fromString,
                    ) ?: throw ParsingException.MissingRequiredAttribute(dicRef, QName("dr_type")),
                    mVol = dicRef.attrInt(QName("m_vol")),
                    mPage = dicRef.attrInt(QName("m_page")),
                    value = eventReader.text(dicRef),
                )
            },
        )
    }

    private fun queryCode(eventReader: XMLEventReader) = eventReader.maybeTag(QName("query_code"), "query_code") {
        Kanjidic2XmlElement.QueryCode(
            qCodes = eventReader.nonEmptyTagList(it, QName("q_code"), "q_code") { qCode ->
                Kanjidic2XmlElement.QCode(
                    qcType = qCode.attrEnum(
                        QName("qc_type"),
                        Kanjidic2XmlElement.QCType.values(),
                        Kanjidic2XmlElement.QCType::fromString,
                    ) ?: throw ParsingException.MissingRequiredAttribute(qCode, QName("qc_type")),
                    skipMisclass = qCode.attrEnum(
                        QName("skip_misclass"),
                        Kanjidic2XmlElement.SkipMisclass.values(),
                        Kanjidic2XmlElement.SkipMisclass::fromString,
                    ),
                    text = eventReader.text(qCode),
                )
            },
        )
    }

    private fun readingMeaning(eventReader: XMLEventReader) = eventReader.maybeTag(QName("reading_meaning"), "reading_meaning") {
        Kanjidic2XmlElement.ReadingMeaning(
            rmGroups = eventReader.simpleTagList(QName("rmgroup"), "rmgroup") { rmGroup ->
                Kanjidic2XmlElement.RmGroup(
                    readings = eventReader.simpleTagList(QName("reading"), "reading") { reading ->
                        Kanjidic2XmlElement.Reading(
                            rType = reading.attrEnum(
                                QName("r_type"),
                                Kanjidic2XmlElement.RType.values(),
                                Kanjidic2XmlElement.RType::fromString,
                            ) ?: throw ParsingException.MissingRequiredAttribute(reading, QName("r_type")),
                            onType = reading.attrString(QName("on_type")),
                            rStatus = reading.attrString(QName("r_status")),
                            text = eventReader.text(reading),
                        )
                    },
                    meanings = eventReader.simpleTagList(QName("meaning"), "meaning") { meaning ->
                        Kanjidic2XmlElement.Meaning(
                            mLang = meaning.attrString(QName("m_lang")) ?: "en",
                            text = eventReader.text(meaning),
                        )
                    },
                )
            },
            nanori = eventReader.simpleTagList(QName("nanori"), "nanori") { nanori ->
                Kanjidic2XmlElement.Nanori(
                    text = eventReader.text(nanori),
                )
            },
        )
    }
}
