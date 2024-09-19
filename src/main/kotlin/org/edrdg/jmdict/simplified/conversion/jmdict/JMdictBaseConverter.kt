package org.edrdg.jmdict.simplified.conversion.jmdict

import org.edrdg.jmdict.simplified.conversion.CommonJsonElement
import org.edrdg.jmdict.simplified.conversion.ConversionException
import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.conversion.OutputDictionaryEntry
import org.edrdg.jmdict.simplified.parsing.JMdictMetadata
import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement

/**
 * We have 2 versions of JMdict:
 *
 * 1. The main version, which has multiple languages
 * 2. The version with examples, which is only English
 *
 * This class contains common parts of both.
 */
abstract class JMdictBaseConverter<O : OutputDictionaryEntry<O>, S> : Converter<JMdictXmlElement.Entry, O, JMdictMetadata>() {
    override fun entity(value: String): String? {
        require(metadata != null) {
            "Metadata must be set"
        }
        return metadata!!.entities.entries.find { it.value == value }?.key
    }

    private val commonIndicators = listOf("news1", "ichi1", "spec1", "spec2", "gai1")

    protected fun kanji(kEle: JMdictXmlElement.KEle, entSeq: String) = JMdictJsonElement.Kanji(
        common = kEle.kePri.any { commonIndicators.contains(it.text) },
        text = kEle.keb.text,
        tags = kEle.keInf.map { entityToTag(it.text, entSeq) }
    )

    protected fun kana(rEle: JMdictXmlElement.REle, entSeq: String) = JMdictJsonElement.Kana(
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

    protected fun lastPartOfSpeech(
        senses: List<JMdictXmlElement.Sense>,
        entSeq: String,
        index: Int,
    ): List<CommonJsonElement.Tag> {
        val lastSenseWithPartOfSpeech = senses.take(index + 1).lastOrNull { it.pos.isNotEmpty() }
        val lastPartOfSpeech = lastSenseWithPartOfSpeech?.pos ?: throw ConversionException(
            entSeq,
            "No part-of-speech (<pos>) found in ${index + 1} first <sense> tags"
        )
        return lastPartOfSpeech.map { entityToTag(it.text, entSeq) }
    }

    protected fun appliesToKanji(sense: JMdictXmlElement.Sense) =
        sense.stagk
            .filter { it.text.trim().isNotEmpty() }
            .map { it.text }
            .ifEmpty { listOf("*") }

    protected fun appliesToKana(sense: JMdictXmlElement.Sense) =
        sense.stagr
            .filter { it.text.trim().isNotEmpty() }
            .map { it.text }
            .ifEmpty { listOf("*") }

    protected fun languageSource(sense: JMdictXmlElement.Sense) = sense.lsource.map {
        JMdictJsonElement.LanguageSource(
            lang = it.lang,
            full = it.lsType == JMdictXmlElement.LsType.FULL,
            wasei = it.lsWasei,
            text = it.text,
        )
    }

    protected fun gloss(sense: JMdictXmlElement.Sense, entSeq: String) = sense.gloss.map {
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
}
