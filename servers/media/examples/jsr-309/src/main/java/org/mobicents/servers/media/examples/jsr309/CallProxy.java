package org.mobicents.servers.media.examples.jsr309;

import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlFactory;
import javax.sip.Dialog;
import javax.sip.ServerTransaction;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;

public abstract class CallProxy implements SipListener {

	protected Jsr309Example jsr309Example = null;

	protected Dialog actingDialog = null;
	protected ServerTransaction actingTransaction = null;
	
	protected SipFactory sipFactory;
	protected SipProvider sipProvider;
	
	protected MsControlFactory msControlFactory = null;
	protected MediaSession mediaSession = null;

	public CallProxy(Jsr309Example jsr309Example) {
		this.jsr309Example = jsr309Example;
		
		this.sipFactory = jsr309Example.getSipFactory();
		this.sipProvider = jsr309Example.getSipProvider();
		
		this.msControlFactory = jsr309Example.getMsControlFactory();

	}
}
