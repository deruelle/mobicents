@echo off
rem This script deploys the SIP Proxy example.
rem It then activates the proxy service
rem The service deployable units are assumed to be stored in the "./examples" directory 

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

echo "Deploying SIP Proxy Service"
call %JBOSS_HOME%\bin\SleeCommandInterface -deploy "file://%DIRNAME%/examples/proxyservice.jar"

IF ERRORLEVEL 0 GOTO SIPRA

:SIPRA

echo "Activating SIP Proxy Service"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -activateService "ServiceID[JAIN SIP Proxy Service#NIST#1.0]"

:END
