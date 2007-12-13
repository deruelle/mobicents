/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.resource.media.ra.rtp;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.DataSink;
import javax.media.IncompatibleSourceException;
import javax.media.MediaLocator;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.protocol.SourceStream;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.events.DtmfEvent;

/**
 * This DtmfDetector class reads from a DataSource and store
 * information of each frame of data received in a buffer and finally
 * extract DTMF digit from RTP Event.
 */
public class DtmfDetector implements DataSink, BufferTransferHandler {
    private static Logger logger = Logger.getLogger(DtmfDetector.class);
    
    private static final int OCTET = 8;
    private static final int RTP_EVENT = 4;
    DataSource source;
    PushBufferStream pushStrms[] = null;
    
    // Data sink listeners.
    private Vector listeners = new Vector(1);
    
    // Stored all the streams that are not yet finished (i.e. EOM
    // has not been received).
    SourceStream unfinishedStrms[] = null;
    Buffer readBuffer;
    
    private String dtmfDigit = null;
    
    private RtpMediaConnectionImpl connection;
    
    // Contructor
    public DtmfDetector(RtpMediaConnectionImpl connection) {
        this.connection = connection;
    }
    
    /**
     * Sets the media source this <code>MediaHandler</code>
     * should use to obtain content.
     */
    public void setSource(DataSource source) throws IncompatibleSourceException {
        
        if (source instanceof PushBufferDataSource) {
            pushStrms = ((PushBufferDataSource)source).getStreams();
            unfinishedStrms = new SourceStream[pushStrms.length];
            // Set the transfer handler to receive pushed data from
            // the push DataSource.
            for (int i = 0; i < pushStrms.length; i++) {
                pushStrms[i].setTransferHandler(this);
                unfinishedStrms[i] = pushStrms[i];
            }
            
        } else {
            // This handler only handles push buffer datasource.
            throw new IncompatibleSourceException();
        }
        
        this.source = source;
        readBuffer = new Buffer();
    }
    
    /**
     * For completeness, DataSink's require this method.
     * But we don't need it.
     */
    public void setOutputLocator(MediaLocator ml) {
    }
    
    public MediaLocator getOutputLocator() {
        return null;
    }
    
    public String getContentType() {
        return source.getContentType();
    }
    
    public void open() {
    }
    
    public void start() {
        try {
            source.start();
            
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public void stop() {
        try {
            source.stop();
            
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public void close() {
        stop();
    }
    
    public void addDataSinkListener(DataSinkListener dsl) {
        if (dsl != null)
            if (!listeners.contains(dsl))
                listeners.addElement(dsl);
    }
    
    public void removeDataSinkListener(DataSinkListener dsl) {
        if (dsl != null)
            listeners.removeElement(dsl);
    }
    
    protected void sendEvent(DataSinkEvent event) {
        if (!listeners.isEmpty()) {
            synchronized (listeners) {
                Enumeration list = listeners.elements();
                while (list.hasMoreElements()) {
                    DataSinkListener listener = (DataSinkListener)list.nextElement();
                    listener.dataSinkUpdate(event);
                }
            }
        }
    }
    
    /**
     * This will get called when there's data pushed from the
     * PushBufferDataSource.
     */
    public void transferData(PushBufferStream stream) {
        
        try {
            stream.read(readBuffer);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            sendEvent(new DataSinkErrorEvent(this, e.getMessage()));
            return;
        }
        
        // Getting the internal data object that holds the media chunk
        // contained in this Buffer.
        Object data = readBuffer.getData();
        // If the media chunk for this Buffer is held in an array, gets
        // the offset into the data array where the valid data begins.
        int offset = readBuffer.getOffset();
        // Getting the length of the valid data in this Buffer if the
        // data is held in an array.
        int length = readBuffer.getLength();
        
        if (data instanceof byte[]) {
            byte[] buf = new byte[length];
            
            if (length == RTP_EVENT) {
                for (int i = 0; i < length; i++) {
                    buf[i] = ((byte[])data)[offset + i];
                }
                
                // The second octet has the "end" bit (bit 8) which indicates that this
                // packet contains the end of the event if E is 1.
                String octet = Integer.toBinaryString((new Byte(buf[1])).intValue());
                
                // octet might be out of range (octet > 8)
                if (octet.length() >= OCTET) {
                    // We are interested in the first 8 bits (Little Endian)
                    String endBit = octet.substring(octet.length() - 8);
                    
                    if (endBit.startsWith("1")) {
                        dtmfDigit = Byte.toString(buf[0]);
                        logger.info("############### DTMF DIGIT: " + dtmfDigit);
                        this.stop();
                        
                        // FIRING A DTMF EVENT, SO WHEN THE SBB HANDLE
                        // THAT EVENT, IT WILL BE TIME TO STOP THE PROCESSOR
                        connection.onDtmf(dtmfDigit);
                    }
                }
            }
        }
    }
    
    public Object [] getControls() {
        return new Object[0];
    }
    
    public Object getControl(String name) {
        return null;
    }
    
    public String getDtmfDigit() {
        return dtmfDigit;
    }
}