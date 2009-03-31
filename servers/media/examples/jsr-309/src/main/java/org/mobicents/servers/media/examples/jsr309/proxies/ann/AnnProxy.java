package org.mobicents.servers.media.examples.jsr309.proxies.ann;

import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.message.Request;

import org.apache.log4j.Logger;
import org.mobicents.servers.media.examples.jsr309.CallProxy;
import org.mobicents.servers.media.examples.jsr309.Jsr309Example;

public class AnnProxy extends CallProxy {
	private static Logger logger = Logger.getLogger(AnnProxy.class);

	private AnnProxyState state = AnnProxyState.INITIAL;

	public AnnProxy(Jsr309Example example) {
		super(example);
	}

	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processRequest(RequestEvent requestEvent) {
		String method = requestEvent.getRequest().getMethod();

		Request request = requestEvent.getRequest();
		if (requestEvent.getServerTransaction() != null
				&& !((method.compareTo(Request.ACK) == 0) || (method.compareTo(Request.BYE) == 0))) {
			// its rtr?
			logger.error("Received retransmission message, message: " + requestEvent.getRequest());
			return;
		}
		switch (this.state) {
		case INITIAL:
			if (method.compareTo(Request.INVITE) == 0) {
				try {
					ServerTransaction st = super.sipProvider.getNewServerTransaction(request);
					super.actingDialog = super.sipProvider.getNewDialog(st);
					super.actingTransaction = st;
					super.actingDialog.terminateOnBye(true);
					super.mediaSession = this.msControlFactory.createMediaSession();

					NetworkConnection myNetworkConnection = this.mediaSession.createNetworkConnection();
				} catch (Exception e) {
					logger.error(e);
				}
			} else {
				logger.error("Received wrong message, message: " + requestEvent.getRequest());
			}
			break;
		}
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
