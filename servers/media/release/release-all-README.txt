==========================================================================
! Welcome to Mobicents Media server (MMS) - The Open Source Media server !
==========================================================================
This mobicents-media-server-all-1.0.0.CR3 binary is having
	* Mobicents Media Server
	* Examples for Mobicents Media Server
	* Controllers which includes MGCP controller and Media Server Controller which is local (in JVM) controller for MMS
        * Resource Adaprors required by examples
        * Mobicents Media Server Management Console to manage the endpoints/connections of MMS via web. Available at http://localhost:8080/mms-console

This is complete Media Server which includes the JBoss AS 4.2.3.GA and Mobicents Core Server (JAIN SLEE 1.2.2.GA).

Mobicents Media Server is open source media server aimed to:

-Deliver competitive, complete, best-of-breed media gateway functionality featuring highest quality. 
-Meet the demands of converged wireless, wireline, cable broadband access and fixed-mobile converged VoIP 
 networks from a single media gateway platform 
-Increase flexibility with a media gateway that supports wide variety of call control protocols and scales down 
 to meet the demands of enterprises and small carrier providers. 
-React quickly to dynamic market requirements. 

Mobicents Media Server Home Page: http://www.mobicents.org/products_media_server.html
Mobicents documentation page: Chapter 8 of http://groups.google.com/group/mobicents-public/web/user-guide
Version information: media-server_1.0.0.CR3


To install media server
----------------------------------------------------------------------
1. Run Mobicents SLEE server.
2. Unzip Media server binary and call run.bat (run.sh for linux from ) mobicents-media-server-all-1.0.0.CR3/jboss-4.2.2.GA/bin

To Deploy Examples
----------------------------------------------------------------------
Go to examples directory /mobicents-media-server-all-1.0.0.CR3/examples/call-controller2 and call 'ant deploy-all' to deploy example and all the dependencies

To undeploy call 'ant undeploy-all'

Look at link to know more about examples http://groups.google.com/group/mobicents-public/web/mobicents-examples


Highlights of 1.0.0.CR3
----------------------------------------------------------------------
1) This is a bug fix release of CR3.
2) One of the important bug fix is media packets arriving at UA in short span of time and over fills jitter buffer causing audio distortion. Look at http://code.google.com/p/mobicents/issues/detail?id=464
3) Enhanced the MSC API and added getMsLinks to MsProvider http://code.google.com/p/mobicents/issues/detail?id=451
4) MsLink now works in half-duplex mode
5) If Application tries to use Endpoint with Package that it doesnt support the MsNotification listener gets event warning that package is not supported with error message in logs. http://code.google.com/p/mobicents/issues/detail?id=434
6) As usual fixed other bugs related to functionality and performance
http://code.google.com/p/mobicents/issues/list?can=1&q=Media-Server%20label%3AMilestone-Release-1.0.0.CR3
7) mms-console now shows the active Endpoints and number of connections that each may be holding with option to destroy them forcefully. Option to start / stop / destroy server from console.

Download the nightly SNAPSHOT from http://hudson.qa.jboss.com/hudson/view/Mobicents/job/MobicentsMediaServerRelease/

WARNING : The G729 is patented and Codec Implementation of G729 bundled with this release is subject to license terms. Look at http://www.sipro.com/g729_licterms.php. 

DISCLAIMER : This code is free (Not charging you to use it), but you might have to pay royalty fees to the G.729 patent holders for using their algorithm. There is  NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND DATA ACCURACY.  We do not warrant or make any representations regarding the use of the software or the  results thereof, including but not limited to the correctness, accuracy, reliability or usefulness of the software





