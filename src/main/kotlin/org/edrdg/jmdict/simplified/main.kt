package org.edrdg.jmdict.simplified

import com.github.ajalt.clikt.core.subcommands
import org.edrdg.jmdict.simplified.commands.*

fun main(args: Array<String>) = JMdictSimplified().subcommands(
    AnalyzeJMdict(),
    ConvertJMdict(),
    AnalyzeJMnedict(),
    ConvertJMnedict(),
).main(args)
