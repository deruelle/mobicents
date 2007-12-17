package com.ptin.xmas.app;

import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.*;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerID;
import javax.slee.profile.ProfileID;
import javax.slee.profile.UnrecognizedProfileNameException;
import javax.slee.profile.UnrecognizedProfileTableNameException;

import com.ptin.xmas.app.DurationProfileCMP;

import com.ptin.xmas.control.common.InCall;

public abstract class BaseCallDurationSbb implements javax.slee.Sbb {
    private TimerFacility timerFacility;
    private DurationProfileCMP duration_profile;
    private double duration;
   
	public void onByeEvent(com.ptin.xmas.control.events.ByeEvent event, ActivityContextInterface aci) {
	    TimerID timerId = getTimerId();
	    if(timerId != null)
	        this.timerFacility.cancelTimer(timerId);
	}

	public void onCallEstablishedEvent(com.ptin.xmas.control.events.CallEstablishedEvent event, ActivityContextInterface aci) {
        TimerOptions options = new TimerOptions();
        options.setPersistent(true);
		TimerID timerId = this.timerFacility.setTimer(aci, null, System.currentTimeMillis(), 1000, 0, options);
			
		String callId = event.getCallId();

		setInCallsList(event.getInCallsList());
		setCallId(callId);
		setTimerId(timerId);
	}

    public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
        String callId = getCallId(); // get the first leg call id
        
        duration--;
        
        System.out.println("Current credit is: " + duration);
        if(duration <= 0) {
            Hashtable inCallsList = getInCallsList();
            InCall selectedCall = (InCall) inCallsList.get(callId);
            System.out.println("Call duration expired - hanging up call.");
            selectedCall.hangup();

            // Stopping the timer: 
		    this.timerFacility.cancelTimer(event.getTimerID());            
        }
    }
	
	// TODO: Perform further operations if required in these methods.
	public void setSbbContext(SbbContext context) { 
	    this.sbbContext = context;
	    
	    try {
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            
            timerFacility = (TimerFacility) myEnv.lookup("slee/facilities/timer");
            
        } catch (NamingException ne) {
            System.out.println("Could not set SBB context in BaseCCSbb:" + ne.getMessage());
        }
        
        try {
            ProfileID profileID = new ProfileID("DurationProfiles", "TenSec");
            duration_profile = getDurationProfileCMP(profileID);
            duration = duration_profile.getDuration();            
        }
        catch(UnrecognizedProfileNameException e) {
            e.printStackTrace();
        }
        catch(UnrecognizedProfileTableNameException e) {
            e.printStackTrace();
        }        
      
	}
    public void unsetSbbContext() { this.sbbContext = null; }
    
    // TODO: Implement the lifecycle methods if required
    public void sbbCreate() throws javax.slee.CreateException {}
    public void sbbPostCreate() throws javax.slee.CreateException {}
    public void sbbActivate() {}
    public void sbbPassivate() {}
    public void sbbRemove() {}
    public void sbbLoad() {}
    public void sbbStore() {}
    public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {}
    public void sbbRolledBack(RolledBackContext context) {}
	
    public abstract void setInCallsList(Hashtable inCallsList);
    
    public abstract Hashtable getInCallsList();
            
    public abstract void setTimerId(TimerID timerId);
    
    public abstract TimerID getTimerId();
        
    public abstract void setCallId(String callId);
    
    public abstract String getCallId();
    
    public abstract DurationProfileCMP getDurationProfileCMP(javax.slee.profile.ProfileID profileID) 
    	throws javax.slee.profile.UnrecognizedProfileNameException, 
    	javax.slee.profile.UnrecognizedProfileTableNameException;
    
	/**
	 * Convenience method to retrieve the SbbContext object stored in setSbbContext.
	 * 
	 * TODO: If your SBB doesn't require the SbbContext object you may remove this 
	 * method, the sbbContext variable and the variable assignment in setSbbContext().
	 *
	 * @return this SBB's SbbContext object
	 */
	
	protected SbbContext getSbbContext() {
		return sbbContext;
	}

	private SbbContext sbbContext; // This SBB's SbbContext

}
