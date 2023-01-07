import { createReadStream } from 'fs';
import makeParser, { Parser } from 'stream-json';

import type {
  JMdictWord,
  JMnedictWord,
} from '@scriptin/jmdict-simplified-types';

import {
  MetadataHandler,
  WordHandler,
  parseMetadata,
  parseWords,
} from './parser';

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
