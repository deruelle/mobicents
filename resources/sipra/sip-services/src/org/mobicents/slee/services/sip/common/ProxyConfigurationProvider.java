package org.mobicents.slee.services.sip.common;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.mobicents.slee.container.SleeContainer;

public class ProxyConfigurationProvider {

	/**
	 * Creates a copy of current proxy configuration MBean - its values are copied, so for we can have its image
	 * which can be used during whole call. Subsequent calls can return different copy.
	 * @return
	 */
	public static ProxyConfiguration getCopy(String name)
	{
		
		MBeanServer mbs=SleeContainer.lookupFromJndi().getMBeanServer();
		try {
			ObjectName on=new ObjectName(ProxyConfiguration.MBEAN_NAME_PREFIX+name);
			ObjectInstance oi=mbs.getObjectInstance(on);
			Object o=mbs.invoke(on, "clone", null, null);
			return (ProxyConfiguration) o;
		} catch (MalformedObjectNameException e) {
			
			e.printStackTrace();
		} catch (NullPointerException e) {
			
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			
			e.printStackTrace();
		} catch (MBeanException e) {
			
			e.printStackTrace();
		} catch (ReflectionException e) {
			
			e.printStackTrace();
		}
		
		
		
		return null;
		
	}
	
	
}
