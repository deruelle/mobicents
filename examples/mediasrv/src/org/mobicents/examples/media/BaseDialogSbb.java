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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;
import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
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
public abstract class BaseDialogSbb implements Sbb {

    public final static String ANNOUNCEMENT_ENDPOINT = "media/trunk/Announcement/$";
    private SbbContext sbbContext;
    private MsProvider msProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    private Logger logger = Logger.getLogger(BaseDialogSbb.class);

    public void onConnectionCreated(MsConnectionEvent evt, ActivityContextInterface aci) {
        MsConnection connection = evt.getConnection();
        setConnection(connection);
        join(ANNOUNCEMENT_ENDPOINT);
    }

    public void onAnnouncementLinkCreated(MsLinkEvent evt, ActivityContextInterface aci) {
        MsLink link = evt.getSource();
        playAnnouncement(link.getEndpoints()[1], getWelcomeMessage());
    }

    public void onAnnouncementLinkFailed(MsLinkEvent evt, ActivityContextInterface aci) {
    //play error tone;
    }

    public void onAnnouncementComplete(MsNotifyEvent evt, ActivityContextInterface aci) {
        MsLink link = this.getLink();
        link.release();
    }

    public void join(String endpoint) {
        MsSession session = getConnection().getSession();
        MsLink link = session.createLink(MsLink.MODE_FULL_DUPLEX);

        ActivityContextInterface linkActivity = null;
        try {
            linkActivity = mediaAcif.getActivityContextInterface(link);
        } catch (UnrecognizedActivityException ex) {
        }

        linkActivity.attach(sbbContext.getSbbLocalObject());
        link.join(this.getConnection().getEndpoint(), endpoint);
    }

    public void playAnnouncement(String endpoint, String url) throws FactoryException {
        MsSignalGenerator generator = msProvider.getSignalGenerator(endpoint);
        try {
            ActivityContextInterface generatorActivity = mediaAcif.getActivityContextInterface(generator);
            generatorActivity.attach(sbbContext.getSbbLocalObject());
            generator.apply(Announcement.PLAY, new String[]{getWelcomeMessage()});
        } catch (UnrecognizedActivityException e) {
        }
    }

    public abstract String getWelcomeMessage();

    public abstract MsConnection getConnection();
    public abstract void setConnection(MsConnection connection);

    public void playErrorTone(MsConnection connection) {
    }

    private MsLink getLink() {
        ActivityContextInterface[] activities = sbbContext.getActivities();
        for (int i = 0; i < activities.length; i++) {
            if (activities[i].getActivity() instanceof MsLink) {
                return (MsLink) activities[i].getActivity();
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
}
