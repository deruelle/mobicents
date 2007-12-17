This is an example application for Mobicents. 
It demonstrates:
- a design pattern for service composition - Call Blocking, Forwarding . 
- possible simplest interaction parent/child


The code is tested to work with Mobicents 1.0.00.CR3

REQUIREMENTS(deploy targets are not needed if deploy-all is called):
*set "jnpHost" variable in lib/build.xml so it is correct jb container bind address
*lib/slee-sip-ra -> ant ra-deploy  = will deploy sip ra - it pics up jboss bind address.
*lib/sipservices -> ant deploy = will deploy sip proxy and registrar, however this examples requires that INVITE IS NOT initial, therefore 
there is special target in lib/sipservices, refer to "ant -projecthelp" and read desc
*lib/mediara -> ant ra-deploy - it will dpeloy media ra
*ANT v 1.7.0


CALL - deployment:
* make sure JBOSS_HOME is set and server is running.
ant deploy-all - this should deploy everything , refer to wiki
ant undeploy-all - this should undeploy everything - along with deps
ant deploy/undeploy - to play with only this service
ACCESS:
?
Also see: - this partialy explains behavior of this app
http://wiki.java.net/bin/view/Communications/CallBlockingForwardingVoicemail

If this example is part of release use ant that has been shipped with mobicents. This concerns all ant calls.
/path_to_mobicents/tools/ant/ant -f /path_to_mobicents/examples/example/build.xml deploy


Make sure to set the system variable JBOSS_HOME to point to the JBoss AS instance where Mobicents is deployed. 

