package org.mobicents.examples.wakeupbot;

import java.util.Date;

public class RequestHolder {
	private Date date;

	private String UID;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uid) {
		UID = uid;
	}

	public String toString() {
		return UID+"    "+date;
	}

}
