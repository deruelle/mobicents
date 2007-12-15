package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class StatusFinishedEvent implements Serializable {
    public static int EVENT_CODE = 31;
    
	public StatusFinishedEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof StatusFinishedEvent) && ((StatusFinishedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "StatusFinishedEvent[" + hashCode() + "]";
	}

	private final long id;
}
