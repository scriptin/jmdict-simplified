package org.edrdg.jmdict.simplified.conversion.kanjidic

import org.edrdg.jmdict.simplified.conversion.Converter
import org.edrdg.jmdict.simplified.parsing.Kanjidic2Metadata
import org.edrdg.jmdict.simplified.parsing.kanjidic.Kanjidic2XmlElement

class Kanjidic2Converter : Converter<Kanjidic2XmlElement.Character, Kanjidic2JsonElement.Character, Kanjidic2Metadata>() {
    override fun entity(value: String) = ""

    override fun convert(xmlEntry: Kanjidic2XmlElement.Character) = Kanjidic2JsonElement.Character(
        literal = xmlEntry.literal.text,
        codepoints = xmlEntry.codepoint.cpValues.map { codepoint(it) },
        radicals = xmlEntry.radical.radValues.map { radical(it) },
        misc = misc(xmlEntry.misc),
        dictionaryReferences = xmlEntry.dicNumber?.dicRefs?.map { dictionaryReference(it) }.orEmpty(),
        queryCodes = xmlEntry.queryCode?.qCodes?.map { queryCode(it) }.orEmpty(),
        readingMeaning = xmlEntry.readingMeaning?.let { readingMeaning(it) },
    )

    private fun codepoint(cpValue: Kanjidic2XmlElement.CpValue) = Kanjidic2JsonElement.Codepoint(
        type = when (cpValue.cpType) {
            Kanjidic2XmlElement.CpType.JIS208 -> Kanjidic2JsonElement.CodepointType.JIS208
            Kanjidic2XmlElement.CpType.JIS212 -> Kanjidic2JsonElement.CodepointType.JIS212
            Kanjidic2XmlElement.CpType.JIS213 -> Kanjidic2JsonElement.CodepointType.JIS213
            Kanjidic2XmlElement.CpType.UCS -> Kanjidic2JsonElement.CodepointType.UCS
        },
        value = cpValue.text,
    )

    private fun radical(radValue: Kanjidic2XmlElement.RadValue) = Kanjidic2JsonElement.Radical(
        type = when (radValue.radType) {
            Kanjidic2XmlElement.RadType.CLASSICAL -> Kanjidic2JsonElement.RadicalType.CLASSICAL
            Kanjidic2XmlElement.RadType.NELSON_C -> Kanjidic2JsonElement.RadicalType.NELSON_C
        },
        value = radValue.value,
    )

    private fun misc(misc: Kanjidic2XmlElement.Misc) = Kanjidic2JsonElement.Misc(
        grade = misc.grade?.value,
        strokeCounts = misc.strokeCounts.map { it.value },
        variants = misc.variants.map { variant ->
            Kanjidic2JsonElement.Variant(
                type = when (variant.varType) {
                    Kanjidic2XmlElement.VarType.JIS208 -> Kanjidic2JsonElement.VariantType.JIS208
                    Kanjidic2XmlElement.VarType.JIS212 -> Kanjidic2JsonElement.VariantType.JIS212
                    Kanjidic2XmlElement.VarType.JIS213 -> Kanjidic2JsonElement.VariantType.JIS213
                    Kanjidic2XmlElement.VarType.DEROO -> Kanjidic2JsonElement.VariantType.DEROO
                    Kanjidic2XmlElement.VarType.NJECD -> Kanjidic2JsonElement.VariantType.NJECD
                    Kanjidic2XmlElement.VarType.S_H -> Kanjidic2JsonElement.VariantType.S_H
                    Kanjidic2XmlElement.VarType.NELSON_C -> Kanjidic2JsonElement.VariantType.NELSON_C
                    Kanjidic2XmlElement.VarType.ONEILL -> Kanjidic2JsonElement.VariantType.ONEILL
                    Kanjidic2XmlElement.VarType.UCS -> Kanjidic2JsonElement.VariantType.UCS
                },
                value = variant.text,
            )
        },
        frequency = misc.freq?.value,
        radicalNames = misc.radNames.map { it.text },
        jlptLevel = misc.jlpt?.value,
    )

