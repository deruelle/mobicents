@ECHO OFF
REM  This is the template for the environment variables setup script for Windows
REM  installations of the JAIN SLEE TCK.
REM
REM  To configure, copy this template to config_variables.bat in the same directory,
REM  then edit config_variables.bat as follows:
REM  - Replace @VENDOR_LIB@ with the absolute path(s) of the classes/jars used by 
REM    the JAIN SLEE implementation. 
REM    The JAIN SLEE TCK executable scripts append these path(s) the java classpath
REM    before invoking the Java Virtual Machine.

SET LIB=lib
SET VENDOR_LIB=..\lib
SET JARS=jars
SET SLEETCK_LIB=%JARS%\sleetck.jar
SET TCKTOOLS_LIB=%JARS%\tcktools.jar
SET JAVATEST_LIB=%LIB%\javatest.jar
SET SIGTEST_LIB=%LIB%\sigtest.jar
SET SLEE_LIB=%LIB%\slee.jar
SET XML_LIB=%LIB%\xerces.jar
rem SET JMX_LIB=%LIB%\jboss-jmx.jar
SET JMX_LIB=%VENDOR_LIB%\jboss-jmx.jar
rem SET JTA_LIB=%LIB%\jta-spec1_0_1.jar
SET JTA_LIB=%VENDOR_LIB%\jta.jar
rem SET JBOSS_COMMON_LIB=%LIB%\jboss-common.jar
SET JBOSS_COMMON_LIB=%VENDOR_LIB%\jboss-common.jar
rem SET DOM4J_LIB=%LIB%\dom4j.jar
SET DOM4J_LIB=%VENDOR_LIB%\dom4j.jar
