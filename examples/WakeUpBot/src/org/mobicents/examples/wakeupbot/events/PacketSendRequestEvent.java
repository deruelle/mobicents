package org.mobicents.examples.wakeupbot.events;
import java.util.Date;
import java.util.Random;
import java.io.Serializable;
public class PacketSendRequestEvent implements Serializable {

	private final long id;
	private String UID;
	private String messageBody;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PacketSendRequestEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof PacketSendRequestEvent) && ((PacketSendRequestEvent)o).id == id;
	}
	
	public int hashCode() {
		return (int) id;
	}
	
	public String toString() {
		return "PacketSendRequestEvent[" + hashCode() + "]";
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uid) {
		UID = uid;
	}

	

}
