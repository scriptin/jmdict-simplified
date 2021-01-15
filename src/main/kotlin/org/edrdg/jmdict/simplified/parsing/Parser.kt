package org.edrdg.jmdict.simplified.parsing

import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.EntityDeclaration
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent

object Parser {
    fun parseMetadata(eventReader: XMLEventReader): Metadata {
        eventReader.skip(
            setOf(
                XMLEvent.START_DOCUMENT,
                XMLEvent.SPACE
            )
        )

        val revisions = eventReader.commentList("revisions comments").map {
            it.text.split("\n").first().trim().split(" ").last()
        }

        @Suppress("UNCHECKED_CAST")
        val entities = (eventReader.dtd("DTD declaration").entities as List<EntityDeclaration>).map {
            it.name to it.replacementText
        }.toMap()

        val date = eventReader.comment("JMdict creation date comment")
            .text.split(":").last().trim()

        return Metadata(revisions, entities, date)
    }

    fun hasNextEntry(eventReader: XMLEventReader): Boolean {
        eventReader.skipSpace()
        val next = eventReader.peek()
        return next.isStartElement && next.asStartElement().name.localPart == "entry"
    }

    fun parseEntry(eventReader: XMLEventReader): JMdictTag.Entry {
        return eventReader.tag(QName("entry"), "entry") { entry ->
            JMdictTag.Entry(
                entSeq = entSeq(eventReader),
                kEle = eventReader.tagList(QName("k_ele")) {
                    kEle(eventReader)
                },
                rEle = eventReader.nonEmptyTagList(entry, QName("r_ele")) {
                    rEle(eventReader)
                },
                sense = eventReader.nonEmptyTagList(entry, QName("sense")) {
                    sense(eventReader)
                },
            )
        }
    }

    private fun entSeq(eventReader: XMLEventReader) = eventReader.tag(QName("ent_seq"), "ent_seq") {
        JMdictTag.EntSeq(eventReader.text(it))
    }

    private fun kEle(eventReader: XMLEventReader) = eventReader.tag(QName("k_ele"), "k_ele") {
        JMdictTag.KEle(
            keb = eventReader.tag(QName("keb"), "keb") {
                JMdictTag.Keb(eventReader.text(it))
            },
            keInf = eventReader.simpleTagList(QName("ke_inf"), "ke_inf") {
                JMdictTag.KeInf(eventReader.text(it))
            },
            kePri = eventReader.simpleTagList(QName("ke_pri"), "ke_pri") {
                JMdictTag.KePri(eventReader.text(it))
            },
        )
    }

    private fun rEle(eventReader: XMLEventReader) = eventReader.tag(QName("r_ele"), "r_ele") {
        JMdictTag.REle(
            reb = eventReader.tag(QName("reb"), "reb") {
                JMdictTag.Reb(eventReader.text(it))
            },
            reNokanji = eventReader.maybeTag(QName("re_nokanji"), "re_nokanji") {
                JMdictTag.ReNokanji(eventReader.maybeText(it))
            },
            reRestr = eventReader.simpleTagList(QName("re_restr"), "re_restr") {
                JMdictTag.ReRestr(eventReader.text(it))
            },
            reInf = eventReader.simpleTagList(QName("re_inf"), "re_inf") {
                JMdictTag.ReInf(eventReader.text(it))
            },
            rePri = eventReader.simpleTagList(QName("re_pri"), "re_pri") {
                JMdictTag.RePri(eventReader.text(it))
            },
        )
    }

    private fun sense(eventReader: XMLEventReader) = eventReader.tag(QName("sense"), "sense") {
        JMdictTag.Sense(
            stagk = eventReader.simpleTagList(QName("stagk"), "stagk") {
                JMdictTag.Stagk(eventReader.text(it))
            },
            stagr = eventReader.simpleTagList(QName("stagr"), "stagr") {
                JMdictTag.Stagr(eventReader.text(it))
            },
            pos = eventReader.simpleTagList(QName("pos"), "pos") {
                JMdictTag.Pos(eventReader.text(it))
            },
            xref = eventReader.simpleTagList(QName("xref"), "xref") {
                JMdictTag.Xref(eventReader.text(it))
            },
            ant = eventReader.simpleTagList(QName("ant"), "ant") {
                JMdictTag.Ant(eventReader.text(it))
            },
            field = eventReader.simpleTagList(QName("field"), "field") {
                JMdictTag.Field(eventReader.text(it))
            },
            misc = eventReader.simpleTagList(QName("misc"), "misc") {
                JMdictTag.Misc(eventReader.text(it))
            },
            sInf = eventReader.simpleTagList(QName("s_inf"), "s_inf") {
                JMdictTag.SInf(eventReader.text(it))
            },
            lsource = eventReader.simpleTagList(QName("lsource"), "lsource") {
                lsource(it, eventReader)
            },
            dial = eventReader.simpleTagList(QName("dial"), "dial") {
                JMdictTag.Dial(eventReader.text(it))
            },
            gloss = eventReader.simpleTagList(QName("gloss"), "gloss") {
                gloss(it, eventReader)
            },
        )
    }

    private fun lsource(
        it: StartElement,
        eventReader: XMLEventReader
    ) = JMdictTag.Lsource(
        lang = it.attrString(QName("xml", "lang")) ?: "eng",
        lsType = it.attrEnum(
            QName("ls_type"),
            JMdictTag.LsType.values(),
            JMdictTag.LsType::fromString
        ) ?: JMdictTag.LsType.FULL,
        lsWasei = it.attrString(QName("ls_wasei")) == "y",
        text = eventReader.maybeCharacters(it)?.data?.trim(),
    )

    private fun gloss(
        it: StartElement,
        eventReader: XMLEventReader
    ) = JMdictTag.Gloss(
        lang = it.attrString(QName("xml", "lang")) ?: "eng",
        gGend = it.attrString(QName("g_gend")),
        gType = it.attrEnum(
            QName("g_type"),
            JMdictTag.GType.values(),
            JMdictTag.GType::fromString
        ),
        text = eventReader.maybeText(it),
    )
}
