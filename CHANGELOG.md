# Version 3.6.1

- Fix version (mistakenly set as `3.5.1`)

# Version 3.6.0

- Feature #31: Added a version of JMdict with examples (from JMdict_e_examp.xml).
  Note that it doesn't affect any other files (except the version) or NPM libraries (not updated)

# Version 3.5.0

- Added [KRADFILE/RADKFILE](https://www.edrdg.org/krad/kradinf.html)
- Minor fixes in documentation
- Bugfix/workaround for invalid format of cross-references in JMdict

# Version 3.4.0

- Added [Kanjidic](https://www.edrdg.org/wiki/index.php/KANJIDIC_Project)
- Publishing NPM packages: `@scriptin/jmdict-simplified-types`, `@scriptin/jmdict-simplified-loader`

# Version 3.3.1

- Bugfix #24: `sense.appliesToKanji` and `sense.appliesToKana` are set to `["*"]`
  (meaning "applies to all/any") by default. This was the logic of the original XQuery converter

# Version 3.3.0

- Added missing field descriptions in readme
- Added one more Xref type variant
- Added Node.js modules for JSON schema validation of dictionary JSON files
- Updated the documentation

# Version 3.2.1

- Bugfix #22: add missing "misc" tags on sense elements in JMdict

# Version 3.2.0

- Converter is fully rewritten from XQuery to Kotlin - XQuery parser was limited by memory and couldn't process large files
- Converter now supports language filtering, i.e. can produce language-specific versions (not only English)
- Converter is now capable of generating multiple output files in parallel
- Fixed an error with non-case-sensitive XML entities
- Documentation is updated to include TypeScript types, cleaner explanations, missing fields, etc.

# Version 3.1.0

- Added conversion for `g_type` attribute on `gloss` elements (words->sense->gloss->type)

# Version 3.0.1

- BaseX is no longer required as an external binary, now it's used internally as a build script dependency
- Latest source files (new data in JMnedict)

# Version 3.0.0

- :warning: Change in the format: entries' `id` fields in all dictionaries are now strings (were numbers), this is to prevent BaseX converting long numbers to exponential notation (like `1.001e+8`). ID values themselves haven't changed
- Migrated from Zorba to [BaseX](http://basex.org/), JSON output formatting changed as a result
- Migrated from bash script to Gradle build
- JMnedict is now being processed in batches and then concatenated. This way the conversion process doesn't consume all system memory
- Distribution files now have version is their names

# Version 2.0.0

- #7: Rename fields: jm(ne)dict-date -> dictDate, jm(ne)dict-revisions -> dictRevisions

# Version 1.2.0

- #6: JMnedict included
- Latest versions of both dictionaries
- Improved build script
- Updated docs
- Many minor fixes

# Version 1.1.1

- #3: Fixed duplicated XML entities text bug
- #4: Removed binary files from the repository

# Version 1.1

- #2: Add 'jmdict-date' and 'jmdict-revisions' fields
- Add 'version' field
- Fix a typo

# Version 1.0

Initial release, with full English version and shorter English version with only common kanji/kana entries.
