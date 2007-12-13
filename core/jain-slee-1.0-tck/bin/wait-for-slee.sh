#!/bin/sh
#
# Waits for the SLEE to initialise, or times out.
# Note that this script does not start the SLEE itself.
#
# Syntax: <script> [<timeout-seconds>] [<wait-after-connect>]
# 
# Return codes: 
#  0 - The SLEE is initialised
#  1 - The timeout was reached
#  2 - An unexpected error occured

DEFAULT_TIMEOUT=240
DEFAULT_WAIT_AFTER_CONNECT=15
if [ -n "$1" ]; then 
 TIMEOUT=$1
else 
 TIMEOUT=$DEFAULT_TIMEOUT
fi
if [ -n "$2" ]; then 
 WAIT_AFTER_CONNECT=$2
else 
 WAIT_AFTER_CONNECT=$DEFAULT_WAIT_AFTER_CONNECT
fi


# Validate current working directory
if [ ! -f bin/read-config-variables.sh ]; then
 echo "This script must be run from the root sleetck directory"; exit 1
fi
. bin/read-config-variables.sh

if [ -z "$VENDOR_LIB" ]; then
 echo "VENDOR_LIB variable not set. Please specify a value for VENDOR_LIB in config/config_variables.unix"; exit 1
fi

CLASSPATH=$SLEETCK_LIB:$TCKTOOLS_LIB:$SLEE_LIB:$JMX_LIB:$VENDOR_LIB

echo "Waiting for the SLEE to initialise, with a timeout of ${TIMEOUT} second(s)..."
$JAVA_HOME/bin/java -classpath $CLASSPATH com.opencloud.sleetck.ext.sleemanagement.WaitForSlee $TIMEOUT $WAIT_AFTER_CONNECT

        
