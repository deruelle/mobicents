package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.util.Set;
import java.util.UUID;

import org.mobicents.slee.sipevent.server.subscription.eventlist.FlatList;

/**
 * 
 * @author martins
 * 
 */
public class FlatListUpdatedEvent {

	private final String eventId = UUID.randomUUID().toString();

	private final FlatList flatList;
	private final Set<String> oldEntries;
	private final Set<String> removedEntries;
	private final Set<String> newEntries;
	
	public FlatListUpdatedEvent(FlatList flatList,Set<String> newEntries, Set<String> oldEntries, Set<String> removedEntries) {
		this.flatList = flatList;
		this.newEntries = newEntries;
		this.oldEntries = oldEntries;
		this.removedEntries = removedEntries;
	}

	public FlatList getFlatList() {
		return flatList;
	}
	
	public Set<String> getNewEntries() {
		return newEntries;
	}
	
	public Set<String> getOldEntries() {
		return oldEntries;
	}
	
	public Set<String> getRemovedEntries() {
		return removedEntries;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((FlatListUpdatedEvent) obj).eventId.equals(this.eventId);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return eventId.hashCode();
	}

}
