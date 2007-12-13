#!/bin/sh
#This script deploys the SIP RA type and RA
#It then creates and activates a RA entity for the RA
#All deployable units are assumed to be located in the current working directory

DIRNAME=`pwd`

JBOSS_HOME="/home/cisco/jslee"
export JBOSS_HOME

echo "Deploying JCC  RA type"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deploy "file://$DIRNAME/dist/jcc-1.1-ra-type.jar"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA type deployment failed"
    exit -1
fi

echo "Deploying JCC RA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deploy "file://$DIRNAME/dist/jcc-1.1-local-ra.jar"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA deployment failed"
    exit -1
fi

echo "Creating JCC RA entity, entity name=JCC"
$JBOSS_HOME/bin/SleeCommandInterface.sh -createRaEntity "ResourceAdaptorID[JCC-1.1-RA#itech#1.0]" JCC
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA createRaEntity failed"
    exit -1
fi

echo "Activating JCC RA entity, entity name=JCC"
$JBOSS_HOME/bin/SleeCommandInterface.sh -activateRaEntity JCC
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA activateRaEntity failed"
    exit -1
fi

echo "Create JCC RA entity link, entity name=JCC"
$JBOSS_HOME/bin/SleeCommandInterface.sh -createEntityLink JCC JCC
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA createEntityLink failed"
    exit -1
fi

