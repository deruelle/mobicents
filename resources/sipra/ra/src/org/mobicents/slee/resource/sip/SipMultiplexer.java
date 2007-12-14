package org.mobicents.slee.resource.sip;

import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Properties;

import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransportAlreadySupportedException;
import javax.sip.TransportNotSupportedException;

import org.jboss.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;

/**
 * This is the multiplexing resource adaptor. The SIP resource adaptor is built
 * on top of the multiplexing resource adaptor. The job of the multiplexing
 * resource adaptor is to multiplex incoming SipEvents to slave resource
 * adaptors.
 * 
 * @author M. Ranganathan
 * 
 */
public class SipMultiplexer implements SipListener {

	private static SipMultiplexer mux=null;
	private static Logger log = Logger.getLogger(SipMultiplexer.class);

	private SipStack sipStack;

	private SipFactory sipFactory;

	private ConcurrentHashMap multiplexer;

	
	public static SipMultiplexer getInstance()
	{
		
		
//		String prefix = "slee/resource";
		//String name = "SipMultiplexer";
		//Context initialContext = new InitialContext();
		//Context compEnv = (Context) initialContext.lookup("java:");
		//Object o = null;
		//try {
		//	o = compEnv.lookup(prefix + "/" + name);

		//} catch (NameNotFoundException NNFE) {
		//	log.debug("===== NO MUX, NEED TO CREATE ONE[ " + NNFE.getMessage()
		//			+ " ] =====");
		//}
		//if (o == null) {
		//	mux = new SipMultiplexer();
		//	SleeContainer.registerWithJndi(prefix, name, mux);
		//} else
		//{
		//	log.info("\n------------------------- MUX:"+o+" \n----------------------------------");
		//	mux = (SipMultiplexer) o;
		//}
		if(mux==null)
			mux=new SipMultiplexer();
		
		return mux;
	}
	/**
	 * Constructor
	 * 
	 */
	private SipMultiplexer() {
		this.multiplexer = new ConcurrentHashMap();
		try {
			log.debug("SipResourceAdaptor: init()");
			// Silently set default values
			Properties props = new Properties();
			props.setProperty("javax.sip.STACK_NAME", "sipmultiplexer");
			props.setProperty("javax.sip.AUTOMATIC_DIALOG_SUPPORT", "off");
			props.setProperty("gov.nist.javax.sip.THREAD_POOL_SIZE", "4");
			props.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "DEBUG");
			//props.setProperty("gov.nist.javax.sip.DEBUG_LOG ", "SIP_DEBUG.log");
			
			// properties = new Properties();
			this.sipFactory = SipFactory.getInstance();
			this.sipFactory.setPathName("gov.nist"); // hmmm
			this.sipStack = this.sipFactory.createSipStack(props);
			this.sipStack.start();
			// Read the listening points to be supported by the stack.

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Could not create SipStack");
		}
	}

	/**
	 * Register a listener with the SIP Stack.
	 * 
	 * @param listener
	 * @param ipAddress
	 * @param port
	 * @return -- the created sipProvider
	 * @throws TransportNotSupportedException --
	 *             if the sip stack does not support the transport
	 * @throws InvalidArgumentException --
	 *             bad ipaddress
	 * @throws ObjectInUseException --
	 *             listeningPoints are already added to the provider and the
	 *             stack is started.
	 * @throws TransportAlreadySupportedException --
	 *             duplicate transport in collection.
	 */
	public SipProvider registerListener(SipListener listener, String ipAddress,
			int port, Collection transports, boolean automaticDialogSupport)
			throws TransportNotSupportedException, InvalidArgumentException,
			ObjectInUseException, TransportAlreadySupportedException {
		boolean created = false;
		SipProvider sipProvider = null;
		for (Iterator it = transports.iterator(); it.hasNext();) {
			String trans = (String) it.next();
			ListeningPoint lp = this.sipStack.createListeningPoint(ipAddress,
					port, trans);

			if (!created) {
				sipProvider = this.sipStack.createSipProvider(lp);
				sipProvider
						.setAutomaticDialogSupportEnabled(automaticDialogSupport);
			} else
				sipProvider.addListeningPoint(lp);

			try {
				sipProvider.addSipListener(this);
			} catch (Exception ex) {
				log.fatal("Unexpected exception ", ex);

			}
		}

		this.multiplexer.put(sipProvider, listener);
		return sipProvider;

	}

	private SipListener getSipListener(EventObject requestEvent) {
		SipProvider sipProvider = (SipProvider) requestEvent.getSource();
		log.debug("======== GETTING SIPLISTENER FOR PROVIDER: "+sipProvider+" =========");
		return (SipListener) this.multiplexer.get(sipProvider);
	}

	public void processRequest(RequestEvent requestEvent) {
		log.debug("========= METHOD CALLED: processRequest ==========");
		SipListener sipListener = this.getSipListener(requestEvent);
		if (sipListener == null) {
			log.error("Could not find Listener -- dropping request!");
		}
		sipListener.processRequest(requestEvent);

	}

	public void processResponse(ResponseEvent responseEvent) {
		log.debug("========= METHOD CALLED: processResponse ==========");
		SipListener sipListener = this.getSipListener(responseEvent);
		if (sipListener == null) {
			log.error("Could not find Listener -- dropping responset!");
		}
		sipListener.processResponse(responseEvent);

	}

	public void processTimeout(TimeoutEvent timeoutEvent) {
		SipListener sipListener = this.getSipListener(timeoutEvent);
		if (sipListener == null) {
			log.error("Could not find Listener -- dropping responset!");
		}
		sipListener.processTimeout(timeoutEvent);

	}

	public void processIOException(IOExceptionEvent ioexception) {
		String host = ioexception.getHost();
		int port = ioexception.getPort();
		String transport = ioexception.getTransport();
		log
				.error("IOException detected when trying to communicate with host = "
						+ host
						+ " port = "
						+ port
						+ " transport = "
						+ transport);
		if (ioexception.getSource() instanceof Dialog) {
			Dialog d = (Dialog) ioexception.getSource();
			d.delete();
		}
	}

	public void processTransactionTerminated(TransactionTerminatedEvent event) {
		SipListener listener = this.getSipListener(event);
		if (listener != null)
			listener.processTransactionTerminated(event);
		else {
			log.error("droppping event -- could not find registered listener");
		}

	}

	public void processDialogTerminated(DialogTerminatedEvent event) {
		SipListener listener = this.getSipListener(event);
		if (listener != null)
			listener.processDialogTerminated(event);
		else {
			log.error("droppping event -- could not find registered listener");
		}

	}

	public void deregisterListener(SipProvider provider) {
		this.multiplexer.remove(provider);
		provider.removeSipListener(this);
		ListeningPoint[] listeningPoints = provider.getListeningPoints();
		for (int i = 0; i < listeningPoints.length; i++) {
			ListeningPoint lp = listeningPoints[i];
			for (int k = 0; k < 10; k++) {
				try {
					sipStack.deleteListeningPoint(lp);
					sipStack.deleteSipProvider(provider);
					break;
				} catch (ObjectInUseException ex) {
					log.error("Object in use -- retrying to delete listening point");
					try {
						Thread.sleep(100);
					} catch (Exception e) {

					}
				}
			}
		}

	}

}
