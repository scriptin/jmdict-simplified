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

class ConvertRadkfile() : CliktCommand(help = "Convert RADKFILE into JSON") {
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

    private val radkfile by argument().file(mustExist = true, canBeDir = false)

    private val outputDirectory by argument().path(canBeFile = false, mustBeWritable = true)

    private val outputFileName by lazy {
        "radkfile-$version.json"
    }

    private val output by lazy {
        outputDirectory.resolve(outputFileName).toFile().writer()
    }

    private fun convertFile(file: File): List<RadicalInfo> {
        val result = mutableListOf<RadicalInfo>()
        var radical = ""
        var strokeCount = 0
        var otherInfo: String? = null
        val kanji = mutableListOf<String>()
        file.forEachLine(Charset.forName("EUC-JP")) { line ->
            if (line.startsWith("#")) return@forEachLine
            if (line.startsWith('$')) { // new radical
                if (radical.isNotEmpty()) {
                    result.add(RadicalInfo(radical, strokeCount, otherInfo, kanji.toList()))
                }
                strokeCount = 0
                otherInfo = null
                kanji.clear()
                val parts = line.split("[\uE000\\s]+".toRegex()).map { it.trim() }
                when (parts.size) {
                    3 -> {
                        radical = parts[1]
                        strokeCount = parts[2].toInt()
                    }
                    4 -> {
                        radical = parts[1]
                        strokeCount = parts[2].toInt()
                        otherInfo = parts[3]
                    }
                    else -> {
                        throw Error("Invalid line format: $line")
                    }
                }
            } else { // kanji list
                kanji.addAll(line.toCharArray().map { it.toString() })
            }
        }
        return result.toList()
    }

    private fun validate(radicals: List<RadicalInfo>) {
        val radicalCounts = radicals.toList().groupBy { it.radical }.mapValues { it.value.size }
        val nonUniqueRadicals = radicalCounts.toList().filter { it.second > 1 }
        if (nonUniqueRadicals.isNotEmpty()) {
            throw Error("Some radicals are not unique: ${nonUniqueRadicals.map { it.first } }")
        }
    }

    override fun run() {
        println("Input file: $radkfile")
        println()
        println("Output file: $outputFileName")
        println()

        val radicals = convertFile(radkfile)

        validate(radicals)

        output.write("{\n")
        output.write("\"version\": \"$version\",\n")
        output.write("\"radicals\": {\n")
        radicals.forEachIndexed { idx, radical ->
            output.write("\"${radical.radical}\": { ")
            output.write("\"strokeCount\": ${radical.strokeCount}, ")
            output.write("\"code\": ${Json.encodeToString(radical.code)}, ")
            output.write("\"kanji\": ${Json.encodeToString(radical.kanji)} ")
            output.write("}")
            if (idx < radicals.size - 1) {
                output.write(",")
            }
            output.write("\n")
        }
        output.write("}\n")
        output.write("}\n")
        output.flush()
    }
}

data class RadicalInfo(
    val radical: String,
    val strokeCount: Int,
    val code: String?,
    val kanji: List<String>,
)
