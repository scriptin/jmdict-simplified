# jmdict-simplified

### [JMdict][], [JMnedict][], and [Kanjidic][] in JSON format

with more comprehensible structure and beginner-friendly documentation

[![Download JSON files](https://img.shields.io/static/v1?label=Download&message=JSON%20files&color=blue&style=for-the-badge)][latest-release]

- Releases are automatically scheduled for every Monday. See [release.yml](.github/workflows/release.yml)
- Found a bug? Need a new feature? See [CONTRIBUTING.md](CONTRIBUTING.md)

## Why?

Original XML files are less than ideal in terms of format.
(My opinion only, the JMdict/Jmnedict project in general is absolutely awesome!)
This project provides the following changes and improvements:

1. JSON format instead of XML. Because the original format used some "advanced" XML features,
   such as entities and DOCTYPE, it could be quite difficult to use in some tech stacks,
   e.g. when your programming language of choice has no libraries for parsing some syntax
2. Regular structure for every item in every collection, no "same as in previous" implicit values.
   This is a problem with original XML files because users' code has to keep track
   of various parts of state while traversing collections. In this project, I tried to make every
   item of every collection "self-contained," with all the fields having all the values,
   without a need to refer to preceding items
3. Avoiding `null` (with few exceptions) and missing fields, preferring empty arrays.
   See <http://thecodelesscode.com/case/6> for the inspiration for this
4. Human-readable names for fields instead of cryptic abbreviations with no explanations
5. Documentation in a single file instead of browsing obscure pages across multiple sites.
   In my opinion, the documentation is the weakest part of JMDict/JMnedict project

## Full, "common-only", and language-specific versions

There are two main types of JSON files for the JMdict dictionary:

- full - same as original files, with no omissions of entries
- "common-only" - containing only dictionary entries considered "common" -
  if any of `/k_ele/ke_pri` or `/r_ele/re_pri` elements in XML files contain
  one of these markers: "news1", "ichi1", "spec1", "spec2", "gai1".
  Only one such element is enough for the whole word to be considered common.
  This corresponds to how online dictionaries such as <https://jisho.org>
  classify words as "common". Common-only distributions are much smaller.
  They are marked with "common" keyword in file names, see the [latest release][latest-release]

Also, JMdict and Kanjidic have language-specific versions with language codes
(3-letter [ISO 639-2](https://en.wikipedia.org/wiki/ISO_639-2) codes for JMdict,
2-letter [ISO 639-1](https://en.wikipedia.org/wiki/ISO_639-1) codes for Kanjidic) in file names:

- `all` - all languages, i.e. no language filter was applied
- `eng`/`en` - English
- `ger`/`de` - German
- `rus`/`ru` - Russian
- `hun`/`hu` - Hungarian
- `dut`/`nl` - Dutch
- `spa`/`es` - Spanish
- `fre`/`fr` - French
- `swe`/`sv` - Swedish
- `slv`/`sl` - Slovenian

JMnedict has only one version, since it's (currently) English-only,
and has no "common" indicators on entries.

## Requirements for running the conversion script

- Java 17 (JRE only, JDK is not necessary) - you can use [Azul Zulu OpenJDK][AzulJava17]

You don't need to install Gradle, just use the Gradle wrapper provided in this repository:
`gradlew` (for Linux/Mac) or `gradlew.bat` (for Windows)

## Converting XML dictionaries

NOTE: You can grab the pre-built JSON files in the [latest release][latest-release]

Use included scripts: `gradlew` (for Linux/Mac OS) or `gradlew.bat` (for Windows).

Tasks to convert dictionary files and create distribution archives:

- `./gradlew clean` - clean all build artifacts to start a fresh build,
  in cases when you need to re-download and convert from scratch
- `./gradlew download` - download and extract original dictionary XML files into `build/dict-xml`
- `./gradlew convert` - convert all dictionaries to JSON and place into `build/dict-json`
- `./gradlew archive` - create distribution archives (zip, tar+gzip) in `build/distributions`

Utility tasks (for CI/CD workflows):

- `./gradlew --quiet jmdictHasChanged`, `./gradlew --quiet jmnedictHasChanged`,
  and `./gradlew --quiet kanjidicHasChanged`-  check if dictionary files have changed
  by comparing checksums of downloaded files with those stored in the [checksums](checksums).
  Outputs `YES` or `NO`. Run this only after `download` task!
  The `--quiet` is to silence Gradle logs, e.g. when you need to put values into environments variables.
- `./gradlew updateChecksums` - update checksum files in the [checksums](checksums) directory.
  Run after creating distribution archives and commit checksum files into the repository,
  so that next time CI/CD workflow knows if it needs to rebuild anything.
- `./gradlew uberJar` - create an Uber JAR for standalone use (i.e. w/o Gradle).
  The JAR program shows help messages and should be intuitive to use if you know how to run it.

For the full list of available tasks, run `./gradlew tasks`

## Troubleshooting

- Make sure to run tasks in order: `download` -> `convert` -> `archive`
- If running Gradle fails, make sure `java` is available on your `$PATH` environment variable
- Run Gradle with `--stacktrace`, `--info`, or `--debug` arguments to see more details
  if you get an error

## Format

> See the [TypeScript types](node/packages/jmdict-simplified-types/index.ts)

A better documentation is in development, but the types have enough comments
to explain the format. Please also read the original documentation:

- [EDRDG wiki](https://www.edrdg.org/wiki/index.php/Main_Page)
- [JMdict][] (also [wiki](https://www.edrdg.org/wiki/index.php/JMdict-EDICT_Dictionary_Project))
- [JMnedict][]
- [Kanjidic][]

You can also find Kotlin types in [JMdictJsonElement.kt](src/main/kotlin/org/edrdg/jmdict/simplified/conversion/jmdict/JMdictJsonElement.kt),
[JMnedictJsonElement.kt](src/main/kotlin/org/edrdg/jmdict/simplified/conversion/jmnedict/JMnedictJsonElement.kt),
and [Kanjidic2JsonElement.kt](src/main/kotlin/org/edrdg/jmdict/simplified/conversion/kanjidic/Kanjidic2JsonElement.kt),
although they contain some methods and annotations you might not need.

JMdict format notes:

- "Kanji" and "kana" versions of words are not always equivalent
  to "spellings" and "readings" correspondingly. Some words are kana-only.
  You should treat "kanji" and "kana" as different ways of spelling,
  although when kanji versions are present, kana versions are indeed "readings" for those
- Some kana versions only apply to particular kanji versions, i.e. different spellings
  of the same word can be read in different ways. You'll see the `appliesToKanji` field
  being filled with a particular version in such cases
- "Sense" in JMdict refers to translations along with some other information.
  Sometimes, some "senses" only apply to some particular kanji/kana versions of a word,
  that's why you'll see fields `appliesToKanji` and `appliesToKana`.
  In JMnedict, translations are simply called "translations," there are no "senses"

## License

### JMdict and JMnedict

The original XML files - **JMdict.xml**, **JMdict_e.xml**, and **JMnedict.xml** -
are the property of the Electronic Dictionary Research and Development Group,
and are used in conformance with the Group's [license][EDRDG-license].
Project started in 1991 by Jim Breen.

All derived files are distributed under the same license, as the original license requires it.

### Kanjidic

The original **kanjidic2.xml** file is released under
[Creative Commons Attribution-ShareAlike License v4.0][CC-BY-SA-4].
See the [Copyright and Permissions](https://www.edrdg.org/wiki/index.php/KANJIDIC_Project#Copyright_and_Permissions)
section on the Kanjidic wiki for details.

All derived files are distributed under the same license, as the original license requires it.

### Other files

The source code and other files of this project (this doesn't include
the distribution archives containing JSON files) are available under
[Creative Commons Attribution-ShareAlike License v4.0][CC-BY-SA-4].
See [LICENSE.txt](LICENSE.txt)

[JMdict]: http://www.edrdg.org/jmdict/j_jmdict.html
[JMnedict]: http://www.edrdg.org/enamdict/enamdict_doc.html
[Kanjidic]: https://www.edrdg.org/wiki/index.php/KANJIDIC_Project
[latest-release]: https://github.com/scriptin/jmdict-simplified/releases/latest
[AzulJava17]: https://www.azul.com/downloads/?version=java-17-lts&package=jre
[EDRDG-license]: http://www.edrdg.org/edrdg/licence.html
[CC-BY-SA-4]: http://creativecommons.org/licenses/by-sa/4.0/
