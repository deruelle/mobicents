package com.ptin.xmas.control.events;

import java.util.Random;
import java.io.Serializable;

public final class ToNoAnswerEvent implements Serializable {
    public static int EVENT_CODE = 12;
    
	public ToNoAnswerEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ToNoAnswerEvent) && ((ToNoAnswerEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "ToNoAnswerEvent[" + hashCode() + "]";
	}

	private final long id;
}
