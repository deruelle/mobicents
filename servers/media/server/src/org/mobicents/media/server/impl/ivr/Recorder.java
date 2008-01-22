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
import java.io.Serializable;
import java.net.URL;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.protocol.FileTypeDescriptor;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

/**
 *
 * @author Oleg Kulikov
 */
public class Recorder implements Serializable {
    
    private Processor recorder;
    private DataSink dataSink;
    
    protected IVREndpointImpl endpoint;
    private Logger logger = Logger.getLogger(Recorder.class);

    private String mediaType;

    private Format audioFormat;
    
    /** Creates a new instance of Recorder */
    public Recorder(IVREndpointImpl endpoint) {
        this.endpoint = endpoint;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.server.spi.ivr.IVREndpoint#record(URL)
     */
    public void record(URL url) {
        NDC.push(endpoint.getLocalName());
        
        if (endpoint.inputStream == null) {
            logger.warn("Input stream is NULL");
            return;
        }
        
        try {
            FileTypeDescriptor ftd= new FileTypeDescriptor(mediaType);
            Format [] formats = new Format[] {audioFormat};
            
            ProcessorModel recorderModel = new ProcessorModel(endpoint.inputStream, formats, ftd);
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
            //endpoint.sendEvent(new NotifyEvent(NotifyEvent.ERROR, endpoint, e.getMessage()));
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
    }
    
}
