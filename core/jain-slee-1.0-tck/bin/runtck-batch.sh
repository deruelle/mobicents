#!/bin/sh
#
# Runs the JAIN SLEE TCK in command line mode.
# With no arguments, this script runs the full test suite using the default directories.
# Syntax: 
# <script> [-o] [-nt] [-c configInterviewFile] [-d workDir] [-r reportDir] [-excludeList excludeList] [-priorStatus status-arguments]
#          [-set key=value [-set key=value ...]] [-tests test1 [test2 ...]]
#  -o   - Overwrite. If set, clears the work directory before running the test suite.
#  -nt  - Run no tests. If set, the reports are generated but no tests are run
#  -c   - Specifies the configuration interview file to use. The default value is config/sleetck.jti, 
#         relative to the base directory of the JAIN SLEE TCK installation.
#  -d   - Specifies the work directory to use.
#  -r   - Specifies the directory to write reports to
#  -excludeList - Specifies the list of excluded tests (must be a valid JavaTest exclude list file)
#  -priorStatus - If specified, only tests which match one the status values in status-arguments will be included in the test run.
#                 Valid values are pass,fail,error, and notRun. Multiple arguments must be separated by commas (with no whitespace).
#                 E.g. to exclude tests which already have a pass result, specify "-priorStatus fail,error,notRun"
#  -set - Sets the configuration property to the given value (use to set default timeout, debug level etc)
#  -tests - Use to specify a subset of the test suite. Each argument may be a directory or a file, 
#           relative to the testsuite root directory, eg tests/events

# Default paths
RUNTCK=./bin/runtck.sh
TCKCONFIG=config/sleetck.jti
TESTSUITE=testsuite
WORKDIR=tckwork
REPORTDIR=tckreports

# Read args
if [ "$1" = "-o" ]; then 
 OVERWRITE=true 
 shift 1
fi
if [ "$1" = "-nt" ]; then 
 shift 1
else 
 RUNTESTS="-runtests"
fi
if [ "$1" = "-c" ]; then
 TCKCONFIG=$2
 shift 2
fi 

if [ "$1" = "-d" ]; then
 WORKDIR=$2
 shift 2
fi
if [ "$1" = "-r" ]; then
 REPORTDIR=$2
 shift 2
fi

# Prepare args and directories
if [ -d "${WORKDIR}" ]; then
 if [ "${OVERWRITE}" = "true" -a -n "`ls ${WORKDIR} | head -1`" ]; then
  WORKDIR_ARGS="-overwrite"
 fi
else
 mkdir -p ${WORKDIR}
 WORKDIR_ARGS="-create"
fi 

JAVATEST_ARGS="-batch -testsuite ${TESTSUITE} -workDirectory ${WORKDIR_ARGS} ${WORKDIR} -open ${TCKCONFIG} $* $RUNTESTS -writeReport ${REPORTDIR} -audit"

# Run the tests, write reports, then audit the results
echo "Invoking JavaTest with the following arguments: ${JAVATEST_ARGS}"
${RUNTCK} ${JAVATEST_ARGS}
