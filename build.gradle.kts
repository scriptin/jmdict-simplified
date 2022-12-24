import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.streams.toList

group = "org.edrdg.jmdict.simplified"
version = "3.2.0-SNAPSHOT"

val jmdictLanguages = listOf("all", "eng", "eng-common")
val jmdictReportFile = "jmdict-release-info.md"
val jmnedictLanguages = listOf("all") // There is only English
val jmnedictReportFile = "jmnedict-release-info.md"

plugins {
    id("de.undercouch.download") version "3.4.3"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
    application
}

application {
    mainClass.set("org.edrdg.jmdict.simplified.MainKt")
}

tasks {
    // See <https://www.baeldung.com/kotlin/gradle-executable-jar>
    val uberJar = register<Jar>("uberJar") {
        // We need this for Gradle optimization to work
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val runtime = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) }
        from(runtime + sourcesMain.output)
    }
    build {
        dependsOn(uberJar)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("io.github.microutils:kotlin-logging:3.0.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("com.github.ajalt.clikt:clikt:3.5.0")
    implementation("net.swiftzer.semver:semver:1.2.0")
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

val createDistDir: Task by tasks.creating {
    val distDir = "$buildDir/dist"
    extra["distDir"] = distDir
    doLast {
        mkdir(distDir)
    }
}

val jmdictConvert: Task by tasks.creating(Exec::class) {
    group = "Convert"
    description = "Convert JMdict"
    dependsOn(createDistDir, tasks.getByName("uberJar"))
    val distDir: String by createDistDir.extra
    val jmdictPath: String by jmdictExtract.extra
    commandLine = listOf(
        "java",
        "-Djdk.xml.entityExpansionLimit=0", // To avoid errors about # of entities in XML files
        "-jar",
        (tasks.getByName("uberJar") as Jar).archiveFile.get().asFile.path,
        "convert-jmdict",
        "--version=$version",
        "--languages=${jmdictLanguages.joinToString(",")}",
        "--report=$distDir${File.separator}$jmdictReportFile",
        jmdictPath,
        distDir,
    )
}

val jmnedictConvert: Task by tasks.creating(Exec::class) {
    group = "Convert"
    description = "Convert JMnedict"
    dependsOn(createDistDir, tasks.getByName("uberJar"))
    val distDir: String by createDistDir.extra
    val jmnedictPath: String by jmnedictExtract.extra
    commandLine = listOf(
        "java",
        "-Djdk.xml.entityExpansionLimit=0", // To avoid errors about # of entities in XML files
        "-jar",
        (tasks.getByName("uberJar") as Jar).archiveFile.get().asFile.path,
        "convert-jmnedict",
        "--version=$version",
        "--languages=${jmnedictLanguages.joinToString(",")}",
        "--report=$distDir${File.separator}$jmnedictReportFile",
        jmnedictPath,
        distDir,
    )
}

val convert: Task by tasks.creating {
    group = "Convert"
    description = "Convert JMdict and JMnedict"
    dependsOn(jmdictConvert, jmnedictConvert)
}
