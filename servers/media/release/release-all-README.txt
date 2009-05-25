==========================================================================
! Welcome to Mobicents Media server (MMS) - The Open Source Media server !
==========================================================================
This mobicents-media-server-all-1.0.0.GA (Stable) binary is having
    * Mobicents Media Server
    * Examples for Mobicents Media Server
    * Controllers which includes MGCP controller which is remote and Media Server Controller which is local (in JVM) controller for MMS
    * Resource Adaprors required by examples


This is complete Media Server which includes the JBoss AS 4.2.3.GA and Mobicents Core Server (JAIN SLEE 1.2.4.GA).

Mobicents Media Server is open source media server aimed to:

-Deliver competitive, complete, best-of-breed media gateway functionality featuring highest quality.
-Meet the demands of converged wireless, wireline, cable broadband access and fixed-mobile converged VoIP
 networks from a single media gateway platform
-Increase flexibility with a media gateway that supports wide variety of call control protocols and scales down
 to meet the demands of enterprises and small carrier providers.
-React quickly to dynamic market requirements.

Mobicents Media Server Home Page: http://www.mobicents.org/products_media_server.html
Mobicents documentation page: http://hudson.jboss.org/hudson/job/MobicentsBooks/lastSuccessfulBuild/artifact/media/index.html
Version information: media-server_1.0.0.GA


To install media server
----------------------------------------------------------------------
1. Run Mobicents SLEE server. See step 2
2. Unzip Media server binary and call run.bat (run.sh for linux from ) mobicents-media-server-all-1.0.0.GA/jboss-4.2.3.GA/bin

To Deploy Examples
----------------------------------------------------------------------
Go to examples directory /mobicents-media-server-all-1.0.0.GA/examples/call-controller2 and call 'ant deploy-all' to deploy example and all the dependencies

To undeploy call 'ant undeploy-all'

Look at link to know more about examples http://groups.google.com/group/mobicents-public/web/mobicents-examples


Highlights of 1.0.0.GA
----------------------------------------------------------------------
1) This is just a release over 1.0.0.CR6 with minor fix on MGCP Stack. 

Note : With this release the development cycle for 1.x.y will come to an end, unless there is a serious bug report in which case we may create a patch. After this we will actively do the development on MMS 2.x.y version.



Download the nightly SNAPSHOT from http://hudson.qa.jboss.com/hudson/view/Mobicents/job/MobicentsMediaServerRelease/

WARNING : The G729 is patented and Codec Implementation of G729 bundled with this release is subject to license terms. Look at http://www.sipro.com/g729_licterms.php.

DISCLAIMER : This code is free (Not charging you to use it), but you might have to pay royalty fees to the G.729 patent holders for using their algorithm. There is  NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND DATA ACCURACY.  We do not warrant or make any representations regarding the use of the software or the  results thereof, including but not limited to the correctness, accuracy, reliability or usefulness of the software






