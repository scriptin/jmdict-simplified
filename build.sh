#!/bin/bash

src=src
build=build

echo "Processing a full EN version"
zorba -i -e doc=JMdict_e.xml $src/convert-dictionary.xq > $build/jmdict_eng.json
echo "Preparing archives"
tar --directory $build -czf $build/jmdict_eng.json.tgz jmdict_eng.json
zip --junk-paths $build/jmdict_eng.json.zip $build/jmdict_eng.json

echo "Processing EN version with common words only"
zorba -i -e doc=JMdict_e.xml $src/convert-dictionary-common.xq > $build/jmdict_eng_common.json
echo "Preparing archives"
tar --directory $build -czf $build/jmdict_eng_common.json.tgz jmdict_eng_common.json
zip --junk-paths $build/jmdict_eng_common.json.zip $build/jmdict_eng_common.json

echo "Done"
