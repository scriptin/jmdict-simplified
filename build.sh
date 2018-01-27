#!/bin/bash

#####################
## Input arguments ##
#####################

command=$1
dict=$2
version=$3

###############
## Constants ##
###############

jmdict_xml=JMdict_e.xml
jmnedict_xml=JMnedict.xml

jmdict_full=jmdict_eng
jmdict_common=jmdict_eng_common

# Build directory
build=build

# Semver testing: https://github.com/fsaintjacques/semver-tool
semver_regex="^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(\-[0-9A-Za-z-]+(\.[0-9A-Za-z-]+)*)?(\+[0-9A-Za-z-]+(\.[0-9A-Za-z-]+)*)?$"

help=$'Usage examples:
> ./build.sh help -- show this help message
> ./build.sh download -- download source dictionary files
> ./build.sh convert jmdict 1.2.3 -- convert only JMdict, version 1.2.3
> ./build.sh convert jmnedict 1.2.3 -- convert only JMnedict, version 1.2.3
> ./build.sh convert all 1.2.3 -- convert all, version 1.2.3
> ./build.sh archive -- create distribution archives
'

################
## Validation ##
################

if [[ "$command" != "help" ]] && [[ "$command" != "download" ]] && [[ "$command" != "convert" ]] && [[ "$command" != "archive" ]]; then
  echo "Error: Unknown command \"$command\", please use \"help\", \"download\", \"convert\", or \"archive\""
  exit 1
fi

if [[ "$command" == "convert" ]]; then
  if [[ "$dict" != "jmdict" ]] && [[ "$dict" != "jmnedict" ]] && [[ "$dict" != "all" ]]; then
    echo "Error: Unknown dictionary \"$dict\", please use \"jmdict\", \"jmnedict\", or \"all\""
    exit 1
  fi
  if [[ ! "$version" =~ $semver_regex ]]; then
    echo "Error: Version \"$version\" does not match the scheme 'X.Y.Z(-PRERELEASE)(+BUILD)', see <https://semver.org/>"
    exit 1
  fi
fi

###############
## Functions ##
###############

function extract_tags() {
  infile=$1
  outfile=$2
  echo "xquery version \"3.0\";"                                                                            > $outfile
  echo "module namespace tags = \"tags\";"                                                                 >> $outfile
  echo ""                                                                                                  >> $outfile
  echo "import module namespace tags-utils = \"tags-utils\" at \"../tags-utils.xq\";"                      >> $outfile
  echo ""                                                                                                  >> $outfile
  echo "(: This file is generated, do not edit manually! :)"                                               >> $outfile
  echo ""                                                                                                  >> $outfile
  echo "declare function tags:convert-entity(\$word-id as xs:string, \$text as xs:string) as xs:string? {" >> $outfile
  echo "  tags:convert(\$word-id, tags-utils:deduplicate(normalize-space(\$text)))"                        >> $outfile
  echo "};"                                                                                                >> $outfile
  echo ""                                                                                                  >> $outfile
  echo "declare function tags:convert(\$word-id as xs:string, \$text as xs:string) as xs:string? {"        >> $outfile
  echo "  switch(\$text)"                                                                                  >> $outfile
  cat $infile | grep ENTITY | sed -e 's/^<!ENTITY\s\+\(.\+\)\s\+"\(.\+\)">$/  case \"\2\" return \"\1\"/g' >> $outfile
  echo "  default return error("                                                                           >> $outfile
  echo "    xs:QName(\"unknown-tag\"),"                                                                    >> $outfile
  echo "    concat(\"Unknown tag '\", \$text, \"' on entity \", \$word-id)"                                >> $outfile
  echo "  )"                                                                                               >> $outfile
  echo "};"                                                                                                >> $outfile
  echo ""                                                                                                  >> $outfile
  echo "declare variable \$tags:tags := <pair name=\"tags\" type=\"object\">"                              >> $outfile
  cat $infile | grep ENTITY \
    | sed -e 's/^<!ENTITY\s\+\(.\+\)\s\+"\(.\+\)">$/  <pair name=\"\1\" type=\"string\">\2<\/pair>/g' \
    | sed -e "s/\`/\&apos;/g" | sed -e "s/'/\&apos;/g"                                                     >> $outfile
  echo "</pair>;"                                                                                          >> $outfile
}

function create_achives() {
  name=$1
  tar --directory $build -czf $build/$name.json.tgz $name.json \
  && zip --junk-paths $build/$name.json.zip $build/$name.json
}

##########
## Help ##
##########

if [[ "$command" == "help" ]]; then
  echo "$help"
  exit 0
fi

##############
## Download ##
##############

if [[ "$command" == "download" ]]; then
  echo "-> Downloading JMdict"
  curl ftp://ftp.monash.edu.au/pub/nihongo/JMdict_e.gz | gunzip > $jmdict_xml
  echo "-> Downloading JMnedict"
  curl http://ftp.monash.edu/pub/nihongo/JMnedict.xml.gz | gunzip > $jmnedict_xml
  echo "-> Done"
  exit 0
fi

#############
## Convert ##
#############

if [[ "$command" == "convert" ]]; then

  # JMdict
  if [[ "$dict" == "jmdict" ]] || [[ "$dict" == "all" ]]; then

    echo "-> Converting JMdict"

    doc=$jmdict_xml
    src=src/jmdict

    echo "  -> Extracting tags"
    extract_tags $doc $src/tags.xq

    # Note:
    # Operator ":=" is intentional in zorba's arguments "--external-variable"
    # Operator "=" is used for files only
    echo "  -> Converting English version, full"
    zorba --indent \
      --external-variable doc=$doc \
      --external-variable version:=$version \
      $src/convert-dictionary.xq > $build/$jmdict_full.json

    echo "  -> Converting English version, common words only"
    zorba --indent \
      --external-variable doc=$doc \
      --external-variable version:=$version \
      $src/convert-dictionary-common.xq > $build/$jmdict_common.json

  fi

  # JMnedict
  if [[ "$dict" == "jmnedict" ]] || [[ "$dict" == "all" ]]; then

    echo "-> Converting JMnedict"

    doc=$jmnedict_xml
    src=src/jmnedict

    echo "  -> Extracting tags"
    extract_tags $doc $src/tags.xq

    echo "  -> TODO"

  fi

  echo "-> Done"
  exit 0
fi

#############
## Archive ##
#############

if [[ "$command" == "archive" ]]; then
  rm -f $build/*.{tgz,zip}

  echo "-> Creating archives for JMdict"
  echo "  -> Creating archives for English version, full"
  create_achives $jmdict_full
  echo "  -> Creating archives for English version, common words only"
  create_achives $jmdict_common

  echo "-> Creating archives for JMnedict"
  echo "  -> TODO"

  echo "-> Done"
  exit 0
fi
