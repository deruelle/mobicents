/*
 * MsSignalDetectorImpl.java
 *
 * The Simple Media API RA
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
package org.mobicents.mscontrol.impl;

import java.rmi.server.UID;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.events.NotifyEvent;
//import org.mobicents.media.server.spi.events.announcement.AnnParams;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsResourceListener;
import org.mobicents.mscontrol.MsSignalGenerator;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.ann.MsPlayRequestedSignal;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;

/**
 * 
 * @author Oleg Kulikov
 */
public class MsSignalGeneratorImpl implements MsSignalGenerator, NotificationListener {

    private Endpoint endpoint;
    private String endpointName;
    private MsProvider provider;
    private String id = (new UID()).toString();
    private ArrayList<MsResourceListener> listeners = new ArrayList<MsResourceListener>();
    private boolean released = false;

    /** Creates a new instance of MsSignalDetectorImpl */
    public MsSignalGeneratorImpl(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public MsSignalGeneratorImpl(MsProvider provider, String endpointName) {
        this.provider = provider;
        this.endpointName = endpointName;
    }

    public String getID() {
        return id;
    }

    public void setResourceStateIdle() {
        /*        MsNotifyEventImpl evt = new MsNotifyEventImpl(this, EventID.DTMF, EventCause.NORMAL,
        "Created new MsSignalGenerator");
        for (MsResourceListener listener : listeners) {
        listener.resourceCreated(evt);
        }
         */
    }

    public void apply(EventID signalID, String[] params) {
        switch (signalID) {
            case PLAY:
                break;
        }
        new Thread(new PlayTx(this, signalID, params)).start();
    }

    public void apply(EventID signalID, MsConnection connection, String[] params) {
        String conID = ((MsConnectionImpl) connection).connection.getId();
        new Thread(new PlayTx1(this, signalID, conID, params)).start();
    }

    public void apply(EventID signalID, MsLink link, String[] params) {
        switch (signalID) {
            case PLAY:
                doPlay(params[0], (MsLinkImpl) link);
                break;
        }
//        String conID = ((MsLinkImpl) link).getConnectionID(endpointName);
//        new Thread(new PlayTx1(this, signalID, conID, params)).start();
    }

    public void update(NotifyEvent event) {
        // EventID eventID = EventID.getEvent(event.getID());
/*        if (!released) {
        MsNotifyEventImpl evt = new MsNotifyEventImpl(this, eventID, event.getCause(), event.getMessage());
        sendEvent(evt);
        }
         */
    }

    private void sendEvent(MsNotifyEvent evt) {
        for (MsResourceListener listener : listeners) {
            listener.update(evt);
        }
    }

    public void addResourceListener(MsResourceListener listener) {
        listeners.add(listener);
    }

    public void removeResourceListener(MsResourceListener listener) {
        listeners.remove(listener);
    }

    public void release() {
        released = true;

    /*        MsNotifyEventImpl evt = new MsNotifyEventImpl(this, EventID.INVALID, EventCause.NORMAL,
    "Inavlidated MsSignalGenerator");
    for (MsResourceListener listener : listeners) {
    listener.resourceInvalid(evt);
    }
     */
    }

    private void doPlay(String url, MsLinkImpl link) {
        MsEventFactory eventFactory = provider.getEventFactory();

        MsPlayRequestedSignal play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);
        play.setURL(url);
        link.append(play, endpointName);


        MsRequestedEvent onCompleted = eventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
        onCompleted.setEventAction(MsEventAction.NOTIFY);
        link.append(onCompleted, endpointName);

        MsRequestedEvent onFailed = eventFactory.createRequestedEvent(MsAnnouncement.FAILED);
        onFailed.setEventAction(MsEventAction.NOTIFY);
        link.append(onFailed, endpointName);
    }

    private void doRecord() {
    }

    @Override
    public String toString() {
        return "SignalGenerator[" + id + "]";
    }

    private class PlayTx implements Runnable {

        private EventID signalID;
        private String[] params;
        private MsSignalGeneratorImpl generator;

        public PlayTx(MsSignalGeneratorImpl generator, EventID signalID, String[] params) {
            this.generator = generator;
            this.signalID = signalID;
            this.params = params;
        }

        public void run() {
            // if endpoint is not known yet
            if (endpoint == null) {
                try {
                    endpoint = EndpointQuery.find(endpointName);
                } catch (NamingException ex) {
//                    MsNotifyEvent error = new MsNotifyEventImpl(generator, EventID.FAIL, EventCause.FACILITY_FAILURE,
//                            ex.getMessage());
//                    sendEvent(error);
                } catch (ResourceUnavailableException ex) {
//                    MsNotifyEvent error = new MsNotifyEventImpl(generator, EventID.FAIL, EventCause.FACILITY_FAILURE,
//                            ex.getMessage());
//                    sendEvent(error);
                }
            }

            //Options options = new Options();
            //options.add(AnnParams.URL, params[0]);
            try {
                // endpoint.play(signalID.toString(), options, generator);
            } catch (Exception ex) {
//                MsNotifyEvent error = new MsNotifyEventImpl(generator, EventID.FAIL, EventCause.FACILITY_FAILURE, ex.getMessage());
//                sendEvent(error);
            }
        }
    }

    private class PlayTx1 implements Runnable {

        private EventID signalID;
        private String[] params;
        private MsSignalGeneratorImpl generator;
        private String connID;

        public PlayTx1(MsSignalGeneratorImpl generator, EventID signalID, String connID,
                String[] params) {
            this.generator = generator;
            this.signalID = signalID;
            this.params = params;
            this.connID = connID;
        }

        public void run() {
            // if endpoint is not known yet
            if (endpoint == null) {
                try {
                    endpoint = EndpointQuery.find(endpointName);
                } catch (NamingException ex) {
//                    MsNotifyEvent error = new MsNotifyEventImpl(generator, EventID.FAIL, EventCause.FACILITY_FAILURE,
//                            ex.getMessage());
//                    sendEvent(error);
                } catch (ResourceUnavailableException ex) {
//                    MsNotifyEvent error = new MsNotifyEventImpl(generator, EventID.FAIL, EventCause.FACILITY_FAILURE,
//                            ex.getMessage());
//                    sendEvent(error);
                }
            }

            //TODO REBUILD THIS 
//            Options options = new Options();
            //options.add(AnnParams.URL, params[0]);
            if (params.length > 1) {
//                options.add("recorder.url", params[1]);
            }
            try {
//                endpoint.play(signalID.toString(), options, connID, generator);
            } catch (Exception ex) {
//                MsNotifyEvent error = new MsNotifyEventImpl(generator, EventID.FAIL, EventCause.FACILITY_FAILURE, ex.getMessage());
//                sendEvent(error);
            }
        }
    }
}
