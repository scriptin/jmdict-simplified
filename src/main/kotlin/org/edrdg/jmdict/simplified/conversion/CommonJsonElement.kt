package org.edrdg.jmdict.simplified.conversion

import kotlinx.serialization.Serializable

/**
 * Common JSON elements for JMdict and JMnedict
 */
open class CommonJsonElement {
    /**
     * Reference to another dictionary entry
     */
    @Serializable(with = XrefSerializer::class)
    data class Xref(val part1: String, val part2: String?, val index: Int?) {
        val size: Int
            get() = 1 + when {
                part2 == null && index == null -> 0
                (part2 != null) xor (index != null) -> 1
                else -> 2
            }
    }

    /**
     * Short tag for describing categories of dictionary items,
     * e.g. "v" = verb, "n" = noun, etc.
     */
    @Serializable(with = TagSerializer::class)
    data class Tag(val abbreviation: String)
}
