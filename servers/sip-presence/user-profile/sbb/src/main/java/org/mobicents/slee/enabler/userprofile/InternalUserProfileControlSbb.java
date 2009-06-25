package org.mobicents.slee.enabler.userprofile;

import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;
import javax.slee.serviceactivity.ServiceStartedEvent;

import org.apache.log4j.Logger;
import org.mobicents.slee.enabler.userprofile.jpa.jmx.UserProfileControlManagement;

public abstract class InternalUserProfileControlSbb implements Sbb,
		UserProfileControlSbbLocalObject {

	private static Logger logger = Logger
			.getLogger(InternalUserProfileControlSbb.class);
			
	private SbbContext sbbContext = null; // This SBB's context
	
	/**
	 * Called when an sbb object is created and enters the pooled state.
	 */
	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
	}

	// -- MANAGEMENT
	
	/**
	 * the Management MBean
	 */
	private static final UserProfileControlManagement management = new UserProfileControlManagement();
	
	public void onServiceStartedEvent(ServiceStartedEvent event, ActivityContextInterface aci) {
		// we want to stay attached to this service activity, to receive the activity end event on service deactivation
		try {                     
			//get this service activity
			ServiceActivity sa = ((ServiceActivityFactory) new InitialContext().lookup("java:comp/env/slee/serviceactivity/factory")).getActivity();                       
			if (!sa.equals(aci.getActivity())) {
				aci.detach(this.sbbContext.getSbbLocalObject());
			}
			else {
				// starts the mbean
				management.startService();
			}
		}
		catch (Exception e) {
			logger.error("failed to process service started event",e);
		}				
	}
	
	public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {
		try {
			// stop mbean
			management.stopService();
		} catch (Exception e) {
			logger.error("Faield to shutdown management interface",e);
		}
	}
	
	// -- SBB LOCAL OBJECT METHODS

	public UserProfile find(String username, String realm) {
		
		org.mobicents.slee.enabler.userprofile.jpa.UserProfile jpaUserProfile = management.getUser(username, realm);
		if (jpaUserProfile != null) {
			return new UserProfile(jpaUserProfile);
		}
		else {
		 	return null;
		}
		
	}

	// SBB OBJECT LIFECYCLE METHODS

	public void sbbActivate() {
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbExceptionThrown(Exception arg0, Object arg1,
			ActivityContextInterface arg2) {
	}

	public void sbbLoad() {
	}

	public void sbbPassivate() {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbRemove() {
	}

	public void sbbRolledBack(RolledBackContext arg0) {
	}

	public void sbbStore() {
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}

}
