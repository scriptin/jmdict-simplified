package org.edrdg.jmdict.simplified.parsing.jmdict

import org.edrdg.jmdict.simplified.parsing.*
import org.edrdg.jmdict.simplified.parsing.commentList
import org.edrdg.jmdict.simplified.parsing.skip
import javax.xml.XMLConstants
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent

object JMdictParser : Parser<JMdictXmlElement.Entry, JMdictMetadata> {
    override fun parseMetadata(eventReader: XMLEventReader): JMdictMetadata {
        eventReader.skip(
            setOf(
                XMLEvent.START_DOCUMENT,
                XMLEvent.SPACE
            )
        )

        val revisions = eventReader.commentList("revisions comments").map {
            it.text.split("\n").first().trim().split(" ").last()
        }

        val entities = eventReader.dtd("DTD declaration").entities.associate {
            it.name to it.replacementText
        }

        val date = eventReader.comment("JMdict creation date comment")
            .text.split(":").last().trim()

        return JMdictMetadata(revisions, entities, date)
    }

    override fun hasNextEntry(eventReader: XMLEventReader): Boolean {
        eventReader.skipSpace()
        val next = eventReader.peek()
        return next.isStartElement && next.asStartElement().name.localPart == "entry"
    }

    override fun parseEntry(eventReader: XMLEventReader): JMdictXmlElement.Entry {
        return eventReader.tag(QName("entry"), "entry") { entry ->
            JMdictXmlElement.Entry(
                entSeq = entSeq(eventReader),
                kEle = kEle(eventReader),
                rEle = rEle(eventReader, entry),
                sense = sense(eventReader, entry),
            )
        }
    }

    private fun entSeq(eventReader: XMLEventReader) = eventReader.tag(QName("ent_seq"), "ent_seq") {
        JMdictXmlElement.EntSeq(eventReader.text(it))
    }

    private fun kEle(eventReader: XMLEventReader) = eventReader.simpleTagList(QName("k_ele"), "k_ele") {
        JMdictXmlElement.KEle(
            keb = eventReader.tag(QName("keb"), "keb") {
                JMdictXmlElement.Keb(eventReader.text(it))
            },
            keInf = eventReader.simpleTagList(QName("ke_inf"), "ke_inf") {
                JMdictXmlElement.KeInf(eventReader.text(it))
            },
            kePri = eventReader.simpleTagList(QName("ke_pri"), "ke_pri") {
                JMdictXmlElement.KePri(eventReader.text(it))
            },
        )
    }

    private fun rEle(eventReader: XMLEventReader, parent: StartElement) = eventReader.nonEmptyTagList(parent, QName("r_ele"), "r_ele") {
        JMdictXmlElement.REle(
            reb = eventReader.tag(QName("reb"), "reb") {
                JMdictXmlElement.Reb(eventReader.text(it))
            },
            reNokanji = eventReader.maybeTag(QName("re_nokanji"), "re_nokanji") {
                JMdictXmlElement.ReNokanji(eventReader.maybeText(it))
            },
            reRestr = eventReader.simpleTagList(QName("re_restr"), "re_restr") {
                JMdictXmlElement.ReRestr(eventReader.text(it))
            },
            reInf = eventReader.simpleTagList(QName("re_inf"), "re_inf") {
                JMdictXmlElement.ReInf(eventReader.text(it))
            },
            rePri = eventReader.simpleTagList(QName("re_pri"), "re_pri") {
                JMdictXmlElement.RePri(eventReader.text(it))
            },
        )
    }

