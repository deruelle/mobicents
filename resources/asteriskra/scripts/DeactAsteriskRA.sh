#/bin/ksh
#This script deactivate the ASTERISK RA entity

JBOSS_HOME=$JBOSS_HOME

echo "Deactivating ASTERISK RA entity, entity name=ASTERISKRA"
$JBOSS_HOME/bin/SleeCommandInterface.sh -deactivateRaEntity "ASTERISKRA"
STATUS=$?
if [ $STATUS -ne 0 ] ; then
    echo "RA deactivateRaEntity failed"
    exit -1
fi

