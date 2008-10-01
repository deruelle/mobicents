/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.slee.resource.media.local;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsResourceListener;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSessionEvent;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.mscontrol.MsSignalDetector;
import org.mobicents.mscontrol.MsSignalGenerator;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.slee.resource.media.ra.MediaResourceAdaptor;

/**
 *
 * @author Oleg Kulikov
 */
public class MsProviderLocal implements MsProvider, MsSessionListener {

    private MsProvider provider;
    private MediaResourceAdaptor ra;
    protected ArrayList<MsSessionListener> sessionListeners = new ArrayList();
    protected ArrayList<MsConnectionListener> connectionListeners = new ArrayList();
    protected ArrayList<MsLinkListener> linkListeners = new ArrayList();
    protected ConcurrentHashMap sessions = new ConcurrentHashMap();
    private Boolean blockState = false;
    private ReentrantLock block = new ReentrantLock();
    private Condition sessionActivityCreated = block.newCondition();

    public MsProviderLocal(MsProvider provider, MediaResourceAdaptor ra) {
        this.provider = provider;
        this.ra = ra;
        provider.addSessionListener(this);
        provider.addConnectionListener(new MsConnectionEventProxy(this));
        provider.addLinkListener(new MsLinkEventProxy(this));
    }

    public void addSessionListener(MsSessionListener listener) {
        sessionListeners.add(listener);
    }

    public void removeSessionListener(MsSessionListener listener) {
        sessionListeners.remove(listener);
    }

    public void addNotificationListener(MsNotificationListener listener) {
        provider.addNotificationListener(listener);
    }

    public void removeNotificationListener(MsNotificationListener listener) {
        provider.removeNotificationListener(listener);
    }

    public void addConnectionListener(MsConnectionListener connectionListener) {
        connectionListeners.add(connectionListener);
    }

    public void removeConnectionListener(MsConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    public void addResourceListener(MsResourceListener listener) {
        provider.addResourceListener(listener);
    }

    public void addLinkListener(MsLinkListener listener) {
        System.out.println("ADD LINK LISTENER: " + listener);
        linkListeners.add(listener);
    }

    public void removeLinkListener(MsLinkListener listener) {
        linkListeners.remove(listener);
    }

    public MsSession createSession() {
        block.lock();
        try {
            MsSession session = provider.createSession();
            while (!sessions.containsKey(session.getId())) {
                try {
                    System.out.println("AWAIT****");
                    sessionActivityCreated.await();
                    System.out.println("RELEASEd****");
                } catch (InterruptedException e) {
                }
            }
            System.out.println("UNLOCK****");
            return (MsSession) sessions.get(session.getId());
        } finally {
            block.unlock();
        }
    }

    public MsSignalGenerator getSignalGenerator(String endpointName) {
        return provider.getSignalGenerator(endpointName);
    }

    public MsSignalDetector getSignalDetector(String endpointName) {
        return provider.getSignalDetector(endpointName);
    }

    public MsConnection getMsConnection(String msConnectionId) {
        return provider.getMsConnection(msConnectionId);
    }

    public List<MsConnection> getMsConnections(String endpointName) {
        return provider.getMsConnections(endpointName);
    }

    public void sessionCreated(MsSessionEvent evt) {
        block.lock();
        try {
            MsSessionLocal session = new MsSessionLocal(evt.getSource(), this);
            sessions.put(session.getId(), session);

            MsSessionEventLocal event = new MsSessionEventLocal(evt, session);
            for (MsSessionListener listener : sessionListeners) {
                listener.sessionCreated(event);
            }

            System.out.println("SIGNAL****");
            sessionActivityCreated.signalAll();
        } finally {
            block.unlock();
        }
    }

    public void sessionActive(MsSessionEvent evt) {
        MsSession session = (MsSession) sessions.get(evt.getSource().getId());
        MsSessionEventLocal event = new MsSessionEventLocal(evt, session);
        for (MsSessionListener listener : sessionListeners) {
            listener.sessionActive(event);
        }
    }

    public void sessionInvalid(MsSessionEvent evt) {
        MsSession session = (MsSession) sessions.remove(evt.getSource().getId());
        MsSessionEventLocal event = new MsSessionEventLocal(evt, session);
        for (MsSessionListener listener : sessionListeners) {
            listener.sessionInvalid(event);
        }
    }

    private void resume() {
        sessionActivityCreated.signalAll();
    }

    public MsEventFactory getEventFactory() {
        return provider.getEventFactory();
    }
}
