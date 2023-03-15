# TypeScript types for [jmdict-simplified](https://github.com/scriptin/jmdict-simplified)

<!-- This file is used for generated API documentation, refer to README.md for package documentation -->

You can find the JSON files here:

[![Download JSON files](https://img.shields.io/static/v1?label=Download&message=JSON%20files&color=blue&style=for-the-badge)](https://github.com/scriptin/jmdict-simplified/releases/latest)

You can find NPM packages here:

- [@scriptin/jmdict-simplified-types][types] - a package with the types described in this API documentation
- [@scriptin/jmdict-simplified-loader][loader] - a package with a loader for JSON files

## Dictionary root types

- [JMdict](interfaces/JMdict.html)
- [JMnedict](interfaces/JMnedict.html)
- [Kanjidic2](interfaces/Kanjidic2.html)

## Metadata

- [DictionaryMetadata](interfaces/DictionaryMetadata.html) - base interface
  - [JMdictDictionaryMetadata](interfaces/JMdictDictionaryMetadata.html) - both JMdict and JMnedict
  - [Kanjidic2DictionaryMetadata](interfaces/Kanjidic2DictionaryMetadata.html)

## Entries (words or characters)

- [JMdictWord](types/JMdictWord.html)
- [JMnedictWord](types/JMnedictWord.html)
- [Kanjidic2Character](types/Kanjidic2Character.html)

[types]: https://www.npmjs.com/package/@scriptin/jmdict-simplified-types
[loader]: https://www.npmjs.com/package/@scriptin/jmdict-simplified-loader
