/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: BounceSbb.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 4th August 2005, 15:26
 * Changed: 5th October 2005, 10:25 (moved RAFActivity to ratype and removed RAFrameProvider)
 * Version: 1.0
 */
package com.maretzke.raframe.service.bounce;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbID;
import javax.slee.facilities.Level;
import javax.slee.facilities.TraceFacility;
import com.maretzke.raframe.message.Message;
import com.maretzke.raframe.message.MessageEvent;
import com.maretzke.raframe.ratype.RAFActivity;
import com.maretzke.raframe.ratype.RAFrameResourceAdaptorSbbInterface;
import javax.slee.CreateException;

/**
 * BounceSbb is a Sbb representing a message bounce service. BounceSbb receives
 * incoming MessageEvents from the underlying resource adaptor. According to the
 * messages, it increases counter in the related activity. If the command "ANY" is
 * received by the Sbb, BounceSbb sends a message back to the originator.
 *
 * @author Michael Maretzke
 */
public abstract class BounceSbb implements Sbb {
    // reference to the trace facility
    private TraceFacility traceFacility;
    // the identifier for this sbb
    private SbbID sbbId;
    // the sbb's sbbContext
    private SbbContext sbbContext;
    // the interface from the sbb into the resource adaptor
    private RAFrameResourceAdaptorSbbInterface sbb2ra;
    
    /** Creates a new instance of BounceSbb */
    public BounceSbb() {
    }
    
    /**
     * EventHandler method for incoming events of type "AnyEvent". AnyEvent is
     * defined in the deployment descriptor "sbb-jar.xml".
     * This method is invoked by the SLEE if an event of type ANY is received and fired
     * by the resource adaptor.
     */
    public void onAnyEvent(MessageEvent event, ActivityContextInterface ac) {
    	trace(Level.INFO, "BounceSbb: " + this + ": received an incoming Request. CallID = " + event.getMessage().getId() + ". Command = " + event.getMessage().getCommand());
            try {
                RAFActivity activity = (RAFActivity) ac.getActivity();
                // change the activity - here only for demonstration purpose, but could be valuable for other Sbbs
                activity.anyReceived();
                trace(Level.INFO, "ANY Event: INIT:" + activity.getInitCounter() + " ANY:" + activity.getAnyCounter() + " END:" + activity.getEndCounter() + " Valid state: " + activity.isValid(event.getMessage().getCommandId()));                
            } catch (Exception e) {
                trace(Level.WARNING, "Exception during onAnyEvent: ", e);
            }            

        // send an answer back to the resource adaptor / stack / invokee
        // generate a message object and ...
        Message answer = sbb2ra.getMessageFactory().createMessage(event.getMessage().getId(), "Command bounced by BounceSbb: " + event.getMessage().getCommand());
        // ... send it using the resource adaptor
        sbb2ra.send(answer);
    }

    /**
     * EventHandler method for incoming events of type "EndEvent". EndEvent is
     * defined in the deployment descriptor "sbb-jar.xml".
     * This method is invoked by the SLEE if an event of type END is received and fired
     * by the resource adaptor.
     */
    public void onEndEvent(MessageEvent event, ActivityContextInterface ac) {
    	trace(Level.INFO, "BounceSbb: " + this + ": received an incoming Request. CallID = " + event.getMessage().getId() + ". Command = " + event.getMessage().getCommand());
            try {
                RAFActivity activity = (RAFActivity) ac.getActivity();
                // change the activity - here only for demonstration purpose, but could be valuable for other Sbbs                
                activity.endReceived();
                trace(Level.INFO, "END Event: INIT:" + activity.getInitCounter() + " ANY:" + activity.getAnyCounter() + " END:" + activity.getEndCounter() + " Valid state: " + activity.isValid(event.getMessage().getCommandId()));
            } catch (Exception e) {
                trace(Level.WARNING, "Exception during onEndEvent: ", e);
            }
    }

