package org.mobicents.slee.services.sip.proxy.mbean;

import org.mobicents.slee.services.sip.common.ProxyConfiguration;



public interface ProxyConfiguratorMBean extends ProxyConfiguration {
	public void addSupportedURIScheme(String schemeToAdd);
	public void removeSupportedURIScheme(String schemeToRemove);
	
	public void addLocalDomain(String localDomainToAdd);
	public void removeLocalDomain(String localDomainToRemove);
	
	public void setSipHostName(String sipHostName);
	//public String getSipHostName();
	
	public void setSipPort(int port);
	//public String getSipPort();
	
	public void setSipTransports(String[] transport);
	//public String getSipTransport();

    
    
     
    public void addMustPassThrough(int pos,String host);
    public void removeMustPassThrough(int pos);
    public void removeMustPassThrough(String host);
    
    public Object clone();
}
