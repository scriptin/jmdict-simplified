import { createReadStream } from 'fs';
import makeParser, { Parser } from 'stream-json';

import type {
  DictionaryMetadata,
  JMdictWord,
  JMnedictWord,
} from '@scriptin/jmdict-simplified-types';

export type MetadataHandler = (metadata: DictionaryMetadata) => void;

export type WordHandler<W extends JMdictWord | JMnedictWord> = (
  word: W,
) => void;

export interface DictionaryLoader<W extends JMdictWord | JMnedictWord> {
  /**
   * In case you need low-level access to JSON stream.
   */
  parser: Parser;

  /**
   * @param handler is called after all metadata fields were parsed,
   *                and words array has just started
   * @returns self for method chaining
   */
  onMetadata(handler: MetadataHandler): DictionaryLoader<W>;

  /**
   * @param handler is called after each subsequent word has been parsed
   * @returns self for method chaining
   */
  onWord(handler: WordHandler<W>): DictionaryLoader<W>;

  /**
   * @param handler is called when JSON stream has ended.
   * Alias for `parser.on('end', handler)`
   * @returns self for method chaining
   */
  onEnd(handler: () => void): DictionaryLoader<W>;
}

type Path = (string | number)[];

function put(obj: object, path: Path, value: any) {
  if (path.length === 0) {
    throw new Error('Empty path is not allowed');
  }
  let current: object | Array<any> = obj;
  let last = path[path.length - 1];
  for (const [index, step] of path.entries()) {
    if (index === path.length - 1) break;
    if (typeof step === 'string') {
      current = (current as any)[step];
    } else {
      current = (current as Array<any>)[step];
    }
  }
  if (
    Array.isArray(current) &&
    typeof last === 'number' &&
    current.length === last
  ) {
    // lastKey is an index (number), but we always append
    current.push(value);
  } else if (typeof current === 'object' && typeof last === 'string') {
    (current as any)[last] = value;
  } else {
    throw new Error(
      `Invalid state: typeof(current)=${typeof current}, path=[${path}]`,
    );
  }
}

type DataChunk = {
  name: string;
  value?: string | number | boolean | null;
};

function updatePathAfterValue(path: Path) {
  if (path.length === 0) return;
  const last = path[path.length - 1];
  if (typeof last === 'string') {
    // we're building an object and just received a new value for a pending key
    path.pop();
  } else {
    // we're building an array, and just received a new item
    path.pop();
    path.push(last + 1);
  }
}

function parseMetadata(parser: Parser, handler: MetadataHandler) {
  const metadata: Partial<DictionaryMetadata> = {};
  let foundsWordsKey = false;
  const path: Path = [];
  parser.on('data', function parserDataHandler({ name, value }: DataChunk) {
    switch (name) {
      case 'startObject':
        if (path.length) put(metadata, path, {});
        return;
      case 'endObject':
        updatePathAfterValue(path);
        return;
      case 'keyValue':
        if (value === 'words') {
          foundsWordsKey = true;
        } else {
          path.push(value as string);
          put(metadata, path, undefined);
        }
        return;
      case 'startArray':
        if (foundsWordsKey) {
          // Array of words have started
          parser.off('data', parserDataHandler);
          handler(metadata as DictionaryMetadata);
        } else {
          put(metadata, path, []);
          path.push(0);
        }
        return;
      case 'endArray':
        path.pop();
        updatePathAfterValue(path);
        return;
      case 'stringValue':
      case 'numberValue':
      case 'trueValue':
      case 'falseValue':
      case 'nullValue':
        put(metadata, path, value);
        updatePathAfterValue(path);
        return;
    }
  });
}

function parseWords<W extends JMdictWord | JMnedictWord>(
  parser: Parser,
  handler: WordHandler<W>,
) {
  let word: Partial<W> = {};
  const path: Path = [];
  parser.on('data', ({ name, value }: DataChunk) => {
    switch (name) {
      case 'startObject':
        if (path.length) put(word, path, {});
        return;
      case 'endObject':
        updatePathAfterValue(path);
        if (path.length === 0) {
          handler(word as W);
          word = {};
        }
        return;
      case 'keyValue':
        path.push(value as string);
        put(word, path, undefined);
        return;
      case 'startArray':
        put(word, path, []);
        path.push(0);
        return;
      case 'endArray':
        path.pop();
        updatePathAfterValue(path);
        return;
      case 'stringValue':
      case 'numberValue':
      case 'trueValue':
      case 'falseValue':
      case 'nullValue':
        put(word, path, value);
        updatePathAfterValue(path);
        return;
    }
  });
}

export function loadDictionary<W extends JMdictWord | JMnedictWord>(
  filePath: string,
): DictionaryLoader<W> {
  const parser = createReadStream(filePath).pipe(
    makeParser({ packValues: true }),
  );

  let wordHandler: WordHandler<W> | null = null;

  return {
    parser,

    onMetadata(handler) {
      parseMetadata(parser, (metadata) => {
        handler(metadata);
        if (wordHandler) {
          parseWords<W>(parser, wordHandler);
        }
      });
      return this;
    },

    onWord(handler) {
      wordHandler = handler;
      return this;
    },

    onEnd(handler) {
      parser.on('end', handler);
      return this;
    },
  } as DictionaryLoader<W>;
}
