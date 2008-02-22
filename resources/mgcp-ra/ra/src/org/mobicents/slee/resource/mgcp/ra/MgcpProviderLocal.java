/*
 * MgcpProviderLocal.java
 *
 * Created on 26 Март 2007 г., 19:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.mgcp.ra;

import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpListener;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.JainMgcpStack;
import java.util.TooManyListenersException;

/**
 *
 * @author Oleg Kulikov
 */
public class MgcpProviderLocal implements JainMgcpProvider {
    
    private JainMgcpProvider provider;
    private MgcpResourceAdaptor ra;
    
    /**
     * Creates a new instance of MgcpProviderLocal
     */
    public MgcpProviderLocal(MgcpResourceAdaptor ra, JainMgcpProvider provider) {
        this.provider = provider;
        this.ra = ra;
    }

    public void addJainMgcpListener(JainMgcpListener jainMgcpListener) 
    throws TooManyListenersException {
        //throw TooManyListenersException("Sbb could not add listeners");
    }

    public void removeJainMgcpListener(JainMgcpListener jainMgcpListener) {
    }

    public JainMgcpStack getJainMgcpStack() {
        return provider.getJainMgcpStack();
    }

    public void sendMgcpEvents(JainMgcpEvent[] events) throws IllegalArgumentException {
        for (int i = 0; i < events.length; i++) {
            ra.send(events[i]);
        }
    }
    
}
