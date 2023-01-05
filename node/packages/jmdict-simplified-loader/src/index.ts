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
  onMetadata(handler: MetadataHandler): void;
  onWord(handler: WordHandler<W>): void;
  onEnd(handler: () => void): void;
}

function parseJMdictMetadata(parser: Parser, handler: MetadataHandler) {
  // TODO
}

function parseJMdictWord(parser: Parser, handler: WordHandler<JMdictWord>) {
  // TODO
}

export function parseJmdict(path: string): DictionaryLoader<JMdictWord> {
  const pipeline = createReadStream(path).pipe(
    makeParser({ packValues: true }),
  );

  return {
    onMetadata(handler) {
      parseJMdictMetadata(pipeline, handler);
    },

    onWord(handler) {
      parseJMdictWord(pipeline, handler);
    },

    onEnd(handler) {
      pipeline.on('end', handler);
    },
  } as DictionaryLoader<JMdictWord>;
}
