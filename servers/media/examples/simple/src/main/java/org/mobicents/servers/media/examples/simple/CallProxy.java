package org.mobicents.servers.media.examples.simple;

import gov.nist.javax.sip.SipStackImpl;
import jain.protocol.ip.mgcp.JainMgcpListener;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.JainMgcpStack;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;

import java.net.InetAddress;
import java.util.logging.Logger;

import javax.sip.Dialog;
import javax.sip.ServerTransaction;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;

import org.mobicents.mgcp.stack.JainMgcpExtendedListener;

public abstract class CallProxy implements JainMgcpExtendedListener, SipListener {

	
	
	
	protected Dialog actingDialog = null;
	protected ServerTransaction actingTransaction = null;
	
	
	protected InetAddress mgcpServerAddress = null;
	protected int mgcpServerPort = 2729;
	protected SimplExample mainDish = null;
	protected JainMgcpStack mgpcStack = null;
	protected JainMgcpProvider mgcpProvider = null;
	protected SipStackImpl sipStack = null;
	protected SipFactory sipFactory;
	protected SipProvider sipProvider;
	
	protected String endpointName = "";
	protected CallIdentifier callIdentifier = null;
	protected EndpointIdentifier endpointIndentifier = null;
	
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	public CallProxy(SimplExample mainDish) {
		super();
		this.mgcpServerAddress = mainDish.getMgcpServerAddress();
		this.mgcpServerPort = mainDish.getMgcpServerPort();
		this.mainDish = mainDish;
		
		this.mgcpProvider = mainDish.getMgcpProvider();
		
		this.sipFactory = mainDish.getSipFactory();
		this.sipProvider = mainDish.getSipProvider();
	}

}
