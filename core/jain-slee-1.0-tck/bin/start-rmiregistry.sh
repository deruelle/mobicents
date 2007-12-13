#!/bin/sh
#
# Starts the rmiregistry with the classes required by the TCK in the classpath
#
# Syntax: <script> [-b]
#  -b - if specified, runs the rmiregistry process in the background, and 
#       echoes the remiregistry process ID to stdout

if [ -n "$1" -a "$1" != "-b"  ]; then 
 echo "Syntax: $0 [-b]"
 exit 1
fi

# Validate current working directory
if [ ! -f bin/read-config-variables.sh ]; then
 echo "This script must be run from the root sleetck directory"; exit 1
fi
. bin/read-config-variables.sh

if [ -z "$VENDOR_LIB" ]; then
 echo "VENDOR_LIB variable not set. Please specify a value for VENDOR_LIB in config/config_variables.unix"; exit 1
fi

RMI_PORT=4099
SECURITY_POLICY=config/rmi-security.policy

CLASSPATH=$SLEETCK_LIB
CLASSPATH=$CLASSPATH:$SLEE_LIB
CLASSPATH=$CLASSPATH:$JMX_LIB
CLASSPATH=$CLASSPATH:$VENDOR_LIB

OPTIONS="-J-Djava.security.policy=$SECURITY_POLICY"
OPTIONS="$OPTIONS -J-classpath -J$CLASSPATH"
FULL_COMMAND="$JAVA_HOME/bin/rmiregistry $OPTIONS $RMI_PORT"

if [ "$1" = "-b" ]; then 
 $FULL_COMMAND &  
 echo $!
else 
 $FULL_COMMAND
fi
