/*
 * PREndpointImpl.java
 *
 * Mobicents Media Gateway
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
package org.mobicents.media.server.impl.enp.prl;

import java.util.HashMap;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseVirtualEndpoint;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author Oleg Kulikov
 */
public class PREndpointImpl extends BaseVirtualEndpoint implements ConnectionListener {

    private transient Logger logger = Logger.getLogger(PREndpointImpl.class);

    /**
     * Creates a new instance of PREndpointImpl
     * @param endpointsMap 
     */
    public PREndpointImpl(String localName, HashMap<String, Endpoint> endpointsMap) {
        super(localName,endpointsMap);
        this.setMaxConnectionsAvailable(2);
        addConnectionListener(this);
    }

    public void onStateChange(Connection connection, ConnectionState oldState) {
        if (this.getConnections().size() == 2) {
            BaseConnection[] connections = new BaseConnection[2];
            this.getConnections().toArray(connections);

           
//            if (connections[1] instanceof LocalConnectionImpl) {
//                System.out.println("!!!!!!! Assigned test sink");
//                connections[1].getDemux().connect(new TestSink());
//            } else {
                connections[1].getDemux().connect(connections[0].getMux());
//            }
//            if (connections[0] instanceof LocalConnectionImpl) {
//                System.out.println("!!!!!!! Assigned test sink");
//                connections[0].getDemux().connect(new TestSink());
//            } else {
                connections[0].getDemux().connect(connections[1].getMux());
//            }
        }
    }

    @Override
    public Endpoint doCreateEndpoint(String localName) {
        return new PREndpointImpl(localName,super.endpoints);
    }

    @Override
    public HashMap initMediaSources() {
        return new HashMap();
    }

    @Override
    public HashMap initMediaSinks() {
        return new HashMap();
    }

	
}
