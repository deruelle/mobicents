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
package org.mobicents.examples.media;

import java.util.List;

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
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsResource;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.events.ann.MsPlayRequestedSignal;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class AnnouncementSbb implements Sbb {

    public final static String ANNOUNCEMENT_ENDPOINT = "media/trunk/Announcement/$";
    private SbbContext sbbContext;
    private MsProvider msProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    private Logger logger = Logger.getLogger(AnnouncementSbb.class);

    public void play(String userEndpoint, List announcements, boolean keepAlive) {
        // hold announcement sequence in the activity context interface
        this.setKeepAlive(keepAlive);
        this.setIndex(0);
        this.setSequence(announcements);

        // join user endpoint with any of the announcement endpoint
        // ActivityContextInterface connectionActivity =
        // sbbContext.getActivities()[0];
        ActivityContextInterface connectionActivity = this.getUserActivity();
        logger.info("Joining " + userEndpoint + " with " + ANNOUNCEMENT_ENDPOINT);

        MsConnection connection = (MsConnection) connectionActivity.getActivity();
        MsSession session = connection.getSession();
        MsLink link = session.createLink(MsLinkMode.FULL_DUPLEX);

        ActivityContextInterface linkActivity = null;
        try {
            linkActivity = mediaAcif.getActivityContextInterface(link);
        } catch (UnrecognizedActivityException ex) {
            ex.printStackTrace();
            return;
        }

        linkActivity.attach(sbbContext.getSbbLocalObject());
        link.join(userEndpoint, ANNOUNCEMENT_ENDPOINT);
    }

    public void onLinkConnected(MsLinkEvent evt, ActivityContextInterface aci) {
        MsLink link = evt.getSource();
        String announcementEndpoint = link.getEndpoints()[1].getLocalName();

        logger.info("Announcement endpoint: " + announcementEndpoint);
        this.setAnnouncementEndpoint(announcementEndpoint);

        playNext(link);
    }

    public void onLinkDisconnected(MsLinkEvent evt, ActivityContextInterface aci) {
        logger.info("Link release completed");

        ActivityContextInterface connectionAci = getUserActivity();
        if (connectionAci != null && !connectionAci.isEnding()) {
            connectionAci.detach(sbbContext.getSbbLocalObject());
            fireLinkDisconnected(evt, connectionAci, null);
        }
    }

    public void onLinkFailed(MsLinkEvent evt, ActivityContextInterface aci) {
        logger.error("Link failed");
    }    
    
    public void onAnnouncementComplete(MsNotifyEvent evt, ActivityContextInterface aci) {
        logger.info("Announcement complete: " + (this.getIndex() - 1));
        MsLink link = this.getLink();
        if (this.getIndex() < this.getSequence().size()) {
            logger.info("Playing announcement[" + this.getIndex() + "]");
            playNext(link);
            return;
        }

        if (this.getIndex() == this.getSequence().size() && !this.getKeepAlive()) {
            logger.info("Releasing link");
            link.release();
        } else {
            this.setIndex(0);
            playNext(link);
        }
    }

    public void playNext(MsLink link) {
        String url = (String) this.getSequence().get(this.getIndex());
        MsEventFactory eventFactory = msProvider.getEventFactory();
        
        MsPlayRequestedSignal play = null;
        try {
            play = (MsPlayRequestedSignal) 
                eventFactory.createRequestedSignal(MsAnnouncement.PLAY);
            play.setURL(url);
            System.out.println("PLAY signal=" + play);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        MsRequestedEvent onCompleted = null;
        MsRequestedEvent onFailed = null;
        
        try {
            onCompleted = eventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
            onCompleted.setEventAction(MsEventAction.NOTIFY);

            onFailed = eventFactory.createRequestedEvent(MsAnnouncement.FAILED);
            onFailed.setEventAction(MsEventAction.NOTIFY);
        } catch (ClassNotFoundException e) {
        }
        
        MsRequestedSignal[] requestedSignals = new MsRequestedSignal[]{play};
        MsRequestedEvent[] requestedEvents = new MsRequestedEvent[]{onCompleted, onFailed};
        
        System.out.println("EXECUTING PLAY");
        link.getEndpoints()[1].execute(requestedSignals, requestedEvents, link);
        setIndex(getIndex()+1);
    }

    public void onUserDisconnected(MsConnectionEvent evt, ActivityContextInterface aci) {
        logger.info("Disconnecting from " + getAnnouncementEndpoint());
        MsResource resource = getResource();

        if (resource != null) {
            resource.release();
        }

        MsLink link = getLink();
        System.out.println("Releasing link=" + link);
        if (link != null) {
            link.release();
        }

    }

    public abstract void fireLinkDisconnected(MsLinkEvent evt, ActivityContextInterface aci, Address address);

    public MsLink getLink() {
        ActivityContextInterface[] activities = sbbContext.getActivities();
        for (int i = 0; i < activities.length; i++) {
            if (activities[i].getActivity() instanceof MsLink) {
                return (MsLink) activities[i].getActivity();
            }
        }
        return null;
    }

    public MsResource getResource() {
        ActivityContextInterface[] activities = sbbContext.getActivities();
        for (int i = 0; i < activities.length; i++) {
            if (activities[i].getActivity() instanceof MsResource) {
                return (MsResource) activities[i].getActivity();
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

    public abstract String getAnnouncementEndpoint();

    public abstract void setAnnouncementEndpoint(String endpoint);

    public abstract int getIndex();

    public abstract void setIndex(int index);

    public abstract List getSequence();

    public abstract void setSequence(List sequence);

    public abstract boolean getKeepAlive();

    public abstract void setKeepAlive(boolean keepAlive);

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
