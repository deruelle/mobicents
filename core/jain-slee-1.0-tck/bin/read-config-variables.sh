# Reads the configuration variables, and tries to set the JAVA_HOME 
# variable if not set. 
# This script exits with an error code if the script is not invoked from 
# the base TCK directory, or if the configuration file cannot be found,
# or if a JAVA_HOME cannot be chosen.

# Validate current directory
if [ ! -f ./bin/read-config-variables.sh ]; then
 echo "This script must be run from the root sleetck directory"; exit 1
fi

# Read the configuration file
CONFIG_PATH=./config/config_variables.unix
if [ ! -f $CONFIG_PATH ]; then
 echo "Configuration file $CONFIG_PATH not found"; exit 1
fi 
. $CONFIG_PATH

# Try to set the JAVA_HOME variable
if [ -z "$JAVA_HOME" ]; then
 JAVA_EXEC=`which java`
 if [ $? -ne 0 ]; then echo "Couldn't find JAVA_HOME or java executable"; exit 1; fi
 JAVA_HOME=`dirname ${JAVA_EXEC}`/..
fi
