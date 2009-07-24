/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.impl.rtp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author kulikov
 */
public class Transceiver implements Runnable {

    private DatagramSocket socket;
    protected DatagramChannel channel;
    private Selector readSelector;
    private InetAddress bindAddress;
    private int localPort;
    private int bufferSize = 8196;
    private ByteBuffer readerBuffer = ByteBuffer.allocate(bufferSize);
    private volatile boolean started = false;
    private HashMap<SocketAddress, RtpSocket> rtpSockets;

    public Transceiver(HashMap<SocketAddress, RtpSocket> rtpSockets, InetAddress bindAddress, int localPort) throws SocketException, IOException {
        this.rtpSockets = rtpSockets;
        this.bindAddress = bindAddress;
        this.localPort = localPort;

        channel = DatagramChannel.open();
        channel.configureBlocking(false);

        socket = channel.socket();

        // creating local address and trying to bind socket to this
        // address
        InetSocketAddress address = new InetSocketAddress(bindAddress, localPort);
        socket.bind(address);

        readSelector = SelectorProvider.provider().openSelector();
        channel.register(readSelector, SelectionKey.OP_READ);
    }

    public void start() {
        started = true;
        new Thread(this, "RTP Transceiver").start();
    }

    public void stop() {
        started = false;
        try {
            channel.disconnect();
            channel.close();
            socket.disconnect();
            socket.close();
            readSelector.close();
        } catch (Exception e) {
        }
    }

    private void notifyError(Exception e) {
        Collection<RtpSocket> list = rtpSockets.values();
        for (RtpSocket s : list) {
            s.notify(e);
        }
    }

    public void send(byte[] data, int len, SocketAddress remoteAddress) throws IOException {
        ByteBuffer byteBuffer1 = ByteBuffer.wrap(data);
        int count = 0;

        while (count < len) {
            count += channel.send(byteBuffer1, remoteAddress);
            byteBuffer1.compact();
            byteBuffer1.flip();
        }
    }

    public void run() {
        while (started) {
            try {
                readSelector.select();
                SocketAddress remoteAddress = channel.receive(readerBuffer);

                readerBuffer.flip();
                RtpSocket rtpSocket = rtpSockets.get(remoteAddress);
                if (rtpSocket != null) {
                    RtpPacket rtpPacket = new RtpPacket(readerBuffer);
                    rtpSocket.receive(rtpPacket);
                }
                readerBuffer.clear();
            } catch (IOException e) {
                if (started) {
                    notifyError(e);
                }
            }
        }
    }
}
