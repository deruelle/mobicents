@ECHO OFF
REM 
REM  Shuts down the SLEE

REM  Validate current working directory
IF NOT EXIST bin\read-config-variables.bat (
 ECHO "This script must be run from the root sleetck directory"
 GOTO End
)
CALL bin\read-config-variables.bat
IF NOT "%ERRORLEVEL%" == "0" GOTO End

SET CLASSPATH=%SLEETCK_LIB%;%TCKTOOLS_LIB%;%SLEE_LIB%;%JMX_LIB%;%VENDOR_LIB%

%JAVA_HOME%\bin\java -classpath %CLASSPATH% com.opencloud.sleetck.ext.sleemanagement.ShutdownSlee
:End
        
