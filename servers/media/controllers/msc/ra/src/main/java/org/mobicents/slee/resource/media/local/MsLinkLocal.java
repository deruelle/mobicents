/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.local;

import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.mscontrol.MsLinkState;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsSession;

/**
 *
 * @author Oleg Kulikov
 */
public class MsLinkLocal implements MsLink {

    private MsSessionLocal session;
    private MsEndpointLocal[] endpoints = new MsEndpointLocal[2];
    protected MsLink link;
    
    protected MsLinkLocal(MsSessionLocal session, MsLink link) {
        this.session = session;
        this.link = link;
        endpoints[0] = new MsEndpointLocal(link.getEndpoints()[0]);
        endpoints[1] = new MsEndpointLocal(link.getEndpoints()[1]);
    }
    
    
    public String getId() {
        return link.getId();
    }

    public MsLinkState getState() {
        return link.getState();
    }

    public MsLinkMode getMode() {
        return link.getMode();
    }

    public void setMode(MsLinkMode mode) {
        link.setMode(mode);
    }
    
    public MsSession getSession() {
        return session;
    }

    public void join(String a, String b) {
        link.join(a, b);
    }

    @Override
    public String toString() {
        return link.toString();
    }
    
    public MsEndpoint[] getEndpoints() {
        endpoints[0] = new MsEndpointLocal(link.getEndpoints()[0]);
        endpoints[1] = new MsEndpointLocal(link.getEndpoints()[1]);
        return endpoints;
    }

    public void release() {
        link.release();
    }

    public void addLinkListener(MsLinkListener listener) {
        session.getProvider().addLinkListener(listener);
    }

    public void removeLinkListener(MsLinkListener listener) {
        session.getProvider().removeLinkListener(listener);
    }

    public void addNotificationListener(MsNotificationListener listener) {
		throw new SecurityException("method is unsupported.");
    }

    public void removeNotificationListener(MsNotificationListener listener) {
		throw new SecurityException("method is unsupported.");
    }


}
