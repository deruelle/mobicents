#/bin/ksh
#This script deploys the SIP RA type and RA
#It then creates and activates a RA entity for the RA
#All deployable units are stored in SLEE_HOME/RAs 

JBOSS_HOME=$JBOSS_HOME

echo "Deactivating SIP RA entity, entity name=SipRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deactivateRaEntity "SipRA"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA activateRaEntity failed"
    exit -1
fi
