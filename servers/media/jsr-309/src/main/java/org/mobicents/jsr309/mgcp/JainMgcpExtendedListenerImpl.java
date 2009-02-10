package org.mobicents.jsr309.mgcp;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;

import java.util.concurrent.CopyOnWriteArrayList;

import org.mobicents.mgcp.stack.JainMgcpExtendedListener;

public class JainMgcpExtendedListenerImpl implements JainMgcpExtendedListener {

	protected CopyOnWriteArrayList<JainMgcpExtendedListener> jainMgcpListenerImplList = new CopyOnWriteArrayList<JainMgcpExtendedListener>();

	public void processMgcpCommandEvent(
			JainMgcpCommandEvent jainmgcpcommandevent) {
		for(JainMgcpExtendedListener j : jainMgcpListenerImplList){
			j.processMgcpCommandEvent(jainmgcpcommandevent);
		}

	}

	public void processMgcpResponseEvent(
			JainMgcpResponseEvent jainmgcpresponseevent) {
		for(JainMgcpExtendedListener j : jainMgcpListenerImplList){
			j.processMgcpResponseEvent(jainmgcpresponseevent);
		}

	}

	public void transactionEnded(int tx) {
		for(JainMgcpExtendedListener j : jainMgcpListenerImplList){
			j.transactionEnded(tx);
		}
	}

	public void transactionRxTimedOut(JainMgcpCommandEvent jainmgcpcommandevent) {
		for(JainMgcpExtendedListener j : jainMgcpListenerImplList){
			j.transactionRxTimedOut(jainmgcpcommandevent);
		}
	}

	public void transactionTxTimedOut(JainMgcpCommandEvent jainmgcpcommandevent) {
		for(JainMgcpExtendedListener j : jainMgcpListenerImplList){
			j.transactionTxTimedOut(jainmgcpcommandevent);
		}
	}

	public void addJainMgcpListenerImpl(
			JainMgcpExtendedListener jainMgcpListenerImpl) {
		this.jainMgcpListenerImplList.add(jainMgcpListenerImpl);
	}

	public void removeJainMgcpListenerImpl(
			JainMgcpExtendedListenerImpl jainMgcpListenerImpl) {
		this.jainMgcpListenerImplList.remove(jainMgcpListenerImpl);
	}

}
