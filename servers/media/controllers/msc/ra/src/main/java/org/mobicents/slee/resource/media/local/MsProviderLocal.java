/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.slee.resource.media.local;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsResourceListener;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSessionEvent;
import org.mobicents.mscontrol.MsSessionEventCause;
import org.mobicents.mscontrol.MsSessionEventID;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.slee.resource.media.ra.MediaResourceAdaptor;

/**
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public class MsProviderLocal implements MsProvider, MsSessionListener {

    private MsProvider provider;
    protected MediaResourceAdaptor ra;
    protected ConcurrentHashMap sessions = new ConcurrentHashMap();

    public MsProviderLocal(MsProvider provider, MediaResourceAdaptor ra) {
        this.provider = provider;
        this.ra = ra;
        provider.addNotificationListener(ra);
        provider.addSessionListener(this);
        provider.addConnectionListener(new MsConnectionEventProxy(this));
        provider.addLinkListener(new MsLinkEventProxy(this));

        provider.addResourceListener(ra);
    }

    public void addSessionListener(MsSessionListener listener) {
        throw new SecurityException("addSessionListener is unsupported. Use event handlers of SBB");
    }

    public void removeSessionListener(MsSessionListener listener) {
        throw new SecurityException("removeSessionListener is unsupported.");
    }

    public void addNotificationListener(MsNotificationListener listener) {
        throw new SecurityException("addNotificationListener is unsupported. Use event handlers of SBB");
    }

    public void removeNotificationListener(MsNotificationListener listener) {
        throw new SecurityException("removeNotificationListener is unsupported.");
    }

    public void addConnectionListener(MsConnectionListener connectionListener) {
        throw new SecurityException("addConnectionListener is unsupported. Use event handlers of SBB");
    }

    public void removeConnectionListener(MsConnectionListener listener) {
        throw new SecurityException("removeConnectionListener is unsupported.");
    }

    public void addResourceListener(MsResourceListener listener) {
        throw new SecurityException("addResourceListener is unsupported. Use event handlers of SBB");
    }

    public void addLinkListener(MsLinkListener listener) {
        throw new SecurityException("addLinkListener is unsupported. Use event handlers of SBB");
    }

    public void removeLinkListener(MsLinkListener listener) {
        throw new SecurityException("removeLinkListener is unsupported.");
    }

    public MsSession createSession() {
        MsSession s = provider.createSession();
        MsSessionLocal session = new MsSessionLocal(s, this);
        sessions.put(session.getId(), session);
        MsSessionEventLocal event = new MsSessionEventLocal(
                MsSessionEventID.SESSION_CREATED,
                MsSessionEventCause.SESSION_CREATED, 
                session);
        this.ra.sessionCreated(event);
        return session;
    }

    public MsConnection getMsConnection(String msConnectionId) {
        return provider.getMsConnection(msConnectionId);
    }

    public List<MsConnection> getMsConnections(String endpointName) {
        return provider.getMsConnections(endpointName);
    }

    public List<MsLink> getMsLinks(String endpointName) {
        return provider.getMsLinks(endpointName);
    }

    public void sessionCreated(MsSessionEvent evt) {
        /*        block.lock();
        try {
        MsSessionLocal session = new MsSessionLocal(evt.getSource(), this);
        sessions.put(session.getId(), session);
        
        MsSessionEventLocal event = new MsSessionEventLocal(evt, session);
        this.ra.sessionCreated(event);
        
        sessionActivityCreated.signal();
        } finally {
        block.unlock();
        }
         */
    }

    public void sessionActive(MsSessionEvent evt) {
        MsSession session = (MsSession) sessions.get(evt.getSource().getId());
        MsSessionEventLocal event = new MsSessionEventLocal(evt, session);
        this.ra.sessionActive(event);
    }

    public void sessionInvalid(MsSessionEvent evt) {
        MsSession session = (MsSession) sessions.remove(evt.getSource().getId());
        MsSessionEventLocal event = new MsSessionEventLocal(evt, session);
        this.ra.sessionInvalid(event);

    }

    public MsEventFactory getEventFactory() {
        return provider.getEventFactory();
    }
}
