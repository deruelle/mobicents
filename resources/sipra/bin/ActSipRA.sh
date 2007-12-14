#/bin/ksh
#This script activates the SIP RA

JBOSS_HOME=$JBOSS_HOME

echo "activating SIP RA entity, entity name=SipRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -activateRaEntity "SipRA"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA activateRaEntity failed"
    exit -1
fi

