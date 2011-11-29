#!/bin/bash

if [ $# -eq 0 ]; then
  echo "Usage: check_class_version <minimum_language_version>"
  exit -1
fi

check_class=$(echo '{ sub(/\)/, "", $9); if ($9 > '$1') { sub(/\:/, "", $1); printf "Detected version %s in class %s\n", $9, $1 } }')

find . -name '*.class' | xargs file | awk "${check_class}"
