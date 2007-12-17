@echo off
rem This script deploys the Bounce SBB example.
rem It then activates the Bounce service

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

echo "Deploying Bounce Proxy Service"
call %JBOSS_HOME%\bin\SleeCommandInterface -deploy "file:///%DIRNAME%/../dist/bouncesbb-service.jar"

IF ERRORLEVEL 0 GOTO SIPRA

:SIPRA

echo "Activating Bounce Sbb Service"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -activateService "ServiceID[Resource Adaptor Framework Bounce Service#maretzke#1.0]"

:END
