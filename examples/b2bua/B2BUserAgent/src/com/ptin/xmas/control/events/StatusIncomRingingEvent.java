package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class StatusIncomRingingEvent implements Serializable {
    public static int EVENT_CODE = 35;
    
	public StatusIncomRingingEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof StatusIncomRingingEvent) && ((StatusIncomRingingEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "StatusIncomRingingEvent[" + hashCode() + "]";
	}

	private final long id;
}
