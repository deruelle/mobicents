#!/bin/sh
#
# A tool which lists the paths of all the test descriptions in a 
# set of directories/files
# 
# Syntax: <script> [-o outputPath] [-tests <testFile1> [<testFile2>, ...]]
# -o - Specifies an optional output path to write the list to
# -tests - Specifies (optional) test files and/or directories to search in. 
#   If not specified, the base test directory is used
#
# e.g.: 
# ./bin/list-test-descriptions.sh -o alltests.txt
# ./bin/list-test-descriptions.sh -o nullactivitytests.txt\
#  -tests tests/activities/nullactivity tests/javax/slee/nullactivity
#  

TEST_SUITE_ROOT=`pwd`/testsuite
DEFAULT_TESTS_DIR="tests"
ARGS=$TEST_SUITE_ROOT

# Validate current working directory
if [ ! -f bin/read-config-variables.sh ]; then
 echo "This script must be run from the root sleetck directory"; exit 1
fi
. bin/read-config-variables.sh

# Set (optional) output stream
if [ -n "$1" -a "$1" = "-o" ]; then
 ARGS="$ARGS -o $2"
 shift 2
fi 

# Set tests directory path
if [ -n "$1" -a "$1" = "-tests" ]; then
 shift 1
 ARGS="$ARGS $*"
else 
 ARGS="$ARGS $DEFAULT_TESTS_DIR"
fi

# Invoke the tool
CLASSPATH=$SLEE_LIB:$SLEETCK_LIB:$TCKTOOLS_LIB:$XML_LIB:$JAVATEST_LIB:
$JAVA_HOME/bin/java -Dorg.xml.sax.driver=org.apache.xerces.parsers.SAXParser\
 -classpath $CLASSPATH com.opencloud.sleetck.ext.SleeTCKTestLister $ARGS
