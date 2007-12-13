@ECHO OFF
REM 
REM  Inserts a valid checksum into the given JAIN SLEE TCK configuration interview file
REM  
REM  Syntax: <script> [<interview-file-path>]
REM   interview-file-path: the path of the interview file, eg config\sleetck.jti
REM   If not specified, the default interview file is processed
REM   

SET DEFAULT_FILE=config\sleetck.jti

REM  Validate current working directory
IF NOT EXIST bin\read-config-variables.sh (
 ECHO This script must be run from the root sleetck directory
 GOTO End
)
CALL bin\read-config-variables.bat
IF NOT "%ERRORLEVEL%" == "0" GOTO End

REM Set file path
SET FILE=%DEFAULT_FILE%
IF NOT "%1" == "" (
 SET FILE=%1
)

REM  Process the file

SET CLASSPATH=%SLEETCK_LIB%;%JAVATEST_LIB%;%SLEE_LIB%;%TCKTOOLS_LIB%
%JAVA_HOME%\bin\java -classpath %CLASSPATH% com.opencloud.sleetck.ext.InterviewChecksumTool %FILE%

:End
