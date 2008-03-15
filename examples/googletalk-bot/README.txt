This example implements a simple GoogleTalk Service to demonstrate usage of the Jabber/XMPP RA. 

Running the example:

You need to first configure the example with a valid GTalk user account. 
Edit src\org\mobicents\examples\googletalk\GoogleTalkBot-sbb-jar.xml and enter the credentials(Defaults provided, which should work).
Then add this account to your regular GTalk buddy list.



REQUIREMENTS(deploy targets are not needed if deploy-all is called):
*set "jnpHost" variable in lib/build.xml so it is correct jb container bind address
*lib/xmppra -> ant ra-deploy  = will deploy sip ra - it pics up jboss bind address.
*ANT v 1.7.0


CALL - deployment:
* make sure JBOSS_HOME is set and server is running.
ant deploy-all - this should deploy everything , refer to wiki
ant undeploy-all - this should undeploy everything - along with deps
ant deploy/undeploy - to play with only this service
ACCESS:
?

If this example is part of release use ant that has been shipped with mobicents. This concerns all ant calls.
/path_to_mobicents/tools/ant/ant -f /path_to_mobicents/examples/example/build.xml deploy


Make sure to set the system variable JBOSS_HOME to point to the JBoss AS instance where Mobicents is deployed. 
Also see:
http://wiki.java.net/bin/view/Communications/MobicentsExamplesGoogleTalkBot

Please, direct questions and comments to the Mobicents forums: http://forums.java.net/jive/category.jspa?categoryID=36
