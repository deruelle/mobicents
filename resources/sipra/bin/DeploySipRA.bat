@echo off
rem This script deploys the SIP RA type and RA
rem It then creates and activates a RA entity for the RA

set JBOSS_HOME=%JBOSS_HOME%
set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

echo "Deploying SIP RA type"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deploy "file://%DIRNAME%/../ratype/jars/sip-ratype-DU.jar"

echo "Deploying SIP RA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -deploy "file://%DIRNAME%/../ra/jars/sip-ra-DU.jar"

echo "Creating SIP RA entity, entity name=SipRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -createRaEntity "ResourceAdaptorID[JainSipResourceAdaptor#net.java.slee.sip#1.2]" SipRA

echo "Activating SIP RA entity, entity name=SipRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -activateRaEntity SipRA

echo "Create SIP RA entity link, entity name=SipRA"
call %JBOSS_HOME%\bin\SleeCommandInterface.bat -createEntityLink SipRA SipRA

:END
