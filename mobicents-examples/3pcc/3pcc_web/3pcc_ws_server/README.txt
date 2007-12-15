This is a web service server that publishes the services used to access the 3pcc app from a remote site.

The available services are makeCall, endCall, cancelCallRequest and getCallInformation.

The web service is usually accessed from the web app or from the standalone web service client. 
See the READMEs in the projects 3pcc_web resp 3pcc_ws_client for more info on how to call the WS server. 
These requires to have the 3pcc SIP app deployed, see README.txt in 3pcc_sip_slee.

In order to deploy the WS server, all you need to do is to set the environment variable JBOSS_HOME 
that is used to locate the directory for deployment. (Ex /opt/jboss-3.2.7)
Then issue 'ant deploy'. However, this is automatically performed when using the web server in for 
ex. the web app.