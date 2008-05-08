package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterHeader;

public class DiameterHeaderImpl implements DiameterHeader {

	private Message msg = null;

	public DiameterHeaderImpl(Message msg) {
		super();
		this.msg = msg;
	}

	public long getApplicationId() {

		return msg.getApplicationId();
	}

	public int getCommandCode() {

		return msg.getCommandCode();
	}

	public long getEndToEndId() {

		return msg.getEndToEndIdentifier();
	}

	public long getHopByHopId() {

		return msg.getHopByHopIdentifier();
	}

	public int getMessageLength() {
		// TODO
		return 0;
	}

	public short getVersion() {

		return msg.getVersion();
	}

	public boolean isError() {

		return msg.isError();
	}

	public boolean isPotentiallyRetransmitted() {
		// FIXME
		return msg.isReTransmitted();
	}

	public boolean isProxiable() {

		return msg.isProxiable();
	}

	public boolean isRequest() {

		return msg.isRequest();
	}

	public Object clone() {
		DiameterHeaderImpl clone = new DiameterHeaderImpl(msg);
		return clone;
	}
}
