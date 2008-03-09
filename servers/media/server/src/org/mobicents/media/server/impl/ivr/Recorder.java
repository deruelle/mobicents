/*
 * Recorder.java
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

package org.mobicents.media.server.impl.ivr;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseResource;
import org.mobicents.media.server.impl.packetrelay.LocalDataSource;
import org.mobicents.media.server.spi.MediaSink;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.AU;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class Recorder extends BaseResource implements MediaSink {
    
    private Processor recorder;
    private DataSink dataSink;
    
    private Logger logger = Logger.getLogger(Recorder.class);

    private String mediaType;
    private Format audioFormat;
    
    private List <NotificationListener> listeners = new ArrayList();
    
    private IVREndpointImpl endpoint;
    private BaseConnection connection;
    private LocalDataSource dataSource;
    private URL url;
    
    /** Creates a new instance of Recorder */
    public Recorder(IVREndpointImpl endpoint) {
        this.endpoint = (IVREndpointImpl) endpoint;
        this.mediaType = this.endpoint.mediaType;
    }
    
    
    public void setURL(URL url) {
        this.url = url;
    }
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.server.spi.ivr.IVREndpoint#record(URL)
     */
    private void record(URL url) {
        NDC.push(endpoint.getLocalName());
        
        try {
            FileTypeDescriptor ftd= new FileTypeDescriptor(mediaType);
            Format [] formats = new Format[] {audioFormat};
            
            ProcessorModel recorderModel = new ProcessorModel(dataSource, formats, ftd);
            recorder = Manager.createRealizedProcessor(recorderModel);
            //recorder.addControllerListener(new RecorderStateListener(this));
            
            if (logger.isDebugEnabled()) {
                logger.debug("Initialized Recorder[processor=" + recorder + "]");
            }
            
            MediaLocator file = new MediaLocator(url);
            dataSink = Manager.createDataSink(recorder.getDataOutput(), file);
            dataSink.open();
            
            if (logger.isDebugEnabled()) {
                logger.debug("Initialized Datasink[" + dataSink + "]");
            }
                        
            recorder.start();           
            if (logger.isDebugEnabled()) {
                logger.debug("Starting recorder[processor=" + recorder + "]");
            }            
            
            dataSink.start();
            if (logger.isDebugEnabled()) {
                logger.debug("Started DataSink[" + dataSink + "]");
            }      
        } catch (Exception e) {
            sendEvent(AU.FAIL, AU.CAUSE_FACILITY_FAILURE, e.getMessage());
        } 
        NDC.remove();
    }
    
    public void stop() {
        if (dataSink != null) {
            try {
                dataSink.stop();
            } catch (IOException e) {
            }
            dataSink.close();
        }
        
        if (recorder != null) {
            recorder.stop();
            recorder.close();
        }

        sendEvent(AU.COMPLETE, AU.CAUSE_NORMAL, null);
    }

    public void start() {
        record(url);
    }

    public void prepare(PushBufferStream mediaStream) throws UnsupportedFormatException {
        dataSource = new LocalDataSource(mediaStream);
    }

    public void configure(Properties config) {
    }

    public void release() {
        stop();
    }

    protected void sendEvent(int eventID, int cause, String msg) {
        NotifyEvent evt = new NotifyEvent(this, eventID, cause, msg);
        for (NotificationListener listener: listeners) {
            listener.update(evt);
        }
        listeners.clear();
        this.stop();
    }
    
    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }
    
}
