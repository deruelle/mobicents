/*
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

package org.mobicents.media.server.impl.jmx.rtp;

import java.net.UnknownHostException;
import javax.naming.NamingException;
import org.jboss.system.ServiceMBean;

/**
 *
 * @author Oleg Kulikov
 */
public interface RTPManagerMBean extends ServiceMBean {
    
    /**
     * Gets the JNDI name to which RTP Factory is boind.
     *
     * @return the JNDI name.
     */
    public String getJndiName();

    /**
     * Sets the JNDI name to which RTF Factory should be bound.
     *
     * @param jndiName the JNDI name to which trunk object will be bound.
     */
    public void setJndiName(String jndiName) throws NamingException;
    
    /**
     * Gets the IP address to which trunk is bound.
     * All endpoints of the trunk use this address for RTP connection.
     *
     * @return the IP address string to which this trunk is bound.
     */
    public String getBindAddress();
    
    /**
     * Modify the bind address.
     * All endpoints of the trunk use this address for RTP connection.
     *
     * @param the IP address string.
     */
    public void setBindAddress(String bindAddress) throws UnknownHostException;
    
   /**
    * Gets packetization period.
    * 
    * The packetization period is the period over which encoded voice bits 
    * are collected for encapsulation in packets.
    * 
    * Higher period will reduce VoIP overhead allowing higher channel utilization
    * 
    * @return packetion period for media in milliseconds.
    */
    public Integer getPacketizationPeriod();
    
    /**
     * Modify packetization period.
     * 
     * The packetization period is the period over which encoded voice bits 
     * are collected for encapsulation in packets.
     * 
     * Higher period will reduce VoIP overhead allowing higher channel utilization
     * 
     * @param period the value of the packetization period in milliseconds.
     */
    public void setPacketizationPeriod(Integer period);
    
    /**
     * Gets the size of the jitter buffer in milliseconds.
     * 
     * Jitter buffer is used at the receiving ends of a VoIP connection. 
     * A jitter buffer stores received, time-jittered VoIP packets, that arrive 
     * within its time window. It then plays stored packets out, in sequence, 
     * and at a constant rate for subsequent decoding. A jitter buffer is
     * typically filled half-way before playing out packets to allow early, 
     * or late, packet-arrival jitter compensation.
     * 
     * Choosing a large jitter buffer reduces packet dropping from jitter but
     * increases VoIP path delay
     * 
     * @return the size of the buffer in milliseconds.
     */
    public Integer getJitter();
    
    /**
     * Modify size of the jitter buffer.
     * 
     * Jitter buffer is used at the receiving ends of a VoIP connection. 
     * A jitter buffer stores received, time-jittered VoIP packets, that arrive 
     * within its time window. It then plays stored packets out, in sequence, 
     * and at a constant rate for subsequent decoding. A jitter buffer is
     * typically filled half-way before playing out packets to allow early, 
     * or late, packet-arrival jitter compensation.
     * 
     * Choosing a large jitter buffer reduces packet dropping from jitter but
     * increases VoIP path delay
     * 
     * @param jitter the new buffer's size in milliseconds
     */
    public void setJitter(Integer jitter);
    
    /**
     * Gets the range of the available port used for RTP connections.
     * 
     * @return the range of available port in the follwing format: low-high
     */
    public String getPortRange();
    
    /**
     * Sets the range of the available port used for RTP connections.
     * 
     * @param portRange string indicating range in the follwing format: low-high
     */
    public void setPortRange(String portRange);  
    
    /**
     * Gets the STUN server address.
     * 
     * @return the address of the server as string.
     */
    public String getStunServerAddress();

    /**
     * Modify STUN server address.
     * 
     * @param stunServerAddress the valid address of the server
     */
    public void setStunServerAddress(String stunServerAddress);

    /**
     * Gets the port of the STUN server.
     * 
     * @return the port number.
     */
    public int getStunServerPort();

    /**
     * Configures port of the STUN server.
     * 
     * @param stunServerPort port number.
     */
    public void setStunServerPort(int stunServerPort);

    /**
     * Shows does STUN enabled.
     * 
     * @return true if STUN is used.
     */
    public boolean isUseStun();

    /**
     * Enables/disables STUN usage.
     * 
     * @param useStun true to use STUN or false otherwise.
     */
    public void setUseStun(boolean useStun);

    /**
     * Shows does NAT uses port mapping.
     * 
     * @return true if NAT uses port mapping.
     */
    public boolean isUsePortMapping();

    /**
     * Configures NAT traversal procedure.
     *  
     * @param usePortMapping true if NAT uses port mapping.
     */
    public void setUsePortMapping(boolean usePortMapping);

    public String getPublicAddressFromStun();
    public void setPublicAddressFromStun(String publicAddressFromStun);
    
    /**
     * Gets the list of registered audio formats.
     * 
     * @return the string in the following format:
     *      <payload>
     *           <!-- payload number -->
     *           <rtpmap>8</rtpmap> 
     *           <!-- format description -->
     *           <format>ALAW,8000, 8, 1</format>
     *       </payload>
     *       <payload>
     *           <rtpmap>0</rtpmap>
     *           <format>ULAW,8000, 8, 1</format>
     *       </payload>
     * 
     */
    public String getAudioFormats();
    
    /**
     * Sets the list of registered audio formats.
     * 
     * @param formats the string in the following format:
     *      <payload>
     *           <!-- payload number -->
     *           <rtpmap>8</rtpmap> 
     *           <!-- format description -->
     *           <format>ALAW,8000, 8, 1</format>
     *       </payload>
     *       <payload>
     *           <rtpmap>0</rtpmap>
     *           <format>ULAW,8000, 8, 1</format>
     *       </payload>
     * 
     */
    public void setAudioFormats(String formats);
    
    /**
     * Gets the list of registered video formats.
     * 
     * @return the string in the following format:
     *      <payload>
     *           <!-- payload number -->
     *           <rtpmap>8</rtpmap> 
     *           <!-- format description -->
     *           <format>H261/90000</format>
     *       </payload>
     *       <payload>
     *           <rtpmap>0</rtpmap>
     *           <format>H261/90000</format>
     *       </payload>
     * 
     */
    public String getVideoFormats();
    
    /**
     * Sets the list of registered audio formats.
     * 
     * @param formats the string in the following format:
     *      <payload>
     *           <!-- payload number -->
     *           <rtpmap>8</rtpmap> 
     *           <!-- format description -->
     *           <format>H261/90000</format>
     *       </payload>
     *       <payload>
     *           <rtpmap>0</rtpmap>
     *           <format>H261/90000</format>
     *       </payload>
     * 
     */
    public void setVideoFormats(String formats);
    
}
