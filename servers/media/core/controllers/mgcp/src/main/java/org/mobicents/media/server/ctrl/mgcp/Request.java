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
package org.mobicents.media.server.ctrl.mgcp;

import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.mobicents.media.server.ctrl.mgcp.evt.EventDetector;
import org.mobicents.media.server.ctrl.mgcp.evt.SignalGenerator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author kulikov
 */
public class Request implements Runnable, ConnectionListener {

    private RequestIdentifier reqID;
    private MgcpController controller;
    
    private Endpoint endpoint;    
    private ArrayList<Connection> connections = new ArrayList<Connection>();
    
    private NotifiedEntity notifiedEntity;

    private HashMap<String, List<EventDetector>> connectionDetectors = new HashMap<String, List<EventDetector>>();
    private HashMap<String, List<SignalGenerator>> connectionGenerators = new HashMap<String, List<SignalGenerator>>();
    
    private ArrayList<EventDetector> endpointDetectors = new ArrayList<EventDetector>();
    private ArrayList<SignalGenerator> endpointGenerators = new ArrayList<SignalGenerator>();

    private HashMap<String, Iterator<SignalGenerator>> connectionSignalQueue = new HashMap<String, Iterator<SignalGenerator>>();
    private Iterator<SignalGenerator> endpointSignalQueue;
    
    private HashMap<String, SignalGenerator> activeConnectionSignals = new HashMap<String, SignalGenerator>();
    private SignalGenerator activeEndpointSignal;
    
    public Request(MgcpController controller, RequestIdentifier reqID,
            Endpoint endpoint, NotifiedEntity notifiedEntity) {
        this.controller = controller;
        this.reqID = reqID;
        this.endpoint = endpoint;
        this.notifiedEntity = notifiedEntity;
    }

    public void append(EventDetector detector, Connection connection) {
        if (connection == null) {
            endpointDetectors.add(detector);
            return;
        }

        if (connectionDetectors.containsKey(connection.getId())) {
            connectionDetectors.get(connection.getId()).add(detector);
        } else {
            ArrayList<EventDetector> list = new ArrayList();
            list.add(detector);
            connectionDetectors.put(connection.getId(), list);
            if (!connections.contains(connection)) {
                connection.addListener(this);
                connections.add(connection);
            }
        }
    }

    public void append(SignalGenerator generator, Connection connection) {

        if (connection == null) {
            endpointGenerators.add(generator);
            return;
        }

        if (connectionGenerators.containsKey(connection.getId())) {
            connectionGenerators.get(connection.getId()).add(generator);
        } else {
            ArrayList<SignalGenerator> list = new ArrayList();
            list.add(generator);
            connectionGenerators.put(connection.getId(), list);
            if (!connections.contains(connection)) {
                connection.addListener(this);
                connections.add(connection);
            }
        }
    }

    public void run() {
        //starting detectors on connections
        Collection<List<EventDetector>> detItems = connectionDetectors.values();
        for (List<EventDetector> item : detItems) {
            for (EventDetector det: item) {
                det.start();
            }
        }        
        
        //starting detectors on endpoint
        for (EventDetector  det: endpointDetectors) {
            det.start();
        }        
        
        //starting first signal from queue for each connection
        Set<String> connectionIDs = connectionSignalQueue.keySet();
        for (String connectionID : connectionIDs) {
            Iterator<SignalGenerator> queue = connectionSignalQueue.get(connectionID);
            SignalGenerator gen = queue.next();
            activeConnectionSignals.put(connectionID, gen);
            gen.start(this);
        }
        
        //starting first signal for endpoint
        activeEndpointSignal = endpointSignalQueue.next();
        activeEndpointSignal.start(this);
    }

    public void cancel() {
        //stopping detectors on connections
        Collection<List<EventDetector>> detItems = connectionDetectors.values();
        for (List<EventDetector> item : detItems) {
            for (EventDetector det: item) {
                det.stop();
            }
        }        
        
        //starting detectors on endpoint
        for (EventDetector  det: endpointDetectors) {
            det.stop();
        }        
        
        //starting first signal from queue for each connection
        Collection<SignalGenerator> signals = activeConnectionSignals.values();
        for (SignalGenerator signal: signals) {
            signal.cancel();
        }
        
        activeEndpointSignal.cancel();
        
        this.connections.clear();
        this.endpointDetectors.clear();
        this.endpointGenerators.clear();
        
        this.connectionDetectors.clear();
        this.connectionGenerators.clear();
        
        this.activeConnectionSignals.clear();
    }
    
    private boolean verifyDetectors(Connection connection) {
        if (!connectionDetectors.containsKey(connection.getId())) {
            return true;
        }
        
        Iterator<EventDetector> list = connectionDetectors.get(connection.getId()).iterator();
        boolean res = true;
        
        while (res && list.hasNext()) {
            res = res & list.next().verify(connection);
        }
        
        return res;
    }

    private boolean verifyGenerators(Connection connection) {

        if (!connectionGenerators.containsKey(connection.getId())) {
            return true;
        }

        Iterator<SignalGenerator> list = connectionGenerators.get(connection.getId()).iterator();
        boolean res = true;

        while (res && list.hasNext()) {
            res = res & list.next().verify(connection);
        }

        if (res) {
            connectionSignalQueue.put(connection.getId(), connectionGenerators.get(connection.getId()).iterator());
        }

        return res;
    }
    
    public boolean verifyDetectors() {
        Iterator<Connection> list = connections.iterator();
        boolean res = true;
        while (res && list.hasNext()) {
            res = res & this.verifyDetectors(list.next());
        }
        
        Iterator <EventDetector> list2 = endpointDetectors.iterator();
        while (res && list2.hasNext()) {
            res = res & list2.next().verify(endpoint);
        }
        
        return res;
    }

    public boolean verifyGenerators() {

        Iterator<Connection> list = connections.iterator();

        boolean res = true;
        while (res && list.hasNext()) {
            res = res & this.verifyGenerators(list.next());
        }

        Iterator <SignalGenerator> list2 = endpointGenerators.iterator();
        while (res && list2.hasNext()) {
            res = res & list2.next().verify(endpoint);
        }

        endpointSignalQueue = endpointGenerators.iterator();
    
        return res;
    }

    public void onStateChange(Connection connection, ConnectionState oldState) {
        if (connection.getState() != ConnectionState.CLOSED) {
            return;
        }
        
        //shutdown signal queue
        connectionSignalQueue.remove(connection.getId());
        
        //shutdown signal generaot list        
        connectionGenerators.remove(connection.getId());        
        
        //terminate current signal        
        SignalGenerator gen = activeConnectionSignals.get(connection.getId());
        if (gen != null) {
            gen.cancel();
        }
        
        //disable detectors if assigned
        connectionDetectors.remove(connection.getId());
        
        //remove connection instance if present
        connections.remove(connection);
        System.out.println("====== REMOVED CONNECTION ===");
    }

    
    public void onModeChange(Connection connection, ConnectionMode oldMode) {
    }

}
