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
