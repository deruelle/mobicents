/*
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
package org.mobicents.examples.media.dtmf;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSignalDetector;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.events.ann.MsPlayRequestedSignal;
import org.mobicents.mscontrol.events.dtmf.MsDtmfNotifyEvent;
import org.mobicents.mscontrol.events.dtmf.MsDtmfRequestedEvent;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class DtmfDemoSbb implements Sbb {

    private final static String IVR_ENDPOINT = "media/endpoint/IVR/$";
    private final static String WELCOME_MSG = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/welcome.wav";
    private final static String DTMF_0 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf0.wav";
    private final static String DTMF_1 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf1.wav";
    private final static String DTMF_2 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf2.wav";
    private final static String DTMF_3 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf3.wav";
    private final static String DTMF_4 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf4.wav";
    private final static String DTMF_5 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf5.wav";
    private final static String DTMF_6 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf6.wav";
    private final static String DTMF_7 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf7.wav";
    private final static String DTMF_8 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf8.wav";
    private final static String DTMF_9 = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1") + ":8080/msdemo/audio/dtmf9.wav";
    private MsProvider msProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    private SbbContext sbbContext;
    private Logger logger = Logger.getLogger(DtmfDemoSbb.class);

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.examples.media.Demo#startConversation(String,
     *      ActivityContextInterface).
     */
    public void startDemo(String endpointName) {
        logger.info("Joining " + endpointName + " with " + IVR_ENDPOINT);

        MsConnection connection = (MsConnection) getConnectionActivity().getActivity();
        MsSession session = connection.getSession();
        MsLink link = session.createLink(MsLinkMode.FULL_DUPLEX);

        ActivityContextInterface linkActivity = null;
        try {
            linkActivity = mediaAcif.getActivityContextInterface(link);
        } catch (UnrecognizedActivityException ex) {
        }

        linkActivity.attach(sbbContext.getSbbLocalObject());
        link.join(endpointName, IVR_ENDPOINT);
    }

    public void onLinkConnected(MsLinkEvent evt, ActivityContextInterface aci) {
        //ask dtmf detector
        MsLink link = evt.getSource();
        MsEndpoint ivr = link.getEndpoints()[1];

        try {
            MsEventFactory factory = msProvider.getEventFactory();
            MsDtmfRequestedEvent dtmf = (MsDtmfRequestedEvent) factory.createRequestedEvent(DTMF.TONE);
            MsRequestedSignal[] signals = new MsRequestedSignal[]{};
            MsRequestedEvent[] events = new MsRequestedEvent[]{dtmf};

            ivr.execute(signals, events, link);
            play(WELCOME_MSG, link);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLinkFailed(MsLinkEvent evt, ActivityContextInterface aci) {
    }

    public void onLinkDisconnected(MsLinkEvent evt, ActivityContextInterface aci) {
    }

    private ActivityContextInterface getConnectionActivity() {
        ActivityContextInterface[] activities = sbbContext.getActivities();
        for (int i = 0; i < activities.length; i++) {
            if (activities[i].getActivity() instanceof MsConnection) {
                return activities[i];
            }
        }

        return null;
    }

    public void onDtmf(MsNotifyEvent evt, ActivityContextInterface aci) {
        MsDtmfNotifyEvent event = (MsDtmfNotifyEvent) evt;
        MsLink link = (MsLink) evt.getSource();
        String seq = event.getSequence();
        if (seq.equals("0")) {
            play(DTMF_0, link);
        } else if (seq.equals("1")) {
            play(DTMF_1, link);
        } else if (seq.equals("2")) {
            play(DTMF_2, link);
        } else if (seq.equals("3")) {
            play(DTMF_3, link);
        } else if (seq.equals("4")) {
            play(DTMF_4, link);
        } else if (seq.equals("5")) {
            play(DTMF_5, link);
        } else if (seq.equals("6")) {
            play(DTMF_6, link);
        } else if (seq.equals("7")) {
            play(DTMF_7, link);
        } else if (seq.equals("8")) {
            play(DTMF_8, link);
        } else if (seq.equals("9")) {
            play(DTMF_9, link);
        }

    }

    private void play(String url, MsLink link) {
        MsEventFactory eventFactory = msProvider.getEventFactory();
        MsPlayRequestedSignal play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);
        play.setURL(url);

        MsRequestedEvent onCompleted = eventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
        onCompleted.setEventAction(MsEventAction.NOTIFY);

        MsRequestedEvent onFailed = eventFactory.createRequestedEvent(MsAnnouncement.FAILED);
        onFailed.setEventAction(MsEventAction.NOTIFY);

        MsRequestedSignal[] requestedSignals = new MsRequestedSignal[]{play};
        MsRequestedEvent[] requestedEvents = new MsRequestedEvent[]{onCompleted, onFailed};

        link.getEndpoints()[1].execute(requestedSignals, requestedEvents, link);
    }

    public void onAnnouncementComplete(MsNotifyEvent evt, ActivityContextInterface aci) {
        MsLink link = (MsLink) evt.getSource();
        MsEndpoint ivr = link.getEndpoints()[1];

        MsEventFactory factory = msProvider.getEventFactory();
        MsDtmfRequestedEvent dtmf = (MsDtmfRequestedEvent) factory.createRequestedEvent(DTMF.TONE);
        MsRequestedSignal[] signals = new MsRequestedSignal[]{};
        MsRequestedEvent[] events = new MsRequestedEvent[]{dtmf};
        
        ivr.execute(signals, events, link);
    }

    @SuppressWarnings("static-access")
    private void initDtmfDetector(MsConnection connection) {
        MsSignalDetector dtmfDetector = msProvider.getSignalDetector(this.getUserEndpoint());
        try {
            ActivityContextInterface dtmfAci = mediaAcif.getActivityContextInterface(dtmfDetector);
            dtmfAci.attach(sbbContext.getSbbLocalObject());
            dtmfDetector.receive(EventID.DTMF, connection, new String[]{});
        } catch (UnrecognizedActivityException e) {
        }
    }

    public void onUserDisconnected(MsConnectionEvent evt, ActivityContextInterface aci) {
        MsLink link = getLink();
        System.out.println("Releasing link=" + link);
        if (link != null) {
            link.release();
        }
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

    /**
     * CMP field accessor
     * 
     * @return the name of the user's endpoint.
     */
    public abstract String getUserEndpoint();

    /**
     * CMP field accessor
     * 
     * @param endpoint
     *            the name of the user's endpoint.
     */
    public abstract void setUserEndpoint(String endpointName);

    /**
     * Relation to Welcome dialog
     * 
     * @return child relation object.
     */
    public abstract ChildRelation getAnnouncementSbb();

    private MsConnection getConnection() {
        ActivityContextInterface[] activities = sbbContext.getActivities();
        for (int i = 0; i < activities.length; i++) {
            if (activities[i].getActivity() instanceof MsConnection) {
                return (MsConnection) activities[i].getActivity();
            }
        }
        return null;
    }

    private ActivityContextInterface getConnectionActivityContext() {
        ActivityContextInterface[] activities = sbbContext.getActivities();
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

    public void sbbExceptionThrown(Exception ex, Object arg1, ActivityContextInterface aci) {
    }

    public void sbbRolledBack(RolledBackContext context) {
    }
}