    private fun sense(eventReader: XMLEventReader, parent: StartElement) = eventReader.nonEmptyTagList(parent, QName("sense"), "sense") {
        JMdictXmlElement.Sense(
            stagk = eventReader.simpleTagList(QName("stagk"), "stagk") {
                JMdictXmlElement.Stagk(eventReader.text(it))
            },
            stagr = eventReader.simpleTagList(QName("stagr"), "stagr") {
                JMdictXmlElement.Stagr(eventReader.text(it))
            },
            pos = eventReader.simpleTagList(QName("pos"), "pos") {
                JMdictXmlElement.Pos(eventReader.text(it))
            },
            xref = eventReader.simpleTagList(QName("xref"), "xref") {
                JMdictXmlElement.Xref(eventReader.text(it))
            },
            ant = eventReader.simpleTagList(QName("ant"), "ant") {
                JMdictXmlElement.Ant(eventReader.text(it))
            },
            field = eventReader.simpleTagList(QName("field"), "field") {
                JMdictXmlElement.Field(eventReader.text(it))
            },
            misc = eventReader.simpleTagList(QName("misc"), "misc") {
                JMdictXmlElement.Misc(eventReader.text(it))
            },
            sInf = eventReader.simpleTagList(QName("s_inf"), "s_inf") {
                JMdictXmlElement.SInf(eventReader.text(it))
            },
            lsource = eventReader.simpleTagList(QName("lsource"), "lsource") {
                lsource(it, eventReader)
            },
            dial = eventReader.simpleTagList(QName("dial"), "dial") {
                JMdictXmlElement.Dial(eventReader.text(it))
            },
            gloss = eventReader.simpleTagList(QName("gloss"), "gloss") {
                gloss(it, eventReader)
            },
            example = eventReader.simpleTagList(QName("example"), "example") {
                example(it, eventReader)
            },
        )
    }

    private fun lsource(
        it: StartElement,
        eventReader: XMLEventReader
    ) = JMdictXmlElement.Lsource(
        lang = it.attrString(QName(XMLConstants.XML_NS_URI, "lang", "xml")) ?: "eng",
        lsType = it.attrEnum(
            QName("ls_type"),
            JMdictXmlElement.LsType.values(),
            JMdictXmlElement.LsType.Companion::fromString
        ) ?: JMdictXmlElement.LsType.FULL,
        lsWasei = it.attrString(QName("ls_wasei")) == "y",
        text = eventReader.maybeCharacters(it)?.data?.trim(),
    )

    private fun gloss(
        it: StartElement,
        eventReader: XMLEventReader
    ) = JMdictXmlElement.Gloss(
        lang = it.attrString(QName(XMLConstants.XML_NS_URI, "lang", "xml")) ?: "eng",
        gGend = it.attrString(QName("g_gend")),
        gType = it.attrEnum(
            QName("g_type"),
            JMdictXmlElement.GType.values(),
            JMdictXmlElement.GType.Companion::fromString
        ),
        text = eventReader.maybeText(it),
    )

    private fun example(
        it: StartElement,
        eventReader: XMLEventReader
    ) = JMdictXmlElement.Example(
        source = eventReader.tag(QName("ex_srce"), "Example source") {
            exampleSource(it, eventReader)
        },
        text = eventReader.tag(QName("ex_text"), "Example text") {
            eventReader.text(it).trim()
        },
        sentences = eventReader.nonEmptyTagList(it, QName("ex_sent"), "Example sentences") {
            exampleSentence(it, eventReader)
        },
    )

    private fun exampleSource(
        it: StartElement,
        eventReader: XMLEventReader
    ) = JMdictXmlElement.ExampleSource(
        type = it.attrEnum(
            QName("exsrc_type"),
            JMdictXmlElement.ExampleSourceType.values(),
            JMdictXmlElement.ExampleSourceType.Companion::fromString
        ) ?: throw ParsingException.MissingRequiredAttribute(it, QName("exsrc_type")),
        value = eventReader.text(it),
    )

    private fun exampleSentence(
        it: StartElement,
        eventReader: XMLEventReader
    ) = JMdictXmlElement.ExampleSentence(
        lang = it.attrString(QName(XMLConstants.XML_NS_URI, "lang", "xml"))?.trim() ?: "eng",
        text = eventReader.text(it).trim(),
    )
}
