@ECHO OFF
REM 
REM  Runs the JAIN SLEE TCK in command line mode.
REM  With no arguments, this script runs the full test suite using the default directories.
REM  Syntax: 
REM  <script> [-o] [-nt] [-c configInterviewFile] [-d workDir] [-r reportDir] [-excludeList excludeList] [-priorStatus status-arguments]
REM           [-set key=value [-set key=value ...]] [-tests test1 [test2 ...]]
REM   -o   - Overwrite. If set, clears the work directory before running the test suite.
REM   -nt  - Run no tests. If set, the reports are generated but no tests are run
REM   -c   - Specifies the configuration interview file to use. The default value is config\sleetck.jti, 
REM          relative to the base directory of the JAIN SLEE TCK installation.
REM   -d   - Specifies the work directory to use.
REM   -r   - Specifies the directory to write reports to
REM   -excludeList - Specifies the list of excluded tests (must be a valid JavaTest exclude list file)
REM   -priorStatus - If specified, only tests which match one the status values in status-arguments will be included in the test run.
REM                  Valid values are pass,fail,error, and notRun. Multiple arguments must be separated by commas (with no whitespace).
REM                  E.g. to exclude tests which already have a pass result, specify "-priorStatus fail,error,notRun"
REM   -set - Sets the configuration property to the given value (use to set default timeout, debug level etc)
REM   -tests - Use to specify a subset of the test suite. Each argument may be a directory or a file, 
REM            relative to the testsuite root directory, eg tests/events. Tests paths are expressed as 
REM            relative URLs -- the path separator is '/', regardless of the underlying file system.

REM  Default paths
SET RUNTCK=bin\runtck.bat
SET TCKCONFIG=config\sleetck.jti
SET TESTSUITE=testsuite
SET WORKDIR=tckwork
SET REPORTDIR=tckreports

REM  Read args
SET OVERWRITE=
IF "%1" == "-o" (
 SET OVERWRITE=true
 SHIFT
)

IF "%1" == "-nt" (
 SET RUNTESTS=
 SHIFT
 GOTO Cont0
)
SET RUNTESTS=-runtests
:Cont0

IF "%1" == "-c" (
 SET TCKCONFIG=%2
 SHIFT
 SHIFT
)

IF "%1" == "-d" (
 SET WORKDIR=%2
 SHIFT
 SHIFT
)
IF "%1" == "-r" (
 SET REPORTDIR=%2
 SHIFT
 SHIFT
)

REM  Prepare args and directories
SET WORKDIR_ARGS=
IF NOT EXIST %WORKDIR% GOTO WorkDirNotExist
IF NOT "%OVERWRITE%" == "true" GOTO Cont1
IF NOT EXIST %WORKDIR%\jtData GOTO Cont1
REM  Work directory exists, is non empty, and the overwrite flag was set
SET WORKDIR_ARGS=-overwrite
GOTO Cont1

:WorkDirNotExist
MKDIR %WORKDIR%
SET WORKDIR_ARGS=-create

:Cont1

REM Prepare extra command line arguments
SET EXTRA_ARGS=
:BuildArgs
IF "%1" == "" GOTO NoMoreArgs
SET EXTRA_ARGS=%EXTRA_ARGS% %1
SHIFT
GOTO BuildArgs
:NoMoreArgs

SET JAVATEST_ARGS=-batch -testsuite %TESTSUITE% -workDirectory %WORKDIR_ARGS% %WORKDIR% -open %TCKCONFIG% %EXTRA_ARGS% %RUNTESTS% -writeReport %REPORTDIR% -audit

REM  Run the tests, write reports, then audit the results
ECHO Invoking JavaTest with the following arguments: %JAVATEST_ARGS%
%RUNTCK% %JAVATEST_ARGS%
