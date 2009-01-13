==========================================================================
! Welcome to Mobicents Media server (MMS) - The Open Source Media server !
==========================================================================
This mobicents-media-server-all-1.0.0.CR4 binary is having
    * Mobicents Media Server
    * Examples for Mobicents Media Server
    * Controllers which includes MGCP controller which is remote and Media Server Controller which is local (in JVM) controller for MMS
    * Resource Adaprors required by examples
    * Mobicents Media Server Management Console to manage the endpoints/connections of MMS via web. Available at http://localhost:8080/mms-console

This is complete Media Server which includes the JBoss AS 4.2.3.GA and Mobicents Core Server (JAIN SLEE 1.2.3.GA).

Mobicents Media Server is open source media server aimed to:

-Deliver competitive, complete, best-of-breed media gateway functionality featuring highest quality.
-Meet the demands of converged wireless, wireline, cable broadband access and fixed-mobile converged VoIP
 networks from a single media gateway platform
-Increase flexibility with a media gateway that supports wide variety of call control protocols and scales down
 to meet the demands of enterprises and small carrier providers.
-React quickly to dynamic market requirements.

Mobicents Media Server Home Page: http://www.mobicents.org/products_media_server.html
Mobicents documentation page: Chapter 8 of http://groups.google.com/group/mobicents-public/web/user-guide
Version information: media-server_1.0.0.CR4


To install media server
----------------------------------------------------------------------
1. Run Mobicents SLEE server.
2. Unzip Media server binary and call run.bat (run.sh for linux from ) mobicents-media-server-all-1.0.0.CR4/jboss-4.2.3.GA/bin

To Deploy Examples
----------------------------------------------------------------------
Go to examples directory /mobicents-media-server-all-1.0.0.CR4/examples/call-controller2 and call 'ant deploy-all' to deploy example and all the dependencies

To undeploy call 'ant undeploy-all'

Look at link to know more about examples http://groups.google.com/group/mobicents-public/web/mobicents-examples


Highlights of 1.0.0.CR4
----------------------------------------------------------------------
1) This is a performance improvement release over CR3. We are glad to announce that MMS can now easily achieve 100 concurrent announcements with transcoding. MGCP Stack has much better performance now and so is MGCP Resource Adaptor.
If you are interested to do the performance test at your environment please drop us a mail.

2) Added support for GSM Codec. With this Mobicents Media Server implements almost all widely used Telco codecs like PCM-A, PCM-U, Speex, G729 and GSM

3) The most important change is transcoding is removed from Announcement and IVR Endpoint. In order to have transcoding, the application needs to use PR Endpoint. Look at VoiceMailSbb of call-controller2 example to understand this. The Conference Endpoint is unchanged. This move is to improve the performance. If the application knows the codec used by UA, the audio files can be pre encoded in that format and hence all MMS will do is create RTP Packets and send it to far end. Even if application doesn't know the Codec's used by UA, application should take onus of having audio files pre encoded in multiple supported codecs and call one of them depending on codec negotiated. The business logic here is much cleaner and cheap as compared to unnecessary transcoding.

4) As usual fixed other bugs related to functionality and performance
http://code.google.com/p/mobicents/issues/list?can=1&q=Media-Server%20label%3AMilestone-Release-1.0.0.CR4

5) For this release we have removed mms-console. Insted JOPR will soon replace mms-console but that will happen only in 2.x releases of MMS

Download the nightly SNAPSHOT from http://hudson.qa.jboss.com/hudson/view/Mobicents/job/MobicentsMediaServerRelease/

WARNING : The G729 is patented and Codec Implementation of G729 bundled with this release is subject to license terms. Look at http://www.sipro.com/g729_licterms.php.

DISCLAIMER : This code is free (Not charging you to use it), but you might have to pay royalty fees to the G.729 patent holders for using their algorithm. There is  NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND DATA ACCURACY.  We do not warrant or make any representations regarding the use of the software or the  results thereof, including but not limited to the correctness, accuracy, reliability or usefulness of the software






