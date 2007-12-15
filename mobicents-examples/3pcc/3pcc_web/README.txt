
Note: if you experience problems during installation/execution, please see READMEs in respective project for details!

+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+
!                                                                                            !  
!  1. How to run the 3pcc app using the web app (servlet)                                    !
!                                                                                            !
+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+

0) This application has been tested on Mobicents running on JBoss 3.2.6 and JBoss 3.2.7 using java 1.5
1) Make sure JBOSS_HOME is set correctly in Eclipse->Window->Preferences->Java->Build Path->Classpath Variables. 
2) Make sure JBOSS_HOME is set correctly as a system environment variable.
3) Deploy Mobicents by using ANT::mobicents/auto_deploy_sip
4) Start JBoss in a prompt/term with >> run -c all
5) Make sure the config file (3pcc_web/common/etc/callservice.properties) is configured correctly ('Servlet').
6) Deploy the SLEE 3ppc app and web app by issuing ANT::3pcc/deploy-3pcc_web_app-(hot).
7*) Start a SIPp client in a prompt/term with           >> sipp -sf uas.xml -i localhost -p 5062 -nr
8*) Start another SIPp client in a new prompt/term with >> sipp -sf uas.xml -i localhost -p 5064 -nr
   (In order to be able to run SIPp on Windows, you need to install cygwin (http://cygwin.com)!)
9) Open a browser in http://localhost:8080/thirdpcc and enter the calling parties. 
   Use sip:caller@localhost:5062 resp sip:callee@localhost:5064 if you've set up the SIPp clients.
   Press 'Launch call' and be astonished!!!


+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+
!                                                                                            !  
!  2. How to run the 3pcc app over ParlayX using the web app (servlet)                       !
!                                                                                            !
+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+

0-4) As above.
5) Make sure the config file (3pcc_web/common/etc/callservice.properties) is configured correctly ('WebService').
6) Deploy the SLEE 3ppc app, web service and web app by issuing ANT::3pcc/deploy-3pcc_web_app+web_service-(hot)
7-8*) As above.
9) As above.


+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+
!                                                                                            !  
!  3. How to run the 3pcc app using the ParlayX web service in standalone mode (no servlet)  !
!                                                                                            !
+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+

0-4) As above.
5) Make sure the config file (3pcc_web/common/etc/callservice.properties) is configured correctly ('WebService').
6) Deploy the SLEE 3ppc app and the web service by issuing ANT::3pcc/deploy-3pcc_ws_server-(hot).
7-8*) As above.
9**) Run WS client by issuing ANT::3pcc/run-3pcc_ws_client


Note that in order to switch call service ('Servlet' and 'WebService'), you need to stop JBoss,
undeploy, change call service (in 3pcc_web/common/etc/callservice.properties) and then deploy!
It's not enough to change the file in the deployed dir, since this info is read by a singleton 
at startup!


* Note: step 7 and 8 are optional; SIPp shows the SIP signaling flow and is great for testing purposes!
** There is a bug in the web server that makes the getCallInformation() executing with errors when 
executing the web service in standalone mode (it works when used by the servlet in 2. above). Therefore, 
the web service client invoker currently doesn't use this functionality. Instead, the flow contains 
makeCall(), wait for 10s (simulate talking) and endCall(), i.e no call status is retrieved during the 
call setup when the web service is used in standalone mode!
