==========================================================================
! Welcome to Mobicents Media server (MMS) - The Open Source Media server !
==========================================================================
This mobicents-media-server-all-1.0.0.BETA4 binary is having
	* Mobicents Media Server
	* Examples for Mobicents Media Server
	* Controllers which includes MGCP controller and Media Server Controller which is local (in JVM) controller for MMS
        * Resource Adaprors required by examples

This is complete Media Server which includes the JBoss AS 4.2.2.GA and Mobicents Core Server (JAIN SLEE).

Mobicents Media Server is open source media server aimed to:

-Deliver competitive, complete, best-of-breed media gateway functionality featuring highest quality. 
-Meet the demands of converged wireless, wireline, cable broadband access and fixed-mobile converged VoIP 
 networks from a single media gateway platform 
-Increase flexibility with a media gateway that supports wide variety of call control protocols and scales down 
 to meet the demands of enterprises and small carrier providers. 
-React quickly to dynamic market requirements. 

Mobicents Media Server Home Page: http://www.mobicents.org/products_media_server.html
Mobicents documentation page: http://groups.google.com/group/mobicents-public/web/mobicents-media-server
Version information: media-server_1.0.0.BETA4


To install media server
----------------------------------------------------------------------
1. Run Mobicents SLEE server.
2. Unzip Media server binary and call run.bat (run.sh for linux from ) mobicents-media-server-all-1.0.0.BETA3/jboss-4.2.2.GA/bin

To Deploy Examples
----------------------------------------------------------------------
Go to examples directory /mobicents-media-server-all-1.0.0.BETA3/examples/call-controller2 and call 'ant deploy-all' to deploy example and all the dependencies

To undeploy call 'ant undeploy-all'

Look at link to know more about examples http://groups.google.com/group/mobicents-public/web/mobicents-examples


Highlights of 1.0.0.BETA4
----------------------------------------------------------------------
1) This release was specificaly targeted on Architecture changes of MMS. 
2) Added G729 Codec
3) Improved MMS test suite. More than 100 unit tests added. Also added the load test for Announcement Endpoint
5) As usual fixed other bugs related to functionality and performance

Download the nightly SNAPSHOT from http://hudson.qa.jboss.com/hudson/view/Mobicents/job/MobicentsMediaServerRelease/
