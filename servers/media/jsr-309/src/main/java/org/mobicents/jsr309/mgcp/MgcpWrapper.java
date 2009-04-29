package org.mobicents.jsr309.mgcp;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class MgcpWrapper implements JainMgcpExtendedListener {

	private String peerIp = "127.0.0.1";
	private int peerPort = 2427;

	private JainMgcpStackProviderImpl jainMgcpStackProviderImpl;
	private static Logger logger = Logger.getLogger(MgcpWrapper.class);
	private Map<Integer, JainMgcpExtendedListener> mgcpListeners = new HashMap<Integer, JainMgcpExtendedListener>();
	
	// This is mapping of RequestIdentifier and Listener
	private Map<String, JainMgcpExtendedListener> mgcpListenersForNotify = new HashMap<String, JainMgcpExtendedListener>();

	public MgcpWrapper(JainMgcpStackProviderImpl jainMgcpStackProviderImpl, int peerPort, String peerIp) {
		this.jainMgcpStackProviderImpl = jainMgcpStackProviderImpl;
		this.peerPort = peerPort;
		this.peerIp = peerIp;
	}

	public void addListnere(int tx, JainMgcpExtendedListener listener) {
		this.mgcpListeners.put(tx, listener);
	}

	public void addListnere(RequestIdentifier requestId, JainMgcpExtendedListener listener) {
		this.mgcpListenersForNotify.put(requestId.toString(), listener);
	}

	public void removeListener(int tx) {
		this.mgcpListeners.remove(tx);
	}

	public void removeListener(RequestIdentifier requestId) {
		this.mgcpListenersForNotify.remove(requestId.toString());
	}

	public JainMgcpStackProviderImpl getJainMgcpStackProvider() {
		return this.jainMgcpStackProviderImpl;
	}

	public void transactionEnded(int tx) {
		JainMgcpExtendedListener listener = mgcpListeners.get(tx);
		if (listener != null) {
			listener.transactionEnded(tx);
		}
	}

	public void transactionRxTimedOut(JainMgcpCommandEvent command) {
		int tx = command.getTransactionHandle();
		JainMgcpExtendedListener listener = mgcpListeners.get(tx);
		if (listener != null) {
			listener.transactionRxTimedOut(command);
		}
	}

	public void transactionTxTimedOut(JainMgcpCommandEvent command) {
		int tx = command.getTransactionHandle();
		JainMgcpExtendedListener listener = mgcpListeners.get(tx);
		if (listener != null) {
			listener.transactionTxTimedOut(command);
		}
	}

	public void processMgcpCommandEvent(JainMgcpCommandEvent command) {
		switch (command.getObjectIdentifier()) {
		case Constants.CMD_NOTIFY:
			Notify ntfy = (Notify) command;
			RequestIdentifier requestId = ntfy.getRequestIdentifier();
			JainMgcpExtendedListener listener = mgcpListenersForNotify.get(requestId.toString());
			if (listener != null) {
				listener.processMgcpCommandEvent(command);
			} else {
				logger.warn("Received NTFY command " + command.toString() + " but no handler for this");
			}
			break;
		default:
			logger.error("This command in un-expected" + command.toString());
			break;
		}
	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent response) {
		int tx = response.getTransactionHandle();
		JainMgcpExtendedListener listener = mgcpListeners.get(tx);
		if (listener != null) {
			listener.processMgcpResponseEvent(response);
		}  else {
			logger.warn("Received Response " + response.toString() + " but no handler for this");
		}
	}

	public void sendMgcpEvents(JainMgcpEvent[] events) throws IllegalArgumentException {
		this.jainMgcpStackProviderImpl.sendMgcpEvents(events);
	}

	public String getPeerIp() {
		return peerIp;
	}

	public int getPeerPort() {
		return peerPort;
	}

	public CallIdentifier getUniqueCallIdentifier() {
		return this.jainMgcpStackProviderImpl.getUniqueCallIdentifier();
	}

	public RequestIdentifier getUniqueRequestIdentifier() {
		return this.jainMgcpStackProviderImpl.getUniqueRequestIdentifier();
	}

	public int getUniqueTransactionHandler() {
		return this.jainMgcpStackProviderImpl.getUniqueTransactionHandler();

	}

}
