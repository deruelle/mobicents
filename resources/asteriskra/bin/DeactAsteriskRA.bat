@echo off
rem This script deactivates the SIP RA entity

set JBOSS_HOME=%JBOSS_HOME%

echo "Deactivating Asterisk RA entity, entity name=ASTERISKRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deactivateRaEntity "ASTERISKRA"

