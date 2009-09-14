Simple app which shows how to use Mobients Cluster timers.

DEPLOY:
 Simply run "mvn install -Pexample". Once maven is done copy $JBOSS_HOME to $JBOSS_HOME_ 
 
RUN
Start two terminals, "cd" to each jboss home, export in each terminal JBOSS_HOME pointing to local jboss (otherwise some services will try to use the same directory(unless JBOSS_HOME is not defined at all))
Start servers with command:

Server1: bin/run.sh -c all -Djboss.messaging.ServerPeerID=1 -Djboss.service.binding.set=ports-default
Server2: bin/run.sh -c all -Djboss.messaging.ServerPeerID=2 -Djboss.service.binding.set=ports-01

Now access on of JMX consoles: http://127.0.0.1:8080/jmx-console or http://127.0.0.1:8180/jmx-console
Go to FTFTest bean, schedule timer, wait to execute. Now set timer again, with bigger delay, so node which starts it, can shutdown before 
timer fires. Once timer is set, kill server on which it started. Wait for it to fire on second server. 
 