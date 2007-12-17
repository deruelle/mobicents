/*
 * WelcomeSbb.java
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

package org.mobicents.examples.media;

import java.net.URL;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.facilities.ActivityContextNamingFacility;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ratype.ConnectionEvent;
import org.mobicents.slee.resource.media.ratype.IVRContext;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;
import org.mobicents.slee.resource.media.ratype.MediaProvider;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;

/**
 * Play welcome message.
 *
 * @author Oleg Kulikov
 */
public abstract class WelcomeSbb implements Sbb {
    
    private final static String msgURL = "file:/C:/sounds/welcome.wav";
    private SbbContext sbbContext;
    
    private MediaProvider mediaProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    
    private Logger logger = Logger.getLogger(WelcomeSbb.class);

    private ActivityContextNamingFacility namingFacility;
    private URL announcement;
    private MediaConnection connection;
    
    /** Creates a new instance of WelcomeSbb */
    public WelcomeSbb() {
    }

    public void onConnectionConnected(ConnectionEvent evt, ActivityContextInterface aci) {
        connection = evt.getConnection();
        logger.info("Starting announcement message for connection " + connection);
        
        IVRContext ivr = (IVRContext) connection.getMediaContext();
        ivr.play(announcement);                
    }

    public void onPlayerStarted(MediaContextEvent evt, ActivityContextInterface aci) {
        logger.info("Player started");
    }
    
    public void onPlayerStopped(MediaContextEvent evt, ActivityContextInterface aci) {
        logger.info("Welcome message finished. Starting DTMF handler");
        
        SbbLocalObject dtmfSbb = null;
        try {
            ChildRelation relation = getDtmfSbb();
            dtmfSbb = relation.create();
        } catch (CreateException e) {
            logger.error("Could not create DTMF SBB", e);
            connection.release();
            return;
        }
        
        SbbLocalObject recorderSbb = null;
        try {
            ChildRelation relation = getRecorderSbb();
            recorderSbb = relation.create();
        } catch (CreateException e) {
            logger.error("Could not create Recorder SBB", e);
            connection.release();
            return;
        }
        
        //attach DTMF handler SBB to media connection and media context activites
        //same time detach this sbb from those activities
        
        ActivityContextInterface activities[] = sbbContext.getActivities();
        for (int i = 0; i < activities.length; i++) {
            activities[i].attach(dtmfSbb);
            activities[i].attach(recorderSbb);
            activities[i].detach(sbbContext.getSbbLocalObject());
        }
    }
    
    /** Relation for Welcome message SBB */
    public abstract ChildRelation getDtmfSbb();
    /** Relation for Welcome message SBB */
    public abstract ChildRelation getRecorderSbb();
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        logger.info("Set sbbContext: SbbID=" + sbbContext.getSbb());

        try {
            Context ctx = (Context) new InitialContext().lookup("java:comp/env");
            namingFacility = (ActivityContextNamingFacility) ctx.lookup("slee/facilities/activitycontextnaming");
            
            //initilize Media API
            mediaProvider = (MediaProvider) ctx.lookup("slee/resources/media/1.0/provider");
            mediaAcif = (MediaRaActivityContextInterfaceFactory)ctx.lookup("slee/resources/media/1.0/acifactory");
        } catch (Exception ne) {
            logger.error("Could not set SBB context:", ne);
        }
        
        try {
            announcement = new URL(msgURL);
        } catch (Exception e) {
            logger.error("Could not load announcement message from: " + msgURL);
        }
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

    public void sbbExceptionThrown(Exception exception, Object object, ActivityContextInterface activityContextInterface) {
    }

    public void sbbRolledBack(RolledBackContext rolledBackContext) {
    }
    
}
