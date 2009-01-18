/*
 * TrunkManagement.java
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
package org.mobicents.media.server.impl.jmx;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.local.management.EndpointLocalManagement;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class TrunkManagement extends ServiceMBeanSupport implements TrunkManagementMBean {

    private String jndiName;
    private Integer channels = 0;
    private RtpFactory rtpFactory = null;
    
    private transient Logger logger = Logger.getLogger(EndpointManagement.class);
    
    /**
     * Creates a new instance of EndpointManagement
     */
    public TrunkManagement() {
    }

    /**
     * Gets the JNDI name to which this trunk instance is bound.
     * 
     * @return the JNDI name.
     */
    public String getJndiName() {
        return jndiName;
    }

    /**
     * Sets the JNDI name to which this trunk object should be bound.
     * 
     * @param jndiName
     *            the JNDI name to which trunk object will be bound.
     */
    public void setJndiName(String jndiName)  {
        String oldName = this.jndiName;
        this.jndiName = jndiName;

//        if (this.getState() == STARTED) {
//            InitialContext ic = new InitialContext();
//            for (int i = 0; i < channels; i++) {
//                BaseEndpoint endpoint = (BaseEndpoint) ic.lookup(oldName +"/" + i);
//                ic.unbind(oldName + "/" + i);
//                rebind(jndiName + "/" + i, endpoint);
//            }
//        }
    }

    /**
     * Gets the name of used RTP Factory.
     * 
     * @return the JNDI name of the RTP Factory
     */
    public RtpFactory getRtpFactory() {
        return this.rtpFactory;
    }

    /**
     * Sets the name of used RTP Factory.
     * 
     * @param rtpFactoryName
     *            the JNDI name of the RTP Factory.
     */
    public void setRtpFactory(RtpFactory rtpFactory) throws Exception {
        this.rtpFactory = rtpFactory;
//        if (this.getState() == STARTED) {
//            InitialContext ic = new InitialContext();
//            for (int i = 0; i < channels; i++) {
//                BaseEndpoint endpoint = (BaseEndpoint) ic.lookup(jndiName +"/" + i);
//                endpoint.setRtpFactoryName(rtpFactoryName);
//            }
//        } 
    }

    public Integer getChannels() {
        return this.channels;
    }

    public void setChannels(Integer channels) {
        this.channels = channels;
    }

    /**
     * Binds trunk object to the JNDI under the jndiName.
     */
