@ECHO OFF
REM 
REM  A tool which lists the paths of all the test descriptions in a 
REM  set of directories/files
REM  
REM  Syntax: <script> [-o outputPath] [-tests <testFile1> [<testFile2>, ...]]
REM  -o - Specifies an optional output path to write the list to
REM  -tests - Specifies (optional) test files and/or directories to search in. 
REM    If not specified, the base test directory is used.
REM 
REM  e.g.: 
REM  .\bin\list-test-descriptions.bat -o alltests.txt
REM  .\bin\list-test-descriptions.bat -o nullactivitytests.txt\
REM   -tests tests\activities\nullactivity tests\javax\slee\nullactivity
REM   

SET TEST_SUITE_ROOT=testsuite
SET DEFAULT_TESTS_DIR=tests
SET ARGS=%TEST_SUITE_ROOT%

REM  Validate current working directory
IF NOT EXIST bin\read-config-variables.bat (
 ECHO This script must be run from the root sleetck directory
 GOTO End
)
CALL bin\read-config-variables.bat
IF NOT "%ERRORLEVEL%" == "0" GOTO End

REM  Set (optional) output stream
IF "%1" == "-o" (
 SET ARGS=%ARGS% -o %2
 SHIFT
 SHIFT
)

REM  Set tests directory path
IF "%1" == "-tests" (
 SHIFT
 GOTO BuildArgs
)
SET ARGS=%ARGS% %DEFAULT_TESTS_DIR%
GOTO Cont0

:BuildArgs
IF "%1" == "" GOTO NoMoreArgs
SET ARGS=%ARGS% %1
SHIFT
GOTO BuildArgs
:NoMoreArgs

:Cont0

REM  Invoke the tool
SET CLASSPATH=%SLEE_LIB%;%SLEETCK_LIB%;%TCKTOOLS_LIB%;%XML_LIB%;%JAVATEST_LIB%
%JAVA_HOME%\bin\java -Dorg.xml.sax.driver=org.apache.xerces.parsers.SAXParser -classpath %CLASSPATH% com.opencloud.sleetck.ext.SleeTCKTestLister %ARGS%

:End
