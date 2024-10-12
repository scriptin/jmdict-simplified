const { readdirSync, readFileSync } = require('fs');
const { join, sep } = require('path');

const { createGenerator } = require('ts-json-schema-generator');
const Ajv = require('ajv');

const { loadDictionary } = require('@scriptin/jmdict-simplified-loader');

// Make sure to build this package first, or this file may be missing/outdated
const typesFile = join(
  __dirname,
  'packages',
  'jmdict-simplified-types',
  'index.ts',
);

const schema = createGenerator({
  path: typesFile,
  type: '*',
});

function printAsJson(obj) {
  console.log(JSON.stringify(obj, null, '  '));
}

const jmdictMetadataSchema = schema.createSchema('JMdictDictionaryMetadata');
const jmdictWordSchema = schema.createSchema('JMdictWord');
const jmnedictWordSchema = schema.createSchema('JMnedictWord');

const kanjidicMetadataSchema = schema.createSchema(
  'Kanjidic2DictionaryMetadata',
);
const kanjidicCharacterSchema = schema.createSchema('Kanjidic2Character');

const kradfileSchema = schema.createSchema('Kradfile');
const radkfileSchema = schema.createSchema('Radkfile');

// printAsJson(jmdictMetadataSchema);
// printAsJson(jmdictWordSchema);
// printAsJson(jmnedictWordSchema);
// printAsJson(kanjidicMetadataSchema);
// printAsJson(kanjidicCharacterSchema);
// printAsJson(kradfileSchema);
// printAsJson(radkfileSchema);

/**
 * @typedef {Function} JMdictWordValidator
 * @param {import('@scriptin/jmdict-simplified-types').JMdictWord} word
 * @param {import('@scriptin/jmdict-simplified-types').JMdictDictionaryMetadata} metadata
 * @returns {string[]} Errors
 */

/**
 * Validate appliesTo(Kanji|Kana) arrays on sense elements
 * @param {import('@scriptin/jmdict-simplified-types').JMdictWord} word
 * @returns {string[]} Errors
 */
function jmdictValidateSenseAppliesTo(word) {
  const errors = [];
  for (const [index, sense] of word.sense.entries()) {
    const { appliesToKanji, appliesToKana } = sense;
    if (appliesToKanji.length === 0) {
      errors.push(`appliesToKanji field cannot be empty, at index ${index}`);
    } else if (appliesToKanji.includes('*') && appliesToKanji.length > 1) {
      errors.push(
        `appliesToKanji field cannot contain both "*" wildcard and other values, sense at index ${index}`,
      );
    }
    if (appliesToKana.length === 0) {
      errors.push(`appliesToKana field cannot be empty, at index ${index}`);
    } else if (appliesToKana.includes('*') && appliesToKana.length > 1) {
      errors.push(
        `appliesToKana field cannot contain both "*" wildcard and other values, sense at index ${index}`,
      );
    }
  }
  return errors;
}

/**
 * @param {import('@scriptin/jmdict-simplified-types').JMdictWord} word
 * @param {import('@scriptin/jmdict-simplified-types').DictionaryMetadata} metadata
 * @returns {string[]} Errors
 */
function jmdictValidateLanguage(word, metadata) {
  const { languages } = metadata;
  if (languages.includes('all')) return [];

  const errors = [];
  for (const [index, sense] of word.sense.entries()) {
    const glossLanguages = sense.gloss.map(({ lang }) => lang);
    const invalidLanguages = glossLanguages.filter(
      (l) => !languages.includes(l),
    );
    if (invalidLanguages.length) {
      errors.push(
        'gloss.lang field cannot contain languages outside those listed in metadata, ' +
          `found ${invalidLanguages}, sense at index ${index}`,
      );
    }
  }
  return errors;
}

/**
 * @param {import('@scriptin/jmdict-simplified-types').JMdictWord} word
 * @param {import('@scriptin/jmdict-simplified-types').DictionaryMetadata} metadata
 * @returns {string[]} Errors
 */
function jmdictValidateIsCommon(word, metadata) {
  const { commonOnly } = metadata;
  if (!commonOnly) return [];

  const errors = [];
  const { id, kanji, kana } = word;
  const kanjiCommon = kanji.filter((k) => k.common);
  const kanaCommon = kana.filter((k) => k.common);
  if (kanjiCommon.length + kanaCommon.length === 0) {
    errors.push(
      `Word with id=${id} with no "common" kanji or kana entries ` +
        'is not allowed in a dictionary with commonOnly set to true',
    );
  }
  return errors;
}

/** @type {JMdictWordValidator[]} */
const JMDICT_WORD_VALIDATORS = [
  jmdictValidateSenseAppliesTo,
  jmdictValidateLanguage,
  jmdictValidateIsCommon,
];

