package org.edrdg.jmdict.simplified.conversion.jmdict

import org.edrdg.jmdict.simplified.conversion.ConversionException
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement
import org.edrdg.jmdict.simplified.parsing.Metadata

class JMdictConverter(metadata: Metadata) : Converter<JMdictXmlElement.Entry, JMdictJsonElement.Word>() {
    override val entities = metadata.entities.entries.associate { (k, v) -> v to k }

    override fun convertWord(xmlEntry: JMdictXmlElement.Entry) = JMdictJsonElement.Word(
        id = xmlEntry.entSeq.text,
        kanji = xmlEntry.kEle.map { kanji(it, xmlEntry.entSeq.text) },
        kana = xmlEntry.rEle.map { kana(it, xmlEntry.entSeq.text) },
        sense = senses(xmlEntry.sense, xmlEntry.entSeq.text)
    )

    private val commonIndicators = listOf("news1", "ichi1", "spec1", "spec2", "gai1")

    private fun kanji(kEle: JMdictXmlElement.KEle, entSeq: String) = JMdictJsonElement.Kanji(
        common = kEle.kePri.any { commonIndicators.contains(it.text) },
        text = kEle.keb.text,
        tags = kEle.keInf.map { entityToTag(it.text, entSeq) }
    )

    private fun kana(rEle: JMdictXmlElement.REle, entSeq: String) = JMdictJsonElement.Kana(
        common = rEle.rePri.any { commonIndicators.contains(it.text) },
        text = rEle.reb.text,
        tags = rEle.reInf.map { entityToTag(it.text, entSeq) },
        appliesToKanji = when {
            rEle.reNokanji != null && rEle.reRestr.isNotEmpty() -> throw ConversionException(
                entSeq,
                "<r_ele> contains a <re_nokanji/>, but also has a non-empty list of <re_restr>"
            )

            rEle.reNokanji != null -> emptyList()
            rEle.reRestr.isEmpty() -> listOf("*")
            else -> rEle.reRestr.map { it.text }
        }
    )

    private fun senses(senses: List<JMdictXmlElement.Sense>, entSeq: String): List<JMdictJsonElement.Sense> {
        return senses.mapIndexed { i, sense ->
            val lastSenseWithPartOfSpeech = senses.take(i + 1).lastOrNull { it.pos.isNotEmpty() }
            val lastPartOfSpeech = lastSenseWithPartOfSpeech?.pos ?: throw ConversionException(
                entSeq,
                "No part-of-speech (<pos>) found in ${i + 1} first <sense> tags"
            )
            JMdictJsonElement.Sense(
                partOfSpeech = lastPartOfSpeech.map { entityToTag(it.text, entSeq) },
                appliesToKanji = sense.stagk.map { it.text },
                appliesToKana = sense.stagr.map { it.text },
                related = sense.xref.map { xref(it.text, "xref", entSeq) },
                antonym = sense.ant.map { xref(it.text, "ant", entSeq) },
                field = sense.field.map { entityToTag(it.text, entSeq) },
                dialect = sense.dial.map { entityToTag(it.text, entSeq) },
                info = sense.sInf.map { it.text },
                languageSource = sense.lsource.map {
                    JMdictJsonElement.LanguageSource(
                        lang = it.lang,
                        full = it.lsType == JMdictXmlElement.LsType.FULL,
                        wasei = it.lsWasei,
                        text = it.text,
                    )
                },
                gloss = sense.gloss.map {
                    JMdictJsonElement.Gloss(
                        lang = it.lang,
                        gender = when (it.gGend?.lowercase()) {
                            "m", "mas", "masc", "mascul", "masculine" -> JMdictJsonElement.Gender.MASCULINE
                            "f", "fem", "femin", "feminine" -> JMdictJsonElement.Gender.FEMININE
                            "n", "neu", "neut", "neuter" -> JMdictJsonElement.Gender.NEUTER
                            null -> null
                            else -> throw ConversionException(entSeq, "Unknown gender: ${it.gGend}")
                        },
                        type = when (it.gType) {
                            JMdictXmlElement.GType.LIT -> JMdictJsonElement.GlossType.LITERAL
                            JMdictXmlElement.GType.FIG -> JMdictJsonElement.GlossType.FIGURATIVE
                            JMdictXmlElement.GType.EXPL -> JMdictJsonElement.GlossType.EXPLANATION
                            JMdictXmlElement.GType.TM -> JMdictJsonElement.GlossType.TRADEMARK
                            null -> null
                        },
                        // There seems to be an issue in the original XML where one translation is empty.
                        // This is a quick fix for that, but <gloss> tags should never be empty.
                        text = it.text ?: "",
                    )
                }
            )
        }
    }
}
