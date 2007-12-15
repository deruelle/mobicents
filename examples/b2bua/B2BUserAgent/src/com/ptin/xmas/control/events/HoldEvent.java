package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class HoldEvent implements Serializable {
    public static int EVENT_CODE = 8;
    
	public HoldEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof HoldEvent) && ((HoldEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "HoldEvent[" + hashCode() + "]";
	}

	private final long id;
}
