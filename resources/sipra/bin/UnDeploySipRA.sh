#!/bin/ksh
#This script un-deploys the SIP RA and SIP RA type

JBOSS_HOME=$JBOSS_HOME

echo "Remove SIP RA entity link, entity name=SipRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -removeEntityLink SipRA 
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA removeEntityLink failed"
    exit -1
fi

echo "Deactivating SIP RA entity, entity name=SipRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deactivateRaEntity SipRA
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA deactivateRaEntity failed"
    exit -1
fi

echo "remove SIP RA entity, entity name=SipRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -removeRaEntity "ResourceAdaptorID[JainSipResourceAdaptor#net.java.slee.sip#1.2]" SipRA
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA removeRaEntity failed"
    exit -1
fi
#fi

echo "UnDeploying SIP RA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -undeploy "ResourceAdaptorID[JainSipResourceAdaptor#net.java.slee.sip#1.2]"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA undeployment failed"
    exit -1
fi

echo "UnDeploying SIP RA type"
$JBOSS_HOME/bin/SleeCommandInterface.sh -undeploy "ResourceAdaptorTypeID[JainSipResourceAdaptor#net.java.slee.sip#1.2]"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA type undeployment failed"
    exit -1
fi
