/*
 * RawPushBufferStream.java
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

package org.mobicents.media.processor;

import com.ibm.media.codec.audio.ulaw.JavaEncoder;
import com.sun.media.codec.audio.ulaw.Packetizer;
import java.io.IOException;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.Track;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullSourceStream;
import javax.media.protocol.PushBufferStream;
import javax.media.protocol.SourceStream;

/**
 *
 * @author Oleg Kulikov
 */
public class RawPushBufferStream implements PushBufferStream, Runnable {
    
    private Format fmt;
    
    private BufferTransferHandler transferHandler;
    private RawDataSource ds;
    
    private Thread thread;
    
    private Track track;
    private SourceStream sourceStream;
    private TrackControl trackControl;
    
    private Buffer frame;
    
    private double timestamp;
    private Time totalDuration;
    
    private long duration;
    private int packetSize = 8000;
    
    private boolean eom = false;
    
    private JavaEncoder encoder = new JavaEncoder();
    private Packetizer packetizer = new Packetizer();
    
    private int seq = 0;
    
    private boolean started = false;
    private boolean isRTP = false;
    
    /** Creates a new instance of RawPushBufferStream */
    public RawPushBufferStream(RawDataSource ds, Track track, TrackControl trackControl) {
        this.ds = ds;
        this.track = track;
        this.trackControl = trackControl;
        this.totalDuration = track.getDuration();
        this.fmt = track.getFormat();
        
        this.isRTP = isRTP(trackControl.getFormat());
        try {
            encoder.setInputFormat(track.getFormat());
            encoder.setOutputFormat(trackControl.getFormat());
            encoder.open();
            
            if (isRTP) {
                packetizer.setInputFormat(new AudioFormat(AudioFormat.ULAW, 8000, 8, 1));
                packetizer.setOutputFormat(new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1));
                
                packetSize = 160;
                packetizer.setPacketSize(160);
                packetizer.open();
            }
        } catch (ResourceUnavailableException ex) {
            ex.printStackTrace();
        }
        
        thread = new Thread(this);
    }
    
    private boolean isRTP(Format fmt) {
        String enc = fmt.getEncoding().toLowerCase();
        return enc.endsWith("rtp");
    }
    
    public Format getFormat() {
        return new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1);
    }
    
    public void read(Buffer buffer) throws IOException {
        byte[] src = (byte[]) frame.getData();
        byte[] data = new byte[src.length];
        
        System.arraycopy(src, 0, data, 0, src.length);
        buffer.setData(data);
        buffer.setDiscard(false);
        buffer.setDuration(duration);
        buffer.setEOM(false);
        buffer.setFlags(0);
        buffer.setFormat(new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1));
        buffer.setHeader(null);
        buffer.setLength(frame.getLength());
        buffer.setOffset(frame.getOffset());
        buffer.setSequenceNumber(seq++);
        buffer.setTimeStamp(duration * seq);
    }
    
    public void setTransferHandler(BufferTransferHandler handler) {
        synchronized(this) {
            this.transferHandler = handler;
            notifyAll();
        }
    }
    
    protected void start(boolean started) {
        synchronized (this) {
            this.started = started;
            if (started && !thread.isAlive()) {
                thread = new Thread(this);
                thread.start();
            }
            notifyAll();
        }
    }
    
    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW_RTP);
    }
    
    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }
    
    public boolean endOfStream() {
        return eom;
    }
    
    public Object[] getControls() {
        return null;
    }
    
    public Object getControl(String string) {
        return null;
    }
    
    public void readData(Buffer buffer) throws IOException {
        if (sourceStream != null) {
            byte[] data = new byte[8000];
            PullSourceStream s = (PullSourceStream) sourceStream;
            s.read(data, 0, 8000);
            buffer.setData(data);
            buffer.setLength(8000);
            buffer.setOffset(0);
        }
    }
    
    public void run() {
        while (started) {
            //check for transferHandler
            synchronized (this) {
                while (transferHandler == null && started) {
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
            
            if (started && transferHandler != null) {
                //reading data from source
                Buffer buffer = new Buffer();
                track.readFrame(buffer);
                
                if (buffer.getData() == null) {
                    //@TODO stop datasource and send endofmedia event
                    System.out.print("********* END OF MEDIA***********");
                    return;
                }
                
                Buffer ulaw = new Buffer();
                encoder.process(buffer, ulaw);
                
                if (isRTP) {
                    duration = 20000000L;
                    for (int i = 0; i < 50; i++) {
                        frame = new Buffer();
                        packetizer.process(ulaw, frame);
                        transferHandler.transferData(this);
                    }
                } else {
                    duration = 1000000000L;
                    frame = ulaw;
                    transferHandler.transferData(this);
                }
                
                try {
                    Thread.currentThread().sleep(10);
                } catch (InterruptedException e) {
                }
                
            }
        }
    }
    
}
