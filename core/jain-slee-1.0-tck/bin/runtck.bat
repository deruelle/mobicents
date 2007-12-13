@ECHO OFF
REM Starts the JavaTest harness.

REM  Validate current working directory
IF NOT EXIST bin\read-config-variables.bat (
 ECHO "This script must be run from the root sleetck directory"
 GOTO End
)
CALL bin\read-config-variables.bat
IF NOT "%ERRORLEVEL%" == "0" GOTO End

SET SECURITY_POLICY=config\tck-security.policy
SET CLASSPATH=%SLEETCK_LIB%;%JAVATEST_LIB%;%SIGTEST_LIB%;%SLEE_LIB%;%JMX_LIB%;%XML_LIB%;%VENDOR_LIB%;%JTA_LIB%;%JBOSS_COMMON_LIB%;%DOM4J_LIB%
SET OPTIONS=-Djava.security.manager
SET OPTIONS=%OPTIONS% -Djava.security.policy=%SECURITY_POLICY%
SET OPTIONS=%OPTIONS% -Djavatest.security.allowPropertiesAccess=true
SET OPTIONS=%OPTIONS% -Dorg.xml.sax.driver=org.apache.xerces.parsers.SAXParser

REM Prepare command line arguments
SET JAVATEST_ARGS=
:BuildArgs
IF "%1" == "" GOTO NoMoreArgs
SET JAVATEST_ARGS=%JAVATEST_ARGS% %1
SHIFT
GOTO BuildArgs
:NoMoreArgs

ECHO Running: %JAVA_HOME%\bin\java -classpath %CLASSPATH% %OPTIONS% com.sun.javatest.tool.Main %JAVATEST_ARGS%
%JAVA_HOME%\bin\java -classpath %CLASSPATH% %OPTIONS% com.sun.javatest.tool.Main %JAVATEST_ARGS% 

:End
