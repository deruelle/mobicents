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
package org.mobicents.media.server.impl.enp.ivr;

import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.server.impl.resource.Demultiplexer;
import org.mobicents.media.server.impl.MediaResource;
import org.mobicents.media.server.impl.enp.ann.AnnEndpointImpl;
import org.mobicents.media.server.impl.events.audio.Recorder;
import org.mobicents.media.server.impl.events.dtmf.DtmfDetector;
import org.mobicents.media.server.spi.Connection;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.dsp.Processor;
import org.mobicents.media.server.impl.events.dtmf.DTMFMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author Oleg Kulikov
 */
public class IVREndpointImpl extends AnnEndpointImpl {

//    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    private Format[] formats;    
    
    private Demultiplexer demux;    
    private Recorder recorder;
    private DtmfDetector dtmfDetector;
    
    protected String recordDir = null;
    private DTMFMode dtmfMode = DTMFMode.AUTO;
    private transient Processor recDsp;
    
    private transient Logger logger = Logger.getLogger(IVREndpointImpl.class);

    /**
     * Creates a new instance of IVREndpointImpl
     * 
     * @param endpointsMap
     */
    public IVREndpointImpl(String localName) throws Exception {
        super(localName);
    }

    @Override
    public Format[] getFormats() {
        if (formats == null) {
            updateFormats();
        }
        return formats;
    }

    private void updateFormats() {
        Format[] annFormats = super.getFormats();
        if (dtmfMode == DTMFMode.INBAND) {
            formats = annFormats;
        } else {
            formats = new Format[annFormats.length + 1];

            System.arraycopy(annFormats, 0, formats, 0, annFormats.length);
            formats[annFormats.length] = DTMF;
        }
    }
    
    @Override
    public void start() throws ResourceUnavailableException {
        super.start();
        startPrimarySink();
    }

    protected void startPrimarySink() {
        demux = new Demultiplexer("Demultiplexer "+this.getLocalName());

        recorder = new Recorder("wav", recordDir);
        dtmfDetector = new DtmfDetector("DtmfDetector[" + getLocalName() + "]");
        dtmfDetector.setMode(dtmfMode);
        
        recDsp = new Processor("");
        recDsp.getInput().connect(demux);
        recDsp.getOutput().connect(recorder);
        recDsp.configure(
                new Format[] {Endpoint.PCMU,Endpoint.PCMA, Endpoint.SPEEX, Endpoint.G729, Endpoint.GSM}, 
                new Format[] {Endpoint.LINEAR_AUDIO});
        recDsp.getOutput().start();
        //demux.connect(recorder);
        dtmfDetector.connect(demux);
        demux.start();
    }

    public void setRecordDir(String recordDir) {
        this.recordDir = recordDir;
    }

    public String getRecordDir() {
        return this.recordDir;
    }

    public String getMediaType() {
        return null;
    }

    public DTMFMode getDtmfMode() {
        return dtmfMode;
    }
    
    public void setDtmfMode(DTMFMode dtmfMode) {
        this.dtmfMode = dtmfMode;
        //updateFormats();
    }
    
    public void setMediaType(String mediaType) {
    }

    @Override
    public MediaSink getPrimarySink(Connection connection) {
        return demux.getInput();
    }

    @Override
    public void allocateMediaSinks(Connection connection) {
        dtmfDetector.addListener((BaseConnection) connection);
    }

    @Override
    protected MediaSink getMediaSink(MediaResource id, Connection connection) {
        if (id == MediaResource.AUDIO_RECORDER) {
            return recorder;
        } else if (id == MediaResource.DTMF_DETECTOR) {
            return dtmfDetector;
        } else {
            return null;
        }
    }

    @Override
    public void releaseMediaSinks(Connection connection) {
        dtmfDetector.removeListener((BaseConnection) connection);
    }
}
