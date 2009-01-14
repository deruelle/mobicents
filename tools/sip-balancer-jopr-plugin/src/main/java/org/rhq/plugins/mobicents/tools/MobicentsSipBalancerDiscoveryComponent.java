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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ProcessScanResult;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.core.system.ProcessInfo;
import org.rhq.core.system.SystemInfo;

/**
 * Discovers Mobicents Sip Balancer
 *
 * @author jean.deruelle@gmail.com
 */
public class MobicentsSipBalancerDiscoveryComponent implements ResourceDiscoveryComponent {

	private static final String MOBICENTS_BALANCER_CONFIG_ARG = "-mobicents-balancer-config=";

	private final Log log = LogFactory.getLog(MobicentsSipBalancerDiscoveryComponent.class);
    
    public static final String HOST_PROP = "host";
    public static final String EXTERNAL_PORT_PROP = "externalPort";
	public static final String INTERNAL_PORT_PROP = "internalPort";
    public static final String RMI_REGISTRY_PORT_PROP = "rmiRegistryPort";
	public static final String JMX_HTML_ADAPTER_PORT_PROP = "jmxHtmlAdapterPort";
    
    /**
     * Formal name used to identify the Mobicents Sip Balancer.
     */
    private static final String PRODUCT_NAME = "Mobicents Sip Load Balancer";
    
    /**
     * Formal description of the Mobicents Sip Balancer product passed into discovered resources.
     */
    private static final String PRODUCT_DESCRIPTION = "Mobicents Sip Load Balancer";

