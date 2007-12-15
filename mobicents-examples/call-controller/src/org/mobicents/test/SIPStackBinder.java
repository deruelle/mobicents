	package org.mobicents.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.ProviderDoesNotExistException;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TransportAlreadySupportedException;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;


import org.apache.log4j.Logger;


public class SIPStackBinder implements Serializable{

	
//	for some reasons
	protected static SIPStackBinder sipStackBinder=null;
	protected static Logger logger=Logger.getLogger(SIPStackBinder.class);
	




	// STACK STUFF
	private static final String SIP_BIND_ADDRESS = "javax.sip.IP_ADDRESS";

	private int port = 5180;

	private String transport = "udp";

	private String stackName = "JNDISipStack";

	private String stackAddress = "0.0.0.0";

	private String stackPrefix = "gov.nist";

	private String peerAddres="127.0.0.1";
	private int peerPort=5060;
	private String peerTransport="UDP";
	private SipStack sipStack;

	private SipFactory sipFactory;

    private ListeningPoint listeningPoint=null;
    private SipProvider provider=null;
    private SipListener currentListener=null;
	private Properties stackProperties;

	private Properties peerProperties;
	//NAME UNDER WHICH THIS CLASS WILL BE BINDED INTO JNDI
	private static String jndiName="JNDISipStack";
	
	private SIPStackBinder()
	{
		
	}
	
	public static InitialContext getContext() throws NamingException {
		Hashtable props = new Hashtable();

		props.put(InitialContext.INITIAL_CONTEXT_FACTORY,
				"org.jnp.interfaces.NamingContextFactory");
		props.put(InitialContext.PROVIDER_URL, "jnp://127.0.0.1:1099");

		// This establishes the security for authorization/authentication
		// props.put(InitialContext.SECURITY_PRINCIPAL,"username");
		// props.put(InitialContext.SECURITY_CREDENTIALS,"password");

		InitialContext initialContext = new InitialContext(props);
		return initialContext;
	}
	
	private static SIPStackBinder getFromJNDI()
	{
		SIPStackBinder binder=null;
		try {
			InitialContext ic=getContext();
			binder=(SIPStackBinder)ic.lookup(jndiName);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.info("NAME NOT bOUND!!!");
		}
		
		return null;
	}
	
	private static SIPStackBinder initializeWithStack()
	{
		SIPStackBinder binder=new SIPStackBinder();
//		 LETS CREATE STACK AND ALL OTHER STUFF.
		// LETS LOAD PROPS FOR STACK
		Properties props = new Properties();
		try {
			props.load(binder.getClass().getResourceAsStream("sipStack.properties"));

		} catch (IOException IOE) {
			logger.info("FAILED TO LOAD: sipStack.properties");
			
		}
		binder.stackProperties = props;
		// LETS LEAVE SOME CLUES
		binder.port = Integer.parseInt(binder.stackProperties.getProperty(
				"javax.sip.PORT", "5180"));
		binder.transport = binder.stackProperties.getProperty(
				"javax.sip.TRANSPORT", "udp");
		binder.stackName = binder.stackProperties.getProperty(
				"javax.sip.STACK_NAME", "JNDISipStack");
		binder.stackAddress = binder.stackProperties.getProperty(binder.SIP_BIND_ADDRESS,
				"127.0.0.1");
		// this.stackPrefix =
		// this.properties.getProperty("javax.sip.STACK_PREFIX", "gov.nist");

		// LETS INITIALIZE
		
		binder.sipFactory = SipFactory.getInstance();
		binder.sipFactory.setPathName("gov.nist"); // hmmm
		try {
			binder.sipStack = binder.sipFactory.createSipStack(props);
			binder.listeningPoint=binder.sipStack.createListeningPoint(binder.stackAddress,binder.port,binder.transport);
			binder.provider=binder.sipStack.createSipProvider(binder.listeningPoint);
		
			binder.sipStack.start();
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProviderDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		binder.peerProperties=new Properties();
		
		try {
			binder.peerProperties.load(binder.getClass().getResourceAsStream("sipPeer.properties"));
		} catch (IOException e) {
			logger.error("=!= CANT READ sipPeer.properties =!=");
			e.printStackTrace();
			
		}
		
		binder.peerPort = Integer.parseInt(binder.peerProperties.getProperty("peer.Port","5060"));
		binder.peerAddres=binder.peerProperties.getProperty("peer.IP","127.0.0.1");
		//DO WE NEED THIS ?
		binder.peerTransport=binder.peerProperties.getProperty("peer.Transport","UDP");
		return binder;
	}
	
	
	
	
	/*
	private void initializeFactories() {
		try {
			addressFactory = sipFactory.createAddressFactory();
			headerFactory = sipFactory.createHeaderFactory();
			messageFactory = sipFactory.createMessageFactory();
		} catch (PeerUnavailableException e) {
			logger.error("=!= CANT CREATE FACTORIES, TERMINATING!! =!=");
			e.printStackTrace();
		
			//System.exit(-1);
		}

	}*/
	
	
	
	
	public static SIPStackBinder getInstance()
	{
		
		
		if(sipStackBinder==null)
			sipStackBinder=initializeWithStack();
		else
		{
			sipStackBinder.stop();
			sipStackBinder=initializeWithStack();
		}
		return sipStackBinder;
	}

	

	

	public AddressFactory getAddressFactory() {
		try {
			return sipFactory.createAddressFactory();
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public HeaderFactory getHeaderFactory() {
		try {
			return sipFactory.createHeaderFactory();
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	

	public MessageFactory getMessageFactory() {
		try {
			return sipFactory.createMessageFactory();
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void registerSipListener(SipListener listener)
	{
		if(currentListener==null)
		{
			currentListener=listener;
			try {
				provider.addSipListener(currentListener);
			} catch (TooManyListenersException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}else
		{
			provider.removeSipListener(currentListener);
			try {
				provider.addSipListener(listener);
				currentListener=listener;
			} catch (TooManyListenersException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public static void stop()
	{
		sipStackBinder.sipStack.stop();
	}
	public void finalize()
	{
		this.stop();
	}

	public static String getJndiName() {
		return jndiName;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static String getSIP_BIND_ADDRESS() {
		return SIP_BIND_ADDRESS;
	}

	public static SIPStackBinder getSipStackBinder() {
		return sipStackBinder;
	}

	public SipListener getCurrentListener() {
		return currentListener;
	}

	public ListeningPoint getListeningPoint() {
		return listeningPoint;
	}

	public Properties getPeerProperties() {
		return peerProperties;
	}

	public int getPort() {
		return port;
	}

	public SipProvider getProvider() {
		return provider;
	}

	public SipFactory getSipFactory() {
		return sipFactory;
	}

	public SipStack getSipStack() {
		return sipStack;
	}

	public String getStackAddress() {
		return stackAddress;
	}

	public String getStackName() {
		return stackName;
	}

	public String getStackPrefix() {
		return stackPrefix;
	}

	public Properties getStackProperties() {
		return stackProperties;
	}

	public String getTransport() {
		return transport;
	}

	public String getPeerAddres() {
		return peerAddres;
	}

	public int getPeerPort() {
		return peerPort;
	}

	public String getPeerTransport() {
		return peerTransport;
	}
	
}
