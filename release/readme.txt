
========================================================
! Welcome to Mobicents - The Open Source SLEE          !
========================================================

Mobicents is the First and Only Certified Open Source implementation of JSLEE.  

Mobicents home page: http://www.mobicents.org

This is the release bundle of Mobicents.

Directory Structure
-------------------

There are 4 top-level directories:

 +jboss-4.2.2.GA	(JBossAS, the JAIN SLEE container, Sip Servlets container, Media Server and other base services)
 +resources			(various resource adaptors and related scripts)
 +servers/xdm		(XDM server and related scripts)
 +examples			(various examples)
 
 
How to use?
-----------

(*) Starting the server - run this from the command line:
 jboss-4.2.2.GA/bin/run.sh  (UNIX)
 jboss-4.2.2.GA\bin\run.bat (Windows)
 
 To verify that the SLEE server started successfully, open your favorite web browser and point it to
  http://localhost:8080/management-console
 You should see a nice web application indicating that Mobicents is Running. 
 There is a lot more to the Management Console; look around and see what's in store for you.

 To verify that the SIP Servlets container is ready you can check these locations:
  http://localhost:8080/sip-servlets-management (The Sip Servlets Management console)
  http://localhost:8080/click2call (Click To Call Demo Application)
 
 Click2Call is a pre-deployed sample application and you can learn more about it from examples/click2call/readme.txt

 Note that the SIP Servlets default SIP port is 5080, while the default SIP port for SIP RA is 5060 or 5070 if 5060 is in use.

(*) Deploy and undeploy Resource Adaptors

Make sure that you have Apache Ant 1.7 installed and configured.
If the Application Server is running you can use JMX to deploy directly:
ant -f resources/<radir>/build.xml deploy-jmx (or undeploy-jmx)

If you want to deploy with the mobicents DU deployer in the Application Server pointed by JBOSS_HOME run this:
ant -f resources/<radir>/build.xml deploy (or undeploy)
Note that you must set the JBOSS_HOME environment variable before trying to deploy this way.

(*) To deploy and run examples 

Make sure that you have Apache Ant 1.7 installed and configured.
Simply run this script and the deployment should start:
ant -f examples/<exampledir>/build.xml deploy-all

Take a look at this page for additional information:
http://wiki.java.net/bin/view/Communications/MobicentsExamples
