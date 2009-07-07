package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.util.concurrent.ConcurrentHashMap;

public class RlsServicesCache {
	
	private ConcurrentHashMap<String, FlatList> cache = new ConcurrentHashMap<String, FlatList>();
	
	public void putFlatList(FlatList flatList) {
		cache.put(flatList.getServiceType().getUri(), flatList);
	}
	
	public FlatList getFlatList(String uri) {
		return cache.get(uri);
	}
	
	public FlatList removeFlatList(String uri) {
		return cache.remove(uri);
	}

	public void clear() {
		cache.clear();
	}

}
