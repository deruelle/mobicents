package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class StatusInvitingPeerEvent implements Serializable {
    public static int EVENT_CODE = 25;
    
	public StatusInvitingPeerEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof StatusInvitingPeerEvent) && ((StatusInvitingPeerEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "StatusInvitingPeerEvent[" + hashCode() + "]";
	}

	private final long id;
}
