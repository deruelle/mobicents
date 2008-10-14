---------< Mobicents Integrated SIP Presence Service >----------

This server is the result of the integration of the Mobicents XDM
and SIP Presence servers. to start the server just start the 
underlying Mobicents JAIN SLEE Server, using run.sh/run.bat script
in the bin directory. 

Two JAIN SLEE application examples, that take advantage of the
integrated server, come pre-deployed:

* Internal Publisher, this application publishes presence state in
the Integrated server. To see this state use a SIP UA and subscribe
presence event package of entity sip:internal-publisher, using the
domain used to start the server (127.0.0.1 or the IP that Mobicents
JAIN SLEE server is bound using -b parameter.


* Internal Subscriber, this application subscribes presence state in
the Integrated server. you can see in the server console the
notifications that the application receive each time the notifier
changes state. The notifier subscribed, which you have to log in, is
sip:user, of the domain used to start the server (127.0.0.1 or the
IP that Mobicents JAIN SLEE server is bound using -b parameter.

To undeploy the examples just remove the deployable unit jars
msps-examples-*.jar from the server/default/deploy directory.

Author: Eduardo Martins, JBoss R&D