    private fun dictionaryReference(dicRef: Kanjidic2XmlElement.DicRef): Kanjidic2JsonElement.DictionaryReference {
        val type = when (dicRef.drType) {
            Kanjidic2XmlElement.DRType.NELSON_C -> Kanjidic2JsonElement.DictionaryReferenceType.NELSON_C
            Kanjidic2XmlElement.DRType.NELSON_N -> Kanjidic2JsonElement.DictionaryReferenceType.NELSON_N
            Kanjidic2XmlElement.DRType.HALPERN_NJECD -> Kanjidic2JsonElement.DictionaryReferenceType.HALPERN_NJECD
            Kanjidic2XmlElement.DRType.HALPERN_KKD -> Kanjidic2JsonElement.DictionaryReferenceType.HALPERN_KKD
            Kanjidic2XmlElement.DRType.HALPERN_KKLD -> Kanjidic2JsonElement.DictionaryReferenceType.HALPERN_KKLD
            Kanjidic2XmlElement.DRType.HALPERN_KKLD_2ED -> Kanjidic2JsonElement.DictionaryReferenceType.HALPERN_KKLD_2ED
            Kanjidic2XmlElement.DRType.HEISIG -> Kanjidic2JsonElement.DictionaryReferenceType.HEISIG
            Kanjidic2XmlElement.DRType.HEISIG6 -> Kanjidic2JsonElement.DictionaryReferenceType.HEISIG6
            Kanjidic2XmlElement.DRType.GAKKEN -> Kanjidic2JsonElement.DictionaryReferenceType.GAKKEN
            Kanjidic2XmlElement.DRType.ONEILL_NAMES -> Kanjidic2JsonElement.DictionaryReferenceType.ONEILL_NAMES
            Kanjidic2XmlElement.DRType.ONEILL_KK -> Kanjidic2JsonElement.DictionaryReferenceType.ONEILL_KK
            Kanjidic2XmlElement.DRType.MORO -> Kanjidic2JsonElement.DictionaryReferenceType.MORO
            Kanjidic2XmlElement.DRType.HENSHALL -> Kanjidic2JsonElement.DictionaryReferenceType.HENSHALL
            Kanjidic2XmlElement.DRType.SH_KK -> Kanjidic2JsonElement.DictionaryReferenceType.SH_KK
            Kanjidic2XmlElement.DRType.SH_KK2 -> Kanjidic2JsonElement.DictionaryReferenceType.SH_KK2
            Kanjidic2XmlElement.DRType.SAKADE -> Kanjidic2JsonElement.DictionaryReferenceType.SAKADE
            Kanjidic2XmlElement.DRType.JF_CARDS -> Kanjidic2JsonElement.DictionaryReferenceType.JF_CARDS
            Kanjidic2XmlElement.DRType.HENSHALL3 -> Kanjidic2JsonElement.DictionaryReferenceType.HENSHALL3
            Kanjidic2XmlElement.DRType.TUTT_CARDS -> Kanjidic2JsonElement.DictionaryReferenceType.TUTT_CARDS
            Kanjidic2XmlElement.DRType.CROWLEY -> Kanjidic2JsonElement.DictionaryReferenceType.CROWLEY
            Kanjidic2XmlElement.DRType.KANJI_IN_CONTEXT -> Kanjidic2JsonElement.DictionaryReferenceType.KANJI_IN_CONTEXT
            Kanjidic2XmlElement.DRType.BUSY_PEOPLE -> Kanjidic2JsonElement.DictionaryReferenceType.BUSY_PEOPLE
            Kanjidic2XmlElement.DRType.KODANSHA_COMPACT -> Kanjidic2JsonElement.DictionaryReferenceType.KODANSHA_COMPACT
            Kanjidic2XmlElement.DRType.MANIETTE -> Kanjidic2JsonElement.DictionaryReferenceType.MANIETTE
        }
        return Kanjidic2JsonElement.DictionaryReference(
            type = type,
            morohashi = when (type) {
                Kanjidic2JsonElement.DictionaryReferenceType.MORO -> morohashi(dicRef)
                else -> {
                    if (dicRef.mVol != null || dicRef.mPage != null)
                        throw Error("mVol and mPage must be null when type == $type")
                    null
                }
            },
            value = dicRef.value,
        )
    }

