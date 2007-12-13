#!/bin/sh
#
# Inserts a valid checksum into the given JAIN SLEE TCK configuration interview file
# 
# Syntax: <script> [<interview-file-path>]
#  interview-file-path: the path of the interview file, eg config/sleetck.jti
#  If not specified, the default interview file is processed
#  

DEFAULT_FILE="config/sleetck.jti"

# Validate current working directory
if [ ! -f bin/read-config-variables.sh ]; then
 echo "This script must be run from the root sleetck directory"; exit 1
fi
. bin/read-config-variables.sh

# Set file path
if [ -n "$1" ]; then
 FILE="$1"
else 
 FILE=$DEFAULT_FILE
fi

# Process the file

CLASSPATH=$SLEETCK_LIB:$JAVATEST_LIB:$SLEE_LIB:$TCKTOOLS_LIB
$JAVA_HOME/bin/java -classpath $CLASSPATH\
 com.opencloud.sleetck.ext.InterviewChecksumTool $FILE
