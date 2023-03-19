package org.edrdg.jmdict.simplified.commands.kradfile

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.swiftzer.semver.SemVer
import java.io.File
import java.nio.charset.Charset

/**
 * For simplicity, this file is doing JSON serialization mostly manually
 * and doesn't use any other classes. KRADFILE has a very simple format,
 * and using the same approach as for JMdict would be too much.
 */
class ConvertKradfile() : CliktCommand(help = "Convert KRADFILE and KRADFILE2 into JSON") {
    val version by option(
        "-v", "--version",
        metavar = "VERSION",
        help = "Version which will be used in output files",
    ).required().check("must be a valid semantic version <https://semver.org/spec/v2.0.0.html>") {
        try {
            SemVer.parse(it)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private val kradfile by argument().file(mustExist = true, canBeDir = false)

    private val kradfile2 by argument().file(mustExist = true, canBeDir = false)

    private val outputDirectory by argument().path(canBeFile = false, mustBeWritable = true)

    private val outputFileName by lazy {
        "kradfile-$version.json"
    }

    private val output by lazy {
        outputDirectory.resolve(outputFileName).toFile().writer()
    }

    private fun covertLine(line: String): KanjiDecomposition {
        val parts = line.split(":")
        if (parts.size != 2) throw Error("Invalid line format: $line")
        val kanji = parts[0].trim()
        val components = parts[1].trim().split("[\uE000\\s]+".toRegex()).map { it.trim() }
        return kanji to components;
    }

    private fun convertFile(file: File): List<KanjiDecomposition> {
        val result = mutableListOf<KanjiDecomposition>()
        file.forEachLine(Charset.forName("EUC-JP")) { line ->
            if (line.startsWith("#")) return@forEachLine
            result.add(covertLine(line))
        }
        return result.toList()
    }

    private fun validate(
        kradfile: List<KanjiDecomposition>,
        kradfile2: List<KanjiDecomposition>,
    ): List<KanjiDecomposition> {
        val keys = kradfile.map { it.first }.toSet()
        val keys2 = kradfile2.map { it.first }.toSet()
        val common = keys.intersect(keys2)
        if (common.isNotEmpty()) {
            throw Error("Unexpected shared kanji: $common")
        }

        val allDecompositions = mutableListOf<KanjiDecomposition>()
        allDecompositions.addAll(kradfile)
        allDecompositions.addAll(kradfile2)
        val result = allDecompositions.toList()

        val kanjiCounts = result.groupBy { it.first }.mapValues { it.value.size }
        val nonUniqueKanji = kanjiCounts.toList().filter { it.second > 1 }
        if (nonUniqueKanji.isNotEmpty()) {
            throw Error("Some kanji are not unique: ${nonUniqueKanji.map { it.first } }")
        }

        return result
    }

    override fun run() {
        println("Input files:")
        println(" - $kradfile")
        println(" - $kradfile2")
        println()
        println("Output file: $outputFileName")
        println()

        val kradfileDecompositions = convertFile(kradfile)
        val kradfile2Decompositions = convertFile(kradfile2)

        val allDecompositions = validate(kradfileDecompositions, kradfile2Decompositions)

        output.write("{\n")
        output.write("\"version\": \"$version\",\n")
        output.write("\"kanji\": {\n")
        allDecompositions.forEachIndexed { idx, decomposition ->
            output.write("\"${decomposition.first}\": ${Json.encodeToString(decomposition.second)}")
            if (idx < allDecompositions.size - 1) {
                output.write(",")
            }
            output.write("\n")
        }
        output.write("}\n")
        output.write("}\n")
        output.flush()
    }
}

typealias KanjiDecomposition = Pair<String, List<String>>
