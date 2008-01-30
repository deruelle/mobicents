#/bin/ksh
#This script deploys the ASTERISK RA type and RA
#It then creates and activates a RA entity for the RA
#All deployable units are stored in SLEE_HOME/RAs 

JBOSS_HOME=$JBOSS_HOME
DIRNAME=$PWD

echo "Deploying ASTERISK RA type"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deploy "file://$DIRNAME/../stage/asterisk-ra-type.jar"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA type deployment failed"
    exit -1
fi

echo "Deploying ASTERISK RA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deploy "file://$DIRNAME/../stage/asterisk-local-ra.jar"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA deployment failed"
    exit -1
fi

echo "Creating ASTERISK RA entity, entity name=ASTERISKRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -createRaEntity "ResourceAdaptorID[asterisk#Asterisk-Java#1.0]" ASTERISKRA

STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA createRaEntity failed"
    exit -1
fi

echo "Activating ASTERISK RA entity, entity name=ASTERISKRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -activateRaEntity "ASTERISKRA"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA activateRaEntity failed"
    exit -1
fi

echo "Create ASTERISK RA entity link, entity name=ASTERISKRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -createEntityLink "ASTERISKRA" "ASTERISKRA"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA createEntityLink failed"
    exit -1
fi

