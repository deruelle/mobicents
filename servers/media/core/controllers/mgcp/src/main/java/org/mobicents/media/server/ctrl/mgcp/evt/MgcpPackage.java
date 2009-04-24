/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.ctrl.mgcp.evt;

import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import java.util.List;

/**
 *
 * @author kulikov
 */
public class MgcpPackage {
    
    private String name;
    private int id;

    private List<MgcpSignalFactory> signals;
    private List<MgcpEventFactory> events;
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MgcpSignalFactory> getSignals() {
        return signals;
    }

    public void setSignals(List<MgcpSignalFactory> signals) {
        this.signals = signals;
    }

    public List<MgcpEventFactory> getEvents() {
        return events;
    }

    public void setEvents(List<MgcpEventFactory> events) {
        this.events = events;
    }
        
    public MgcpSignal getSignal(MgcpEvent evt) {
        for (MgcpSignalFactory factory : signals) {
            if (factory.getName().equals(evt.getName())) {
                return factory.getInstance(evt.getParms());
            }
        }
        return null;
    }
}
