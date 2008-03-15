========================================================
!    This is a public release bundle of Mobicents      !
========================================================

Directory structure:
/
  +server - core Mobicents platform: JSLEE cotainer and base services 
  +resources - contains a set of JSLEE resource adaptors
  +tools - contains Mobicents Command Line Interface tool, recent ant distribution and other basic tools
  +examples - contains a set of Mobicents example applications

Use:

-to start the core JSLEE server

cd server
bin/run.bat
(on Linux and *NIX, use run.sh instead of run.bat)

To verify that the server started successfully, open your favorite web browser and point it to
http://localhost:8080/management-console
You should see a nice web application indicating that Mobicents is Running. 
There is a lot more to the Management Console; look around and see what's in store for you.

-to deploy and undeploy Resource Adaptors

tools/ant/bin/ant -f resources/<radir>/build.xml ra-deploy (or ra-undeploy)

-To deploy and run examples 

follow examples wiki page: http://wiki.java.net/bin/view/Communications/MobicentsExamples

- tools/cli contains the CLI tool for command line Mobicents control
  Configure the CLI via slee-management.xml. Make sure to set the correct values for jnpHost and jnpPort. The defaults should work
  if you started the server with '-mc'. However if you made changes to the binding address via '-b', make sure to set the same address for jnpHost.
  Respectively jnpPort should correspond to the value used at server startup.

