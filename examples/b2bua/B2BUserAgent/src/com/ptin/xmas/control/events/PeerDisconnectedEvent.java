package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class PeerDisconnectedEvent implements Serializable {
    public static int EVENT_CODE = 24;
    
	public PeerDisconnectedEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof PeerDisconnectedEvent) && ((PeerDisconnectedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "PeerDisconnectedEvent[" + hashCode() + "]";
	}

	private final long id;
}
