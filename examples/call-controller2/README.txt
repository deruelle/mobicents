This is an example application for Mobicents. 
It demonstrates:
- a design pattern for service composition - Call Blocking, Forwarding and VoiceMail. 
- usage of the SIP 1.2 RA with Dialog support
- usage of the Media RA and Mobicents Media Server

Running the Example:

HOW TO DEPLOY:
/path_to_mobicents_distro/tools/ant/ant deploy-all
(depending on your command line environment settings, you may need to stop the server and run the "deploy" command a second time if it fails the first time)

HOW IT WORKS:
See the following page for usage instructions.
http://wiki.java.net/bin/view/Communications/CallBlockingForwardingVoicemail





WHEN BUILDING FROM SOURCE:

REQUIREMENTS(deploy targets are not needed if deploy-all is called):
*set "jnpHost" variable in lib/build.xml so it is correct jb container bind address
*lib/slee-sip-ra -> ant ra-deploy  = will deploy sip ra - it pics up jboss bind address.
*lib/sipservices -> ant deploy = will deploy sip proxy and registrar, however this examples requires that INVITE IS NOT initial, therefore 
there is special target in lib/sipservices, refer to "ant -projecthelp" and read desc
*lib/mediara -> ant ra-deploy - it will deploy media ra
*ANT v 1.7.0


CALL - deployment:
* make sure JBOSS_HOME is set and server is running.
ant deploy-all - this should deploy everything , refer to wiki
ant undeploy-all - this should undeploy everything - along with deps
ant deploy/undeploy - to play with only this service


Please, direct questions and comments to the Mobicents forums: http://forums.java.net/jive/category.jspa?categoryID=36