    /**
     * EventHandler method for incoming events of type "InitEvent". InitEvent is
     * defined in the deployment descriptor "sbb-jar.xml".
     * This method is invoked by the SLEE if an event of type INIT is received and fired
     * by the resource adaptor.
     */
    public void onInitEvent(MessageEvent event, ActivityContextInterface ac) {
    	trace(Level.INFO, "BounceSbb: " + this + ": received an incoming Request. CallID = " + event.getMessage().getId() + ". Command = " + event.getMessage().getCommand());
            try {
                RAFActivity activity = (RAFActivity) ac.getActivity();
                // change the activity - here only for demonstration purpose, but could be valuable for other Sbbs                
                activity.initReceived();
                trace(Level.INFO, "INIT Event: INIT:" + activity.getInitCounter() + " ANY:" + activity.getAnyCounter() + " END:" + activity.getEndCounter() + " Valid state: " + activity.isValid(event.getMessage().getCommandId()));
                    
            } catch (Exception e) {
                trace(Level.WARNING, "Exception during onInitEvent: ", e);
            }            
    }

    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 54 for further information.
     * <br>
     * The SLEE invokes this method after a new instance of the SBB abstract class is created. 
     * During this method, an SBB entity has not been assigned to the SBB object. The SBB object can take advantage
     * of this method to allocate and initialize state or connect to resources that are to be held by the SBB
     * object during its lifetime. Such state and resources cannot be specific to an SBB entity because the SBB
     * object might be reused during its lifetime to serve multiple SBB entities.
     * <br>
     * This method indicates a transition from state "DOES NOT EXIST" to "POOLED" (see page 52)
     */            
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        sbbId = sbbContext.getSbb();

