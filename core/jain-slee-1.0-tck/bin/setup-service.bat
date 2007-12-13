@ECHO OFF
REM 
REM  Sets up a service in the SLEE
REM  Syntax: <script> [-t <trace-level>] <service-DU-URL1> [<service-DU-URL2>,...]
REM  e.g.: .\bin\setup-service.bat -t Info file:///home/myname/sbbs/fooDU.jar

REM  Validate current working directory
IF NOT EXIST bin\read-config-variables.bat (
 ECHO This script must be run from the root sleetck directory
 GOTO End
)
CALL bin\read-config-variables.bat
IF NOT "%ERRORLEVEL%" == "0" GOTO End

SET CLASSPATH=%SLEETCK_LIB%;%TCKTOOLS_LIB%;%SLEE_LIB%;%JMX_LIB%;%VENDOR_LIB%

REM Prepare command line arguments
SET SETUP_SERVICE_ARGS=
:BuildArgs
IF "%1" == "" GOTO NoMoreArgs
SET SETUP_SERVICE_ARGS=%SETUP_SERVICE_ARGS% %1
SHIFT
GOTO BuildArgs
:NoMoreArgs

%JAVA_HOME%\bin\java -classpath %CLASSPATH% com.opencloud.sleetck.ext.sleemanagement.SetupService %SETUP_SERVICE_ARGS% 

:End
