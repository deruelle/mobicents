package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class FromRingingEvent implements Serializable {
    public static int EVENT_CODE = 17;
    
	public FromRingingEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof FromRingingEvent) && ((FromRingingEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "FromRingingEvent[" + hashCode() + "]";
	}

	private final long id;
}
