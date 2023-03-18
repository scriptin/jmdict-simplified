package org.edrdg.jmdict.simplified

import com.github.ajalt.clikt.core.subcommands
import org.edrdg.jmdict.simplified.commands.JMdictSimplified
import org.edrdg.jmdict.simplified.commands.jmdict.*
import org.edrdg.jmdict.simplified.commands.jmnedict.*
import org.edrdg.jmdict.simplified.commands.kanjidic.*
import org.edrdg.jmdict.simplified.commands.kradfile.ConvertKradfile

fun main(args: Array<String>) = JMdictSimplified().subcommands(
    AnalyzeJMdict(),
    ConvertJMdict(),
    AnalyzeJMnedict(),
    ConvertJMnedict(),
    AnalyzeKanjidic(),
    ConvertKanjidic(),
    ConvertKradfile(),
).main(args)
