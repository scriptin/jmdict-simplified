package org.edrdg.jmdict.simplified.parsing

import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.EntityDeclaration
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
                entSeq = eventReader.tag(QName("ent_seq"), "ent_seq") {
                    JMdictTag.EntSeq(eventReader.characters(it).data.trim())
                },
                kEle = eventReader.tagList(QName("k_ele")) {
                    eventReader.tag(QName("k_ele"), "k_ele") {
                        JMdictTag.KEle(
                            keb = eventReader.tag(QName("keb"), "keb") {
                                JMdictTag.Keb(eventReader.characters(it).data.trim())
                            },
                            keInf = eventReader.simpleTagList(QName("ke_inf"), "ke_inf") {
                                JMdictTag.KeInf(eventReader.characters(it).data.trim())
                            },
                            kePri = eventReader.simpleTagList(QName("ke_pri"), "ke_pri") {
                                JMdictTag.KePri(eventReader.characters(it).data.trim())
                            }
                        )
                    }
                },
                rEle = eventReader.nonEmptyTagList(entry, QName("r_ele")) {
                    eventReader.tag(QName("r_ele"), "r_ele") {
                        JMdictTag.REle(
                            reb = eventReader.tag(QName("reb"), "reb") {
                                JMdictTag.Reb(eventReader.characters(it).data.trim())
                            },
                            reNokanji = eventReader.maybeTag(QName("re_nokanji"), "re_nokanji") {
                                JMdictTag.ReNokanji(eventReader.maybeCharacters(it)?.data?.trim())
                            },
                            reRestr = eventReader.simpleTagList(QName("re_restr"), "re_restr") {
                                JMdictTag.ReRestr(eventReader.characters(it).data.trim())
                            },
                            reInf = eventReader.simpleTagList(QName("re_inf"), "re_inf") {
                                JMdictTag.ReInf(eventReader.characters(it).data.trim())
                            },
                            rePri = eventReader.simpleTagList(QName("re_pri"), "re_pri") {
                                JMdictTag.RePri(eventReader.characters(it).data.trim())
                            }
                        )
                    }
                },
                sense = eventReader.nonEmptyTagList(entry, QName("sense")) {
                    eventReader.tag(QName("sense"), "sense") {
                        JMdictTag.Sense(
                            stagk = eventReader.simpleTagList(QName("stagk"), "stagk") {
                                JMdictTag.Stagk(eventReader.characters(it).data.trim())
                            },
                            stagr = eventReader.simpleTagList(QName("stagr"), "stagr") {
                                JMdictTag.Stagr(eventReader.characters(it).data.trim())
                            },
                            pos = eventReader.simpleTagList(QName("pos"), "pos") {
                                JMdictTag.Pos(eventReader.characters(it).data.trim())
                            },
                            xref = eventReader.simpleTagList(QName("xref"), "xref") {
                                JMdictTag.Xref(eventReader.characters(it).data.trim())
                            },
                            ant = eventReader.simpleTagList(QName("ant"), "ant") {
                                JMdictTag.Ant(eventReader.characters(it).data.trim())
                            },
                            field = eventReader.simpleTagList(QName("field"), "field") {
                                JMdictTag.Field(eventReader.characters(it).data.trim())
                            },
                            misc = eventReader.simpleTagList(QName("misc"), "misc") {
                                JMdictTag.Misc(eventReader.characters(it).data.trim())
                            },
                            sInf = eventReader.simpleTagList(QName("s_inf"), "s_inf") {
                                JMdictTag.SInf(eventReader.characters(it).data.trim())
                            },
                            lsource = eventReader.simpleTagList(QName("lsource"), "lsource") {
                                JMdictTag.Lsource(
                                    lang = it.attrString(QName("xml", "lang")) ?: "eng",
                                    lsType = it.attrEnum(
                                        QName("ls_type"),
                                        JMdictTag.LsType.values(),
                                        JMdictTag.LsType::fromString
                                    ) ?: JMdictTag.LsType.FULL,
                                    lsWasei = it.attrString(QName("ls_wasei")) == "y",
                                    text = eventReader.maybeCharacters(it)?.data?.trim()
                                )
                            },
                            dial = eventReader.simpleTagList(QName("dial"), "dial") {
                                JMdictTag.Dial(eventReader.characters(it).data.trim())
                            },
                            gloss = eventReader.simpleTagList(QName("gloss"), "gloss") {
                                JMdictTag.Gloss(
                                    lang = it.attrString(QName("xml", "lang")) ?: "eng",
                                    gGend = it.attrString(QName("g_gend")),
                                    gType = it.attrEnum(
                                        QName("g_type"),
                                        JMdictTag.GType.values(),
                                        JMdictTag.GType::fromString
                                    ),
                                    text = listOf(
                                        JMdictTag.GlossContent.PCData(eventReader.characters(it).data.trim())
                                    )
                                )
                            }
                        )
                    }
                }
            )
        }
    }
}
