/**
 * @description Copy types from project README into `index.ts`
 */

import { readFileSync, writeFileSync } from 'fs';
import { dirname } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));

const readme = readFileSync(`${__dirname}/../../../README.md`, { encoding: 'utf-8' });
const lines = readme.split("\n");

const typesStart = lines.findIndex(line => /^```(ts|typescript)\s*$/.test(line));
const typesEnd = lines.findIndex((line, index) => /^```\s*$/.test(line) && index > typesStart);

const packageJson = readFileSync(`${__dirname}/package.json`, { encoding: 'utf-8' });
const packageObject = JSON.parse(packageJson);

const typesLines = [`declare module "${packageObject.name}";`].concat(lines.slice(typesStart + 1, typesEnd));

writeFileSync(`${__dirname}/index.ts`, typesLines.join("\n"), { encoding: "utf-8" });
