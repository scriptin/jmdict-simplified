const { readdirSync } = require('fs');
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

const dictionaryMetadataSchema = schema.createSchema('DictionaryMetadata');
const jmdictWordSchema = schema.createSchema('JMdictWord');
const jmnedictWordSchema = schema.createSchema('JMnedictWord');

// printAsJson(dictionaryMetadataSchema);
// printAsJson(jmdictWordSchema);
// printAsJson(jmnedictWordSchema);

function printAsJson(obj) {
  console.log(JSON.stringify(obj, null, '  '));
}

/**
 * Validate appliesTo(Kanji|Kana) arrays on sense elements
 * @param {import('@scriptin/jmdict-simplified-loader').JMdictWord} word
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
        `appliesToKanji field cannot contain both "*" wildcard and other values, at index ${index}`,
      );
    }
    if (appliesToKana.length === 0) {
      errors.push(`appliesToKana field cannot be empty, at index ${index}`);
    } else if (appliesToKana.includes('*') && appliesToKana.length > 1) {
      errors.push(
        `appliesToKana field cannot contain both "*" wildcard and other values, at index ${index}`,
      );
    }
  }
  return errors;
}

/**
 * Validate a JSON dictionary file
 * @param {string} filePath
 * @returns {Promise<void>} Rejects with an error if validation failed
 */
async function validate(filePath) {
  return new Promise((resolve, reject) => {
    const fileName = filePath.split(sep).pop();
    const isJMdict = fileName.startsWith('jmdict');

    const validateMetadata = new Ajv().compile(dictionaryMetadataSchema);

    const validateWord = new Ajv().compile(
      isJMdict ? jmdictWordSchema : jmnedictWordSchema,
    );

    const loader = loadDictionary(filePath)
      .onMetadata((metadata) => {
        validateMetadata(metadata);
        if (validateMetadata.errors && validateMetadata.errors.length) {
          console.error('Invalid metadata: ', validateMetadata.errors);
          printAsJson(metadata);
          // This Error will be caught in `parser.on('error')` handler below
          loader.parser.destroy(new Error('Invalid dictionary metadata'));
        }
      })
      .onWord((word) => {
        validateWord(word);
        if (validateWord.errors && validateWord.errors.length) {
          console.error(`Invalid word [id=${word.id}]: `, validateWord.errors);
          printAsJson(word);
          loader.parser.destroy(new Error('Invalid dictionary entry'));
        }
        if (isJMdict) {
          const errors = jmdictValidateSenseAppliesTo(
            /** @type import('@scriptin/jmdict-simplified-loader').JMdictWord */ word,
          );
          if (errors.length) {
            console.error(`Invalid word [id=${word.id}]: `, errors);
            printAsJson(word);
            loader.parser.destroy(new Error('Invalid dictionary entry'));
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
 * Validate all JSON dictionary files in a directory
 * @param {string} baseDir Base directory containing JSON dictionary files
 * @returns {Promise<boolean>}
 */
async function validateAll(baseDir) {
  const files = readdirSync(baseDir).filter((f) => f.endsWith('.json'));
  for (const file of files) {
    const isJMdict = file.startsWith('jmdict');
    const isJMnedict = file.startsWith('jmnedict');
    if (!isJMdict && !isJMnedict) continue;
    console.log(`Validating ${file}...`);
    try {
      await validate(join(baseDir, file));
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
