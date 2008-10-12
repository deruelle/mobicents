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
package org.mobicents.media.server.impl.enp.test;

import java.util.HashMap;
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
public class LoopEndpointImpl extends BaseVirtualEndpoint implements ConnectionListener{


    public LoopEndpointImpl(String localName, HashMap<String, Endpoint> endpointsMap) {
        super(localName,endpointsMap);
        this.setMaxConnectionsAvailable(1);
        addConnectionListener(this);
    }

    public void onStateChange(Connection connection, ConnectionState oldState) {
        BaseConnection con = (BaseConnection) connection;
        switch (connection.getState()) {
            case OPEN :
//                con.getDemux().connect(new TestSink());
                con.getMux().connect(con.getDemux());
                break;
            case CLOSED :
                con.getMux().disconnect(con.getDemux());
                break;
        }
    }

    @Override
    public Endpoint doCreateEndpoint(String localName) {
        return new LoopEndpointImpl(localName,super.endpoints);
    }

    @Override
    public HashMap initMediaSources() {
        return new HashMap();
    }

    @Override
    public HashMap initMediaSinks() {
        return new HashMap();
    }

	public int getConnectionsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getCreationTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getGatherPerformanceFlag() {
		// TODO Auto-generated method stub
		return false;
	}

	public long getNumberOfBytes() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getPacketsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setGatherPerformanceFlag(boolean flag) {
		// TODO Auto-generated method stub
		
	}

    

}
