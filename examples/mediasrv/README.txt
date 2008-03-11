The motive of this example is to demonstarte the capabilities of new Media Server (MS) and Media Server Resource Adaptors (MSC-RA)

The example demonstrates usage of
- Announcement Endpoint
- Packet Relay Endpoint
- Loop Endpoint
- Conference Endpoint
- IVR Endpoint

To understand each of this Endpoint's look at Media Server doco http://groups.google.com/group/mobicents-public/web/mobicents-media-server

The example has detailed description at http://groups.google.com/group/mobicents-public/web/mobicents-mediasrv-example

INSTALL and RUN

Start the Mobicents Server (this will also start Media Server). Make sure you have server/default/deploy/mobicents.sar and server/default/deploy/mediaserver.sar in your Mobicents Server


DEPLOY from BINARY

Go to /examples/mediasrv and call 'ant deploy-all'. This will deploy the SIP RA, MSC RA, the mediasrv example and also msdemo-audio.war. The war file contains the audio *.wav files that are used by mediasrv example.

DEPLOY from SOURCE

If you are deploying from source code, you may deploy each of the resource adaptors individually
1) make sure JBOSS_HOME is set and server is running.
2) Call ant from /resources/sipra to deploy SIP RA (default target is ra-deploy)
3) Call ant from /servers/media/mscontrol/msc-ra to deploy msc-ra (default target is ra-deploy)
4) Call ant deploy from mobicents/examples/mediasrv to deploy example
5) Call ant deploy-audio from mobicents/examples/mediasrv to deploy war file containing *.wav audio 

Once the example is deployed, make a call from your SIP Phone to

1010 : This demonstrates the usage of Loop Endpoint. 
1011 : This demonstrates the usage of DTMF. 
1012 : This demonstrates the usage of ConfEndpointImpl.

