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
	 * @return an event with detailed data about the flat list update, null if it is a new put or no update 
	 */
	public FlatListUpdatedEvent putFlatList(FlatList flatList, XDMClientControlSbbLocalObject xdmClientSbb) {
		FlatListUpdatedEvent result = null;
		FlatList oldFlatList = cache.put(flatList.getServiceType().getUri(), flatList);
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
			Set<String> oldEntries = new HashSet<String>();
			Set<String> newEntries = new HashSet<String>();
			Set<String> removedEntries = new HashSet<String>();
			boolean updated = false;
			for (String entry : flatList.getEntries().keySet()) {
				if (oldFlatList.getEntries().remove(entry) == null) {
					newEntries.add(entry);
					updated = true;
				}
				else {
					oldEntries.add(entry);
				}
			}
			removedEntries.addAll(oldFlatList.getEntries().keySet());
			if (!updated && !removedEntries.isEmpty()) {
				updated = true;
			}
			if (updated) {
				result = new FlatListUpdatedEvent(flatList,newEntries,oldEntries,removedEntries);
			}
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
		return result;
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
