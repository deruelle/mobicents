/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.resource.media.ra;
import org.mobicents.slee.resource.media.ratype.MediaSession;
import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;
import org.mobicents.slee.runtime.ActivityContext;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

public class MediaRaActivityContextInterfaceFactoryImpl implements
		MediaRaActivityContextInterfaceFactory,
		ResourceAdaptorActivityContextInterfaceFactory {
	
    private static Logger logger = Logger.getLogger(
    		MediaRaActivityContextInterfaceFactoryImpl.class);    
    // Reference to the SLEE for further usage
    private SleeContainer serviceContainer; 
    // The JNDI name of the ActivityContextInterfaceFactory object
    private final String jndiName;
    
    private String raEntityName;
    private ActivityContextFactory activityContextFactory;
    
    public MediaRaActivityContextInterfaceFactoryImpl(SleeContainer serviceContainer,
    		String entityName) {	
        this.serviceContainer = serviceContainer;
        this.activityContextFactory = serviceContainer.getActivityContextFactory();
        this.raEntityName = entityName;
        this.jndiName = "java:slee/resources/" + entityName + "/mediaraacif";        
        logger.debug("MediaRaActivityContextInterfaceFactoryImpl.jndiName = " + jndiName);
    }

	public String getJndiName() {
		return jndiName;
	}
	
    public ActivityContextInterface getActivityContextInterface(
            MediaSession mediaSession) throws NullPointerException,
            UnrecognizedActivityException, FactoryException {
        if (mediaSession == null)
            throw new NullPointerException("session activity ! huh!!");
        
        ActivityContext AC = this.getActivityContextForActivity(mediaSession);
        
        return new ActivityContextInterfaceImpl(this.serviceContainer,
        		AC.getActivityContextId());
    }
    
    /**
     * Convenient method to wrap same code for factory methods.
     * @param activity - Activity object for which we will return AC, 
     * should implement one of interfaces: <b>javax.sip.Dialog</b> or
     * <b>javax.sip.Transaction</b>.
     * @return ActovityContext for passed object.
     */
   private ActivityContext getActivityContextForActivity(Object activity) {
	   // Activity Handle for activity
	   MediaActivityHandle MAH = null;
	   
	   MediaSession session = (MediaSession)activity;
	   MAH = new MediaActivityHandle(session.getSessionId());
       // Slee AH which is proxy like class for wrapped activity handle object
       SleeActivityHandle SLAH = new SleeActivityHandle(raEntityName, MAH, serviceContainer);
       // Acitvity context
       // NOTE that SLAH is passed, not an activity object
       ActivityContext AC=this.activityContextFactory.getActivityContext(SLAH);
       
       return AC;   
   }

    public ActivityContextInterface getActivityContextInterface(MediaConnection connection) throws NullPointerException, UnrecognizedActivityException, FactoryException {
        ConnectionHandle connectionHandle = new ConnectionHandle(connection.getId());
        SleeActivityHandle sleeActivityHandle = new SleeActivityHandle(raEntityName, connectionHandle, serviceContainer);
        ActivityContext ac = activityContextFactory.getActivityContext(sleeActivityHandle);
        
        return new ActivityContextInterfaceImpl(serviceContainer, ac.getActivityContextId());
    }

    public ActivityContextInterface getActivityContextInterface(MediaContext context) throws NullPointerException, UnrecognizedActivityException, FactoryException {
        ContextHandle contextHandle = new ContextHandle(context.getId());
        SleeActivityHandle sleeActivityHandle = new SleeActivityHandle(raEntityName, contextHandle, serviceContainer);
        ActivityContext ac = activityContextFactory.getActivityContext(sleeActivityHandle);
        
        return new ActivityContextInterfaceImpl(serviceContainer, ac.getActivityContextId());
    }
}