# jmdict-simplified

**[JMdict][], [JMnedict][], [Kanjidic][], and [Kradfile/Radkfile][Kradfile] in JSON format**<br>
with more comprehensible structure and beginner-friendly documentation

[![Download JSON files](https://img.shields.io/static/v1?label=Download&message=JSON%20files&color=blue&style=for-the-badge)][latest-release]
[![Format docs](https://img.shields.io/static/v1?label=Read&message=Format%20Docs&color=blue&style=for-the-badge)][format]

[![NPM package: @scriptin/jmdict-simplified-types](https://img.shields.io/static/v1?label=NPM&message=@scriptin/jmdict-simplified-types&color=blue&style=flat-square&logo=npm)][npm-types]<br>
[![NPM package: @scriptin/jmdict-simplified-loader](https://img.shields.io/static/v1?label=NPM&message=@scriptin/jmdict-simplified-loader&color=blue&style=flat-square&logo=npm)][npm-loader]

---

- Releases are automatically scheduled for every Monday. See [release.yml](.github/workflows/release.yml)
- Found a bug? Need a new feature? See [CONTRIBUTING.md](CONTRIBUTING.md)

## Why?

Original XML files are less than ideal in terms of format.
(My opinion only, the JMdict/JMnedict project in general is absolutely awesome!)
This project provides the following changes and improvements:

1. JSON instead of XML (or custom text format of RADKFILE/KRADFILE).
   Because the original format used some "advanced" XML features,
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

## Format

> See the [Format documentation][format] or [TypeScript types](node/packages/jmdict-simplified-types/index.ts)

Please also read the original documentation if you have more questions:

- [EDRDG wiki](https://www.edrdg.org/wiki/index.php/Main_Page)
- [JMdict][] (also [wiki](https://www.edrdg.org/wiki/index.php/JMdict-EDICT_Dictionary_Project))
- [JMnedict][]
- [Kanjidic][]
- [RADKFILE/KRADFILE][Kradfile]

There are also Kotlin types, although they contain some methods and annotations you might not need.

- [JMdictJsonElement.kt](src/main/kotlin/org/edrdg/jmdict/simplified/conversion/jmdict/JMdictJsonElement.kt)
- [JMnedictJsonElement.kt](src/main/kotlin/org/edrdg/jmdict/simplified/conversion/jmnedict/JMnedictJsonElement.kt)
- [Kanjidic2JsonElement.kt](src/main/kotlin/org/edrdg/jmdict/simplified/conversion/kanjidic/Kanjidic2JsonElement.kt)

## Full, "common-only", with examples, and language-specific versions

There are three main types of JSON files for the JMdict dictionary:

- full - same as original files, with no omissions of entries
- "common-only" - containing only dictionary entries considered "common" -
  if any of `/k_ele/ke_pri` or `/r_ele/re_pri` elements in XML files contain
  one of these markers: "news1", "ichi1", "spec1", "spec2", "gai1".
  Only one such element is enough for the whole word to be considered common.
  This corresponds to how online dictionaries such as <https://jisho.org>
  classify words as "common". Common-only distributions are much smaller.
  They are marked with "common" keyword in file names, see the [latest release][latest-release]
- with example sentences (built from JMdict_e_examp.xml source file) - English-only version
  with example sentences from Tanaka corpus maintained by <https://tatoeba.org>.
  This version doesn't have a full support in this project: NPM libraries do not provide
  parsers and type definitions

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

JMnedict and JMdict with examples have only one respective version each,
since they are both English-only, and JMnedict has no "common" indicators on entries.

## Requirements for running the conversion script

- Java 17 (JRE only, JDK is not necessary) - you can use [Azul Zulu OpenJDK][AzulJava17]

You don't need to install Gradle, just use the Gradle wrapper provided in this repository:
`gradlew` (for Linux/Mac) or `gradlew.bat` (for Windows)

## Converting XML dictionaries

NOTE: You can grab the pre-built JSON files in the [latest release][latest-release]

Use included scripts: `gradlew` (for Linux/macOS) or `gradlew.bat` (for Windows).

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

## License

### JMdict and JMnedict

The original XML files - **JMdict.xml**, **JMdict_e.xml**, **JMdict_e_examp.xml**,and **JMnedict.xml** -
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

### RADKFILE/KRADFILE

The RADKFILE and KRADFILE files are copyright and available under the [EDRDG Licence][EDRDG-license].
The copyright of the RADKFILE2 and KRADFILE2 files is held by Jim Rose.

### NPM packages

NPM packages [`@scriptin/jmdict-simplified-types`][npm-types] and
[`@scriptin/jmdict-simplified-loader`][npm-loader] are available under [MIT license][MIT].

### Other files

The source code and other files of this project, excluding the files and packages mentioned above,
are available under [Creative Commons Attribution-ShareAlike License v4.0][CC-BY-SA-4].
See [LICENSE.txt](LICENSE.txt)

[JMdict]: http://www.edrdg.org/jmdict/j_jmdict.html
[JMnedict]: http://www.edrdg.org/enamdict/enamdict_doc.html
[Kanjidic]: https://www.edrdg.org/wiki/index.php/KANJIDIC_Project
[Kradfile]: https://www.edrdg.org/krad/kradinf.html
[latest-release]: https://github.com/scriptin/jmdict-simplified/releases/latest
[format]: https://scriptin.github.io/jmdict-simplified/
[npm-types]: https://www.npmjs.com/package/@scriptin/jmdict-simplified-types
[npm-loader]: https://www.npmjs.com/package/@scriptin/jmdict-simplified-loader
[AzulJava17]: https://www.azul.com/downloads/?version=java-17-lts&package=jre
[EDRDG-license]: http://www.edrdg.org/edrdg/licence.html
[CC-BY-SA-4]: http://creativecommons.org/licenses/by-sa/4.0/
[MIT]: https://opensource.org/license/mit/
