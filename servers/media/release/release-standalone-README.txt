====================================================================
! Welcome to Mobicents Media server - The Open Source Media server !
====================================================================
* This media-server_1_0_0_CR1 binary is only having the Mobicents Media Server related     * 
* jars.                                                                                    *

Mobicents Media Server is open source media server aimed to:

-Deliver competitive, complete, best-of-breed media gateway functionality featuring highest quality. 
-Meet the demands of converged wireless, wireline, cable broadband access and fixed-mobile converged VoIP 
 networks from a single media gateway platform 
-Increase flexibility with a media gateway that supports wide variety of call control protocols and scales down 
 to meet the demands of enterprises and small carrier providers. 
-React quickly to dynamic market requirements. 

Mobicents Media Server Home Page: http://www.mobicents.org/products_media_server.html
Mobicents documentation page: http://groups.google.com/group/mobicents-public/web/section-Mobicents_Media_Server.html
Version information: media-server_1.0.0.CR1


To install media server
----------------------------------------------------------------------
1. Run Mobicents SLEE server.
2. Install Media server binary. Copy content of media-server_1_0_0_CR1/server/ to the JBoss deployment directory (jboss-4.2.2.GA/server/default/deploy)
   2.1 mobicents-media-server-1.0.0.CR1 is core Mobicents Media Server (MMS)


Highlights of 1.0.0.CR1
----------------------------------------------------------------------
1) This release is continued effort of Architecture changes of MMS. (First change went out in BETA4) It is highly recommended that you read the docs thoroughly before you start making changes in your application 
Chapter 8 of http://groups.google.com/group/mobicents-public/web/user-guide

2) Added MMS Management Console. http://groups.google.com/group/mobicents-public/web/section-MMS_Management_console.html

3) Improved MSC API and is more feature rich. Please look at http://groups.google.com/group/mobicents-public/web/section-MMS_Control_API.html
4) Media RA has also changed in terms of events fired. Please look at http://groups.google.com/group/mobicents-public/web/mobicents-media-ra 
5) As usual fixed other bugs related to functionality and performance

Download the nightly SNAPSHOT from http://hudson.qa.jboss.com/hudson/view/Mobicents/job/MobicentsMediaServerRelease/