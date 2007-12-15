@echo off
rem This script deploys the Asterisk RA type and RA
rem It then creates and activates a RA entity for the RA
rem All deployable units are assumed to be stored in current working directory 

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

echo "Deploying Call Duration Service"
echo %DIRNAME%
call %JBOSS_HOME%\bin\SleeCommandInterface -deploy "file://%DIRNAME%/CallDuration-DU.jar"
IF ERRORLEVEL 0 GOTO ACTIVATE

:ACTIVATE
echo "Activating Call Duration Service"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -activateService "ServiceID[Call_Duration_Service#PTIn#0.1]"

:END
