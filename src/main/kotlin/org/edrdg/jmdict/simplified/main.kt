package org.edrdg.jmdict.simplified

import com.github.ajalt.clikt.core.subcommands
import org.edrdg.jmdict.simplified.commands.AnalyzeJMdict
import org.edrdg.jmdict.simplified.commands.ConvertJMdict
import org.edrdg.jmdict.simplified.commands.JMdictSimplified

fun main(args: Array<String>) = JMdictSimplified().subcommands(
    AnalyzeJMdict(),
    ConvertJMdict(),
).main(args)
