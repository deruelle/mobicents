package org.mobicents.media.server.impl.jmx.enp.test;


import org.mobicents.media.server.impl.enp.test.LoopEndpointImpl;
import org.mobicents.media.server.impl.jmx.TrunkManagement;
import org.mobicents.media.server.spi.Endpoint;

public class LoopTrunkManagement extends TrunkManagement implements LoopTrunkManagementMBean {


    public Endpoint createEndpoint(String name) throws Exception {
        LoopEndpointImpl endpoint = new LoopEndpointImpl(name);
        return endpoint;
    }



}