    public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext context) {
    	log.debug("Discovering Mobicents Sip Load Balancers ...");

        Set<DiscoveredResourceDetails> resources = new HashSet<DiscoveredResourceDetails>();

        // For each Mobicents Sip Load Balancer process found in the context, process and create a resource details instance
        List<ProcessScanResult> autoDiscoveryResults = context.getAutoDiscoveredProcesses();
        for (ProcessScanResult autoDiscoveryResult : autoDiscoveryResults) {
            log.debug("Discovered Mobicents Sip Load Balancer process: " + autoDiscoveryResult);

            try {
                DiscoveredResourceDetails resource = parseMobicentsSipBalancerProcess(context, autoDiscoveryResult);
                log.debug("Mobicents Sip Load Balancer resource: " + resource);
                if (resource != null) {
                    resources.add(resource);
                }
            } catch (Exception e) {
                log.error("Error creating discovered resource for process: " + autoDiscoveryResult, e);
            }
        }

        return resources;
    }

    /**
     * Processes a process that has been detected to be a Mobicents Sip Balancer. The result will be a JON resource ready to be
     * returned as part of the discovery report.
     *
     * @param  context             discovery context making this call
     * @param  autoDiscoveryResult process scan being parsed for a Mobicents Sip Balancer resource
     *
     * @return resource object describing the Mobicents Sip Balancer running in the specified process
     */
	private DiscoveredResourceDetails parseMobicentsSipBalancerProcess(
			ResourceDiscoveryContext context,
			ProcessScanResult autoDiscoveryResult) {
		// Pull out data from the discovery call
        ProcessInfo processInfo = autoDiscoveryResult.getProcessInfo();
        SystemInfo systemInfo = context.getSystemInformation();
        String[] commandLine = processInfo.getCommandLine();        
        
        String installationPath =  
        	determineMobicentsSipBalancerConfigPath(processInfo);
        log.debug("Installation path for the newly found mobicents sip balancer process is " + installationPath);
        
        Properties config = parseMobicentsSipBalancerConfig(commandLine, installationPath);

        // Create pieces necessary for the resource creation
        String resourceVersion = determineVersion(commandLine);
        log.debug("Version for the newly found mobicents sip balancer process is " + resourceVersion);
        String hostname = systemInfo.getHostname();
        String resourceName = ((hostname == null) ? "" : (hostname + " ")) + PRODUCT_NAME + " " + resourceVersion;
        String resourceKey = installationPath;

        Configuration pluginConfiguration = populatePluginConfiguration(config);

        DiscoveredResourceDetails resource = new DiscoveredResourceDetails(context.getResourceType(), resourceKey,
            resourceName, resourceVersion, PRODUCT_DESCRIPTION, pluginConfiguration, processInfo);
        return resource;
	}  
	
	private String determineMobicentsSipBalancerConfigPath(
			ProcessInfo processInfo) {
		String installationPath = processInfo.getCurrentWorkingDirectory();
		log.debug("Process Info Working Directory" + installationPath);
		
//		String argMainJar = findMainJarArgument(processInfo.getCommandLine());
//		log.debug("Argument Main Balancer Jar" + argMainJar);
//		
//		if(argMainJar != null) {
//			int indexOfLastSeparator = argMainJar.lastIndexOf(File.separatorChar);
//			if(indexOfLastSeparator != -1) {
//				installationPath = argMainJar.substring(0, indexOfLastSeparator); 
//			}
//		}
		
		return installationPath;
	}

	private String determineVersion(String[] commandLine) {
		String version = "1.0";
		String argMainJar = findMainJarArgument(commandLine);
		if(argMainJar != null) {
			try {
				JarFile mainJar = new JarFile(argMainJar);
				version = mainJar.getManifest().getMainAttributes().getValue("Implementation-Version");
			} catch (IOException e) {
				log.error("An unexpected exception occured while trying to open the following jar file " + argMainJar, e);
			}
		}
		return version;
	}

	/**
	 * @param commandLine
	 * @return
	 */
	private String findMainJarArgument(String[] commandLine) {
		String argMainJar = null;
		int i = 0;
		while(i < commandLine.length) {
			if(commandLine[i].indexOf(".jar") != -1) {
				argMainJar = commandLine[i];
			}
			i++;
		}
		return argMainJar;
	}

	private Properties parseMobicentsSipBalancerConfig(
			String[] commandLine, String installationPath) {
		String configurationFileLocation = null;
		int i = 0;
		while(i < commandLine.length) {
			if(commandLine[i].indexOf(MOBICENTS_BALANCER_CONFIG_ARG) != -1) {
				configurationFileLocation = commandLine[i].substring(MOBICENTS_BALANCER_CONFIG_ARG.length());
			}
			i++;
		}
		if(!configurationFileLocation.startsWith("file://")) {
			configurationFileLocation = installationPath + File.separatorChar + configurationFileLocation;
		}
		File file = new File(configurationFileLocation);
        FileInputStream fileInputStream = null;
        try {
        	fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("the configuration file location " + configurationFileLocation + " does not exists !");
		}
        
        Properties properties = new Properties(System.getProperties());
        try {
			properties.load(fileInputStream);
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to load the properties configuration file located at " + configurationFileLocation);
		}
		return properties;
	}

    
    /**
     * Populates the plugin configuration for the Mobicents Sip Load Balancer instance being discovered.
     *
     * @param  installationPath identifies the Mobicents Sip Load Balancer being discovered
     *
     * @return populated plugin configuration instance
     */
    private Configuration populatePluginConfiguration(Properties properties) {
        Configuration configuration = new Configuration();

        configuration.put(new PropertySimple(HOST_PROP, properties.getProperty(HOST_PROP)));
        configuration.put(new PropertySimple(INTERNAL_PORT_PROP, properties.getProperty(INTERNAL_PORT_PROP)));
        configuration.put(new PropertySimple(EXTERNAL_PORT_PROP, properties.getProperty(EXTERNAL_PORT_PROP)));
        configuration.put(new PropertySimple(RMI_REGISTRY_PORT_PROP, properties.getProperty(RMI_REGISTRY_PORT_PROP)));
        configuration.put(new PropertySimple(JMX_HTML_ADAPTER_PORT_PROP, properties.getProperty(JMX_HTML_ADAPTER_PORT_PROP)));

        return configuration;
    }
}
