package org.edrdg.jmdict.simplified.conversion

import org.edrdg.jmdict.simplified.parsing.JMdictTag
import org.edrdg.jmdict.simplified.parsing.Metadata

class Converter(metadata: Metadata) {
    private val entities = metadata.entities.entries.associate { (k, v) -> v to k }

    fun convertWord(xmlEntry: JMdictTag.Entry) = JMdictSimplified.Word(
        id = xmlEntry.entSeq.text,
        kanji = xmlEntry.kEle.map { kanji(it, xmlEntry.entSeq.text) },
        kana = xmlEntry.rEle.map { kana(it, xmlEntry.entSeq.text) },
        sense = senses(xmlEntry.sense, xmlEntry.entSeq.text)
    )

    private val commonIndicators = listOf("news1", "ichi1", "spec1", "spec2", "gai1")

    private fun kanji(kEle: JMdictTag.KEle, entSeq: String) = JMdictSimplified.Kanji(
        common = kEle.kePri.any { commonIndicators.contains(it.text) },
        text = kEle.keb.text,
        tags = kEle.keInf.map { entityToTag(it.text, entSeq) }
    )

    private fun kana(rEle: JMdictTag.REle, entSeq: String) = JMdictSimplified.Kana(
        common = rEle.rePri.any { commonIndicators.contains(it.text) },
        text = rEle.reb.text,
        tags = rEle.reInf.map { entityToTag(it.text, entSeq) },
        appliesToKanji = when {
            rEle.reNokanji != null && rEle.reRestr.isNotEmpty() -> throw Exception(
                "[ent_seq=$entSeq] <r_ele> contains a <re_nokanji/>, but also has a non-empty list of <re_restr>"
            )
            rEle.reNokanji != null -> emptyList()
            rEle.reRestr.isEmpty() -> listOf("*")
            else -> rEle.reRestr.map { it.text }
        }
    )

    private fun senses(senses: List<JMdictTag.Sense>, entSeq: String): List<JMdictSimplified.Sense> {
        return senses.mapIndexed { i, sense ->
            val lastSenseWithPartOfSpeech = senses.take(i + 1).lastOrNull { it.pos.isNotEmpty() }
            val lastPartOfSpeech = lastSenseWithPartOfSpeech?.pos ?: throw Exception(
                "[ent_seq=$entSeq] No part-of-speech (<pos>) found in ${i + 1} first <sense> tags"
            )
            JMdictSimplified.Sense(
                partOfSpeech = lastPartOfSpeech.map { entityToTag(it.text, entSeq) },
                appliesToKanji = sense.stagk.map { it.text },
                appliesToKana = sense.stagr.map { it.text },
                related = sense.xref.map { xref(it.text, entSeq) },
                antonym = sense.ant.map { xref(it.text, entSeq) },
                field = sense.field.map { entityToTag(it.text, entSeq) },
                dialect = sense.dial.map { entityToTag(it.text, entSeq) },
                info = sense.sInf.map { it.text },
                languageSource = sense.lsource.map {
                    JMdictSimplified.LanguageSource(
                        lang = it.lang,
                        full = it.lsType == JMdictTag.LsType.FULL,
                        wasei = it.lsWasei,
                        text = it.text,
                    )
                },
                gloss = sense.gloss.map {
                    JMdictSimplified.Gloss(
                        lang = it.lang,
                        gender = when (it.gGend?.toLowerCase()) {
                            "m", "mas", "masc", "mascul", "masculine" -> JMdictSimplified.Gender.MASCULINE
                            "f", "fem", "femin", "feminine" -> JMdictSimplified.Gender.FEMININE
                            "n", "neu", "neut", "neuter" -> JMdictSimplified.Gender.NEUTER
                            null -> null
                            else -> throw Exception("[ent_seq=$entSeq] unknown gender: ${it.gGend}")
                        },
                        type = when (it.gType) {
                            JMdictTag.GType.LIT -> JMdictSimplified.GlossType.LITERAL
                            JMdictTag.GType.FIG -> JMdictSimplified.GlossType.FIGURATIVE
                            JMdictTag.GType.EXPL -> JMdictSimplified.GlossType.EXPLANATION
                            null -> null
                        },
                        text = it.text,
                    )
                }
            )
        }
    }

    private fun entityToTag(text: String, entSeq: String) = JMdictSimplified.Tag(
        entities[text] ?: throw Exception(
            "[ent_seq=$entSeq] Following text doesn't match any entities from the original XML file: $text"
        )
    )

    private fun xref(text: String, entSeq: String): JMdictSimplified.Xref {
        val parts = text.split("・").map { it.trim() }
        return when (parts.size) {
            1 -> JMdictSimplified.Xref(parts[0], null, null)
            2 ->
                if (parts[1].matches("\\d+".toRegex()))
                    JMdictSimplified.Xref(parts[0], null, parts[1].toInt())
                else
                    JMdictSimplified.Xref(parts[0], parts[1], null)
            3 -> JMdictSimplified.Xref(parts[0], parts[1], parts[2].toInt())
            else -> throw Exception(
                "[ent_seq=$entSeq] Unexpected number of parts (${parts.size}) in xref (related/antonym): $text"
            )
        }
    }
}
