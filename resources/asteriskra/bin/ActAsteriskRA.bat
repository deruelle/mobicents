@echo off
rem This script activates the ASTERISK RA

set JBOSS_HOME=%JBOSS_HOME%

echo "activating ASTERISK RA entity, entity name=ASTERISKRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -activateRaEntity "ASTERISKRA"
