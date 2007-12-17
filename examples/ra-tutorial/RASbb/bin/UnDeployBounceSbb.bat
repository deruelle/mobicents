@echo off

rem This script deactivates the Bounce service
rem It then undeploys the Bounce SBB example.

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

echo "Deactivating Bounce Proxy Service"
call %JBOSS_HOME%\bin\SleeCommandInterface -deactivateService "ServiceID[Resource Adaptor Framework Bounce Service#maretzke#1.0]"

IF ERRORLEVEL 0 GOTO SIPRA

:SIPRA

echo "Uninstalling Bounce Sbb Service"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -undeploy "DeployableUnitID[2]" 

:END
