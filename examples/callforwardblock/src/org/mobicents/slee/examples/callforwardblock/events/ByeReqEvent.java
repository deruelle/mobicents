package org.mobicents.slee.examples.callforwardblock.events;

import java.io.Serializable;
import java.util.Random;

import javax.sip.Dialog;

/**
 * Request event to handle BYE from a UA
 * @author hchin
 */
public class ByeReqEvent implements Serializable {
	private final long id;
	/** Dialog of UA making request */
	private Dialog reqDialog;

	public ByeReqEvent() {
		id = new Random().nextLong() ^ System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		return (o instanceof ByeReqEvent) && 
			((ByeReqEvent)o).id == id &&
			((ByeReqEvent)o).reqDialog.equals(reqDialog);
	}
	
	public int hashCode() {
		return (int) id + reqDialog.hashCode();
	}
	
	public String toString() {
		return "ByeReqEvent[" + hashCode() + "\n" +
			"request dialog " + reqDialog.toString() + "]";
	}

	public Dialog getReqDialog() {
		return reqDialog;
	}

	public void setReqDialog(Dialog reqDialog) {
		this.reqDialog = reqDialog;
	}

}
