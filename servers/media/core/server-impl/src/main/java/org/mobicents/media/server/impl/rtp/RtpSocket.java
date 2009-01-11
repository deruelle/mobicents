/*
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

package org.mobicents.media.server.impl.rtp;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;

/**
 * Provides RTP/RTCP implementaion for transmitting audio/video 
 * in a packet network.
 *
 * Note that RTP itself does not provide any mechanism to ensure timely
 * delivery or provide other quality-of-service guarantees, but relies
 * on lower-layer services to do so.  It does not guarantee delivery or
 * prevent out-of-order delivery, nor does it assume that the underlying
 * network is reliable and delivers packets in sequence.  The sequence
 * numbers included in RTP allow the receiver to reconstruct the
 * sender's packet sequence, but sequence numbers might also be used to
 * determine the proper location of a packet, for example in video
 * decoding, without necessarily decoding packets in sequence.
 *
 * @author Oleg Kulikov
 */
public interface RtpSocket extends Serializable {
    /**
     * Binds the RTP socket adapator to a specified address using any available
     * port number between minimum and maximum alllowed number.
     *
     * @param localAddress the address to bind to.
     * @param lowPort the mimimal allowed port number.
     * @param highPort the maximum allowed port number.
     * @return the actual used port;
     * @throws SocketException if any error happens durring the bind, 
     * or if socket already bound.
     */
    public int init(InetAddress localAddress, int lowPort, int highPort) throws SocketException;
    
    /**
     * Resets the RTP map to configured value.
     * 
     * This methods should be called when endpoint allocates RTPSocket.
     */
    public void resetRtpMap();
    
    /**
     * Gets the address to which this socket is bound.
     * 
     * @return the address to which this socket bound.
     */
    public String getLocalAddress();
    
    /**
     * Gets the actualy used port number.
     * 
     * @return port number.
     */
    public int getPort();
    
    /**
     * Modify packetization period.
     * 
     * @param period the new value of the packetization period in milliseconds.
     */
    public void setPeriod(int period);
    
    /**
     * Gets the currently used period of packetization.
     * 
     * @return period in milliseconds.
     */
    public int getPeriod();
    
    /**
     * Modify jitter buffer size.
     * 
     * @param jitter the size in milliseconds.
     */
    public void setJitter(int jitter);
    
    /**
     * Gets the size of jitter buffer.
     * 
     * @return the size of jitter buffer in milliseconds.
     */
    public int getJitter();
    
    /**
     * Sets the a peer to the RTP socket. 
     *
     * @param address the address of the peer.
     * @param port the port of the peer.
     */
    public void setPeer(InetAddress address, int port);
    
    /**
     * Gets list of registered formats.
     * 
     * @return the map where key is payload number and value id Format object.
     */
    public HashMap<Integer, Format> getRtpMap();
    
    /**
     * Modify map of registered payloads.
     * 
     * @param rtpMap the map where keys are paylioad types and values are formats
     */
    public void setRtpMap(HashMap<Integer, Format> rtpMap);
    
    /**
     * Gets the Receive stream.
     * 
     * @return ReceiveStream object
     */
    public MediaSource getReceiveStream();
    
    /**
     * Gets send stream object which controls transmission of the
     * RTP data to the participant.
     *
     * @param stream the data to sent out.
     */
    public SendStream getSendStream();
    
    /**
     * Closes this socket.
     */
    public void close();
    
}
