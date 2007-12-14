package org.mobicents.slee.resource.sip.wrappers;

import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.TransactionTerminatedEvent;

public class TransactionTerminatedEventWrapper extends
		TransactionTerminatedEvent {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7363453023732951366L;

	public TransactionTerminatedEventWrapper(Object source, ClientTransaction tx) {
		super(source, tx);
		// TODO Auto-generated constructor stub
	}

	public TransactionTerminatedEventWrapper(Object source, ServerTransaction tx) {
		super(source, tx);
		// TODO Auto-generated constructor stub
	}
	

}
