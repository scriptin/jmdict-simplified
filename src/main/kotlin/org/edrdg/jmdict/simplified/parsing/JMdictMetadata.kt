package org.edrdg.jmdict.simplified.parsing

data class JMdictMetadata(
    val revisions: List<String>,
    val entities: Map<String, String>,
    override val date: String,
) : Metadata(date)
