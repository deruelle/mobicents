/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.local;

import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkListener;

/**
 *
 * @author Oleg Kulikov
 */
public class MsLinkEventProxy implements MsLinkListener {

    private MsProviderLocal provider;
    
    protected MsLinkEventProxy(MsProviderLocal provider) {
        this.provider = provider;
    }
    
    public void linkCreated(MsLinkEvent evt) {
        MsSessionLocal session = (MsSessionLocal) provider.sessions.get(evt.getSource().getSession().getId());
        MsLink link = new MsLinkLocal(session, evt.getSource());
        session.links.put(link.getId(), link);
        
        
        MsLinkEventLocal event = new MsLinkEventLocal(evt, link);
        for (MsLinkListener listener : provider.linkListeners) {
            listener.linkCreated(event);
        }
        session.linkActivityCreated();
    }

    public void linkConnected(MsLinkEvent evt) {
        MsSessionLocal session = (MsSessionLocal) provider.sessions.get(evt.getSource().getSession().getId());
        MsLink link = (MsLink) session.links.get(evt.getSource().getId());
        MsLinkEventLocal event = new MsLinkEventLocal(evt, link);
        for (MsLinkListener listener : provider.linkListeners) {
            listener.linkConnected(event);
        }
    }

    public void linkDisconnected(MsLinkEvent evt) {
        MsSessionLocal session = (MsSessionLocal) provider.sessions.get(evt.getSource().getSession().getId());
        MsLink link = (MsLink) session.links.remove(evt.getSource().getId());
        MsLinkEventLocal event = new MsLinkEventLocal(evt, link);
        for (MsLinkListener listener : provider.linkListeners) {
            listener.linkDisconnected(event);
        }
    }

    public void linkFailed(MsLinkEvent evt) {
        MsSessionLocal session = (MsSessionLocal) provider.sessions.get(evt.getSource().getSession().getId());
        MsLink link = (MsLink) session.links.get(evt.getSource().getId());
        MsLinkEventLocal event = new MsLinkEventLocal(evt, link);
        for (MsLinkListener listener : provider.linkListeners) {
            listener.linkFailed(event);
        }
    }

}
