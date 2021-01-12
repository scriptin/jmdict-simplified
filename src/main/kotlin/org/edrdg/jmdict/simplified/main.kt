package org.edrdg.jmdict.simplified

import mu.KotlinLogging
import org.edrdg.jmdict.simplified.parsing.*
import java.io.File
import java.io.FileInputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(
            """
            Arguments (positional):
            1. /path/to/JMdict.xml
            2. /path/to/result/jmdict.json
            Example:
            ./gradlew run --args="./build/data/JMdict_e.xml ./build/jmdict-eng-4.0.0-SNAPSHOT.json"
            """.trimIndent()
        )
    } else {
        val inputFilePath = args[0]
        val outputFilePath = args[1]
        logger.info { "Input file: $inputFilePath" }
        logger.info { "Output file: $outputFilePath" }

        val file = File(inputFilePath)
        val factory = XMLInputFactory.newFactory()
        factory.setProperty(XMLInputFactory.IS_COALESCING, true)
        val eventReader = factory.createXMLEventReader(FileInputStream(file))

        try {
            val metadata = Parser.parseMetadata(eventReader)
            logger.info { "Metadata: $metadata" }

            eventReader.openTag(QName("JMdict"), "root opening tag")

            // val commonIndicators = setOf("news1", "ichi1", "spec1", "spec2", "gai1")
            var n = 10000
            while (n > 0) {
                if (!Parser.hasNextEntry(eventReader)) break
                val entry = Parser.parseEntry(eventReader)
                logger.info { "Entry: $entry" }
                n -= 1
            }
        } catch (e: Exception) {
            logger.error { e }
        } finally {
            eventReader.close()
        }
    }
}
