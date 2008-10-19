==========================================================================
! Welcome to Mobicents Media server (MMS) - The Open Source Media server !
==========================================================================
This mobicents-media-server-all-1.0.0.CR1 binary is having
	* Mobicents Media Server
	* Examples for Mobicents Media Server
	* Controllers which includes MGCP controller and Media Server Controller which is local (in JVM) controller for MMS
        * Resource Adaprors required by examples
        * Mobicents Media Server Management Console to manage the endpoints/connections of MMS via web. Available at http://localhost:8080/mms-console

This is complete Media Server which includes the JBoss AS 4.2.2.GA and Mobicents Core Server (JAIN SLEE 1.2.1.GA).

Mobicents Media Server is open source media server aimed to:

-Deliver competitive, complete, best-of-breed media gateway functionality featuring highest quality. 
-Meet the demands of converged wireless, wireline, cable broadband access and fixed-mobile converged VoIP 
 networks from a single media gateway platform 
-Increase flexibility with a media gateway that supports wide variety of call control protocols and scales down 
 to meet the demands of enterprises and small carrier providers. 
-React quickly to dynamic market requirements. 

Mobicents Media Server Home Page: http://www.mobicents.org/products_media_server.html
Mobicents documentation page: Chapter 8 of http://groups.google.com/group/mobicents-public/web/user-guide
Version information: media-server_1.0.0.CR1


To install media server
----------------------------------------------------------------------
1. Run Mobicents SLEE server.
2. Unzip Media server binary and call run.bat (run.sh for linux from ) mobicents-media-server-all-1.0.0.CR1/jboss-4.2.2.GA/bin

To Deploy Examples
----------------------------------------------------------------------
Go to examples directory /mobicents-media-server-all-1.0.0.CR1/examples/call-controller2 and call 'ant deploy-all' to deploy example and all the dependencies

To undeploy call 'ant undeploy-all'

Look at link to know more about examples http://groups.google.com/group/mobicents-public/web/mobicents-examples


Highlights of 1.0.0.CR1
----------------------------------------------------------------------
1) This release is continued effort of Architecture changes of MMS. (First change went out in BETA4) It is highly recommended that you read the docs thoroughly before you start making changes in your application 
Chapter 8 of http://groups.google.com/group/mobicents-public/web/user-guide

2) Added MMS Management Console. http://groups.google.com/group/mobicents-public/web/section-MMS_Management_console.html

3) Improved MSC API and is more feature rich. Please look at http://groups.google.com/group/mobicents-public/web/section-MMS_Control_API.html
4) Media RA has also changed in terms of events fired. Please look at http://groups.google.com/group/mobicents-public/web/mobicents-media-ra 
5) As usual fixed other bugs related to functionality and performance
http://code.google.com/p/mobicents/issues/list?can=1&q=label%3ARelease-1.0.0.CR1+status%3AFixed&colspec=ID+Priority+Component+Milestone+Version+Release+Type+Status+Owner+Summary&cells=tiles

Download the nightly SNAPSHOT from http://hudson.qa.jboss.com/hudson/view/Mobicents/job/MobicentsMediaServerRelease/

WARNING : The G729 is patented and Codec Implementation of G729 bundled with this release is subject to license terms. Look at http://www.sipro.com/g729_licterms.php. 

DISCLAIMER : This code is free (Not charging you to use it), but you might have to pay royalty fees to the G.729 patent holders for using their algorithm. There is  NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND DATA ACCURACY.  We do not warrant or make any representations regarding the use of the software or the  results thereof, including but not limited to the correctness, accuracy, reliability or usefulness of the software

FAQ:
Q:1 : So what has changed in MSC API? 

Ans : a) The main difference is now Applications shouldn't use the MsSignalGenerator or MsSignalDetector to detect DTMF or Play/Record. Rather use the MsEventFactory. Please look at MSC API Doc.

b) The MsResourceListener is not used any more. Rather use MsNotificationListener to listen for events like DTM, Announcement Complete or Failed etc.

Q:2 : I don't use MSC API but depend on Media RA, should I care of new changes?

Ans : Yes. Since media-ra is wrapper on top of MSC API, SBB's shouldn't make use of MsSignalGenerator and MsSignalDetector. Both of these Activities are not used any more in media-ra. Please look at media-ra doco and mms-demo example




