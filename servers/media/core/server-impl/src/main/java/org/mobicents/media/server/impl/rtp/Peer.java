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

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import org.mobicents.media.Buffer;

/**
 *
 * @author Oleg Kulikov
 */
public class Peer implements Serializable {

    private InetAddress address;
    private int port;
    private RtpSocketImpl rtpSocket;
    private byte[] data = new byte[172];
    private transient DatagramPacket dp;
    
    /**
     * Creates new instance of the peer.
     *
     * @param rtpSocket the instance of the RTP socket.
     * @param address the address of the participant.
     * @param port the address of the participant.
     */
    public Peer(RtpSocketImpl rtpSocket, InetAddress address, int port) {
        this.rtpSocket = rtpSocket;
        this.address = address;
        this.port = port;
        dp = new DatagramPacket(data, 0, data.length, address, port);
    }

    public InetAddress getInetAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void start() {
    }

    public void stop() {
    }

    public void send(Buffer buffer) throws IOException {
    	
    	int i = 0;
        RtpHeader header = (RtpHeader) buffer.getHeader();
        byte[] headerByte = header.toByteArray();
        i = headerByte.length + buffer.getLength();
        
        byte[] data1 = new byte[i];
        
        
        System.arraycopy(headerByte, 0, data1, 0, headerByte.length);
        System.arraycopy((byte[]) buffer.getData(), 0, data1, headerByte.length, buffer.getLength());
        
        dp.setData(data1, 0, data1.length);

        rtpSocket.sendPacket(dp);
    }

}
