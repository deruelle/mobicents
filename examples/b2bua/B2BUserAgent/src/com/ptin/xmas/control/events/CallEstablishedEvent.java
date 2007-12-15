package com.ptin.xmas.control.events;

import java.util.Random;
import java.util.Hashtable;
import java.io.Serializable;

public final class CallEstablishedEvent implements Serializable {
    private String callId;
    private Hashtable inCallsList;
    
    public static int EVENT_CODE = 2;
        
	public CallEstablishedEvent(String callId, Hashtable inCallsList) {
		id = new Random().nextLong() ^ System.currentTimeMillis();
		this.callId = callId;
		this.inCallsList = inCallsList;
	}

	public String getCallId() {
	    return callId;
	}
	
	public void setCallId(String callId) {
	    this.callId = callId;
	}
	
	public Hashtable getInCallsList() {
	    return inCallsList;
	}
	
	public void setInCallsList(Hashtable inCallsList) {
	    this.inCallsList = inCallsList;
	}
	
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof CallEstablishedEvent) && ((CallEstablishedEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "CallEstablishedEvent[" + hashCode() + "]";
	}

	private final long id;
}
