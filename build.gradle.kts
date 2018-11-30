import de.undercouch.gradle.tasks.download.Download
import java.io.FileOutputStream
import kotlin.streams.toList

plugins {
    id ("de.undercouch.download") version "3.4.3"
}

/**
 * Clean the build dir
 */
val clean: Task by tasks.creating {
    doLast {
        delete(buildDir)
    }
}

val createDataDir: Task by tasks.creating {
    val dataDir = "$buildDir/data"
    extra["dataDir"] = dataDir
    doLast {
        mkdir(dataDir)
    }
}

val jmdictDownload by tasks.creating(Download::class) {
    val dataDir: String by createDataDir.extra
    val filePath = "$dataDir/JMdict_e.gz"
    src("http://ftp.monash.edu.au/pub/nihongo/JMdict_e.gz")
    dest(filePath)
    extra["archivePath"] = filePath
    overwrite(true)
    onlyIfModified(true)
}

val jmdictExtract: Task by tasks.creating {
    dependsOn(jmdictDownload)
    val dataDir: String by createDataDir.extra
    val archivePath: String by jmdictDownload.extra
    val filePath = "$dataDir/JMdict_e.xml"
    extra["jmdictPath"] = filePath
    doLast {
        resources.gzip(archivePath).read().copyTo(file(filePath).outputStream())
    }
}

val jmnedictDownload by tasks.creating(Download::class) {
    val dataDir: String by createDataDir.extra
    val filePath = "$dataDir/JMnedict.xml.gz"
    src("http://ftp.monash.edu/pub/nihongo/JMnedict.xml.gz")
    dest(filePath)
    extra["archivePath"] = filePath
    overwrite(true)
    onlyIfModified(true)
}

val jmnedictExtract: Task by tasks.creating {
    dependsOn(jmnedictDownload)
    val dataDir: String by createDataDir.extra
    val archivePath: String by jmnedictDownload.extra
    val filePath = "$dataDir/JMnedict.xml"
    extra["jmnedictPath"] = filePath
    doLast {
        resources.gzip(archivePath).read().copyTo(file(filePath).outputStream())
    }
}

/**
 * Download and extract all dictionaries
 */
val download: Task by tasks.creating {
    dependsOn(jmdictExtract, jmnedictExtract)
}

fun getTags(inputFilePath: String): List<Pair<String, String>> {
    val regex = """<!ENTITY\s+(.+)\s+"([^"]+)">""".toRegex()
    return file(inputFilePath).bufferedReader().lines()
        .filter { it.matches(regex) }
        .map { line ->
            val groups = regex.find(line)!!.groupValues
            Pair(groups[1], groups[2])
        }
        .toList()
}

fun generateTagsXQuery(tags: List<Pair<String, String>>): String {
    val cases = tags.map { (tag, description) ->
        "  case \"$description\" return \"$tag\""
    }
    val pairs = tags.map { (tag, description) ->
        "  <pair name=\"$tag\" type=\"string\">${description.replace("[`']".toRegex(), "&apos;")}</pair>"
    }
    return """
        |xquery version "3.0";
        |module namespace tags = "tags";
        |
        |import module namespace tags-utils = "tags-utils" at "../tags-utils.xq";
        |
        |(: This file is generated, do not edit manually! :)
        |
        |declare function tags:convert-entity(${'$'}word-id as xs:string, ${'$'}text as xs:string) as xs:string? {
        |  tags:convert(${'$'}word-id, tags-utils:deduplicate(normalize-space(${'$'}text)))
        |};
        |
        |declare function tags:convert(${'$'}word-id as xs:string, ${'$'}text as xs:string) as xs:string? {
        |  switch(${'$'}text)
        |${cases.joinToString("\n")}
        |  default return error(
        |    xs:QName("unknown-tag"),
        |    concat("Unknown tag '", ${'$'}text, "' on entity ", ${'$'}word-id)
        |  )
        |};
        |
        |declare variable ${'$'}tags:tags := <pair name="tags" type="object">
        |${pairs.joinToString("\n")}
        |</pair>;
        |""".trimMargin()
}

val jmdictGenerateTags: Task by tasks.creating {
    val jmdictPath: String by jmdictExtract.extra
    doLast {
        file("$projectDir/src/jmdict/tags.xq").writeText(generateTagsXQuery(getTags(jmdictPath)))
    }
}

val jmnedictGenerateTags: Task by tasks.creating {
    val jmnedictPath: String by jmnedictExtract.extra
    doLast {
        file("$projectDir/src/jmnedict/tags.xq").writeText(generateTagsXQuery(getTags(jmnedictPath)))
    }
}

/**
 * (Re)generate tags.xq for all dictionaries
 */
val generateTags: Task by tasks.creating {
    dependsOn(jmdictGenerateTags, jmnedictGenerateTags)
}
