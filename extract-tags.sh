#!/bin/bash

infile=JMdict_e.xml
outfile=src/tags.xq

echo "xquery version \"3.0\";" > $outfile
echo "module namespace tags = \"tags\";" >> $outfile

echo "" >> $outfile

echo "(: This file is generated, do not edit manually! :)" >> $outfile

echo "" >> $outfile

echo "declare function tags:convert-entity(\$word-id as xs:string, \$text as xs:string) as xs:string? {" >> $outfile
echo "  switch(\$text)" >> $outfile
cat $infile | grep ENTITY | sed -e 's/^<!ENTITY\s\+\(.\+\)\s\+"\(.\+\)">$/  case \"\2\" return \"\1\"/g' >> $outfile
echo "  default return error(xs:QName(\"unknown-tag\"), concat(\"Unknown tag '\", \$text, \"' on entity \", \$word-id))" >> $outfile
echo "};" >> $outfile

echo "" >> $outfile

echo "declare variable \$tags:tags := <pair name=\"tags\" type=\"object\">" >> $outfile
cat $infile | grep ENTITY \
  | sed -e 's/^<!ENTITY\s\+\(.\+\)\s\+"\(.\+\)">$/  <pair name=\"\1\" type=\"string\">\2<\/pair>/g' \
  | sed -e "s/\`/\&apos;/g" \
  | sed -e "s/'/\&apos;/g" >> $outfile
echo "</pair>;" >> $outfile
