#!/bin/bash

java -Xms256m -Xmx1024m -Didea.home=/Applications/IntelliJ\ IDEA\ 9.0.app -jar lib/sbt-launch*.jar "$@"
