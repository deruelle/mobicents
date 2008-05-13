package net.java.slee.resource.diameter.base;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.AppSession;
import org.mobicents.slee.resource.diameter.base.events.AbortSessionRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.CapabilitiesExchangeAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DeviceWatchdogAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.DisconnectPeerAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.ReAuthAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.SessionTerminationAnswerImpl;


import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

public class DiameterActivityImpl implements DiameterActivity {

	
	protected String sessionId=null;
	protected DiameterActivityHandle handle=null;
	protected DiameterMessageFactoryImpl messageFactory=null;
	protected DiameterAvpFactoryImpl avpFactory=null;
	protected Session session=null;
	protected EventListener<Request,Answer> raEventListener=null;
	protected long timeout=5000;
	protected static Logger logger=Logger.getLogger(DiameterActivityImpl.class);
	protected DiameterIdentityAvp destinationHost,destinationRealm;
	
	
	public DiameterActivityImpl(DiameterMessageFactoryImpl messageFactory,
			DiameterAvpFactoryImpl avpFactory, Session session,
			EventListener<Request, Answer> raEventListener, long timeout,DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm) {
		super();
		this.messageFactory = messageFactory;
		this.avpFactory = avpFactory;
		this.session = session;
		this.raEventListener = raEventListener;
		this.sessionId=session.getSessionId();
		this.handle=new DiameterActivityHandle(this.sessionId);
		this.timeout=timeout;
		this.destinationHost=destinationHost;
		this.destinationRealm=destinationRealm;
	}

	public void endActivity() {

		//FIXME: baranowb; session.release(); ???
		throw new UnsupportedOperationException("Not implemented!!!!");

	}

	public DiameterAvpFactory getDiameterAvpFactory() {
		
		return this.avpFactory;
	}

	public DiameterMessageFactory getDiameterMessageFactory() {

		return this.messageFactory;
	}

	public String getSessionId() {
		
		return this.sessionId;
	}

	public void sendMessage(DiameterMessage message) throws IOException {
		//FIXME: baranowb - this is async send?
		try
	    {
	      if(message instanceof DiameterMessageImpl)
	      {
	        DiameterMessageImpl msg = (DiameterMessageImpl)message;
	        this.session.send(msg.getGenericData(), this.raEventListener);
	        //FIXME: baranowb; get dest host and realm :], possibly some other avps
	        
	      }else
	      {
	    	  throw new OperationNotSupportedException("Trying to send wrong type of message? ["+message.getClass()+"] \n"+message);
	      }
	    }
	    catch (Exception e)
	    {
	      logger.error( "Failure sending sync request.", e );
	    }
	}	
	
	
	//============= IMPL methods
	
	
	public DiameterActivityHandle getActivityHandle()
	{
		return this.handle;
	}
	
	public DiameterMessage sendSyncMessage(DiameterMessage message)
	{
		return null;
	}
	
}
