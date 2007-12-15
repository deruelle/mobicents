This is a web service client that uses the web services to access the 3pcc app from a remote site. 
When calling the web service using this client, it is often referred to (throuout the rest of the 
documentation) as using the web service in "standalone mode". 

This invoker performs the following:

 - calls makeCall() in order to setup the call 
   (default parties are sip:caller@localhost:5062 and sip:caller@localhost:5064)
 - wait for 10s (simulate talking) 
 - calls endCall()
  
 i.e no call status is retrieved during the call setup when the web service is 
 used in standalone mode (due to the bug described in the release notes of the WS server)!

In order run the the WS client, all you need to do is to set the environment variable JBOSS_HOME 
and then issue 'ant run-ws'. 

The WS client requires the 3pcc SIP app, see README.txt in 3pcc_sip_slee.