#!/bin/ksh
#This script un-deploys the ASTERISK RA type and RA

JBOSS_HOME=$JBOSS_HOME

echo "Remove ASTERISK RA entity link, entity name=ASTERISKRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -removeEntityLink ASTERISKRA 
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA removeEntityLink failed"
    exit -1
fi

echo "Deactivating ASTERISK RA entity, entity name=ASTERISKRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deactivateRaEntity ASTERISKRA
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA deactivateRaEntity failed"
    exit -1
fi

echo "remove ASTERISK RA entity, entity name=ASTERISKRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -removeRaEntity "ResourceAdaptorID[asterisk#Asterisk-Java#1.0]" ASTERISKRA
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA removeRaEntity failed"
    exit -1
fi
#fi

echo "UnDeploying ASTERISK RA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -undeploy "ResourceAdaptorID[asterisk#Asterisk-Java#1.0]"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA undeployment failed"
    exit -1
fi

echo "UnDeploying ASTERISK RA type"
$JBOSS_HOME/bin/SleeCommandInterface.sh -undeploy "ResourceAdaptorID[asterisk#Asterisk-Java#1.0]"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA type undeployment failed"
    exit -1
fi
