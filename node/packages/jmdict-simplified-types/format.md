# TypeScript types for [jmdict-simplified](https://github.com/scriptin/jmdict-simplified)

<!-- This file is used for generated API documentation, refer to README.md for package documentation -->

[![Download JSON files](https://img.shields.io/static/v1?label=Download&message=JSON%20files&color=blue&style=for-the-badge)](https://github.com/scriptin/jmdict-simplified/releases/latest)

NPM packages:

- [@scriptin/jmdict-simplified-types][types] - TypeScript types described in this documentation
- [@scriptin/jmdict-simplified-loader][loader] - loader for JSON files

## Dictionary root types

- [JMdict](interfaces/JMdict.html)
- [JMnedict](interfaces/JMnedict.html)
- [Kanjidic2](interfaces/Kanjidic2.html)
- [Kradfile](interfaces/Kradfile.html)
- [Radkfile](interfaces/Radkfile.html)

## Metadata

- [DictionaryMetadata](interfaces/DictionaryMetadata.html) - base interface
  - [JMdictDictionaryMetadata](interfaces/JMdictDictionaryMetadata.html) - both JMdict and JMnedict
  - [Kanjidic2DictionaryMetadata](interfaces/Kanjidic2DictionaryMetadata.html)

## Entries (words or characters)

- [JMdictWord](types/JMdictWord.html)
- [JMnedictWord](types/JMnedictWord.html)
- [Kanjidic2Character](types/Kanjidic2Character.html)

## Languages

- JMdict and JMnedict use 3-letters ISO 639-2 codes - [Language3Letter](types/Language3Letter.html)
- Kanjidic uses 2-letters ISO 639-1 codes - [Language2Letter](types/Language2Letter.html)

[types]: https://www.npmjs.com/package/@scriptin/jmdict-simplified-types
[loader]: https://www.npmjs.com/package/@scriptin/jmdict-simplified-loader
