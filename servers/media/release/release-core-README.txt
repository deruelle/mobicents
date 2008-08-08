====================================================================
! Welcome to Mobicents Media server - The Open Source Media server !
====================================================================
* This media-server_1_0_0_BETA3 binary is only having the Mobicents Media Server related   * 
* jars. The complete binary media-server-all_1_0_0_BETA2 with MGCP Controller and examples *
* will be released after mobicents-all 1.2.0.GA is released.                               *

Mobicents Media Server is open source media server aimed to:

-Deliver competitive, complete, best-of-breed media gateway functionality featuring highest quality. 
-Meet the demands of converged wireless, wireline, cable broadband access and fixed-mobile converged VoIP 
 networks from a single media gateway platform 
-Increase flexibility with a media gateway that supports wide variety of call control protocols and scales down 
 to meet the demands of enterprises and small carrier providers. 
-React quickly to dynamic market requirements. 

Mobicents Media Server Home Page: http://www.mobicents.org/products_media_server.html
Mobicents documentation page: http://groups.google.com/group/mobicents-public/web/mobicents-media-server
Version information: media-server_1.0.0.BETA3


To install media server
----------------------------------------------------------------------
1. Run Mobicents SLEE server.
2. Install Media server binary. Copy content of media-server_1_0_0_BETA2/server/ to the JBoss deployment directory (jboss-4.2.2.GA/server/default/deploy)
   2.1 mobicents-media-server-1.0.0.BETA2 is core Mobicents Media Server (MMS)


Highlights of 1.0.0.BETA3
----------------------------------------------------------------------
1) Added Speex Codec to MMS
2) Added STUN Support
3) Improved the performance by fine tunning the Threads Model
4) Improved MMS test suite
5) As usual fixed other bugs related to functionality and performance

Download the nightly SNAPSHOT from http://hudson.qa.jboss.com/hudson/view/Mobicents/job/MobicentsMediaServerRelease/