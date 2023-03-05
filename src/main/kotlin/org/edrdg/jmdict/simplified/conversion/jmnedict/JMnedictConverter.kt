package org.edrdg.jmdict.simplified.conversion.jmnedict

import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.jmnedict.JMnedictXmlElement
import org.edrdg.jmdict.simplified.parsing.Metadata

class JMnedictConverter : Converter<JMnedictXmlElement.Entry, JMnedictJsonElement.Word, JMdictMetadata>() {
    override fun entity(value: String): String? {
        require(metadata != null) {
            "Metadata must be set"
        }
        return metadata!!.entities.entries.find { it.value == value }?.key
    }

    override fun convert(xmlEntry: JMnedictXmlElement.Entry) = JMnedictJsonElement.Word(
        id = xmlEntry.entSeq.text,
        kanji = xmlEntry.kEle.map { kanji(it, xmlEntry.entSeq.text) },
        kana = xmlEntry.rEle.map { kana(it, xmlEntry.entSeq.text) },
        translation = translations(xmlEntry.trans, xmlEntry.entSeq.text)
    )

    private fun kanji(kEle: JMnedictXmlElement.KEle, entSeq: String) = JMnedictJsonElement.Kanji(
        text = kEle.keb.text,
        tags = kEle.keInf.map { entityToTag(it.text, entSeq) }
    )

    private fun kana(rEle: JMnedictXmlElement.REle, entSeq: String) = JMnedictJsonElement.Kana(
        text = rEle.reb.text,
        tags = rEle.reInf.map { entityToTag(it.text, entSeq) },
        appliesToKanji = when {
            rEle.reRestr.isEmpty() -> listOf("*")
            else -> rEle.reRestr.map { it.text }
        }
    )

    private fun translations(
        translations: List<JMnedictXmlElement.Trans>,
        entSeq: String,
    ): List<JMnedictJsonElement.Translation> {
        return translations.map { trans ->
            JMnedictJsonElement.Translation(
                type = trans.nameType.map { entityToTag(it.text, entSeq) },
                related = trans.xref.map { xref(it.text, "xref", entSeq) },
                translation = trans.transDet.map {
                    JMnedictJsonElement.TranslationText(
                        lang = it.lang,
                        text = it.text,
                    )
                }
            )
        }
    }
}
