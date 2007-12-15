This is a web application used to trigger the SIP callflow in the 3pcc_sip_slee project.

The app consists of a small MVC implementation using a servlet and a few JSPs.
The monitoring of state is done with AJAX. 

The app has a form for submitting two SIP addresses and a page that monitors call flow and provides
functionality for terminating the call once setup, or cancelling the call setup while still in progress.

All you need to do is to set the environment variable JBOSS_HOME that is used to locate the directory for deployment 
of the web app. (Ex /opt/jboss-3.2.7)

Then issue 'ant deploy' and point your browser to http://localhost:8080/thirdpcc

You must also deploy the 3pcc SIP app, see README.txt in 3pcc_sip_slee

(In case you wonder why the name of the web app isn't 3pcc it's because it's an illegal name according to specification 
or JBOSS implementation. It cannot be used.)   