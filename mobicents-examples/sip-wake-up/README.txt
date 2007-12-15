This example implements a simple SIP Wake Up Call service. Its intent is to show how to quickly write a basic JSLEE application.

Running the Example:

The code is tested to work with Mobicents 1.0.02.GA
REQUIREMENTS(deploy targets are not needed if deploy-all is called):
*set "jnpHost" variable in lib/build.xml so it is correct jb container bind address
*lib/slee-sip-ra -> ant ra-deploy  = will deploy sip ra - it pics up jboss bind address.
*lib/sipservices -> ant deploy = will deploy sip proxy and registrar, however this examples requires that INVITE IS  initial, therefore 
there is special target in lib/sipservices, refer to "ant -projecthelp" and read desc
*ANT 1.7.0
CALL:
* make sure JBOSS_HOME is set and server is running.
ant deploy-all - this should deploy everything , refer to wiki
ant undeploy-all - this should undeploy everything - along with deps
ant deploy/undeploy - to play with only this service

ACCESS:
?
Also see:
http://wiki.java.net/bin/view/Communications/MobicentsExamplesWakeUp


Direct questions and comments to the Mobicents forums: http://forums.java.net/jive/category.jspa?categoryID=36

If this example is part of release use ant that has been shipped with mobicents. This concerns all ant calls.
/path_to_mobicents/tools/ant/ant -f /path_to_mobicents/examples/example/build.xml deploy


