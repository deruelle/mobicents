@ECHO OFF
REM 
REM  Starts the rmiregistry with the classes required by the TCK in the classpath

REM  Validate current working directory
IF NOT EXIST bin\read-config-variables.bat (
 ECHO "This script must be run from the root sleetck directory"
 GOTO End
)
CALL bin\read-config-variables.bat
IF NOT "%ERRORLEVEL%" == "0" GOTO End

SET RMI_PORT=4099
SET SECURITY_POLICY=config\rmi-security.policy

SET CLASSPATH=%SLEETCK_LIB%
SET CLASSPATH=%CLASSPATH%;%SLEE_LIB%
SET CLASSPATH=%CLASSPATH%;%JMX_LIB%
SET CLASSPATH=%CLASSPATH%;%VENDOR_LIB%

SET OPTIONS=-J-Djava.security.policy=%SECURITY_POLICY%
SET OPTIONS=%OPTIONS% -J-classpath -J%CLASSPATH%

%JAVA_HOME%\bin\rmiregistry %OPTIONS% %RMI_PORT%

:End
