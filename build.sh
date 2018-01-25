#!/bin/bash

#####################
## Input arguments ##
#####################

type=$1
version=$2
archive=$3

###############
## Constants ##
###############

help=$'Usage examples:
> ./build.sh jmdict 1.2.3 -- build only JMdict, version 1.2.3
> ./build.sh jmnedict 1.2.3 -- build only JMnedict, version 1.2.3
> ./build.sh all 1.2.3 archive  -- build all, version 1.2.3, and create archives
'

# Build directory
build=build

# Semver testing: https://github.com/fsaintjacques/semver-tool
semver_regex="^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(\-[0-9A-Za-z-]+(\.[0-9A-Za-z-]+)*)?(\+[0-9A-Za-z-]+(\.[0-9A-Za-z-]+)*)?$"

################
## Validation ##
################

if [[ "$type" == "help" ]] || [[ "$type" == "--help" ]] || [[ "$type" == "-h" ]] || [[ "$type" == "-?" ]]; then
  echo "$help"
  exit 0
fi

if [[ "$type" != "jmdict" ]] && [[ "$type" != "jmnedict" ]] && [[ "$type" != "all" ]]; then
  echo "Error: Unknown type \"$type\", please use \"jmdict\", \"jmnedict\", or \"all\""
  echo "$help"
  exit 1
fi

if [[ ! "$version" =~ $semver_regex ]]; then
  echo "Error: Version \"$version\" does not match the semver scheme 'X.Y.Z(-PRERELEASE)(+BUILD)', see <https://semver.org/>"
  echo "$help"
  exit 1
fi

if [[ "$archive" != "archive" ]] && [[ ! -z "$archive" ]]; then
  echo "Error: The last argument must be either \"archive\" or omitted"
  echo "$help"
  exit 1
fi

###########
## Utils ##
###########

function create_achives() {
  name=$1
  tar --directory $build -czf $build/$name.json.tgz $name.json \
  && zip --junk-paths $build/$name.json.zip $build/$name.json
}

# Note:
# Operator ":=" is intentional in zorba's arguments "--external-variable"
# Operator "=" is used for files only

############
## JMdict ##
############

if [[ "$type" == "jmdict" ]] || [[ "$type" == "all" ]]; then

  echo "-> Processing JMdict"

  doc=JMdict_e.xml
  src=src/jmdict

  full=jmdict_eng
  echo "  -> English version, full"
  zorba --indent \
    --external-variable doc=$doc \
    --external-variable version:=$version \
    $src/convert-dictionary.xq > $build/$full.json \
  && [[ "$archive" == "archive" ]] && echo "    -> Preparing archives" && create_achives $full

  common=jmdict_eng_common
  echo "  -> English version, common words only"
  zorba --indent \
    --external-variable doc=$doc \
    --external-variable version:=$version \
    $src/convert-dictionary-common.xq > $build/$common.json \
  && [[ "$archive" == "archive" ]] && echo "    -> Preparing archives" && create_achives $common

fi

##############
## JMnedict ##
##############

if [[ "$type" == "jmnedict" ]] || [[ "$type" == "all" ]]; then

  echo "-> Processing JMnedict"

  doc=JMnedict.xml
  src=src/jmnedict

  echo "  -> TODO"

fi

echo "-> Done"
