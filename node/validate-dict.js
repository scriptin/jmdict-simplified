const { readdirSync } = require('fs');
const { join, sep } = require('path');

const tsj = require('ts-json-schema-generator');
const Ajv = require('ajv');
const { loadDictionary } = require('@scriptin/jmdict-simplified-loader');

const JSON_DIR = join(__dirname, '..', 'build', 'dict-json');
const files = readdirSync(JSON_DIR).filter((f) => f.endsWith('.json'));

const schema = tsj.createGenerator({
  path: join(__dirname, 'packages', 'jmdict-simplified-types', 'index.ts'),
  type: '*',
});

const DictionaryMetadata = schema.createSchema('DictionaryMetadata');
const JMdictWord = schema.createSchema('JMdictWord');
const JMnedictWord = schema.createSchema('JMnedictWord');

// console.log(JSON.stringify(DictionaryMetadata, null, '  '));
// console.log(JSON.stringify(JMdictWord, null, '  '));
// console.log(JSON.stringify(JMnedictWord, null, '  '));

async function validate(filePath) {
  return new Promise((resolve, reject) => {
    const fileName = filePath.split(sep).pop();
    const isJMdict = fileName.startsWith('jmdict');

    const validateMetadata = new Ajv().compile(DictionaryMetadata);

    const validateWord = new Ajv().compile(
      isJMdict ? JMdictWord : JMnedictWord,
    );

    const loader = loadDictionary(filePath)
      .onMetadata((metadata) => {
        validateMetadata(metadata);
        if (validateMetadata.errors && validateMetadata.errors.length) {
          console.error('Invalid metadata: ', validateMetadata.errors);
          reject();
        }
      })
      .onWord((word) => {
        validateWord(word);
        if (validateWord.errors && validateWord.errors.length) {
          console.error(`Invalid word [id=${word.id}]: `, validateWord.errors);
          console.log(JSON.stringify(word, null, '  '));
          loader.parser.pause();
          reject();
        }
      })
      .onEnd(() => resolve());

    loader.parser.on('error', (error) => {
      reject(error);
    });
  });
}

async function validateAll(files) {
  for (const file of files) {
    const isJMdict = file.startsWith('jmdict');
    const isJMnedict = file.startsWith('jmnedict');
    if (!isJMdict && !isJMnedict) continue;
    console.log('Validating', file);
    try {
      await validate(join(JSON_DIR, file));
      console.log('PASS');
    } catch (e) {
      console.log('FAIL');
      console.error(e);
    }
  }
}

validateAll(files).then(() => console.log('DONE'));
