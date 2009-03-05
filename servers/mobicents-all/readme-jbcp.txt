

============================================================================
! Welcome to JBoss Communications platform - The Open Source SLEE          !
============================================================================

JBoss Communications Platform (JBCP) is the First and Only Certified Open Source implementation of JSLEE.  

JBCP homepage: http://www.redhat.com/solutions/telco/communications_platform/
You can also check the community project Mobicents home page: http://www.mobicents.org

This is the release bundle of JBCP.

Directory Structure
-------------------

There are 5 top-level directories:

 +jboss-eap-4.X (JBossAS, the JAIN SLEE container, Sip Servlets container, Media Server and other base services)
 +resources         (various resource adaptors and related scripts)
 +sip-presence      (SIP Presence Service and related scripts)
 +examples          (various examples)
 +documentation     (user guides for the mobicents servers)
 
How to use?
-----------

(*) Starting the server - run this from the command line:
 jboss-eap-4.X/jboss-as/bin/run.sh  (UNIX)
 jboss-eap-4.X\jboss-as\bin\run.bat (Windows)
 
 To verify that the SLEE server started successfully, open your favorite web browser and point it to
  http://localhost:8080/management-console
 You should see a nice web application indicating that JBCP/Mobicents is Running. 
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

If you want to deploy with the mobicents DU deployer in the Application Server run this:
ant -f resources/<radir>/build.xml deploy (or undeploy)

(*) To deploy and run examples 

Make sure that you have Apache Ant 1.7 installed and configured.
Simply run this script and the deployment should start:
ant -f examples/<exampledir>/build.xml deploy-all

Take a look at this page for additional information:
http://groups.google.com/group/mobicents-public/web

Clustering for Mobicents Sip Servlets
-------------------------------------

This distribution includes a pre-configured cluster-enabled server. It is located in the 'all' configuration.
Be aware that the clustering and failover support in Mobicents Sip Servlets are still in the early experimental
stage and have limitations.
Before starting the cluster-enabled server you must have the Sip Load Balancer from the sip-balancer 
directory running on your machine or on the network. For more information read the following documents:

http://www.mobicents.org/load-balancer.html
http://www.mobicents.org/clustering.html
http://www.mobicents.org/failover.html

Note that most of the configuration is already done in the 'all' configuration from this distribution. You should
just change the binding addresses and the ports specific to your set up.

To run the 'all' configuration you you should execute:
run.sh -c all
or run.bat -c all

---------------------------------------------------------------
  IMPORTANT NOTE ABOUT CALL-CONTROLLER2 AND MMS-DEMO EXAMPLES
---------------------------------------------------------------

For call-controller2 and mms-demo examples, if you experience any issues with media (like not hearing anything), try running 
Mobicents with the -b <external_interface_ip_address> (eg, jboss-eap-4.X\jboss-as\bin\run.bat -b 192.168.2.100), so that the SLEE 
server is bound to the external interface.

Please remember to also use that IP address wherever you used to have 127.0.0.1/localhost (such as registrar, proxy address,
management console, etc). Please note that in case you choose to use JMX deployment, you'll also have to change jnpHost to the
external interface IP address in every dependency (in du-management.xml, property "jnpHost"). You can see which are the 
dependencies by looking at the build.xml at the example folder.
