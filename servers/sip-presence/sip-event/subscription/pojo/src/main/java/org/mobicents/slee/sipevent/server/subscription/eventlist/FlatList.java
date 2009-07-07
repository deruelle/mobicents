package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.ServiceType;
import org.openxdm.xcap.common.uri.DocumentSelector;

public class FlatList {

	private final ConcurrentHashMap<String, EntryType> entries = new ConcurrentHashMap<String, EntryType>();
	
	private final ServiceType serviceType;
	
	private final Set<DocumentSelector> resourceLists = new HashSet<DocumentSelector>(); 
	
	private int status = 200;  
	
	public FlatList(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	
	public Map<String, EntryType> getEntries() {
		return entries;
	}
	
	public Set<DocumentSelector> getResourceLists() {
		return resourceLists;
	}
	
	public ServiceType getServiceType() {
		return serviceType;
	}
	
	public void putEntry(EntryType entryType) {
		entries.putIfAbsent(entryType.getUri(), entryType);
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
	public int hashCode() { 
		return serviceType.getUri().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((FlatList)obj).serviceType.getUri().equals(this.serviceType.getUri());
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "FlatList(uri="+getServiceType().getUri()+") = "+getEntries().keySet();		
	}
}
