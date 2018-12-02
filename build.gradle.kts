import de.undercouch.gradle.tasks.download.Download
import java.io.FileOutputStream
import kotlin.streams.toList

version = "3.0.0-dev"

project.extra["jmdictFullMem"] = project.findProperty("jmdictFullMem") ?: "6g"
project.extra["jmdictCommonMem"] = project.findProperty("jmdictFullMem") ?: "2g"
project.extra["jmnedictMem"] = project.findProperty("jmnedictMem") ?: "10g"

plugins {
    id ("de.undercouch.download") version "3.4.3"
}

/**
 * Clean the build dir
 */
val clean: Task by tasks.creating {
    group = "Clean"
    description = "Remove all build artifacts and source XML files"
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
    group = "Download"
    description = "Download JMdict source XML archive"
    val dataDir: String by createDataDir.extra
    val filePath = "$dataDir/JMdict_e.gz"
    src("http://ftp.monash.edu.au/pub/nihongo/JMdict_e.gz")
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
    val filePath = "$dataDir/JMdict_e.xml"
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
    src("http://ftp.monash.edu/pub/nihongo/JMnedict.xml.gz")
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

val jmdictFullConvert by tasks.creating(Exec::class) {
    group = "Convert"
    description = "Convert JMdict full version from XML to JSON"
    val jmdictPath: String by jmdictExtract.extra
    val jmdictFullMem: String by project.extra
    val fileName = "jmdict-eng-$version.json"
    val filePath = "$buildDir/$fileName"
    extra["jmdictFullJsonName"] = fileName
    extra["jmdictFullJsonPath"] = filePath
    environment = mapOf(
        "PATH" to System.getenv("PATH"),
        "JAVA_HOME" to System.getenv("JAVA_HOME"),
        "BASEX_JVM" to "-Xmx$jmdictFullMem -Djdk.xml.entityExpansionLimit=0"
    )
    commandLine(
        "basex",
        "-i", jmdictPath,
        "-b", "version=$version",
        "-o", filePath,
        "$projectDir/src/jmdict/convert-dictionary.xq"
    )
}

val jmdictCommonConvert by tasks.creating(Exec::class) {
    group = "Convert"
    description = "Convert JMdict common-only version from XML to JSON"
    val jmdictPath: String by jmdictExtract.extra
    val jmdictCommonMem: String by project.extra
    val fileName = "jmdict-end-common-$version.json"
    val filePath = "$buildDir/$fileName"
    extra["jmdictCommonJsonName"] = fileName
    extra["jmdictCommonJsonPath"] = filePath
    environment = mapOf(
        "PATH" to System.getenv("PATH"),
        "JAVA_HOME" to System.getenv("JAVA_HOME"),
        "BASEX_JVM" to "-Xmx$jmdictCommonMem -Djdk.xml.entityExpansionLimit=0"
    )
    commandLine(
        "basex",
        "-i", jmdictPath,
        "-b", "version=$version",
        "-o", filePath,
        "$projectDir/src/jmdict/convert-dictionary-common.xq"
    )
}

val jmnedictConvert by tasks.creating(Exec::class) {
    group = "Convert"
    description = "Convert JMnedict from XML to JSON"
    val jmnedictPath: String by jmnedictExtract.extra
    val jmnedictMem: String by project.extra
    val fileName = "jmnedict-$version.json"
    val filePath = "$buildDir/$fileName"
    extra["jmnedictJsonName"] = fileName
    extra["jmnedictJsonPath"] = filePath
    environment = mapOf(
        "PATH" to System.getenv("PATH"),
        "JAVA_HOME" to System.getenv("JAVA_HOME"),
        "BASEX_JVM" to "-Xmx$jmnedictMem -Djdk.xml.entityExpansionLimit=0"
    )
    commandLine(
        "basex",
        "-i", jmnedictPath,
        "-b", "version=$version",
        "-o", filePath,
        "$projectDir/src/jmnedict/convert-dictionary.xq"
    )
}

val convert: Task by tasks.creating {
    group = "Convert"
    description = "Convert all dictionaries from XML to JSON"
    dependsOn(jmdictFullConvert, jmdictCommonConvert, jmnedictConvert)
}

val createDistDir: Task by tasks.creating {
    val distDir = "$buildDir/data"
    extra["distDir"] = distDir
    doLast {
        mkdir(distDir)
    }
}

val jmdictFullZip by tasks.creating(Zip::class) {
    group = "Distribution"
    description = "Create JMdict full version distribution archive (zip)"
    dependsOn(jmdictFullConvert)
    val jmdictFullJsonName: String by jmdictFullConvert.extra
    val jmdictFullJsonPath: String by jmdictFullConvert.extra
    val distDir: String by createDistDir.extra
    archiveName = "$jmdictFullJsonName.zip"
    destinationDir = file(distDir)
    from(file(jmdictFullJsonPath))
}

val jmdictFullTar by tasks.creating(Tar::class) {
    group = "Distribution"
    description = "Create JMdict full version distribution archive (tar+gzip)"
    dependsOn(jmdictFullConvert)
    val jmdictFullJsonName: String by jmdictFullConvert.extra
    val jmdictFullJsonPath: String by jmdictFullConvert.extra
    val distDir: String by createDistDir.extra
    archiveName = "$jmdictFullJsonName.tgz"
    compression = Compression.GZIP
    destinationDir = file(distDir)
    from(file(jmdictFullJsonPath))
}

val jmdictCommonZip by tasks.creating(Zip::class) {
    group = "Distribution"
    description = "Create JMdict common-only version distribution archive (zip)"
    dependsOn(jmdictCommonConvert)
    val jmdictCommonJsonName: String by jmdictCommonConvert.extra
    val jmdictCommonJsonPath: String by jmdictCommonConvert.extra
    val distDir: String by createDistDir.extra
    archiveName = "$jmdictCommonJsonName.zip"
    destinationDir = file(distDir)
    from(file(jmdictCommonJsonPath))
}

val jmdictCommonTar by tasks.creating(Tar::class) {
    group = "Distribution"
    description = "Create JMdict common-only version distribution archive (tar+gzip)"
    dependsOn(jmdictCommonConvert)
    val jmdictCommonJsonName: String by jmdictCommonConvert.extra
    val jmdictCommonJsonPath: String by jmdictCommonConvert.extra
    val distDir: String by createDistDir.extra
    archiveName = "$jmdictCommonJsonName.tgz"
    compression = Compression.GZIP
    destinationDir = file(distDir)
    from(file(jmdictCommonJsonPath))
}

val jmnedictZip by tasks.creating(Zip::class) {
    group = "Distribution"
    description = "Create JMnedict distribution archive (zip)"
    dependsOn(jmnedictConvert)
    val jmnedictJsonName: String by jmnedictConvert.extra
    val jmnedictJsonPath: String by jmnedictConvert.extra
    val distDir: String by createDistDir.extra
    archiveName = "$jmnedictJsonName.zip"
    destinationDir = file(distDir)
    from(file(jmnedictJsonPath))
}

val jmnedictTar by tasks.creating(Tar::class) {
    group = "Distribution"
    description = "Create JMnedict distribution archive (tar+gzip)"
    dependsOn(jmnedictConvert)
    val jmnedictJsonName: String by jmnedictConvert.extra
    val jmnedictJsonPath: String by jmnedictConvert.extra
    val distDir: String by createDistDir.extra
    archiveName = "$jmnedictJsonName.tgz"
    compression = Compression.GZIP
    destinationDir = file(distDir)
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
