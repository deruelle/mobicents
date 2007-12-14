@echo off
rem This script activates the RA

set JBOSS_HOME=%JBOSS_HOME%

echo "activating SIP RA entity, entity name=SipRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -activateRaEntity "SipRA"
