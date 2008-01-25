/*
 * CallSbb.java
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.examples.media.loop;

import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import org.apache.log4j.Logger;
import org.mobicents.examples.media.Conversation;
import org.mobicents.examples.media.events.DialogCompletedEvent;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class LoopDemoSbb implements Sbb {

    
    public final static int STATE_NULL = 0;
    public final static int STATE_PLAY_WELCOME = 1;
    public final static int STATE_LOOPBACK = 2;
    public final static int STATE_FAILED = 3;
    
    public final static String CONVERSATION_WELCOME = 
            "org.mobicents.media.loopback.demo.Welcome";
    public final static String CONVERSATION_LOOPBACK = 
            "org.mobicents.media.loopback.demo.Loopback";
    
    private int state = STATE_NULL;
    private SbbContext sbbContext;
    
    private final static String[] states = new String[] {
        "NULL", "PLAY_ANNOUNCEMENT", "LOOPBACK", "FAILED"
    };
    
    private Logger logger = Logger.getLogger(LoopDemoSbb.class);
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.examples.media.Demo#startConversation(String, ActivityContextInterface).
     */
    public void startDemo(String endpointName) {
        this.setUserEndpoint(endpointName);
        setState(STATE_PLAY_WELCOME, sbbContext.getActivities()[0]);
    }

    /**
     * Change current state.
     */
    public void setState(int state, ActivityContextInterface activity) {
        logger.info("Current state=" + states[this.state] + 
                ", target state=" + states[state]);
        this.state = state;
        switch (state) {
            case STATE_NULL:
                break;
            case STATE_PLAY_WELCOME:
                playWelcomeMessage(activity);
                break;
            case STATE_LOOPBACK:
                startLoopback(activity);
                break;
            case STATE_FAILED :
                break;
        }
    }

    /**
     * Runs welcome dialog.
     */
    public void playWelcomeMessage(ActivityContextInterface activity) {
        try {
            ChildRelation childRelation = this.getWelcomeConversationSbb();
            logger.info("Child relation:" + childRelation);
            
            SbbLocalObject locObj = childRelation.create();
            logger.info("SbbLocalObject: " + locObj);
            
            Conversation welcome = (Conversation) childRelation.create();
            logger.info("Starting welcome conversation");
            activity.attach(welcome);
            welcome.startConversation(this.getUserEndpoint());
        } catch (CreateException e) {
            setState(STATE_FAILED, activity);
        }
    }

    /**
     * Runs loopback
     */
    public void startLoopback(ActivityContextInterface activity) {
        try {
            ChildRelation childRelation = this.getLoopbackConversationSbb();
            Conversation loopback = (Conversation) childRelation.create();
            logger.info("Starting loopback");
            activity.attach(loopback);
            loopback.startConversation(this.getUserEndpoint());
        } catch (CreateException e) {
            setState(STATE_FAILED, activity);
        }
    }
    
    /**
     * Conversation callback handler.
     * 
     * @param evt the event instance.
     * @param aci the activity context interface
     */
    public void onDialogCompletedEvent(DialogCompletedEvent evt, ActivityContextInterface aci) {
        logger.info("Conversation completed: " + evt.getDialogName());
        String dialogName = evt.getDialogName();
        if (dialogName.equals(CONVERSATION_WELCOME)) {
            setState(STATE_LOOPBACK, aci);
        } else if (dialogName.equals(CONVERSATION_LOOPBACK)) {
            setState(STATE_NULL, aci);
        }
    }

    /**
     * CMP field accessor
     *  
     * @return the name of the user's endpoint. 
     */
    public abstract String getUserEndpoint();

    /**
     * CMP field accessor
     *  
     * @param endpoint the name of the user's endpoint. 
     */
    public abstract void setUserEndpoint(String endpointName);

    /**
     * Relation to Welcome dialog
     * 
     * @return child relation object.
     */
    public abstract ChildRelation getWelcomeConversationSbb();

    /**
     * Relation with Loop back dialog.
     * 
     * @return child relation object.
     */
    public abstract ChildRelation getLoopbackConversationSbb();

    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
    }
    
    public void unsetSbbContext() {
    }

    public void sbbCreate() throws CreateException {
    }

    public void sbbPostCreate() throws CreateException {
    }

    public void sbbActivate() {
    }

    public void sbbPassivate() {
    }

    public void sbbLoad() {
    }

    public void sbbStore() {
    }

    public void sbbRemove() {
    }

    public void sbbExceptionThrown(Exception arg0, Object arg1, ActivityContextInterface arg2) {
    }

    public void sbbRolledBack(RolledBackContext arg0) {
    }
}
