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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.Address;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;
import org.apache.log4j.Logger;
import org.mobicents.examples.media.BaseWelcomeConversationSbb;
import org.mobicents.examples.media.events.DialogCompletedEvent;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSignalGenerator;
import org.mobicents.mscontrol.signal.Announcement;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class WelcomeConversationSbb implements Sbb {
    
    public final static String ANNOUNCEMENT_ENDPOINT = "media/trunk/Announcement/$";
    private SbbContext sbbContext;
    private MsProvider msProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    private Logger logger = Logger.getLogger(BaseWelcomeConversationSbb.class);
    
    public void startConversation(String endpointName) {
        logger.info("Joining " + endpointName + " with " + ANNOUNCEMENT_ENDPOINT);
        
        MsConnection connection = (MsConnection) sbbContext.getActivities()[0].getActivity();
        MsSession session = connection.getSession();
        MsLink link = session.createLink(MsLink.MODE_FULL_DUPLEX);

        ActivityContextInterface linkActivity = null;
        try {
            linkActivity = mediaAcif.getActivityContextInterface(link);
        } catch (UnrecognizedActivityException ex) {
        }

        linkActivity.attach(sbbContext.getSbbLocalObject());
        link.join(endpointName, ANNOUNCEMENT_ENDPOINT);
    }
    
    /**
     * Terminates conversation
     */
    public void stopConversation() {
        
    }
    
    public void onAnnouncementLinkCreated(MsLinkEvent evt, ActivityContextInterface aci) {
        logger.info("Play announcement message: url=" + getWelcomeMessage());
        MsLink link = evt.getSource();
        MsSignalGenerator generator = msProvider.getSignalGenerator(link.getEndpoints()[1]);
        try {
            ActivityContextInterface generatorActivity = mediaAcif.getActivityContextInterface(generator);
            generatorActivity.attach(sbbContext.getSbbLocalObject());
            generator.apply(Announcement.PLAY, new String[]{getWelcomeMessage()});
        } catch (UnrecognizedActivityException e) {
        }
    }

    public void onAnnouncementLinkFailed(MsLinkEvent evt, ActivityContextInterface aci) {
        logger.error("Joining error: cause = " + evt.getCause());
    }

    public void onAnnouncementComplete(MsNotifyEvent evt, ActivityContextInterface aci) {
        logger.info("Announcement complete");
        MsLink link = this.getLink();
        link.release();            
    }

    public void onLinkReleased(MsLinkEvent evt, ActivityContextInterface aci) {
        logger.info("Dialog completed, fire DIALOG_COMPLETE event");
        DialogCompletedEvent event = new DialogCompletedEvent(LoopDemoSbb.CONVERSATION_WELCOME);
        this.fireDialogCompletedEvent(event, this.getUserActivity(), null);
    }
    
    public abstract void fireDialogCompletedEvent(DialogCompletedEvent evt, 
            ActivityContextInterface aci, Address address);
    
    
    public String getWelcomeMessage() {
        return "file:/c:/sounds/welcome.wav";
    }

    public MsLink getLink() {
        ActivityContextInterface[] activities = sbbContext.getActivities();
        for (int i = 0; i < activities.length; i++) {
            if (activities[i].getActivity() instanceof MsLink) {
                return (MsLink) activities[i].getActivity();
            }
        }
        return null;
    }

    public ActivityContextInterface getUserActivity() {
        ActivityContextInterface activities[] = sbbContext.getActivities();
        for (int i = 0; i < activities.length; i++) {
            if (activities[i].getActivity() instanceof MsConnection) {
                return activities[i];
            }
        }
        return null;
    }
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        try {
            Context ctx = (Context) new InitialContext().lookup("java:comp/env");
            msProvider = (MsProvider) ctx.lookup("slee/resources/media/1.0/provider");
            mediaAcif = (MediaRaActivityContextInterfaceFactory) ctx.lookup("slee/resources/media/1.0/acifactory");
        } catch (Exception ne) {
            logger.error("Could not set SBB context:", ne);
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

    public void sbbExceptionThrown(Exception arg0, Object arg1, ActivityContextInterface arg2) {
    }

    public void sbbRolledBack(RolledBackContext arg0) {
    }
}
