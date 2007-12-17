package org.mobicents.slee.graph.viewer;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.slee.ComponentID;
import javax.slee.SbbID;
import javax.slee.ServiceID;
import javax.slee.UnrecognizedComponentException;
import javax.slee.management.ManagementException;
import javax.slee.resource.ResourceAdaptorID;
import javax.slee.resource.ResourceAdaptorTypeID;

public class MBeanUtils {

	// the remote SLEE MBean Server
	private static MBeanServerConnection mbeanServer = null;
	
	// object name of the SLEE deployment mbean
	private static ObjectName deploymentMBean = null;

	static {
		try {
	        InitialContext ctx = new InitialContext();
	        String sleeManagementMBeanName = (String)ctx.getEnvironment().get("slee.management.mbean");
	        mbeanServer = (MBeanServerConnection) ctx.lookup("jmx/rmi/RMIAdaptor");
	        if( mbeanServer == null ) System.out.println( "RMIAdaptor is null");
	        ObjectName sleeManagementMBean = new ObjectName( sleeManagementMBeanName );
	        deploymentMBean  = (ObjectName) mbeanServer.getAttribute(sleeManagementMBean, "DeploymentMBean");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to initialize MBeanUtils", e);
		}
	}
	
    public static SbbID[] getSbbs() throws ManagementException {
        SbbID[] sbbs;
		try {
			sbbs = (SbbID[]) mbeanServer.getAttribute(deploymentMBean, "Sbbs");
	    	return sbbs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ManagementException("Failed to obtaine SBBs from remote DeploymentMBean",e);
		}
    }

    public static ServiceID[] getServices() throws ManagementException {
        ServiceID[] services;
		try {
			services = (ServiceID[]) mbeanServer.getAttribute(deploymentMBean, "Services");
	    	return services;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ManagementException("Failed to obtaine Services from remote DeploymentMBean",e);
		}
    }

	public static ResourceAdaptorTypeID[] getResourceAdaptorTypes() throws ManagementException {
		ResourceAdaptorTypeID[] rts;
		try {
			rts = (ResourceAdaptorTypeID[]) mbeanServer.getAttribute(deploymentMBean, "ResourceAdaptorTypes");
	    	return rts;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ManagementException("Failed to obtaine Resource Adaptor Types from remote DeploymentMBean",e);
		}
	}
	
	/*
	public DeployableUnitDescriptor getDescriptor(DeployableUnitID id)
	    throws NullPointerException, UnrecognizedDeployableUnitException, ManagementException;
    */
 
	public static ResourceAdaptorID[] getResourceAdaptors() throws ManagementException {
		ResourceAdaptorID[] ras;
		try {
			ras = (ResourceAdaptorID[]) mbeanServer.getAttribute(deploymentMBean, "ResourceAdaptors");
	    	return ras;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ManagementException("Failed to obtaine Resource Adaptors from remote DeploymentMBean",e);
		}
	}
	
    public static ComponentID[] getReferringComponents(ComponentID id)
    	throws NullPointerException, UnrecognizedComponentException, ManagementException {
        ComponentID[] references;
		try {
			references = (ComponentID[]) mbeanServer.invoke(deploymentMBean, "getReferringComponents", 
					new Object[] {id}, new String[] {ComponentID.class.getName()});
	    	return references;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ManagementException("Failed to obtaine referencing components for " + id + " from remote DeploymentMBean",e);
		}
    }

}