function reportWordAndStop(word, errors, loader) {
  console.error(
    `Invalid entry [${word.id ? 'id' : 'literal'}=${
      word.id ?? word.literal
    }]: `,
    errors,
  );
  printAsJson(word);
  loader.parser.destroy(new Error('Invalid dictionary entry'));
}

function getMetadataSchema(fileName) {
  if (fileName.startsWith('kanjidic')) return kanjidicMetadataSchema;
  return jmdictMetadataSchema;
}

function getEntrySchema(fileName) {
  if (fileName.startsWith('kanjidic')) return kanjidicCharacterSchema;
  if (fileName.startsWith('jmdict')) return jmdictWordSchema;
  return jmnedictWordSchema;
}

/**
 * Validate a JSON dictionary file, metadata and entries separately,
 * validating entries asynchronously.
 * @param {string} filePath Path to JMdict, JMnedict, or Kanjidic file
 * @returns {Promise<void>} Rejects with an error if validation failed
 */
async function validateMetadataAndEntries(filePath) {
  return new Promise((resolve, reject) => {
    const fileName = filePath.split(sep).pop();

    const validateMetadata = new Ajv().compile(getMetadataSchema(fileName));
    const validateEntry = new Ajv().compile(getEntrySchema(fileName));

    /** @type {import("@scriptin/jmdict-simplified-loader").DictionaryType} */
    const dictType = fileName.startsWith('jmdict')
      ? 'jmdict'
      : fileName.startsWith('jmnedict')
      ? 'jmnedict'
      : fileName.startsWith('kanjidic')
      ? 'kanjidic'
      : null;

    if (!dictType) {
      throw new Error(`Unknown dictionary type, fileName=${fileName}`);
    }

    const loader = loadDictionary(dictType, filePath)
      .onMetadata((metadata) => {
        validateMetadata(metadata);
        if (validateMetadata.errors && validateMetadata.errors.length) {
          console.error('Invalid metadata: ', validateMetadata.errors);
          printAsJson(metadata);
          // This Error will be caught in `parser.on('error')` handler below
          loader.parser.destroy(new Error('Invalid dictionary metadata'));
        }
      })
      .onEntry((entry, metadata) => {
        validateEntry(entry);
        if (validateEntry.errors && validateEntry.errors.length) {
          reportWordAndStop(entry, validateEntry.errors, loader);
        }
        if (dictType === 'jmdict') {
          for (const validate of JMDICT_WORD_VALIDATORS) {
            const errors = validate(entry, metadata);
            if (errors.length) {
              reportWordAndStop(entry, errors, loader);
              break;
            }
          }
        }
      })
      .onEnd(() => resolve());

    loader.parser.on('error', (error) => {
      reject(error);
    });
  });
}

/**
 * Validate a JSON dictionary file, reading the whole file into memory
 * @param {string} filePath Path to kradfile or radkfile
 * @returns {Promise<void>} Rejects with an error if validation failed
 */
async function validateWholeFile(filePath) {
  const fileName = filePath.split(sep).pop();
  const validate = new Ajv().compile(
    fileName.startsWith('kradfile') ? kradfileSchema : radkfileSchema,
  );
  const fileContents = readFileSync(filePath, { encoding: 'utf-8' });
  const dictionary = JSON.parse(fileContents);
  validate(dictionary);
  if (validate.errors && validate.errors.length) {
    console.error('Invalid format of file: ', validate.errors);
    return Promise.reject(new Error('Invalid format of file'));
  }
  return Promise.resolve();
}

/**
 * Validate all JSON dictionary files in a directory
 * @param {string} baseDir Base directory containing JSON dictionary files
 * @returns {Promise<boolean>}
 */
async function validateAll(baseDir) {
  const files = readdirSync(baseDir).filter((f) => f.endsWith('.json'));
  for (const file of files) {
    const isJMdict = file.startsWith('jmdict');
    const isWithExamples = file.includes('examples'); // Do not validate JMdict with examples
    const isJMnedict = file.startsWith('jmnedict');
    const isKanjidic = file.startsWith('kanjidic2');
    const isKradfile = file.startsWith('kradfile');
    const isRadkfile = file.startsWith('radkfile');
    const isValidFile =
      (isJMdict && !isWithExamples) || isJMnedict || isKanjidic || isKradfile || isRadkfile;
    if (!isValidFile) continue;
    console.log(`Validating ${file}...`);
    try {
      const filePath = join(baseDir, file);
      if (isKradfile || isRadkfile) {
        await validateWholeFile(filePath);
      } else {
        await validateMetadataAndEntries(filePath);
      }
      console.log('PASS');
    } catch (e) {
      console.log('FAIL');
      console.error(e);
      return false;
    }
  }
  return true;
}

const jsonDictionariesDirectory = join(__dirname, '..', 'build', 'dict-json');

validateAll(jsonDictionariesDirectory).then((success) => {
  if (success) {
    console.log('Finished successfully');
  } else {
    console.log('Finished with errors');
    process.exit(1);
  }
});
