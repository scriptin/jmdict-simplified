# JSON loader for [jmdict-simplified](https://github.com/scriptin/jmdict-simplified)

[![Download JSON files](https://img.shields.io/static/v1?label=Download&message=JSON%20files&color=blue&style=for-the-badge)](https://github.com/scriptin/jmdict-simplified/releases/latest)

```shell
npm install @scriptin/jmdict-simplified-loader
```

This library is server-only, and allows you to import JSON files
(e.g. load into a database) using a streaming JSON parse
with a simple event API,  which doesn't load the whole file into memory.

This library handles JMdict, JMnedict and Kanjidic dictionary JSON files.
There are also JSON files for Kradfile and Radkfile, but those are small
enough to be fully loaded into memory, thus don't need a streaming parser.

You can also install the TypeScript types from
[`@scriptin/jmdict-simplified-types`](https://www.npmjs.com/package/@scriptin/jmdict-simplified-types):

```shell
npm install --save-dev @scriptin/jmdict-simplified-types
```

You can skip installing the types because `@scriptin/jmdict-simplified-loader` includes types
as a dependency. Unless you separate "loading" and "using" phases into separate apps/scripts
with separate dependencies.

Process the data using the simple event API (uses JSON streaming API under the hood):

```ts
// load-jmdict.ts
import { loadDictionary } from "@scriptin/jmdict-simplified-loader";

// Arguments:
// 1. dictionary type: 'jmdict' | 'jmnedict' | 'kanjidic'
// 2. path to a file: string
const loader = loadDictionary("jmdict", "path/to/jmdict-1.2.3.json")
  .onMetadata((metadata) => {
    // Process metadata
  })
  .onEntry((entry, metadata) => {
    // Load an entry into database
  })
  .onEnd(() => {
    console.log("Finished!");
  });

// To handle parsing errors:
loader.parser.on('error', (error) => {
  console.error(error);
});
```

The API is strongly typed. Based on the first argument of `loadDictionary()`
(`'jmdict' | 'jmnedict' | 'kanjidic'`), you'll get proper types
for dictionary metadata and entries.
