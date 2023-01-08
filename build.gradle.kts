import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

group = "org.edrdg.jmdict.simplified"
version = "3.3.1"

val jmdictLanguages = listOf("all", "eng", "eng-common")
val jmdictReportFile = "jmdict-release-info.md"
val jmnedictLanguages = listOf("all") // There is only English
val jmnedictReportFile = "jmnedict-release-info.md"

plugins {
    id("de.undercouch.download") version "5.3.0"
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
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

val createDictXmlDir: Task by tasks.creating {
    val dictXmlDir = "$buildDir/dict-xml"
    extra["dictXmlDir"] = dictXmlDir
    doLast {
        mkdir(dictXmlDir)
    }
}

val jmdictDownload by tasks.creating(Download::class) {
    group = "Download"
    description = "Download JMdict source XML archive"
    val dictXmlDir: String by createDictXmlDir.extra
    val filePath = "$dictXmlDir/JMdict.gz"
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
    val dictXmlDir: String by createDictXmlDir.extra
    val archivePath: String by jmdictDownload.extra
    val filePath = "$dictXmlDir/JMdict.xml"
    extra["jmdictPath"] = filePath
    doLast {
        resources.gzip(archivePath).read().copyTo(file(filePath).outputStream())
    }
}

val jmnedictDownload by tasks.creating(Download::class) {
    group = "Download"
    description = "Download JMnedict source XML archive"
    val dictXmlDir: String by createDictXmlDir.extra
    val filePath = "$dictXmlDir/JMnedict.xml.gz"
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
    val dictXmlDir: String by createDictXmlDir.extra
    val archivePath: String by jmnedictDownload.extra
    val filePath = "$dictXmlDir/JMnedict.xml"
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

val createDictJsonDir: Task by tasks.creating {
    val dictJsonDir = "$buildDir/dict-json"
    extra["dictJsonDir"] = dictJsonDir
    doLast {
        mkdir(dictJsonDir)
    }
}

val jmdictConvert: Task by tasks.creating(Exec::class) {
    group = "Convert"
    description = "Convert JMdict"
    dependsOn(createDictJsonDir, tasks.getByName("uberJar"))
    val dictJsonDir: String by createDictJsonDir.extra
    val jmdictPath: String by jmdictExtract.extra
    commandLine = listOf(
        "java",
        "-Djdk.xml.entityExpansionLimit=0", // To avoid errors about # of entities in XML files
        "-jar",
        (tasks.getByName("uberJar") as Jar).archiveFile.get().asFile.path,
        "convert-jmdict",
        "--version=$version",
        "--languages=${jmdictLanguages.joinToString(",")}",
        "--report=$dictJsonDir${File.separator}$jmdictReportFile",
        jmdictPath,
        dictJsonDir,
    )
}

val jmnedictConvert: Task by tasks.creating(Exec::class) {
    group = "Convert"
    description = "Convert JMnedict"
    dependsOn(createDictJsonDir, tasks.getByName("uberJar"))
    val dictJsonDir: String by createDictJsonDir.extra
    val jmnedictPath: String by jmnedictExtract.extra
    commandLine = listOf(
        "java",
        "-Djdk.xml.entityExpansionLimit=0", // To avoid errors about # of entities in XML files
        "-jar",
        (tasks.getByName("uberJar") as Jar).archiveFile.get().asFile.path,
        "convert-jmnedict",
        "--version=$version",
        "--languages=${jmnedictLanguages.joinToString(",")}",
        "--report=$dictJsonDir${File.separator}$jmnedictReportFile",
        jmnedictPath,
        dictJsonDir,
    )
}

val convert: Task by tasks.creating {
    group = "Convert"
    description = "Convert JMdict and JMnedict"
    dependsOn(jmdictConvert, jmnedictConvert)
}

val zipAll: Task by tasks.creating {
    group = "Distribution"
    description = "Zip all JSON files"
    val dictJsonDir: String by createDictJsonDir.extra
    fileTree(dictJsonDir)
        .filter { it.isFile && it.extension == "json" }
        .forEachIndexed { idx, file ->
            dependsOn.add(tasks.create("zip$idx", Zip::class) {
                from(dictJsonDir) { include(file.name) }
                archiveFileName.set("${file.name}.zip")
            })
        }
}

val tarAll: Task by tasks.creating {
    group = "Distribution"
    description = "Tar+gzip all JSON files"
    val dictJsonDir: String by createDictJsonDir.extra
    fileTree(dictJsonDir)
        .filter { it.isFile && it.extension == "json" }
        .forEachIndexed { idx, file ->
            dependsOn.add(tasks.create("tar$idx", Tar::class) {
                from(dictJsonDir) { include(file.name) }
                archiveFileName.set("${file.name}.tgz")
            })
        }
}

val archive: Task by tasks.creating {
    group = "Distribution"
    description = "Create archives of all JSON files"
    dependsOn(zipAll, tarAll)
}
