#!/bin/bash

doc=JMdict_e.xml
target=$1
version=$2
src=src
build=build

function create_achives() {
  name=$1
  tar --directory $build -czf $build/$name.json.tgz $name.json \
  && zip --junk-paths $build/$name.json.zip $build/$name.json
}

full=jmdict_eng
echo "Processing a full EN version" \
&& zorba -i \
  -e doc=$doc \
  -e version:=$version \
  $src/convert-dictionary.xq > $build/$full.json \
&& [ "$target" == "archives" ] && echo "Preparing archives" && create_achives $full

common=jmdict_eng_common
echo "Processing EN version with common words only" \
&& zorba -i \
  -e doc=$doc \
  -e version:=$version \
  $src/convert-dictionary-common.xq > $build/$common.json \
&& [ "$target" == "archives" ] && echo "Preparing archives" && create_achives $common

echo "Done"
