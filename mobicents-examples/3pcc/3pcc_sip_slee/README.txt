Installation instructions

What is this?
This is an example application that implements click to dial, a.k.a. third party call control.
The application consists of three parts: a Jain SLEE part implementing the actual sip signalling logic,
a web app providing a page for launching the call flow and a web service that exposes the
sip functionality.

1. This application has been tested on Mobicents running on JBoss 3.2.6 and JBoss 3.2.7 using java 1.5.
Don't try running this using java 1.4, it will not work because we use Eclipslee Ant targets in the ant build file 
which requires java 1.5. As a consequence you will get class incompatibility problems when the app is 
deployed on a server using java 1.4. (The infamous "class version is 48.0 should be 49.0")

2. Using Eclipse for building with ANT you must ensure that:
a) The project build path uses java 1.5
Project --> Properties --> Java build path --> Libraries must include "JRE System Library [jdk1.5.0_xx]"
b) The ant settings use java 1.5
Window --> Preferences --> Ant --> Runtime --> Global Entries must include $JAVA_HOME/lib/tools.jar
where $JAVA_HOME should be replaced by your concrete path to the java 1.5 installation.
Example /opt/jdk1.5.0_06/lib/tools.jar 

Without this ANT may not use java 1.5 to compile with the javac target.

3. Ensure that the environment variable JBOSS_HOME is set to your JBoss installation folder.
Ex: /opt/jboss-3.2.7  

4. You need Mobicents deployed with the sip RA.
Note: You may run the sip proxy included in the Mobicents distribution but sip BYE requests will be processed by the 
proxy app which is not relevant. You may get disturbed by the debug messages for this.

The recommended installation procedure is to use the "auto-deploy-sip" target and comment out 
the part in the bean shell script that deploys the proxy. 

See the scripts/sip-deploy-sipraonly.bsh which you can use.

To build and deploy Mobicents simply run the "auto-deploy-sip" target and replace
the $JBOSS_HOME/server/all/deploy-mobicents/scripts/sip-deploy.bsh file with the scripts/sip-deploy-sipraonly.bsh.


5. If you use another ipaddress than 127.0.0.1 for running JBoss it may be necessary to edit the file
server/all/deploy/mobicents.sar/slee-ds.xml by changing "localhost" to your ipaddress in the following line:

<config-property name="SleeJndiName" type="java.lang.String">jnp://localhost:1099/SleeService</config-property>

It has been observed that it is not possible to communicate on 127.0.0.1:1099 unless this change is performed.

6. Run the target "auto-deploy-ThirdPCCTrigger" (in the build.xml for this project).
Note: This target installs the bean shell script "thirdPCCTrigger-deploy.bsh" which 
must be processed after the sip RA has been deployed.
To achieve this its name must be alphanumerically preceded by "sip-deploy-sipraonly.bsh"
or whatever name you use for this file.

7. Run the target "deploy-jmx-testmbean".
This target deploys an MBean that can be used to test the click to dial application independent of the web app.
To do this, simply use the JBoss jmx console by pointing a browser to 
http://localhost:8080/jmx-console and go to the mbean represented by 
click2dial --> service:CallControl

8. Run JBoss by:
a) Go to $JBOSS_HOME
b) Issue ./bin/run.sh -c all -b youripaddress

9. The sip UAs used must not be registered with the proxy shipped in Mobicents, this does not work.
Instead let the 3pcc application address th sip UAs directly by calling them by ipaddress or register with another proxy.
One recommended proxy that has been tested with the application is Sip Express Router freely available at www.iptel.org.

 