#/bin/ksh
#This script deploys the ASTERISK RA type and RA
#It then creates and activates a RA entity for the RA
#All deployable units are stored in SLEE_HOME/RAs 

JBOSS_HOME=$JBOSS_HOME


echo "remove ASTERISK RA entity, entity name=ASTERISKRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -removeRaEntity "ResourceAdaptorID[asterisk#Asterisk-Java#1.0]" ASTERISKRA

STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA removeRaEntity failed"
    exit -1
fi

