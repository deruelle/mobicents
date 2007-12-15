@echo off
rem This script undeploys the Asterisk RA type and RA
rem First it gracefully deactivates the service and 
rem afterwards it effectively undeploys the service.
rem All deployable units are assumed to be stored in 
rem current working directory 

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

:DEACTIVATE
echo "Deactivating Call Duration Service"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deactivateService "ServiceID[Call_Duration_Service#PTIn#0.1]"

:INITIALIZE
set DUPATH=
set DEPID=
echo "UnDeploying Call Duration Service"

:CONVERT_PATH
call java -cp . PathNotationConverter "%DIRNAME%\CallDuration-DU.jar" > ANSWER.DAT
echo e 100 "set DUPATH=">TEMP.FIL
echo n PREFIX.DAT>>TEMP.FIL
for %%i in (rcx b w q) do echo %%i >> TEMP.FIL
debug < TEMP.FIL
copy PREFIX.DAT+ANSWER.DAT  VARIAB.BAT
call VARIAB.BAT
echo Converting path notation to: %DUPATH%
for %%f in (PREFIX.DAT ANSWER.DAT VARIAB.BAT TEMP.FIL) do del %%f >NUL

:DEPLOY_DU
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -getDeploymentId "file://%DUPATH%">ANSWER.DAT

echo e 100 "set DEPID=">TEMP.FIL
echo n PREFIX.DAT>>TEMP.FIL
for %%i in (rcx a w q) do echo %%i >> TEMP.FIL
debug < TEMP.FIL
copy PREFIX.DAT+ANSWER.DAT  VARIAB.BAT
call VARIAB.BAT
echo Undeploying unit %DEPID%
for %%f in (PREFIX.DAT ANSWER.DAT VARIAB.BAT TEMP.FIL) do del %%f >NUL
call %JBOSS_HOME%\bin\SleeCommandInterface -undeploy %DEPID%
IF ERRORLEVEL 0 GOTO SUCCESS

:NO_SUCCESS
echo "Undeployment failed"
GOTO END

:SUCCESS
echo "Undeployment successfull"

:END
