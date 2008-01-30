@echo off
rem This script deploys the ASTERISK RA type and RA
rem It then creates and activates a RA entity for the RA
rem All deployable units are stored in SLEE_HOME/RAs 

set JBOSS_HOME=%JBOSS_HOME%

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

echo "Deploying ASTERISK RA type"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deploy "file://%DIRNAME%/../stage/asterisk-ra-type.jar"

echo "Deploying ASTERISK RA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deploy "file://%DIRNAME%/../stage/asterisk-local-ra.jar"

echo "Creating ASTERISK RA entity entity name=ASTERISKRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -createRaEntity "ResourceAdaptorID[asterisk#Asterisk-Java#1.0]" ASTERISKRA

echo "Activating ASTERISK RA entity, entity name=ASTERISKRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -activateRaEntity ASTERISKRA

echo "Create ASTERISK RA entity link, entity name=ASTERISKRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -createEntityLink ASTERISKRA ASTERISKRA

:END
