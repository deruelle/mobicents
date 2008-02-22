/*
 * File Name     : JainMgcpStackImpl.java
 *
 * The JAIN MGCP API implementaion.
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

package org.mobicents.mgcp;

import jain.protocol.ip.mgcp.CreateProviderException;
import jain.protocol.ip.mgcp.DeleteProviderException;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.JainMgcpStack;
import jain.protocol.ip.mgcp.OAM_IF;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class JainMgcpStackImpl extends Thread implements JainMgcpStack, OAM_IF {
    
    private String protocolVersion = "1.0";
    protected int port = 2727;
    
    private DatagramSocket socket;
    protected JainMgcpProviderImpl provider;
    
    private boolean stopped = true;
    
    private Logger logger = Logger.getLogger(JainMgcpStackImpl.class);
    private ExecutorService pool = Executors.newFixedThreadPool(5);
    
    protected HashMap transactions = new HashMap();
    protected int GENERATOR = 1;
    
    /** Creates a new instance of JainMgcpStackImpl */
    public JainMgcpStackImpl() {
    }
    
    public JainMgcpProvider createProvider() throws CreateProviderException {
        if (socket == null) {
            try {
                socket = new DatagramSocket(port);
                logger.debug("Bound to local UDP port " + port);
            } catch (SocketException e) {
                logger.error("Failed to bound to local port " + port + ". Caused by", e);
                throw new CreateProviderException(e.getMessage());
            }
        }
        
        provider = new JainMgcpProviderImpl();
        provider.stack = this;
        
        stopped = false;
        
        logger.debug("Starting main thread " + this);
        start();
        return provider;
    }
    
    public void deleteProvider(JainMgcpProvider provider) throws DeleteProviderException {
        provider = null;
        stopped = true;
        try {
            logger.debug("Closing socket");
            socket.close();
        } catch (Exception e) {
            logger.warn("Could not gracefully close socket", e);
        }
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getProtocolVersion() {
        return protocolVersion;
    }
    
    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
    
    protected synchronized void send(DatagramPacket packet) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Sending " + packet.getLength() + " bytes to " +
                        packet.getAddress());
            }
            socket.send(packet);
        } catch (IOException e) {
            logger.error("I/O Exception uccured, caused by", e);
        }
    }
    
    public boolean isRequest(String header) {
        return header.matches("[\\w]{4}(\\s|\\S)*");
    }
    
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("MGCP stack started successfully on " +
                    socket.getLocalAddress() + ":" + socket.getLocalPort());
        }
        
        byte[] buffer = new byte[86400];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        while (!stopped) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Waiting for packet delivery");
                }
                socket.receive(packet);
            } catch (IOException e) {
                if (stopped) break;
                logger.error("I/O exception occured:", e);
                continue;
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("Receive " + packet.getLength() + " bytes from " +
                        packet.getAddress() + ":" + packet.getPort());
            }
            
            // uses now the actual data length from the DatagramPacket
            // instead of the length of the  byte[] buffer
            byte[] data = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), 0, data, 0, data.length);
            
            MessageHandler handler = new MessageHandler(this, data,
                    packet.getAddress(), packet.getPort());
            
            pool.execute(handler);
            
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("MGCP stack stopped gracefully");
        }
    }
}
