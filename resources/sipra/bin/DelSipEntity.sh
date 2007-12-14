#/bin/ksh
#This script deploys the SIP RA type and RA
#It then creates and activates a RA entity for the RA
#All deployable units are stored in SLEE_HOME/RAs 

JBOSS_HOME=$JBOSS_HOME


echo "remove SIP RA entity, entity name=SipRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -removeRaEntity "ResourceAdaptorID[JainSipResourceAdaptor#net.java.slee.sip#1.2]" SipRA
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA removeRaEntity failed"
    exit -1
fi

