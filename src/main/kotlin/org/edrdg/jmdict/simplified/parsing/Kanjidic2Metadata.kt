package org.edrdg.jmdict.simplified.parsing

data class Kanjidic2DatabaseVersion(val year: Int, val version: Int)

data class Kanjidic2Metadata(
    val dtdVersion: String,
    val dtdDate: String,
    val fileVersion: Int,
    val databaseVersion: Kanjidic2DatabaseVersion,
    override val date: String, // <date_of_creation> tag in the XML
) : Metadata(date)