        try {
            Context ctx = (Context) new InitialContext().lookup("java:comp/env");            
            // lookup the trace facility and store it for further usage
            traceFacility = (TraceFacility) ctx.lookup("slee/facilities/trace");
            // get the reference to the RAFrameProvider class which implements RAFrameResourceAdaptorSbbInterface
            sbb2ra = (RAFrameResourceAdaptorSbbInterface) ctx.lookup("slee/resources/raframe/1.0/sbb2ra");
        } 
        catch (NamingException ne) {
            System.out.println("Could not set SBB context: " + ne.toString());
        }              
    }

    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 54 for further information.
     * <br>
     * The SLEE invokes this method before terminating the life of the SBB object. The SBB object can take 
     * advantage of this method to free state or resources that are held by the SBB object. These state 
     * and resources typically had been allocated by the setSbbContext method.
     * <br>
     * This method indicates a transition from state "POOLED" to "DOES NOT EXIST" (see page 52)
     */
    public void unsetSbbContext() {
        trace(Level.INFO, "BounceSBB: " + this + ": unsetSbbContext() called.");
    }

    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 55 for further information.
     * <br>
     * The SLEE invokes this method on an SBB object before the SLEE creates a new SBB entity in response to
     * an initial event or an invocation of the create method on a ChildRelation object. This method
     * should initialize the SBB object using the CMP field get and set accessor methods, such that when this
     * method returns, the persistent representation of the SBB entity can be created.
     * <br>
     * This method is the first part of a transition from state "POOLED" to "READY" (see page 52)
     */    
    public void sbbCreate() throws javax.slee.CreateException {
        trace(Level.INFO, "BounceSBB: " + this + ": sbbCreate() called.");
    }

    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 55 for further information.
     * <br>
     * The SLEE invokes this method on an SBB object after the SLEE creates a new SBB entity. The SLEE invokes
     * this method after the persistent representation of the SBB entity has been created and the SBB object
     * is assigned to the created SBB entity. This method gives the SBB object a chance to initialize additional
     * transient state and acquire additional resources that it needs while it is in the Ready state.     
     * <br>
     * This method is the second part of a transition from state "POOLED" to "READY" (see page 52)
     */    
    public void sbbPostCreate() throws CreateException {
        trace(Level.INFO, "BounceSBB: " + this + ": sbbPostCreate() called.");
    }

    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 55 for further information.
     * <br>
     * The SLEE invokes this method on an SBB object when the SLEE picks the SBB object in the pooled state
     * and assigns it to a specific SBB entity. This method gives the SBB object a chance to initialize additional
     * transient state and acquire additional resources that it needs while it is in the Ready state.
     * <br>
     * This method indicates a transition from state "POOLED" to "READY" (see page 52)
     */    
    public void sbbActivate() {
        trace(Level.INFO, "BounceSBB: " + this + ": sbbActivate() called.");
    }
    
    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 56 for further information.
     * <br>
     * The SLEE invokes this method on an SBB object when the SLEE decides to disassociate the SBB object
     * from the SBB entity, and to put the SBB object back into the pool of available SBB objects. This method
     * gives the SBB object the chance to release any state or resources that should not be held while the SBB
     * object is in the pool. These state and resources typically had been allocated during the sbbActivate
     * method.     
     * <br>
     * This method indicates a transition from state "READY" to "POOLED" (see page 52)
     */    
    public void sbbPassivate() {
        trace(Level.INFO, "BounceSBB: " + this + ": sbbPassivate() called.");
    }
   
    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 56 for further information.
     * <br>
     * The SLEE invokes the sbbRemove method on an SBB object before the SLEE removes the SBB entity
     * assigned to the SBB object.
     * <br>
     * This method indicates a transition from state "READY" to "POOLED" (see page 52)
     */    
    public void sbbRemove() {
        trace(Level.INFO, "BounceSBB: " + this + ": sbbRemove() called.");
    }

    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 56 for further information.
     * <br>
     * The SLEE calls this method to synchronize the state of an SBB object with its assigned SBB entity’s persistent
     * state. The SBB Developer can assume that the SBB object’s persistent state has been loaded just
     * before this method is invoked.
     * <br>
     * This method indicates a transition from state "READY" to "READY" (see page 52)
     */    
    public void sbbLoad() {
        trace(Level.INFO, "BounceSBB: " + this + ": sbbLoad() called.");
    }

    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 57 for further information.
     * <br>
     * The SLEE calls this method to synchronize the state of the SBB entity’s persistent state with the state of the
     * SBB object. The SBB Developer should use this method to update the SBB object using the CMP field
     * accessor methods before its persistent state is synchronized.
     * <br>
     * This method indicates a transition from state "READY" to "READY" (see page 52)
     */    
    public void sbbStore() {
        trace(Level.INFO, "BounceSBB: " + this + ": sbbStore() called.");
    }    
    
    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 67 for further information.
     * <br>
     * The SLEE invokes the sbbRolledBack callback method after a transaction used in a SLEE originated
     * invocation has rolled back.
     */    
    public void sbbRolledBack(javax.slee.RolledBackContext rolledBackContext) {
        trace(Level.INFO, "BounceSBB: " + this + ": sbbRolledBack() called.");
    }
    
    /**
     * implements javax.slee.Sbb
     * Please refer to JSLEE v1.1 Specification, Early Draft Review Page 65 for further information.
     * <br> 
     * The SLEE invokes this method after a SLEE originated invocation of a transactional method of
     * the SBB object returns by throwing a RuntimeException.
     */    
    public void sbbExceptionThrown(Exception exception, Object obj, javax.slee.ActivityContextInterface activityContextInterface) {
        trace(Level.INFO, "BounceSBB: " + this + ": sbbExceptionThrown() called.");
    }

    // trace wrappers
    protected final void trace(Level level, String message) {
        try {
            traceFacility.createTrace(sbbId, level, "BounceSbb", message, System.currentTimeMillis());
       } catch (Exception e) { }
       System.out.println("BounceSbb [" + System.currentTimeMillis() + "]: " + message);
    }
    
    protected final void trace(Level level, String message, Throwable t) {
        try {
            traceFacility.createTrace(sbbId, level, "BounceSbb", message, t, System.currentTimeMillis());
        } catch (Exception e) { }
        System.out.println("BounceSbb [" + System.currentTimeMillis() + "]: " + message);
    }    
}

