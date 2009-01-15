/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.rhq.plugins.mobicents.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mc4j.ems.connection.ConnectionFactory;
import org.mc4j.ems.connection.EmsConnectException;
import org.mc4j.ems.connection.EmsConnection;
import org.mc4j.ems.connection.settings.ConnectionSettings;
import org.mc4j.ems.connection.support.ConnectionProvider;
import org.mc4j.ems.connection.support.metadata.ConnectionTypeDescriptor;
import org.mc4j.ems.connection.support.metadata.InternalVMTypeDescriptor;
import org.mc4j.ems.connection.support.metadata.J2SE5ConnectionTypeDescriptor;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.plugins.jmx.JMXComponent;

 /**
 * Supports Mobicents Sip Balancer
 *
 * @author jean.deruelle@gmail.com
 */
public class MobicentsSipBalancerComponent implements JMXComponent {
    private static Log log = LogFactory.getLog(MobicentsSipBalancerComponent.class);

    private ResourceContext resourceContext;
    private EmsConnection connection;
    /**
     * Controls the dampening of connection error stack traces in an attempt to control spam to the log
     * file. Each time a connection error is encountered, this will be incremented. When the connection
     * is finally established, this will be reset to zero.
     */
    private int consecutiveConnectionErrors;
    
    public void start(ResourceContext context) {
        resourceContext = context;
        // Attempt to load the connection now. If we cannot, do not consider the start operation as failed. The only
        // exception to this rule is if the connection cannot be made due to a JMX security exception. In this case,
        // we treat it as an invalid plugin configuration and throw the appropriate exception (see the javadoc for
        // ResourceComponent)
        try {
            getEmsConnection();
        } catch (Exception e) {

            // Explicit checking for security exception (i.e. invalid credentials for connecting to JMX)
            if (e instanceof EmsConnectException) {
                Throwable cause = e.getCause();

                if (cause instanceof SecurityException) {
                    throw new InvalidPluginConfigurationException(
                        "Invalid JMX credentials specified for connecting to this balancer.", e);
                }
            }

        }
    }

    public void stop() {
    	if (this.connection != null) {
            try {
                this.connection.close();
            } catch (Exception e) {
                log.error("Error closing Mobicents Sip Balancer connection: " + e);
            }
            this.connection = null;
        }
    }

    public AvailabilityType getAvailability() {
    	try {
            EmsConnection connection = getEmsConnection();
            if(connection != null) {
	            connection.getBean("mobicents:type=LoadBalancer,name=LoadBalancer");
	            return AvailabilityType.UP;
            } else {
            	return AvailabilityType.DOWN;
            }
        } catch (Exception e) {
            return AvailabilityType.DOWN;
        }
    }

    public EmsConnection getEmsConnection() {
    	if (this.connection == null) {
            try {
            	Configuration pluginConfig = resourceContext.getPluginConfiguration();
            	
		    	ConnectionSettings connectionSettings = new ConnectionSettings();
		    	connectionSettings.initializeConnectionType((ConnectionTypeDescriptor) Class.forName(
		    			J2SE5ConnectionTypeDescriptor.class.getCanonicalName()).newInstance());
		    	connectionSettings.setServerUrl("service:jmx:rmi:///jndi/rmi://" + pluginConfig.getSimpleValue(MobicentsSipBalancerDiscoveryComponent.HOST_PROP, "localhost") + ":" + pluginConfig.getSimpleValue(MobicentsSipBalancerDiscoveryComponent.RMI_REGISTRY_PORT_PROP, "2000") + "/server");
                connectionSettings.setPrincipal(null);
                connectionSettings.setCredentials(null);
                
                String balancerExecutableJarFilePath = pluginConfig.getSimpleValue(MobicentsSipBalancerDiscoveryComponent.BALANCER_EXECUTABLE_JAR_FILE_PATH, null);
                String balancerExecutableJarFile = pluginConfig.getSimpleValue(MobicentsSipBalancerDiscoveryComponent.BALANCER_EXECUTABLE_JAR_FILE, null);
                log.debug("JarBalancer = " + balancerExecutableJarFile);                
                log.debug("JarBalancerPath = " + balancerExecutableJarFilePath);
                // allow JMX to unmarshall SIPNode class in LoadBalancerComponent
                List<File> classpathEntries = connectionSettings.getClassPathEntries();
                if(classpathEntries == null) {
                	classpathEntries = new ArrayList<File>();
                }
                classpathEntries.add(new File(balancerExecutableJarFile));
                connectionSettings.setClassPathEntries(classpathEntries);
//                connectionSettings.setLibraryURI(pluginConfig.getSimpleValue("balancerJarPath", null));
                
                
                ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.discoverServerClasses(connectionSettings);
                
                if (connectionSettings.getAdvancedProperties() == null) {
                    connectionSettings.setAdvancedProperties(new Properties());
                }

                // Tell EMS to make copies of jar files so that the ems classloader doesn't lock
                // application files (making us unable to update them)  Bug: JBNADM-670
                connectionSettings.getControlProperties().setProperty(ConnectionFactory.COPY_JARS_TO_TEMP,
                    String.valueOf(Boolean.TRUE));

                // But tell it to put them in a place that we clean up when shutting down the agent
                connectionSettings.getControlProperties().setProperty(ConnectionFactory.JAR_TEMP_DIR,
                    resourceContext.getTemporaryDirectory().getAbsolutePath());

                connectionSettings.getAdvancedProperties().setProperty(InternalVMTypeDescriptor.DEFAULT_DOMAIN_SEARCH,
                    "mobicents");

                log.info("Loading Mobicents Sip Balancer connection [" + connectionSettings.getServerUrl() + "]...");

                ConnectionProvider connectionProvider = connectionFactory.getConnectionProvider(connectionSettings);
                this.connection = connectionProvider.connect();

                this.connection.loadSynchronous(false); // this loads all the MBeans

                this.consecutiveConnectionErrors = 0;

                if (log.isDebugEnabled())
                    log.debug("Successfully made connection to the Mobicents Sip Balancer instance for resource ["
                        + this.resourceContext.getResourceKey() + "]");
            } catch (Exception e) {

                // The connection will be established even in the case that the principal cannot be authenticated,
                // but the connection will not work. That failure seems to come from the call to loadSynchronous after
                // the connection is established. If we get to this point that an exception was thrown, close any
                // connection that was made and null it out so we can try to establish it again.
                if (connection != null) {
                    if (log.isDebugEnabled())
                        log.debug("Connection created but an exception was thrown. Closing the connection.", e);
                    connection.close();
                    connection = null;
                }

                // Since the connection is attempted each time it's used, failure to connect could result in log
                // file spamming. Log it once for every 10 consecutive times it's encountered. 
                if (consecutiveConnectionErrors % 10 == 0) {
                    log.warn("Could not establish connection to the Mobicents Sip Balancer instance ["
                        + (consecutiveConnectionErrors + 1) + "] times for resource ["
                        + resourceContext.getResourceKey() + "]", e);
                }

                if (log.isDebugEnabled())
                    log.debug("Could not connect to the Mobicents Sip Balancer instance for resource ["
                        + resourceContext.getResourceKey() + "]", e);

                consecutiveConnectionErrors++;
            }
        }

        return connection;
    }
}