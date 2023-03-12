import { createReadStream } from 'fs';
import makeParser, { Parser } from 'stream-json';

import type {
  JMdictWord,
  JMnedictWord,
  Kanjidic2Character,
} from '@scriptin/jmdict-simplified-types';

import {
  MetadataHandler,
  EntryHandler,
  parseMetadata,
  parseEntries,
} from './parser';

export interface DictionaryLoader<
  W extends JMdictWord | JMnedictWord | Kanjidic2Character,
> {
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
  onEntry(handler: EntryHandler<W>): DictionaryLoader<W>;

  /**
   * @param handler is called when JSON stream has ended.
   * Alias for `parser.on('end', handler)`
   * @returns self for method chaining
   */
  onEnd(handler: () => void): DictionaryLoader<W>;
}

export function loadDictionary<
  W extends JMdictWord | JMnedictWord | Kanjidic2Character,
>(filePath: string): DictionaryLoader<W> {
  const parser = createReadStream(filePath).pipe(
    makeParser({ packValues: true }),
  );

  let entryHandler: EntryHandler<W> | null = null;

  return {
    parser,

    onMetadata(handler) {
      parseMetadata(parser, (metadata) => {
        handler(metadata);
        if (entryHandler) {
          parseEntries<W>(parser, entryHandler);
        }
      });
      return this;
    },

    onEntry(handler) {
      entryHandler = handler;
      return this;
    },

    onEnd(handler) {
      parser.on('end', handler);
      return this;
    },
  } as DictionaryLoader<W>;
}
