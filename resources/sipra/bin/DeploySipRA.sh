#/bin/ksh
#This script deploys the SIP RA type and RA
#It then creates and activates a RA entity for the RA

JBOSS_HOME=$JBOSS_HOME
DIRNAME=$PWD

echo "Deploying SIP RA type"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deploy "file://$DIRNAME/../ratype/jars/sip-ratype-DU.jar"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA type deployment failed"
    exit -1
fi

echo "Deploying SIP RA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deploy "file://$DIRNAME/../ra/jars/sip-ra-DU.jar"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA deployment failed"
    exit -1
fi

echo "Creating SIP RA entity, entity name=SipRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -createRaEntity "ResourceAdaptorID[JainSipResourceAdaptor#net.java.slee.sip#1.2]" SipRA
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA createRaEntity failed"
    exit -1
fi

echo "Activating SIP RA entity, entity name=SipRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -activateRaEntity "SipRA"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA activateRaEntity failed"
    exit -1
fi

echo "Create SIP RA entity link, entity name=SipRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -createEntityLink "SipRA" "SipRA"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA createEntityLink failed"
    exit -1
fi

