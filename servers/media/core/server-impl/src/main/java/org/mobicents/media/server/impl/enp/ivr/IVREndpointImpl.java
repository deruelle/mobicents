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

import java.util.HashMap;
import org.mobicents.media.format.AudioFormat;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseVirtualEndpoint;
import org.mobicents.media.server.impl.Generator;
import org.mobicents.media.server.impl.events.announcement.AudioPlayer;
import org.mobicents.media.server.impl.events.au.Recorder;
import org.mobicents.media.server.impl.events.dtmf.BaseDtmfDetector;
import org.mobicents.media.server.spi.Endpoint;

/**
 * 
 * @author Oleg Kulikov
 */
public class IVREndpointImpl extends BaseVirtualEndpoint {

    protected AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
    //protected String mediaType = FileTypeDescriptor.WAVE;
    protected String recordDir = null;
    private transient Logger logger = Logger.getLogger(IVREndpointImpl.class);

    /** Creates a new instance of IVREndpointImpl */
    public IVREndpointImpl(String localName) {
        super(localName);
        this.setMaxConnectionsAvailable(1);
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

    public void setMediaType(String mediaType) {
    }

    @Override
    public Endpoint doCreateEndpoint(String localName) {
        IVREndpointImpl enp = new IVREndpointImpl(localName);
        enp.setRecordDir(recordDir);
        enp.setMediaType(localName);
        return enp;
    }

/*    public void play(String signalID, Options options, String connectionID, NotificationListener listener)
            throws UnknownSignalException, FacilityException {
        if (signalID.equals("org.mobicents.media.au.PLAY_RECORD")) {
            if (recordDir != null) {
                String param1 = (String) options.get("recorder.url");
                int index = param1.lastIndexOf("/");
                if (index > 0) {
                    String folderStructure = param1.substring(0, index);

                    java.io.File file = new java.io.File(new StringBuffer(recordDir).append("/").append(folderStructure).toString());
                    boolean fileCreationSuccess = file.mkdirs();
                }
                options.add("recorder.url", recordDir + "/" + param1);
            } 
        }
        super.play(signalID, options, connectionID, listener);
    }
*/
    @Override
    public HashMap initMediaSources() {
        HashMap map = new HashMap();
        //init audio player
        map.put(Generator.AUDIO_PLAYER, new AudioPlayer());
        return map;
    }

    @Override
    public HashMap initMediaSinks() {
        HashMap map = new HashMap();
        //init audio player
        map.put(Generator.AUDIO_RECORDER, new Recorder(""));
        map.put(Generator.DTMF_DETECTOR, new BaseDtmfDetector());
        
        return map;
    }
}
