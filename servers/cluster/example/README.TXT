Simple app which shows how to use FTF timers.

SETUP
1. Change beans/src/main/resources/META-INF/jboss-beans.xml
For now we make use of CacheManager to get cache, rather than local cache configuration

Alter definition of bean to reassemble following:

<bean name="Mobicents.Cache" class="org.mobicents.slee.runtime.cache.MobicentsCache">
		<constructor>

          	<parameter><inject bean="jboss.cache:service=CacheManager"></inject></parameter>
          	<parameter class="java.lang.String">pessimistic-shared-repeatable</parameter>
          	<parameter><inject bean="RealTransactionManager"/></parameter>
 
     	</constructor>			
     	<start method="startRootRegion">
     		<parameter>
     			<inject bean="Mobicents.CacheClassLoader"/>
     		</parameter>
     	</start> 
     	<demand>Mobicents.CacheClassLoader</demand>
     	<demand>Mobicents.JBossCacheFactory</demand>

</bean>


2. change cluster/pom.xml
 - add definition of node, properties should look like:
 
 <properties>
		<jboss.version>5.1.0.GA</jboss.version>
		<!-- OTHER MOBICENTS COMPONENTS VERSIONS -->
		<mobicents.tools.mavenplugin.du.version>2.0.0.BETA2</mobicents.tools.mavenplugin.du.version>
		<mobicents.tools.mavenplugin.eclipse.version>1.0.0.BETA2</mobicents.tools.mavenplugin.eclipse.version>
		<node>all</node>
  </properties>
  
  - add definition of example module
  <modules>
		<module>ftf</module>
		<module>cache</module>
		<module>timers</module>
		<module>example</module>
		<module>beans</module>
	</modules>
	

DEPLOY:
 Simply run "mvn install". Once maven is done copy from JBOSS1_HOME/server/all/deploy to JBOSS_2HOME/server/all/deploy
 
 

RUN
Start two terminals, "cd" to each jboss home, export in each terminal JBOSS_HOME pointing to local jboss (otherwise some services will try to use the same directory(unless JBOSS_HOME is not defined at all))
Start servers with command:

Server1: bin/run.sh -b 127.0.0.1 -c all -Djboss.messaging.ServerPeerID=1 -Djboss.service.binding.set=ports-default
Server2: bin/run.sh -b 127.0.0.1 -c all -Djboss.messaging.ServerPeerID=2 -Djboss.service.binding.set=ports-01

Now access on of JMX consoles: http://127.0.0.1:8080/jmx-console or http://127.0.0.1:8180/jmx-console
Go to FTFTest bean, schedule timer, wait to execute. Now set timer again, with bigger delay, so node which starts it, can shutdown before 
timer fires. Once timer is set, kill server on which it started. Wait for it to fire on second server. 
 