/*    private void rebind(String theJndiName, Endpoint tmpEndpoint) throws NamingException {
        Context ctx = new InitialContext();

        String tokens[] = theJndiName.split("/");

        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].trim().length() > 0) {
                try {
                    ctx = (Context) ctx.lookup(tokens[i]);
                } catch (NamingException e) {
                    ctx = ctx.createSubcontext(tokens[i]);
                }
            }
        }

        ctx.bind(tokens[tokens.length - 1], tmpEndpoint);
    } */

    /**
     * Unbounds object under specified name.
     * 
     * @param jndiName
     *            the JNDI name of the object to be unbound.
     */
 /*   private void unbind(String jndiName) {
        try {
            InitialContext initialContext = new InitialContext();
            Endpoint enp = (Endpoint) initialContext.lookup(jndiName);
            enp.stop();
            initialContext.unbind(jndiName);
        } catch (NamingException e) {
            logger.error("Failed to unbind endpoint", e);
        }
    } */

    public abstract Endpoint createEndpoint(String localName) throws Exception;

    /**
     * Starts MBean.
     */
    @Override
    public void startService() throws Exception {
        logger.info("Starting trunk " + jndiName  + ", channels=" + channels);
        EndpointQuery endpointQuery = EndpointQuery.getInstance();
        for (int i = 0; i < channels; i++) {
            String name = jndiName + "/" + i;
            Endpoint enp = createEndpoint(name);
            
            ((BaseEndpoint) enp).setRtpFactory(this.rtpFactory);
            enp.start();
            
            endpointQuery.addEndpoint(enp.getLocalName(), enp);
            
            //rebind(enp.getLocalName(), enp);
            logger.info("Started endpoint: " + jndiName + "/" + i);
        }

    }

    /**
     * Stops MBean.
     */
    @Override
    public void stopService() {
        logger.info("Stoping trunk " + jndiName  + ", channels=" + channels);
        for (int i = 0; i < channels; i++) {
            EndpointQuery.getInstance().remove(jndiName + "/" + i);
            logger.info("Stopped endpoint: " + jndiName + "/" + i);
        }
    }

    // #########################
    // # MANAGEMENT OPERATIONS #
    // #########################
    private EndpointLocalManagement getEndpoint(String endpointName) throws IllegalArgumentException {
        return null;
    }

    public int getConnectionsCount(String endpointName) throws IllegalArgumentException {
        return getEndpoint(endpointName).getConnectionsCount();
    }

    public long getCreationTime(String endpointName) throws IllegalArgumentException {
        return getEndpoint(endpointName).getCreationTime();
    }

    public boolean getGatherPerformanceFlag(String endpointName) throws IllegalArgumentException {
        return getEndpoint(endpointName).getGatherPerformanceFlag();
    }

    public long getNumberOfBytes(String endpointName) throws IllegalArgumentException {
        return getEndpoint(endpointName).getNumberOfBytes();
    }

    public long getPacketsCount(String endpointName) throws IllegalArgumentException {
        return getEndpoint(endpointName).getPacketsCount();
    }

    public String[] getEndpointNames() throws IllegalArgumentException {

        return null;

    }

    public int getEndpointsCount() throws IllegalArgumentException {
        // This is just to have this info clearly visible in MBean view.
        String[] tmp = getEndpointNames();
        if (tmp == null) {
            return -1;
        } else {
            return tmp.length;
        }
    }

    public long getConnectionCreationTime(String endpoint, String connectionId) throws IllegalArgumentException {

        return getEndpoint(endpoint).getConnectionCreationTime(connectionId);

    }

    public String[] getConnectionIds(String endpointName) throws IllegalArgumentException {
        return getEndpoint(endpointName).getConnectionIds();
    }

    public String getConnectionLocalSDP(String endpoint, String connectionId) throws IllegalArgumentException {
        return getEndpoint(endpoint).getConnectionLocalSDP(connectionId);
    }

    public String getConnectionRemoteSDP(String endpoint, String connectionId) throws IllegalArgumentException {
        return getEndpoint(endpoint).getConnectionRemoteSDP(connectionId);
    }

    public String getOtherEnd(String endpoint, String connectionId) throws IllegalArgumentException {

        return getEndpoint(endpoint).getOtherEnd(connectionId);
    }

    public String getConnectionMode(String endpointName, String connectionId) throws IllegalArgumentException {
        return getEndpoint(endpointName).getConnectionMode(connectionId);
    }

    public String getConnectionState(String endpointName, String connectionId) throws IllegalArgumentException {
        return getEndpoint(endpointName).getConnectionState(connectionId);
    }

    public String getRTPFacotryJNDIName(String endpointName) throws IllegalArgumentException {
        return getEndpoint(endpointName).getRTPFacotryJNDIName();
    }

    public void setGatherPerformanceData(String endpointName, boolean value) throws IllegalArgumentException {
    }

    public void setRTPFacotryJNDIName(String endpointName, String jndiName) throws IllegalArgumentException {
        EndpointLocalManagement elm = getEndpoint(endpointName);
        elm.setRTPFacotryJNDIName(jndiName);
    }

    public void destroyConnection(String name, String connectionId) throws IllegalArgumentException {
        BaseEndpoint elm = (BaseEndpoint) getEndpoint(name);
        try {
            elm.deleteConnection(connectionId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Connection does not exist?");
        }
    }

    public void destroyEndpoint(String name) throws IllegalArgumentException {
    }

    public String getTrunkName() {
        return jndiName;
    }

    public int getInterArrivalJitter(String endpointName, String connectionId) throws IllegalArgumentException {
        EndpointLocalManagement elm = getEndpoint(endpointName);

        return 0;//return elm.getInterArrivalJitter(connectionId);
    }

    public int getOctetsReceived(String endpointName, String connectionId) throws IllegalArgumentException {
        EndpointLocalManagement elm = getEndpoint(endpointName);

        return 0;//return elm.getOctetsReceived(connectionId);
    }

    public int getOctetsSent(String endpointName, String connectionId) throws IllegalArgumentException {
        EndpointLocalManagement elm = getEndpoint(endpointName);

        return 0;//return elm.getOctetsSent(connectionId);
    }

    public int getPacketsLost(String endpointName, String connectionId) throws IllegalArgumentException {
        EndpointLocalManagement elm = getEndpoint(endpointName);

        return 0;//return elm.getPacketsLost(connectionId);
    }

    public int getPacketsReceived(String endpointName, String connectionId) throws IllegalArgumentException {
        EndpointLocalManagement elm = getEndpoint(endpointName);

        return 0;//return elm.getPacketsReceived(connectionId);
    }

    public int getPacketsSent(String endpointName, String connectionId) throws IllegalArgumentException {
        EndpointLocalManagement elm = getEndpoint(endpointName);
        return 0;//return elm.getPacketsSent(connectionId);
    }

    // ////////////////
    // // STOP/START //
    // ////////////////
    public void startPlatform() {
    }

    public void stopPlatform() {
    }

    public void tearDownPlatform() {
        this.stopPlatform();
        for (String name : this.getEndpointNames()) {
            this.destroyEndpoint(name);
        }

    }
}
