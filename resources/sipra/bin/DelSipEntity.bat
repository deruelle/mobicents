@echo off
rem This script removes SIP RA entity

set JBOSS_HOME=%JBOSS_HOME%

echo "remove SIP RA entity, entity name=SipRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -removeRaEntity "ResourceAdaptorID[JainSipResourceAdaptor#net.java.slee.sip#1.2]" SipRA
