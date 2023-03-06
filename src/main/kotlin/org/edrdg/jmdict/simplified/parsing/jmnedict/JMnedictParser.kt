package org.edrdg.jmdict.simplified.parsing.jmnedict

import org.edrdg.jmdict.simplified.parsing.*
import org.edrdg.jmdict.simplified.parsing.dtd
import org.edrdg.jmdict.simplified.parsing.skip
import javax.xml.XMLConstants
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent

object JMnedictParser : Parser<JMnedictXmlElement.Entry, JMdictMetadata> {
    override fun parseMetadata(eventReader: XMLEventReader): JMdictMetadata {
        eventReader.skip(
            setOf(
                XMLEvent.START_DOCUMENT,
                XMLEvent.SPACE,
            )
        )

        val dtd = eventReader.dtd("DTD declaration")

        // Unlike JMdict, revisions are added as comments *inside* DTD, not before.
        val revisionCommentRegex = "<!-- Rev (\\d+\\.\\d+)".toRegex()
        val revisions = revisionCommentRegex.findAll(dtd.documentTypeDeclaration)
            .map { it.groupValues[1] }
            .toList()

        val entities = dtd.entities.associate {
            it.name to it.replacementText
        }

        val date = eventReader.comment("JMnedict creation date comment")
            .text.split(":").last().trim()

        return JMdictMetadata(revisions, entities, date) // same as JMdict, thus reusing
    }

    override fun hasNextEntry(eventReader: XMLEventReader): Boolean {
        eventReader.skipSpace()
        val next = eventReader.peek()
        return next.isStartElement && next.asStartElement().name.localPart == "entry"
    }

    override fun parseEntry(eventReader: XMLEventReader): JMnedictXmlElement.Entry {
        return eventReader.tag(QName("entry"), "entry") { entry ->
            JMnedictXmlElement.Entry(
                entSeq = entSeq(eventReader),
                kEle = kEle(eventReader),
                rEle = rEle(eventReader, entry),
                trans = trans(eventReader, entry),
            )
        }
    }

    private fun entSeq(eventReader: XMLEventReader) = eventReader.tag(QName("ent_seq"), "ent_seq") {
        JMnedictXmlElement.EntSeq(eventReader.text(it))
    }

    private fun kEle(eventReader: XMLEventReader) = eventReader.simpleTagList(QName("k_ele"), "k_ele") {
        JMnedictXmlElement.KEle(
            keb = eventReader.tag(QName("keb"), "keb") {
                JMnedictXmlElement.Keb(eventReader.text(it))
            },
            keInf = eventReader.simpleTagList(QName("ke_inf"), "ke_inf") {
                JMnedictXmlElement.KeInf(eventReader.text(it))
            },
            kePri = eventReader.simpleTagList(QName("ke_pri"), "ke_pri") {
                JMnedictXmlElement.KePri(eventReader.text(it))
            },
        )
    }

    private fun rEle(eventReader: XMLEventReader, parent: StartElement) = eventReader.nonEmptyTagList(parent, QName("r_ele"), "r_ele") {
        JMnedictXmlElement.REle(
            reb = eventReader.tag(QName("reb"), "reb") {
                JMnedictXmlElement.Reb(eventReader.text(it))
            },
            reRestr = eventReader.simpleTagList(QName("re_restr"), "re_restr") {
                JMnedictXmlElement.ReRestr(eventReader.text(it))
            },
            reInf = eventReader.simpleTagList(QName("re_inf"), "re_inf") {
                JMnedictXmlElement.ReInf(eventReader.text(it))
            },
            rePri = eventReader.simpleTagList(QName("re_pri"), "re_pri") {
                JMnedictXmlElement.RePri(eventReader.text(it))
            },
        )
    }

    private fun trans(eventReader: XMLEventReader, parent: StartElement) = eventReader.nonEmptyTagList(parent, QName("trans"), "trans") {
        JMnedictXmlElement.Trans(
            nameType = eventReader.simpleTagList(QName("name_type"), "name_type") {
                JMnedictXmlElement.NameType(eventReader.text(it))
            },
            xref = eventReader.simpleTagList(QName("xref"), "xref") {
                JMnedictXmlElement.Xref(eventReader.text(it))
            },
            transDet = eventReader.simpleTagList(QName("trans_det"), "trans_det") {
                transDet(it, eventReader)
            },
        )
    }

    private fun transDet(
        it: StartElement,
        eventReader: XMLEventReader,
    ) = JMnedictXmlElement.TransDet(
        lang = it.attrString(QName(XMLConstants.XML_NS_URI, "lang", "xml")) ?: "eng",
        text = eventReader.text(it),
    )
}
