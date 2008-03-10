====================================================================
! Welcome to Mobicents Media server - The Open Source Media server !
====================================================================

Mobicents Media Server is open source, based on Java Media Framework implementation aimed to:

-Deliver competitive, complete, best-of-breed media gateway functionality featuring highest quality. 
-Meet the demands of converged wireless, wireline, cable broadband access and fixed-mobile converged VoIP 
 networks from a single media gateway platform 
-Increase flexibility with a media gateway that supports wide variety of call control protocols and scales down 
 to meet the demands of enterprises and small carrier providers. 
-React quickly to dynamic market requirements. 

Mobicents home page: http://groups.google.com/group/mobicents-public/web/mobicents-media-server
Version information: media-server_1_0_0_ALPHA1


To install media server
----------------------------------------------------------------------
1. Run Mobicents SLEE server and install SIP resource adaptor.
2. Install Media server binary. Copy mediaserver.sar to the JBoss deployment directory (jboss-4.2.2.GA/server/default/deploy).Copy dtmf.properties to the 
JBoss conf directory (jboss-4.2.2.GA/server/default/conf)
3. Intall local Media server control resource adaptor. Copy media-ratype-DU.jar and media-ra-DU.jar from folder RA to
the mobicents deployment directory (jboss-4.2.2.GA/server/default/deploy-mobicents). Copy  msc-ra-deploy.bsh to the mobicents' bean shell script directory (jboss-4.2.2.GA/server/default/deploy-mobicents/scripts).
4. Install examples. Copy demo-service.jar to the mobicents deployment directory and then copy examples-deploy.bsh to 
the mobicent's bean shell script's directory.
5. Deploy audio samples. Copy msdemo-audio.war to the JBoss deployment directory.
6. Run and configure SIP phone.
7. Dial 1010 for echo test. The purpose of this is actable latency between you and the machine running the echo test.
8. Dial 1011 for DTMF handling test.
9. Dial 1012 for conference mixer test.

