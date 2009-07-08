package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.common.uri.DocumentSelector;

public class RlsServicesCache {
	
	private ConcurrentHashMap<String, FlatList> cache = new ConcurrentHashMap<String, FlatList>();
	
	private Map<DocumentSelector, Set<FlatList>> resourceLists = new HashMap<DocumentSelector, Set<FlatList>>();
	
	/**
	 * 
	 * @param flatList
	 * @param xdmClientSbb
	 * @return false if there was already a list with same service uri and with same entries, true otherwise
	 */
	public boolean putFlatList(FlatList flatList, XDMClientControlSbbLocalObject xdmClientSbb) {
		FlatList oldFlatList = cache.put(flatList.getServiceType().getUri(), flatList);
		boolean updated = false;
		if (oldFlatList != null) {
			oldFlatList.getResourceLists().removeAll(flatList.getResourceLists());
			for (DocumentSelector documentSelector : oldFlatList.getResourceLists()) {
				synchronized (resourceLists) {
					Set<FlatList> flatListsLinked = resourceLists.get(documentSelector);
					if (flatListsLinked != null) {
						flatListsLinked.remove(oldFlatList);
						if (flatListsLinked.isEmpty()) {
							resourceLists.remove(documentSelector);
							xdmClientSbb.unsubscribeDocument(documentSelector);
						}
					}
				}
			}
			for (String entry : flatList.getEntries().keySet()) {
				if (oldFlatList.getEntries().remove(entry) == null) {
					// it is a new entry
					updated = true;
					break;
				}
			}
			if (!updated && !oldFlatList.getEntries().isEmpty()) {
				// new list lost some entries
				updated = true;
			}
		}
		else {
			updated = true;
		}
		for (DocumentSelector documentSelector : flatList.getResourceLists()) {
			synchronized (resourceLists) {
				Set<FlatList> flatListsLinked = resourceLists.get(documentSelector);
				if (flatListsLinked == null) {
					flatListsLinked = new HashSet<FlatList>();
					resourceLists.put(documentSelector, flatListsLinked);
					xdmClientSbb.subscribeDocument(documentSelector);
				}
				flatListsLinked.add(flatList);
			}
		}
		return updated;
	}
	
	public FlatList getFlatList(String uri) {
		return cache.get(uri);
	}
	
	public FlatList removeFlatList(String uri, XDMClientControlSbbLocalObject xdmClientSbb) {
		FlatList flatList = cache.remove(uri);
		if (flatList != null) {
			for (DocumentSelector documentSelector : flatList.getResourceLists()) {
				synchronized (resourceLists) {
					Set<FlatList> flatListsLinked = resourceLists.get(documentSelector);
					if (flatListsLinked != null) {
						flatListsLinked.remove(flatList);
						if (flatListsLinked.isEmpty()) {
							resourceLists.remove(documentSelector);
							xdmClientSbb.unsubscribeDocument(documentSelector);
						}
					}
				}
			}
		}
		return flatList;
	}

	public Set<String> getFlatListServiceURIs() {
		return cache.keySet();
	}

	public Set<FlatList> getFlatListsLinkedToResourceList(
			DocumentSelector documentSelector) {
		return resourceLists.get(documentSelector);
	}
}
