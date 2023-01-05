/**
 * @description Copy types from project README into `index.ts`
 */

const { readFileSync, writeFileSync } = require('fs');

const defaultFileOpts = {
  encoding: 'utf-8',
};

const readme = readFileSync(`${__dirname}/../../../README.md`, defaultFileOpts);
const lines = readme.split('\n');

const typesStart = lines.findIndex((line) =>
  /^```(ts|typescript)\s*$/.test(line),
);
const typesEnd = lines.findIndex(
  (line, index) => /^```\s*$/.test(line) && index > typesStart,
);

const packageJson = readFileSync(`${__dirname}/package.json`, defaultFileOpts);
const packageObject = JSON.parse(packageJson);

const typesLines = [`declare module "${packageObject.name}";`].concat(
  lines.slice(typesStart + 1, typesEnd),
);

writeFileSync(`${__dirname}/index.ts`, typesLines.join('\n'), defaultFileOpts);
