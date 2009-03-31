package org.mobicents.servers.media.examples.jsr309.proxies.echo;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;

import org.mobicents.servers.media.examples.jsr309.CallProxy;
import org.mobicents.servers.media.examples.jsr309.Jsr309Example;

/**
 * 
 * @author amit bhayani
 * 
 */
public class EchoProxy extends CallProxy {

	public EchoProxy(Jsr309Example example) {
		super(example);
	}

	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processRequest(RequestEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processResponse(ResponseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

}
