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
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import javax.media.Format;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpSessionManagerImpl implements RtpSocketAdaptor, ReceiveStreamListener {
    
    private RTPManager rtpManager;
    private DataSource receiveStream;
    private SessionAddress address;
    private ArrayList<AdaptorListener> listeners = new ArrayList();
    /**
     * Creates a new instance of RtpSessionManagerImpl
     */
    public RtpSessionManagerImpl(InetAddress bindAddress, int lowPort, int highPort) throws InvalidSessionAddressException, IOException {
        SessionAddress localAddress = new SessionAddress(bindAddress, 0);
        rtpManager = RTPManager.newInstance();
        rtpManager.initialize(localAddress);
    }

    public void addTarget(SessionAddress target) throws InvalidSessionAddressException, IOException {
        rtpManager.addTarget(target);
    }
    
    public void addReceiverStreamListener(AdaptorListener listener) {
        listeners.add(listener);
    }
    
    public void removeReceiverStreamListener(AdaptorListener listener) {
        listeners.remove(listener);
    }
    
    public DataSource getReceiveStream() {
        return receiveStream;
    }

    public void startSendStream(DataSource dataSource) throws IOException, UnsupportedFormatException {
        SendStream sendStream = rtpManager.createSendStream(dataSource, 0);
        sendStream.start();
    }

    public void update(ReceiveStreamEvent event) {
        if (event instanceof NewReceiveStreamEvent) {
            receiveStream = ((NewReceiveStreamEvent) event).getReceiveStream().getDataSource();
            for (AdaptorListener listener : listeners) {
                //listener.newReceiveStream(this);
            }
        }
    }

    public void addFormat(int pt, Format format) {
    }

    public void addAdaptorListener(AdaptorListener listener) {
    }

    public void removeAdaptorListener(AdaptorListener listener) {
    }

    public int init(InetAddress localAddress, int lowPort, int highPort) throws SocketException {
        return -1;
    }

    public void start() {
    }

    public void stop() {
    }

    public void setPeer(InetAddress address, int port) {
    }

    public org.mobicents.media.server.impl.rtp.SendStream createSendStream(PushBufferStream stream) throws UnsupportedFormatException {
        return null;
    }

    public void close() {
    }

    
}
