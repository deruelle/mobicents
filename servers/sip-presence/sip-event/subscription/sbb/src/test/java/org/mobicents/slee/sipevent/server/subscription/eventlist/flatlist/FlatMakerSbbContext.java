package org.mobicents.slee.sipevent.server.subscription.eventlist.flatlist;

import javax.slee.ActivityContextInterface;
import javax.slee.NotAttachedException;
import javax.slee.SLEEException;
import javax.slee.SbbContext;
import javax.slee.SbbID;
import javax.slee.SbbLocalObject;
import javax.slee.ServiceID;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.UnrecognizedEventException;
import javax.slee.facilities.Tracer;

import org.mobicents.slee.sipevent.server.subscription.FlatListMakerSbbLocalObject;


public class FlatMakerSbbContext implements SbbContext {

	private FlatListMakerSbbLocalObject sbb;
	
	public FlatMakerSbbContext(FlatListMakerSbbLocalObject sbb) {
		this.sbb = sbb;
	}
	
	public ActivityContextInterface[] getActivities()
			throws TransactionRequiredLocalException, IllegalStateException,
			SLEEException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getEventMask(ActivityContextInterface arg0)
			throws NullPointerException, TransactionRequiredLocalException,
			IllegalStateException, NotAttachedException, SLEEException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getRollbackOnly() throws TransactionRequiredLocalException,
			SLEEException {
		// TODO Auto-generated method stub
		return false;
	}

	public SbbID getSbb() throws SLEEException {
		// TODO Auto-generated method stub
		return null;
	}

	public SbbLocalObject getSbbLocalObject()
			throws TransactionRequiredLocalException, IllegalStateException,
			SLEEException {
		return sbb;
	}

	public ServiceID getService() throws SLEEException {
		// TODO Auto-generated method stub
		return null;
	}

	public Tracer getTracer(String arg0) throws NullPointerException,
			IllegalArgumentException, SLEEException {
		// TODO Auto-generated method stub
		return null;
	}

	public void maskEvent(String[] arg0, ActivityContextInterface arg1)
			throws NullPointerException, TransactionRequiredLocalException,
			IllegalStateException, UnrecognizedEventException,
			NotAttachedException, SLEEException {
		// TODO Auto-generated method stub
		
	}

	public void setRollbackOnly() throws TransactionRequiredLocalException,
			SLEEException {
		// TODO Auto-generated method stub
		
	}

}
