# TypeScript types for [jmdict-simplified][]

[![Download JSON files](https://img.shields.io/static/v1?label=Download&message=JSON%20files&color=blue&style=for-the-badge)](https://github.com/scriptin/jmdict-simplified/releases/latest)
[![Format documentation](https://img.shields.io/static/v1?label=Read&message=Format%20docs&color=blue&style=for-the-badge)](https://scriptin.github.io/jmdict-simplified/)

```shell
npm install --save-dev @scriptin/jmdict-simplified-types
```

Includes types for JSON files in [jmdict-simplified][]:

- JMdict
- JMnedict
- Kanjidic
- KRADFILE/RADKFILE

## Usage

### Option 1: using JSON files directly

Install the types:

```shell
npm install --save-dev @scriptin/jmdict-simplified-types
```

You should use `--save-dev` because this package only includes type definitions and no code.

With Node.js:

```ts
// app.ts
import type { Kanjidic2 } from "@scriptin/jmdict-simplified-types";

const json = JSON.parse(
  readFileSync('path/to/kanjidic2-1.2.3.json', { encoding: 'utf-8' })
) as Kanjidic2;
```

> **Note** Some JSON files can be large and won't fit into memory,
> but it should work fine for Kanjidic, JMnedict, KRADFILE/RADKFILE,
> and filtered (by language or "common-only") versions of JMdict

With bundlers (e.g. Webpack, Vite) which support importing JSON via loaders/plugins:

```ts
// app.ts
import type { Kanjidic2 } from "@scriptin/jmdict-simplified-types";
import kanjidicJson from "path/to/kanjidic2-1.2.3.json";

const kanjidic = kanjidicJson as Kanjidic2;
```

### Option 2: using [@scriptin/jmdict-simplified-loader](https://www.npmjs.com/package/@scriptin/jmdict-simplified-loader)

This method works with server only, and allows you to import JSON files
(e.g. load into a database) using a streaming JSON parser,
which doesn't load the whole file into memory.
This is ideal when working with full version of JMdict JSON file,
but works for JMnedict and Kanjidic as well.

Install the loader:

```shell
npm install @scriptin/jmdict-simplified-loader
```

You can skip installing the types because `@scriptin/jmdict-simplified-loader` includes types
as a dependency. Unless you separate "loading" and "using" phases into separate apps/scripts
with separate dependencies.

Process the data using the simple event API (uses JSON streaming API under the hood):

```ts
// load-jmdict.ts
import { loadDictionary } from "@scriptin/jmdict-simplified-loader";

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

[jmdict-simplified]: https://github.com/scriptin/jmdict-simplified
