#!/bin/sh
#
# Starts the JavaTest harness
#

# Validate current working directory
if [ ! -f bin/read-config-variables.sh ]; then
 echo "This script must be run from the root sleetck directory"; exit 1
fi
. bin/read-config-variables.sh

if [ -z "$VENDOR_LIB" ]; then
 echo "VENDOR_LIB variable not set. Please specify a value for VENDOR_LIB in config/config_variables.unix"; exit 1
fi

SECURITY_POLICY=config/tck-security.policy
CLASSPATH=$SLEETCK_LIB:$JAVATEST_LIB:$SIGTEST_LIB:$SLEE_LIB:$JMX_LIB:$XML_LIB:$VENDOR_LIB:$JTA_LIB
OPTIONS="-Djava.security.manager"
OPTIONS="$OPTIONS -Djava.security.policy=$SECURITY_POLICY"
OPTIONS="$OPTIONS -Djavatest.security.allowPropertiesAccess=true"
OPTIONS="$OPTIONS -Dorg.xml.sax.driver=org.apache.xerces.parsers.SAXParser"
OPTIONS="$OPTIONS "

$JAVA_HOME/bin/java -classpath $CLASSPATH $OPTIONS com.sun.javatest.tool.Main $*
