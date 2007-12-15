package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class RejectEvent implements Serializable {
    public static int EVENT_CODE = 10;
    
	public RejectEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof RejectEvent) && ((RejectEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "RejectEvent[" + hashCode() + "]";
	}

	private final long id;
}
