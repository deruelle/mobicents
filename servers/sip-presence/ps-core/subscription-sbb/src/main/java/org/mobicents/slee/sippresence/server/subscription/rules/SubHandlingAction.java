package org.mobicents.slee.sippresence.server.subscription.rules;

import javax.sip.message.Response;

/**
 * sub-handling values on pres-rules actions.
 * @author emmartins
 *
 */
public enum SubHandlingAction {
	
	block(0),
	confirm(10),
	politeblock(20),
	allow(30);
	
	private final int value;
	
	private SubHandlingAction(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public int getResponseCode() {
		switch (this) {
		case allow:
		case politeblock:
			return Response.OK;
		case block:
			return Response.FORBIDDEN;
		case confirm:
		default:
			return Response.ACCEPTED;
		}
	}
}
