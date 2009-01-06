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
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;

/**
 *
 * @author Oleg Kulikov
 */
public class ReceiveStream extends AbstractSource {

    /**
     * 
     */
    private static final long serialVersionUID = -2277812497480986797L;
    private int period;
    private JitterBuffer jitterBuffer;
    private transient ScheduledExecutorService timer;
    private transient Future receiver;
    private boolean started = false;
    private Buffer frame;
    protected Format[] formats;
    private RtpSocketImpl rtpSocket;
    private BufferFactory bufferFactory = new BufferFactory(10);
    
    private transient ExecutorService worker = Executors.newSingleThreadExecutor();
    private transient Future workerTask;
    
    /** Creates a new instance of ReceiveStream */
    public ReceiveStream(ScheduledExecutorService timer, RtpSocket rtpSocket, int period, int jitter) {
        super("ReceiveStream");
        this.rtpSocket = (RtpSocketImpl) rtpSocket;
        this.timer = timer;
        this.period = period;        
        jitterBuffer = new JitterBuffer(jitter, period);
    }

    private void push(Buffer buffer) {
        jitterBuffer.write(buffer);
    }

    public void stop() {
        if (started) {
            started = false;
            if (workerTask != null) {
                workerTask.cancel(true);
            }
            receiver.cancel(true);
        }
    }

    public void start() {
        if (!started) {
            started = true;
            jitterBuffer.reset();
            workerTask = worker.submit(new UDPReceiver());
            receiver = timer.scheduleAtFixedRate(new Receiver(), 0, period, TimeUnit.MILLISECONDS);
        }
    }

    public Format[] getFormats() {
        Format[] fmts = new Format[rtpSocket.getRtpMap().size()];
        rtpSocket.getRtpMap().values().toArray(fmts);
        return fmts;
    }

    
    private class UDPReceiver implements Runnable {
        
        private int pt = -1;
        private Format fmt = null;
        
        public void run() {
            byte[] buff = new byte[172]; 
            DatagramPacket udpPacket = new DatagramPacket(buff, buff.length);
            while (started) {
                try {
                    rtpSocket.receivePacket(udpPacket);
                } catch (SocketTimeoutException e) {
                    continue;
                } catch (IOException e) {
                    continue;
                }

                RtpHeader header = new RtpHeader();
                header.init(buff);

                //change format if payload type is changed
                if (pt != header.getPayloadType()) {
                    pt = header.getPayloadType();
                    fmt = rtpSocket.getRtpMap().get(header.getPayloadType());
                }
                
                Buffer buffer = bufferFactory.allocate();
                buffer.setLength(udpPacket.getLength() - 12);
                buffer.setFormat(fmt);
                
//                buffer.setFormat(rtpSocket.getRtpMap().get(header.getPayloadType()));
                System.arraycopy(buff, 12, (byte[]) buffer.getData(), 0, buffer.getLength());

                try {
                    push(buffer);
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    private class Receiver implements Runnable {

        public void run() {
            frame = jitterBuffer.read();

            if (frame == null) {
                return;
            }
            if (sink == null) {
                return;
            }

            //The sink for ReceiveStream is Processor.Input
            try {
                sink.receive(frame);
            } catch (Exception e) {
            }
        }
    }
}
