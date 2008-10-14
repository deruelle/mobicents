This example is an JAIN SLEE application that take advantage of the
Mobicents SIP Presence service. It subscribes presence state in
the Integrated server. you can see in the server console the
notifications that the application receive each time the notifier
changes state. The notifier subscribed, which you have to log in, is
sip:user, of the domain used to start the server (127.0.0.1 or the
IP that Mobicents JAIN SLEE server is bound using -b parameter.

To deploy from binary release just do "ant deploy", to undeploy do
"ant undeploy". If you're building and deploying from source you do
"mvn install" to build and deploy, "mvn clean" to undeploy. If you
are not using the Mobicents ALL binary release make sure that
JBOSS_HOME environment variable is set and pointing to the Mobicents
JAIN SLEE server with the SIP Presence Service deployed.