@echo off
rem This script un-deploys the Asterisk RA

set JBOSS_HOME=%JBOSS_HOME%

echo "Remove ASTERISK RA entity link, entity name=ASTERISKRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -removeEntityLink ASTERISKRA 

echo "Deactivating ASTERISK RA entity, entity name=ASTERISKRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deactivateRaEntity ASTERISKRA

echo "remove ASTERISK RA entity, entity name=ASTERISKRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -removeRaEntity "ResourceAdaptorID[asterisk#Asterisk-Java#1.0]" ASTERISKRA

echo "UnDeploying ASTERISK RA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -undeploy "ResourceAdaptorID[asterisk#Asterisk-Java#1.0]"

echo "UnDeploying ASTERISK RA type"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -undeploy "ResourceAdaptorTypeID[asterisk#net.sf.asterisk#1.0]"

:END
