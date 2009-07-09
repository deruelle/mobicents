package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.util.UUID;

import org.mobicents.slee.sipevent.server.subscription.eventlist.FlatList;

/**
 * 
 * @author martins
 * 
 */
public class FlatListRemovedEvent {

	private final String eventId = UUID.randomUUID().toString();

	private final FlatList flatList;
	
	public FlatListRemovedEvent(FlatList flatList) {
		this.flatList = flatList;
	}

	public FlatList getFlatList() {
		return flatList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((FlatListRemovedEvent) obj).eventId.equals(this.eventId);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return eventId.hashCode();
	}

}
