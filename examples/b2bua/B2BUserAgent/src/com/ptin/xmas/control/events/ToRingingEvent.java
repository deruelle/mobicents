package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class ToRingingEvent implements Serializable {
    public static int EVENT_CODE = 18;
    
	public ToRingingEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ToRingingEvent) && ((ToRingingEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "ToRingingEvent[" + hashCode() + "]";
	}

	private final long id;
}
