@echo off
rem This script removes ASTERISK RA entity

set JBOSS_HOME=%JBOSS_HOME%

echo "remove ASTERISK RA entity, entity name=ASTERISKRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -removeRaEntity "ResourceAdaptorID[asterisk#Asterisk-Java#1.0]" ASTERISKRA
