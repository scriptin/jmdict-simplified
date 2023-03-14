import { createReadStream } from 'fs';
import makeParser, { Parser } from 'stream-json';

import type {
  JMdictDictionaryMetadata,
  JMdictWord,
  JMnedictWord,
  Kanjidic2Character,
  Kanjidic2DictionaryMetadata,
} from '@scriptin/jmdict-simplified-types';

import {
  Entry,
  Metadata,
  EntryHandler,
  MetadataHandler,
  parseMetadata,
  parseEntries,
} from './parser';

export interface DictionaryLoader<M extends Metadata, E extends Entry> {
  /**
   * In case you need low-level access to JSON stream.
   */
  parser: Parser;

  /**
   * @param handler is called after all metadata fields were parsed,
   *                and words array has just started
   * @returns self for method chaining
   */
  onMetadata(handler: MetadataHandler<M>): DictionaryLoader<M, E>;

  /**
   * @param handler is called after each subsequent word has been parsed
   * @returns self for method chaining
   */
  onEntry(handler: EntryHandler<M, E>): DictionaryLoader<M, E>;

  /**
   * @param handler is called when JSON stream has ended.
   * Alias for `parser.on('end', handler)`
   * @returns self for method chaining
   */
  onEnd(handler: () => void): DictionaryLoader<M, E>;
}

export type DictionaryType = 'jmdict' | 'jmnedict' | 'kanjidic';

export function loadDictionary(
  dictionaryType: 'jmdict',
  filePath: string,
): DictionaryLoader<JMdictDictionaryMetadata, JMdictWord>;
export function loadDictionary(
  dictionaryType: 'jmnedict',
  filePath: string,
): DictionaryLoader<JMdictDictionaryMetadata, JMnedictWord>;
export function loadDictionary(
  dictionaryType: 'kanjidic',
  filePath: string,
): DictionaryLoader<Kanjidic2DictionaryMetadata, Kanjidic2Character>;

export function loadDictionary<M extends Metadata, E extends Entry>(
  dictionaryType: DictionaryType,
  filePath: string,
): DictionaryLoader<M, E> {
  const parser = createReadStream(filePath).pipe(
    makeParser({ packValues: true }),
  );

  let entryHandler: EntryHandler<M, E> | null = null;

  return {
    parser,

    onMetadata(handler) {
      parseMetadata<M>(parser, (metadata) => {
        handler(metadata);
        if (entryHandler) {
          parseEntries<M, E>(parser, metadata, entryHandler);
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
  } as DictionaryLoader<M, E>;
}
