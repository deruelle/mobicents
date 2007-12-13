@ECHO OFF
REM  Reads the configuration variables, and checks that JAVA_HOME is set.
REM  This script sets ERRORLEVEL to 1 if the script is not invoked from 
REM  the base TCK directory, or if the configuration file cannot be found,
REM  or if the JAVA_HOME environment variable is not set. 
REM  Otherwise ERRORLEVEL is set to 0.

SET ERRORLEVEL=0

REM  Validate current directory
IF NOT EXIST bin\read-config-variables.bat (
 ECHO This script must be run from the root sleetck directory
 GOTO Error
)

REM  Read the configuration file
SET CONFIG_PATH=config\config_variables.bat
IF NOT EXIST %CONFIG_PATH% (
 ECHO Configuration file %CONFIG_PATH% not found
 GOTO Error
)
CALL %CONFIG_PATH%

REM  Check the JAVA_HOME environment variable
IF NOT DEFINED JAVA_HOME (
  ECHO JAVA_HOME environment variable not set
  GOTO Error
)

GOTO End

:Error
SET ERRORLEVEL=1

:End

