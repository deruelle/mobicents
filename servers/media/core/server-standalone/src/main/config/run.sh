#!/bin/sh

java -Djava.ext.dirs=`pwd`/lib -cp .:mms-standalone-${pom.version}.jar org.mobicents.media.server.standalone.MMSBootstrap
