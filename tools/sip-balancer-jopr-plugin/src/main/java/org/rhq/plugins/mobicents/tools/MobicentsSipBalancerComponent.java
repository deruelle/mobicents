 /*
  * Jopr Management Platform
  * Copyright (C) 2005-2008 Red Hat, Inc.
  * All rights reserved.
  *
  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License, version 2, as
  * published by the Free Software Foundation, and/or the GNU Lesser
  * General Public License, version 2.1, also as published by the Free
  * Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  * GNU General Public License and the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU General Public License
  * and the GNU Lesser General Public License along with this program;
  * if not, write to the Free Software Foundation, Inc.,
  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
  */
package org.rhq.plugins.mobicents.tools;

import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mc4j.ems.connection.ConnectionFactory;
import org.mc4j.ems.connection.EmsConnection;
import org.mc4j.ems.connection.settings.ConnectionSettings;
import org.mc4j.ems.connection.support.ConnectionProvider;
import org.mc4j.ems.connection.support.metadata.ConnectionTypeDescriptor;
import org.mc4j.ems.connection.support.metadata.InternalVMTypeDescriptor;
import org.mc4j.ems.connection.support.metadata.J2SE5ConnectionTypeDescriptor;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.pluginapi.operation.OperationFacet;
import org.rhq.core.pluginapi.operation.OperationResult;
import org.rhq.plugins.jmx.JMXComponent;

 /**
 * Supports Mobicents Sip Balancer
 *
 * @author jean.deruelle@gmail.com
 */
public class MobicentsSipBalancerComponent implements JMXComponent, MeasurementFacet, OperationFacet {
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
    }

    public void stop() {
    }

    public AvailabilityType getAvailability() {
        // TODO: Implement availability check.
        return AvailabilityType.UP;
    }

    public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> requests) {
        for (MeasurementScheduleRequest request : requests) {
            String name = request.getName();

            // TODO: based on the request information, you must collect the requested measurement(s)
            //       you can use the name of the measurement to determine what you actually need to collect
            try {
                Number value = new Integer(1); // dummy measurement value - this should come from the managed resource
                report.addData(new MeasurementDataNumeric(request, value.doubleValue()));
            } catch (Exception e) {
                log.error("Failed to obtain measurement [" + name + "]. Cause: " + e);
            }
        }

        return;
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

                ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.discoverServerClasses(connectionSettings);

                if (connectionSettings.getAdvancedProperties() == null) {
                    connectionSettings.setAdvancedProperties(new Properties());
                }

                // Tell EMS to make copies of jar files so that the ems classloader doesn't lock
                // application files (making us unable to update them)  Bug: JBNADM-670
                // TODO GH: turn this off in the embedded case
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

	public OperationResult invokeOperation(String arg0, Configuration arg1)
			throws InterruptedException, Exception {
		// TODO Auto-generated method stub
		return null;
	}
}