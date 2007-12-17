package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class ToBusyEvent implements Serializable {
    public static int EVENT_CODE = 11;
    
	public ToBusyEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ToBusyEvent) && ((ToBusyEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "ToBusyEvent[" + hashCode() + "]";
	}

	private final long id;
}