    private fun morohashi(dicRef: Kanjidic2XmlElement.DicRef) =
        if (dicRef.mVol == null && dicRef.mPage == null)
            null
        else if (dicRef.mVol != null && dicRef.mPage != null)
            Kanjidic2JsonElement.Morohashi(
                volume = dicRef.mVol,
                page = dicRef.mPage,
            )
        else if (dicRef.mVol == null)
            throw Error("mVol must be non-null when type == ${Kanjidic2JsonElement.DictionaryReferenceType.MORO.value} and mPage is set")
        else
            throw Error("mPage must be non-null when type == ${Kanjidic2JsonElement.DictionaryReferenceType.MORO.value} and mVol is set")

    private fun queryCode(qCode: Kanjidic2XmlElement.QCode): Kanjidic2JsonElement.QueryCode {
        val type = when (qCode.qcType) {
            Kanjidic2XmlElement.QCType.SKIP -> Kanjidic2JsonElement.QueryCodeType.SKIP
            Kanjidic2XmlElement.QCType.SH_DESC -> Kanjidic2JsonElement.QueryCodeType.SH_DESC
            Kanjidic2XmlElement.QCType.FOUR_CORNER -> Kanjidic2JsonElement.QueryCodeType.FOUR_CORNER
            Kanjidic2XmlElement.QCType.DEROO -> Kanjidic2JsonElement.QueryCodeType.DEROO
            Kanjidic2XmlElement.QCType.MISCLASS -> Kanjidic2JsonElement.QueryCodeType.MISCLASS
        }
        return Kanjidic2JsonElement.QueryCode(
            type = type,
            skipMisclassification = when (type) {
                Kanjidic2JsonElement.QueryCodeType.SKIP -> when (qCode.skipMisclass) {
                    Kanjidic2XmlElement.SkipMisclass.POSN -> Kanjidic2JsonElement.SkipMisclassification.POSN
                    Kanjidic2XmlElement.SkipMisclass.STROKE_COUNT -> Kanjidic2JsonElement.SkipMisclassification.STROKE_COUNT
                    Kanjidic2XmlElement.SkipMisclass.STROKE_AND_POSN -> Kanjidic2JsonElement.SkipMisclassification.STROKE_AND_POSN
                    Kanjidic2XmlElement.SkipMisclass.STROKE_DIFF -> Kanjidic2JsonElement.SkipMisclassification.STROKE_DIFF
                    else -> null
                }
                else -> {
                    if (qCode.skipMisclass != null)
                        throw Error("skipMisclass must be null when qcType != $type")
                    null
                }
            },
            value = qCode.text,
        )
    }

    private fun readingMeaning(
        readingMeaning: Kanjidic2XmlElement.ReadingMeaning,
    ) = Kanjidic2JsonElement.ReadingMeaning(
        groups = readingMeaning.rmGroups.map { group ->
            Kanjidic2JsonElement.ReadingMeaningGroup(
                readings = group.readings.map { reading ->
                    Kanjidic2JsonElement.Reading(
                        type = when (reading.rType) {
                            Kanjidic2XmlElement.RType.PINYIN -> Kanjidic2JsonElement.ReadingType.PINYIN
                            Kanjidic2XmlElement.RType.KOREAN_R -> Kanjidic2JsonElement.ReadingType.KOREAN_R
                            Kanjidic2XmlElement.RType.KOREAN_H -> Kanjidic2JsonElement.ReadingType.KOREAN_H
                            Kanjidic2XmlElement.RType.VIETNAM -> Kanjidic2JsonElement.ReadingType.VIETNAM
                            Kanjidic2XmlElement.RType.JA_ON -> Kanjidic2JsonElement.ReadingType.JA_ON
                            Kanjidic2XmlElement.RType.JA_KUN -> Kanjidic2JsonElement.ReadingType.JA_KUN
                        },
                        onType = reading.onType,
                        status = reading.rStatus,
                        value = reading.text,
                    )
                },
                meanings = group.meanings.map { meaning ->
                    Kanjidic2JsonElement.Meaning(
                        lang = meaning.mLang,
                        value = meaning.text,
                    )
                },
            )
        },
        nanori = readingMeaning.nanori.map { it.text },
    )
}
