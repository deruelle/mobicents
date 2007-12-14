@echo off
rem This script un-deploys the SIP RA and SIP RA Type

set JBOSS_HOME=%JBOSS_HOME%

echo "Remove SIP RA entity link, entity name=SipRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -removeEntityLink SipRA 

echo "Deactivating SIP RA entity, entity name=SipRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deactivateRaEntity SipRA

echo "remove SIP RA entity, entity name=SipRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -removeRaEntity "ResourceAdaptorID[JainSipResourceAdaptor#net.java.slee.sip#1.2]" SipRA

echo "UnDeploying SIP RA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -undeploy "ResourceAdaptorID[JainSipResourceAdaptor#net.java.slee.sip#1.2]"

echo "UnDeploying SIP RA type"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -undeploy "ResourceAdaptorTypeID[JainSipResourceAdaptor#net.java.slee.sip#1.2]"

:END
