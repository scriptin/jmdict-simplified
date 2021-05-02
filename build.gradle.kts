import de.undercouch.gradle.tasks.download.Download
import org.basex.core.Context
import org.basex.core.cmd.CreateDB
import org.basex.core.cmd.DropDB
import org.basex.query.QueryProcessor
import org.basex.query.value.item.Item
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.streams.toList

group = "org.edrdg.jmdict.simplified"
version = "3.2.0-SNAPSHOT"

plugins {
    id("de.undercouch.download") version "3.4.3"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    application
}

application {
    mainClass.set("org.edrdg.jmdict.simplified.MainKt")
    applicationDefaultJvmArgs = listOf("-Djdk.xml.entityExpansionLimit=0")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        "classpath"(group = "org.basex", name = "basex", version = "9.1")
    }
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-simple:1.7.29")
    implementation("io.github.microutils:kotlin-logging:1.8.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("com.github.ajalt.clikt:clikt:3.1.0")
    implementation("net.swiftzer.semver:semver:1.1.1")
}

val createDataDir: Task by tasks.creating {
    val dataDir = "$buildDir/data"
    extra["dataDir"] = dataDir
    doLast {
        mkdir(dataDir)
    }
}

val jmdictDownload by tasks.creating(Download::class) {
    group = "Download"
    description = "Download JMdict source XML archive"
    val dataDir: String by createDataDir.extra
    val filePath = "$dataDir/JMdict.gz"
    src("http://ftp.edrdg.org/pub/Nihongo/JMdict.gz")
    dest(filePath)
    extra["archivePath"] = filePath
    overwrite(true)
    onlyIfModified(true)
}

val jmdictExtract: Task by tasks.creating {
    group = "Extract"
    description = "Extract JMdict source XML from an archive"
    dependsOn(jmdictDownload)
    val dataDir: String by createDataDir.extra
    val archivePath: String by jmdictDownload.extra
    val filePath = "$dataDir/JMdict.xml"
    extra["jmdictPath"] = filePath
    doLast {
        resources.gzip(archivePath).read().copyTo(file(filePath).outputStream())
    }
}

val jmnedictDownload by tasks.creating(Download::class) {
    group = "Download"
    description = "Download JMnedict source XML archive"
    val dataDir: String by createDataDir.extra
    val filePath = "$dataDir/JMnedict.xml.gz"
    src("http://ftp.edrdg.org/pub/Nihongo/JMnedict.xml.gz")
    dest(filePath)
    extra["archivePath"] = filePath
    overwrite(true)
    onlyIfModified(true)
}

val jmnedictExtract: Task by tasks.creating {
    group = "Extract"
    description = "Extract JMnedict source XML from an archive"
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
    group = "Download"
    description = "Download and unpack all dictionaries"
    dependsOn(jmdictExtract, jmnedictExtract)
}

fun getFileHash(inputFilePath: String): String {
    try {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(file(inputFilePath).readBytes())
            .joinToString("") { b -> (0xFF and b.toInt()).toString(16) }
    } catch (e: NoSuchAlgorithmException) {
        throw Exception("SHA-256 is not supported in this Java instance", e)
    }
}

val jmdictUpdateChecksumFile: Task by tasks.creating {
    group = "Checksum"
    description = "Generate a checksum of JMdict XML file and write it into a checksum file"
    val jmdictPath: String by jmdictExtract.extra
    val jmdictChecksumPath = "$projectDir/checksums/JMdict.xml.sha256"
    extra["jmdictChecksumPath"] = jmdictChecksumPath
    doLast {
        file(jmdictChecksumPath).writeText(getFileHash(jmdictPath))
    }
}

val jmnedictUpdateChecksumFile: Task by tasks.creating {
    group = "Checksum"
    description = "Generate a checksum JMnedict XML file and write it into a checksum file"
    val jmnedictPath: String by jmnedictExtract.extra
    val jmnedictChecksumPath = "$projectDir/checksums/JMnedict.xml.sha256"
    extra["jmnedictChecksumPath"] = jmnedictChecksumPath
    doLast {
        file(jmnedictChecksumPath).writeText(getFileHash(jmnedictPath))
    }
}

