@echo off
rem This script deploys the RAFrame RA type and RA
rem It then creates and activates a RA entity for the RA

set JBOSS_HOME=%JBOSS_HOME%
set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

echo "Deploying RAFrame RA type"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deploy "file:///%DIRNAME%../stage/raframe-ra-type.jar"

echo "Deploying RAFrame RA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deploy "file:///%DIRNAME%../stage/raframe-local-ra.jar"

echo "Creating RAFrame RA entity, entity name=RAFrameRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -createRaEntity "ResourceAdaptorID[raframe#maretzke#1.0]" RAFrameRA

echo "Activating RAFrame RA entity, entity name=RAFrameRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -activateRaEntity RAFrameRA

echo "Create RAFrame RA entity link, entity name=RAFrameRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -createEntityLink RAFrameRA RAFrameRA

:END
