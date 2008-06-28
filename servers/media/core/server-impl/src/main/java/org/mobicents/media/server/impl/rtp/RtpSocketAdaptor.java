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

import java.net.InetAddress;
import java.net.SocketException;
import org.mobicents.media.Format;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;

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
public interface RtpSocketAdaptor {
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
     * Starts packet receiver.
     *
     * If peer list may be empty, it does not know whether the packets that 
     * it receives have been authorized by the server. It must thus navigate 
     * between two risks, i.e. clipping some important announcements or 
     * listening to insane data. Server uses this method to start receiver
     * handler upon request.
     */
    public void start();
    
    /**
     * Stops receiver handler.
     *
     * This method does not close RTP Socket adaptor.
     */
    public void stop();
    
    /**
     * Sets the a peer to the RTP socket. 
     *
     * @param address the address of the peer.
     * @param port the port of the peer.
     */
    public void setPeer(InetAddress address, int port);
    
    /**
     * This method is used to add a dynamic payload.
     *
     * @param int payload type.
     * @param format the format for specified payload type.
     */
    public void addFormat(int pt, Format format);
    
    /**
     * Creates new send stream object which controls transmission of the
     * RTP data to the participant.
     *
     * @param stream the data to sent out.
     */
    public SendStream createSendStream(PushBufferStream stream) throws UnsupportedFormatException;
    
    /**
     * Adds a ReceiverStreamListener.
     *
     * @param listener the listener receives all events for receiver stream 
     * state transition
     */
    public void addAdaptorListener(AdaptorListener listener);
    
    /**
     * Removess a ReceiverStreamListener.
     *
     * @param listener the listener that was previously registered.
     */
    public void removeAdaptorListener(AdaptorListener listener);
    
    /**
     * Closes this socket.
     */
    public void close();
    
}
