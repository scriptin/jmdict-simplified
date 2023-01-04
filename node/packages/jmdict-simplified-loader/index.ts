import { createReadStream } from 'fs';
import makeParser from 'stream-json';

import type { DictionaryMetadata, JMdictWord } from '@scriptin/jmdict-simplified-types';

export interface JMdictLoader {
  onMetadata(handler: (metadata: DictionaryMetadata) => void);
  onWord(handler: (word: JMdictWord) => void);
  onEnd(handler: () => void);
  onError(handler: (error: Error | string) => void);
}

export function parseJmdict(path: string): JMdictLoader {
  const pipeline = createReadStream(path).pipe(makeParser({ packValues: true }));

  return {
    onMetadata(handler) {
      // TODO
    },

    onWord(handler) {
      // TODO
    },

    onEnd(handler) {
      pipeline.on('end', handler);
    },

    onError(handler) {
      // TODO
    },
  } as JMdictLoader;
}
