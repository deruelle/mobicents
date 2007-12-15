package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class WaitEvent implements Serializable {
    public static int EVENT_CODE = 9;
    
	public WaitEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof WaitEvent) && ((WaitEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "WaitEvent[" + hashCode() + "]";
	}

	private final long id;
}
