This example implements a simple service to demonstrate the use of Http-Client-RA

HttpClientRAExampleSbb is a simple Service which reads the RSS Feed URL (specified in sbb-jar.xml) using ROME api (https://rome.dev.java.net/)
For each link present in parsed RSS Feed, SBB uses http-client-ra to send the Request Asynchronously and just displays the Response

SBB uses timer facility to peridoically check if there are any update in RSS Feed link and if there are updates it retrieves the content for each 
link present in RSS Feed.

Running the example:

Before deploying the Service deploy http-client-ra
call 'ant ra-deploy' from /lib/http-client-ra/
	
To deploy the example call
ant deploy
    - this deploys the example
    


Other Notes:
* You may need to set "jnpHost" variable in lib/build.xml so it points to the correct server bind address. 
    There is no need to do that if the server is running with default parameters.

* lib/http-client-ra -> ant ra-deploy  = will deploy http-client-ra - it pics up jboss bind address.

* ANT 1.7.0 is required

If this example is part of release use ant that has been shipped with mobicents. This concerns all ant calls.
/path_to_mobicents/tools/ant/ant -f /path_to_mobicents/examples/example/build.xml deploy


Make sure to set the system variable JBOSS_HOME to point to the JBoss AS instance where Mobicents is deployed. 
Also see: http://groups.google.com/group/mobicents-public/web/mobicents-http-client-ra

