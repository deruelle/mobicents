package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class MediaChangeReqEvent implements Serializable {
    public static int EVENT_CODE = 36;
    
	public MediaChangeReqEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof MediaChangeReqEvent) && ((MediaChangeReqEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "MediaChangeReqEvent[" + hashCode() + "]";
	}

	private final long id;
}
