========================================================
!    This is a public release bundle of Mobicents      !
========================================================

Directory structure:
/
  +server - core Mobicents platform: JSLEE container, SIP Servlets (JSR 289) container and base services 
  +resources - contains a set of JSLEE resource adaptors
  +tools - contains Mobicents Command Line Interface tool, recent ant distribution and other basic tools
  +examples - contains a set of Mobicents example applications

Use:

-to start the core JSLEE server and the SIP Servlets container

cd server
bin/run.bat
(on Linux and *NIX, use run.sh instead of run.bat)

To verify that the SLEE server started successfully, open your favorite web browser and point it to
http://localhost:8080/management-console
You should see a nice web application indicating that Mobicents is Running. 
There is a lot more to the Management Console; look around and see what's in store for you.

To verify that the SIP Servlets container is ready navigate to:
http://localhost:8080/click2call

This is a pre-deployed sample application named "click2call" and you can learn more about it from examples/click2call/readme.txt

Note that the SIP Servlets default SIP port is 5080, while the default SIP port for SIP RA is 5060 or 5070 if 5060 is in use.

-to deploy and undeploy Resource Adaptors

tools/ant/bin/ant -f resources/<radir>/build.xml ra-deploy (or ra-undeploy)

-To deploy and run examples 

follow examples wiki page: http://wiki.java.net/bin/view/Communications/MobicentsExamples

- tools/cli contains the CLI tool for command line Mobicents control
  Configure the CLI via slee-management.xml. Make sure to set the correct values for jnpHost and jnpPort. Starting the server with 
  just run.sh or run.bat should work. However if you made changes to the binding address via '-b', make sure to set the same address for jnpHost.
  Respectively jnpPort should correspond to the value used at server startup.

