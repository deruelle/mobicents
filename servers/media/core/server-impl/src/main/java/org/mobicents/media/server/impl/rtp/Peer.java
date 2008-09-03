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

/**
 *
 * @author Oleg Kulikov
 */
public class Peer implements Serializable {
        private InetAddress address;
        private int port;
        private RtpSocketImpl rtpSocket;
        
        
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
        }

        public InetAddress getInetAddress() {
            return address;
        }
        
        public int getPort() {
            return port;
        }
        
        public synchronized void send(RtpPacket packet) throws IOException {
            byte[] data = packet.toByteArray();
            DatagramPacket dp = new DatagramPacket(data, data.length, address, port);
            rtpSocket.sendPacket(dp);
        }

}
