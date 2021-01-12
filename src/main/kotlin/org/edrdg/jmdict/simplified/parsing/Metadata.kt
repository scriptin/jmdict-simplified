package org.edrdg.jmdict.simplified.parsing

data class Metadata(
    val revisions: List<String>,
    val entities: Map<String, String>,
    val date: String,
)
