#/bin/ksh
#This script activates the ASTERISK RA

JBOSS_HOME=$JBOSS_HOME

echo "activating ASTERISK RA entity, entity name=ASTERISKRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -activateRaEntity "ASTERISKRA"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA activateRaEntity failed"
    exit -1
fi

