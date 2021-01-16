package org.edrdg.jmdict.simplified.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import org.edrdg.jmdict.simplified.parsing.Parser
import org.edrdg.jmdict.simplified.parsing.openTag
import java.io.FileInputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory

class AnalyzeJMdict : CliktCommand(
    help = "Analyze JMdict.xml file contents without converting"
) {
    val jmdictFile by argument().file(mustExist = true, canBeDir = false)

    override fun run() {
        println("Reading from $jmdictFile")
        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        val eventReader = factory.createXMLEventReader(FileInputStream(jmdictFile))

        try {
            val metadata = Parser.parseMetadata(eventReader)
            println("Collected metadata")

            eventReader.openTag(QName("JMdict"), "root opening tag")

            var count: Long = 0
            while (Parser.hasNextEntry(eventReader)) {
                val entry = Parser.parseEntry(eventReader)
                println(entry.entSeq.text)
                count += 1
            }
            println("\nAnalyzed $count entries")
        } finally {
            eventReader.close()
        }
    }
}
