package org.edrdg.jmdict.simplified.conversion

class ConversionException : RuntimeException {
    constructor(entSeq: String, message: String) : super("[ent_seq=$entSeq] $message")
    constructor(entSeq: String, message: String, cause: Throwable?) : super("[ent_seq=$entSeq] $message", cause)
}
