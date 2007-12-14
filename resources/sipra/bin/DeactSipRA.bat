@echo off
rem This script deactivates the SIP RA

set JBOSS_HOME=%JBOSS_HOME%

echo "Deactivating SIP RA entity, entity name=SipRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deactivateRaEntity "SipRA"

