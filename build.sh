#!/bin/bash

build=build

echo "Processing a full EN version"
zorba -i -e doc=JMdict_e.xml convert-dictionary.xq > $build/jmdict_eng.json
echo "Preparing archives"
tar czf $build/jmdict_eng.json.tgz $build/jmdict_eng.json
zip $build/jmdict_eng.json.zip $build/jmdict_eng.json

echo "Processing EN version with common words only"
zorba -i -e doc=JMdict_e.xml convert-dictionary-common.xq > $build/jmdict_eng_common.json
echo "Preparing archives"
tar czf $build/jmdict_eng_common.json.tgz $build/jmdict_eng_common.json
zip $build/jmdict_eng_common.json.zip $build/jmdict_eng_common.json

echo "Done"
