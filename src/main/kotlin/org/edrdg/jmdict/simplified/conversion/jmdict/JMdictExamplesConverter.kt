package org.edrdg.jmdict.simplified.conversion.jmdict

import org.edrdg.jmdict.simplified.parsing.jmdict.JMdictXmlElement

/**
 * A version of converter for JMdict file with additional example sentences.
 */
class JMdictExamplesConverter : JMdictBaseConverter<JMdictJsonElement.WordWithExamples, JMdictJsonElement.SenseWithExamples>() {
    override fun convert(xmlEntry: JMdictXmlElement.Entry) = JMdictJsonElement.WordWithExamples(
        id = xmlEntry.entSeq.text,
        kanji = xmlEntry.kEle.map { kanji(it, xmlEntry.entSeq.text) },
        kana = xmlEntry.rEle.map { kana(it, xmlEntry.entSeq.text) },
        sense = senses(xmlEntry.sense, xmlEntry.entSeq.text)
    )

    private fun senses(senses: List<JMdictXmlElement.Sense>, entSeq: String): List<JMdictJsonElement.SenseWithExamples> {
        return senses.mapIndexed { i, sense ->
            JMdictJsonElement.SenseWithExamples(
                partOfSpeech = lastPartOfSpeech(senses, entSeq, i),
                appliesToKanji = appliesToKanji(sense),
                appliesToKana = appliesToKana(sense),
                related = sense.xref.map { xref(it.text, "xref", entSeq) },
                antonym = sense.ant.map { xref(it.text, "ant", entSeq) },
                field = sense.field.map { entityToTag(it.text, entSeq) },
                dialect = sense.dial.map { entityToTag(it.text, entSeq) },
                misc = sense.misc.map { entityToTag(it.text, entSeq) },
                info = sense.sInf.map { it.text },
                languageSource = languageSource(sense),
                gloss = gloss(sense, entSeq),
                examples = sense.example.map {
                    JMdictJsonElement.Example(
                        source = JMdictJsonElement.ExampleSource(
                            type = when (it.source.type) {
                                JMdictXmlElement.ExampleSourceType.TAT -> JMdictJsonElement.ExampleSourceType.TATOEBA
                            },
                            value = it.source.value,
                        ),
                        text = it.text,
                        sentences = it.sentences.map {
                            JMdictJsonElement.ExampleSentence(
                                land = it.lang,
                                text = it.text,
                            )
                        },
                    )
                },
            )
        }
    }
}
