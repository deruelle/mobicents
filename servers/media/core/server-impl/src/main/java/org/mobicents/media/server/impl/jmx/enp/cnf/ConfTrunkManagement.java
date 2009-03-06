package org.mobicents.media.server.impl.jmx.enp.cnf;


import org.mobicents.media.server.impl.enp.cnf.ConfEndpointImpl;
import org.mobicents.media.server.impl.jmx.TrunkManagement;
import org.mobicents.media.server.spi.Endpoint;

public class ConfTrunkManagement extends TrunkManagement implements ConfTrunkManagementMBean {


    @Override
    public Endpoint createEndpoint(String name) throws Exception {

        return new ConfEndpointImpl(name);
    }
}
