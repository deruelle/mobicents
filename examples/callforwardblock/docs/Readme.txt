README file for callforwardblock service

ControllerSbb:
Controls incoming INVITE requests and queries existing profiles to determine what course of action to take.
A profile must be created for each end user.

CallForwardSbb:
The design of this SBB uses B2BUA to forward a call to its destination

CallBlockSbb:
Sends a 486 busy back to the client.

CallForwardBlockTable Provisioning:
To manually provision profiles for this service, bring up the JMX Console for Jboss.  Scroll down all the 
way to ProfileProvisioningMBean.  Follow these steps: (in all steps return back to previous screen
1) invoke create operation - create the ProfileProvisioningMBean
2) invoke start operation - start the MBean
3) invoke createProfileTable set p1=CallForwardBlock Profile Spec#Mobicents#1.0, set p2=CallForwardBlockProfileTable
4) invoke createProfile set p1=CallForwardBlockProfileTable, set p2=<nameOfNewProfile>
5) go back to Agent View and click on >> profile="<nameOfNewProfile>",profileTableName="CallForwardBlockProfileTable",type=profile
6) change parameters for Address to represent the sip address of end user
7) change Callblock or Callforward boolean values as appropriate
8) if forwarding change ForwardToDestination to be the new sip end point
9) Apply Changes
10) invoke commit profile
11) invoke close profile

Running Service:
Startup 3 user agents, make sure they register with Mobicents server.  To check go to Jboss JMX view and look
for "service=RuntimeTreeCache".  In this view invoke the operation printDetails.  This should print everything
in the runtime cache.  Search for "/location:" string, after this string should be the URI of user agents
registers with Mobicents.
Have one user agent call another.  Depending on the profiles that were setup this should either redirect
and forward the call to another user agent or send back 486 busy indicating the user agent has been blocked.