val updateChecksums: Task by tasks.creating {
    group = "Checksum"
    description = "Generate checksums of all dictionaries and write into checksum files"
    dependsOn(jmdictUpdateChecksumFile, jmnedictUpdateChecksumFile)
}

val jmdictHasChanged: Task by tasks.creating {
    group = "Checksum"
    description = "Check if a checksum of JMdict XML file has changed"
    val jmdictPath: String by jmdictExtract.extra
    val jmdictChecksumPath: String by jmdictUpdateChecksumFile.extra
    doLast {
        val previousChecksum = file(jmdictChecksumPath).readText().trim()
        val newChecksum = getFileHash(jmdictPath).trim()
        println(if (previousChecksum == newChecksum) "NO" else "YES")
    }
}

val jmnedictHasChanged: Task by tasks.creating {
    group = "Checksum"
    description = "Check if a checksum of JMnedict XML file has changed"
    val jmnedictPath: String by jmnedictExtract.extra
    val jmnedictChecksumPath: String by jmnedictUpdateChecksumFile.extra
    doLast {
        val previousChecksum = file(jmnedictChecksumPath).readText().trim()
        val newChecksum = getFileHash(jmnedictPath).trim()
        println(if (previousChecksum == newChecksum) "NO" else "YES")
    }
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
        "  <j:string key=\"$tag\">${description.replace("[`']".toRegex(), "&apos;")}</j:string>"
    }
    return """
        |xquery version "3.0";
        |module namespace tags = "tags";
        |
        |import module namespace j = "http://www.w3.org/2005/xpath-functions";
        |
        |(: This file is generated, do not edit manually! :)
        |
        |declare function tags:convert-entity(${'$'}word-id as xs:string, ${'$'}text as xs:string) as xs:string? {
        |  switch(${'$'}text)
        |${cases.joinToString("\n")}
        |  default return error(
        |    xs:QName("unknown-tag"),
        |    concat("Unknown tag '", ${'$'}text, "' on entity ", ${'$'}word-id)
        |  )
        |};
        |
        |declare variable ${'$'}tags:tags := <j:map key="tags">
        |${pairs.joinToString("\n")}
        |</j:map>;
        |""".trimMargin()
}

val jmdictTags: Task by tasks.creating {
    group = "Tags"
    description = "Generate JMdict tag.xq file"
    val jmdictPath: String by jmdictExtract.extra
    doLast {
        file("$projectDir/src/jmdict/tags.xq").writeText(generateTagsXQuery(getTags(jmdictPath)))
    }
}

val jmnedictTags: Task by tasks.creating {
    group = "Tags"
    description = "Generate JMnedict tag.xq file"
    val jmnedictPath: String by jmnedictExtract.extra
    doLast {
        file("$projectDir/src/jmnedict/tags.xq").writeText(generateTagsXQuery(getTags(jmnedictPath)))
    }
}

/**
 * (Re)generate tags.xq for all dictionaries
 */
val tags: Task by tasks.creating {
    group = "Tags"
    description = "Generate all tag files"
    dependsOn(jmdictTags, jmnedictTags)
}

val defaultVars = mapOf("version" to version)

fun runXQuery(
    inputFilePath: String,
    queryFilePath: String,
    outputFilePath: String,
    vars: Map<String, Any> = defaultVars
) {
    val dbName = "doc"
    val context = Context()
    try {
        val xqFile = File(queryFilePath)
        val outputFile = File(outputFilePath)
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        DropDB(dbName).execute(context)
        CreateDB(dbName, inputFilePath).execute(context)
        QueryProcessor(
            xqFile.readText(),
            xqFile.absolutePath,
            context
        ).use { processor ->
            vars.forEach { (k, v) -> processor.bind(k, v) }
            val iter = processor.iter()
            processor.getSerializer(outputFile.outputStream()).use { serializer ->
                var item: Item? = iter.next()
                while (item != null) {
                    serializer.serialize(item)
                    item = iter.next()
                }
            }
        }
    } finally {
        DropDB(dbName).execute(context)
    }
}

val jmdictFullConvert: Task by tasks.creating {
    group = "Convert"
    description = "Convert JMdict full version from XML to JSON"
    val jmdictPath: String by jmdictExtract.extra
    val fileName = "jmdict-eng-$version.json"
    val filePath = "$buildDir/$fileName"
    extra["jmdictFullJsonName"] = fileName
    extra["jmdictFullJsonPath"] = filePath
    doLast {
        runXQuery(jmdictPath, "$projectDir/src/jmdict/convert-dictionary.xq", filePath)
    }
}

val jmdictCommonConvert: Task by tasks.creating {
    group = "Convert"
    description = "Convert JMdict common-only version from XML to JSON"
    val jmdictPath: String by jmdictExtract.extra
    val fileName = "jmdict-eng-common-$version.json"
    val filePath = "$buildDir/$fileName"
    extra["jmdictCommonJsonName"] = fileName
    extra["jmdictCommonJsonPath"] = filePath
    doLast {
        runXQuery(jmdictPath, "$projectDir/src/jmdict/convert-dictionary-common.xq", filePath)
    }
}

fun jmnedictConvertWrapper(): String {
    val fileName = "jmnedict-part-0-$version.json"
    val filePath = "$buildDir/$fileName"
    val jmnedictPath: String by jmnedictExtract.extra
    runXQuery(jmnedictPath, "$projectDir/src/jmnedict/convert-dictionary.xq", filePath)
    return filePath
}

fun jmnedictConvertEntriesRange(idx: Int, start: Long, end: Long): String {
    val fileName = "jmnedict-part-${idx + 1}-$version.json"
    val filePath = "$buildDir/$fileName"
    val jmnedictPath: String by jmnedictExtract.extra
    runXQuery(
        jmnedictPath,
        "$projectDir/src/jmnedict/convert-entries-range.xq",
        filePath,
        defaultVars.plus(mapOf("start" to start, "end" to end))
    )
    return filePath
}

fun jmnedictGenerateFiles(partsCount: Int, partSize: Long): Pair<String, List<String>> {
    val progressTotal = partsCount + 1
    println("Converting: 1/$progressTotal")
    val wrapperFile = jmnedictConvertWrapper()
    val parts = mutableListOf<String>()
    for (i in 0 until partsCount) {
        println("Converting: ${i + 2}/$progressTotal")
        val start = i * partSize
        val end = start + partSize
        parts.add(jmnedictConvertEntriesRange(i, start, end))
    }
    return Pair(wrapperFile, parts.toList())
}

fun jmnedictConcat(f: File, wrapperFile: String, parts: List<String>) {
    val offset = "  "
    val startRegex = "^$offset\"words\"\\s*:\\s*\\[\\s*$".toRegex()
    val endRegex = "^$offset\\]\\s*$".toRegex()

    var started: Boolean
    var ended = false
    var needsNewLine = false

    val totalProgress = parts.size + 1
    println("Concatenating: 1/$totalProgress")
    file(wrapperFile).bufferedReader().lines().forEachOrdered { line ->
        if (!ended) {
            ended = startRegex.matches(line)
            f.appendText(if (needsNewLine) "\n$line" else line)
        }
        needsNewLine = true
    }

    for (i in parts.indices) {
        println("Concatenating: ${i + 2}/$totalProgress")
        val part = parts[i]
        val needsComma = i != (parts.size - 1)
        started = false
        ended = false
        file(part).bufferedReader().lines().forEachOrdered { line ->
            if (started && !ended) {
                ended = endRegex.matches(line)
                if (!ended) {
                    f.appendText("\n$line")
                } else if (needsComma) {
                    f.appendText(",")
                }
            }
            if (!started) {
                started = startRegex.matches(line)
            }
        }
    }
    f.appendText("\n$offset]")
    f.appendText("\n}\n")
}

val jmnedictConvert: Task by tasks.creating {
    group = "Convert"
    description = "Convert JMnedict from XML to JSON"
    val fileName = "jmnedict-$version.json"
    val filePath = "$buildDir/$fileName"
    extra["jmnedictJsonName"] = fileName
    extra["jmnedictJsonPath"] = filePath
    doLast {
        val (wrapperFile, parts) = jmnedictGenerateFiles(8, 100_000L)
        val f = file(filePath)
        f.delete()
        f.createNewFile()
        jmnedictConcat(f, wrapperFile, parts)
    }
}

val convert: Task by tasks.creating {
    group = "Convert"
    description = "Convert all dictionaries from XML to JSON"
    dependsOn(jmdictFullConvert, jmdictCommonConvert, jmnedictConvert)
}

val createDistDir: Task by tasks.creating {
    val distDir = "$buildDir/dist"
    extra["distDir"] = distDir
    doLast {
        mkdir(distDir)
    }
}

val jmdictFullZip by tasks.creating(Zip::class) {
    group = "Distribution"
    description = "Create JMdict full version distribution archive (zip)"
    val jmdictFullJsonName: String by jmdictFullConvert.extra
    val jmdictFullJsonPath: String by jmdictFullConvert.extra
    val distDir: String by createDistDir.extra
    archiveFileName.set("$jmdictFullJsonName.zip")
    destinationDirectory.set(file(distDir))
    from(file(jmdictFullJsonPath))
}

val jmdictFullTar by tasks.creating(Tar::class) {
    group = "Distribution"
    description = "Create JMdict full version distribution archive (tar+gzip)"
    val jmdictFullJsonName: String by jmdictFullConvert.extra
    val jmdictFullJsonPath: String by jmdictFullConvert.extra
    val distDir: String by createDistDir.extra
    archiveFileName.set("$jmdictFullJsonName.tgz")
    compression = Compression.GZIP
    destinationDirectory.set(file(distDir))
    from(file(jmdictFullJsonPath))
}

val jmdictCommonZip by tasks.creating(Zip::class) {
    group = "Distribution"
    description = "Create JMdict common-only version distribution archive (zip)"
    val jmdictCommonJsonName: String by jmdictCommonConvert.extra
    val jmdictCommonJsonPath: String by jmdictCommonConvert.extra
    val distDir: String by createDistDir.extra
    archiveFileName.set("$jmdictCommonJsonName.zip")
    destinationDirectory.set(file(distDir))
    from(file(jmdictCommonJsonPath))
}

val jmdictCommonTar by tasks.creating(Tar::class) {
    group = "Distribution"
    description = "Create JMdict common-only version distribution archive (tar+gzip)"
    val jmdictCommonJsonName: String by jmdictCommonConvert.extra
    val jmdictCommonJsonPath: String by jmdictCommonConvert.extra
    val distDir: String by createDistDir.extra
    archiveFileName.set("$jmdictCommonJsonName.tgz")
    compression = Compression.GZIP
    destinationDirectory.set(file(distDir))
    from(file(jmdictCommonJsonPath))
}

val jmnedictZip by tasks.creating(Zip::class) {
    group = "Distribution"
    description = "Create JMnedict distribution archive (zip)"
    val jmnedictJsonName: String by jmnedictConvert.extra
    val jmnedictJsonPath: String by jmnedictConvert.extra
    val distDir: String by createDistDir.extra
    archiveFileName.set("$jmnedictJsonName.zip")
    destinationDirectory.set(file(distDir))
    from(file(jmnedictJsonPath))
}

val jmnedictTar by tasks.creating(Tar::class) {
    group = "Distribution"
    description = "Create JMnedict distribution archive (tar+gzip)"
    val jmnedictJsonName: String by jmnedictConvert.extra
    val jmnedictJsonPath: String by jmnedictConvert.extra
    val distDir: String by createDistDir.extra
    archiveFileName.set("$jmnedictJsonName.tgz")
    compression = Compression.GZIP
    destinationDirectory.set(file(distDir))
    from(file(jmnedictJsonPath))
}

/**
 * Create distribution archives of all dictionaries in zip format
 */
val zip: Task by tasks.creating {
    group = "Distribution"
    description = "Create distribution archives (zip)"
    dependsOn(jmdictFullZip, jmdictCommonZip, jmnedictZip)
}

/**
 * Create distribution archives of all dictionaries in tar+gzip format
 */
val tar: Task by tasks.creating {
    group = "Distribution"
    description = "Create distribution archives (tar+gzip)"
    dependsOn(jmdictFullTar, jmdictCommonTar, jmnedictTar)
}

/**
 * Create distribution archives of all dictionaries in all formats
 */
val dist: Task by tasks.creating {
    group = "Distribution"
    description = "Create distribution archives (all formats)"
    dependsOn(zip, tar)
}
