@ECHO OFF
REM 
REM  Waits for the SLEE to initialise, or times out.
REM  Note that this script does not start the SLEE itself.
REM 
REM  Syntax: <script> [<timeout-seconds>] [<wait-after-connect>]
REM  
REM  Return codes: 
REM   0 - The SLEE is initialised
REM   1 - The timeout was reached
REM   2 - An unexpected error occured

SET DEFAULT_TIMEOUT=240
SET DEFAULT_WAIT_AFTER_CONNECT=15

SET TIMEOUT=%DEFAULT_TIMEOUT%
SET WAIT_AFTER_CONNECT=%DEFAULT_WAIT_AFTER_CONNECT%
IF NOT "%1" == "" (
 SET TIMEOUT=%1
)
IF NOT "%2" == "" (
 SET WAIT_AFTER_CONNECT=%2
)

REM  Validate current working directory
IF NOT EXIST bin\read-config-variables.bat (
 ECHO "This script must be run from the root sleetck directory" 
 GOTO End
)
CALL bin\read-config-variables.bat
IF NOT "%ERRORLEVEL%" == "0" GOTO End

SET CLASSPATH=%SLEETCK_LIB%;%TCKTOOLS_LIB%;%SLEE_LIB%;%JMX_LIB%;%VENDOR_LIB%

ECHO Waiting for the SLEE to initialise, with a timeout of %TIMEOUT% second(s)
%JAVA_HOME%\bin\java -classpath %CLASSPATH% com.opencloud.sleetck.ext.sleemanagement.WaitForSlee %TIMEOUT% %WAIT_AFTER_CONNECT%

:End

