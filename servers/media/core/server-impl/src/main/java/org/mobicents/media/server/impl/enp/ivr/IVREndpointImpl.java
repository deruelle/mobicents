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

import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.enp.ann.AnnEndpointImpl;

import org.apache.log4j.Logger;

/**
 * 
 * @author Oleg Kulikov
 */
public class IVREndpointImpl extends AnnEndpointImpl {

    protected AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
//    protected String mediaType = FileTypeDescriptor.WAVE;
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


    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.server.spi.BaseEndpoint#play(int, String
     *      NotificationListener, boolean.
     */
/*    public void play(EventID signalID, String[] params, String connectionID, NotificationListener listener,
            boolean keepAlive, boolean startRecordingImmediately) throws UnknownSignalException {
        logger.info("Play signal, signalID = " + signalID);

        if (signal != null) {
            signal.stop();
            signal = null;
        }

        if (params == null) {
            return;
        }

        if (connectionID == null) {
            connectionID = getConnectionID();
        }

        switch (signalID) {
            case PLAY:
                // signal = new AnnouncementSignal(this, listener, params);
                // signal.start();
                AnnouncementPackage pkg = new AnnouncementPackage(this);
                HashMap opts = new HashMap();
                opts.put("announcement.url", params[0]);
                signal = pkg.play(signalID, opts, connectionID, listener);
                logger.info("Execute announcement signal");
                break;
            case PLAY_RECORD:
                String recordURL = null;

                if (recordDir != null) {
                    String param1 = params[1];
                    int index = param1.lastIndexOf("/");
                    if (index > 0) {
                        String folderStructure = param1.substring(0, index);

                        java.io.File file = new java.io.File(new StringBuffer(recordDir).append("/").append(folderStructure).toString());
                        boolean fileCreationSuccess = file.mkdirs();

                        if (!fileCreationSuccess) {
                            logger.warn("The creation of record file " + param1 + " failed");
                        }
                    }
                    recordURL = recordDir + "/" + param1;
                } else {
                    recordURL = params[1];
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("record=" + recordURL);
                }

                opts = new HashMap();
                opts.put("announcement.url", params[0]);
                opts.put("record.url", recordURL);

                AdvancedAudioPackage au = new AdvancedAudioPackage(this);
                signal = au.play(signalID, opts, connectionID, listener, startRecordingImmediately);
                logger.info("Execute Play/Record signal for connection: " + connectionID);
                break;
            case TEST_SINE:
                //signal = new SineSignal(this, listener, params);
                //signal.start();
                break;
            default:
                throw new UnknownSignalException("Signal is unknown: " + signalID);
        }

    }

    @Override
    public void subscribe(EventID eventID, String connectionID, String params[], NotificationListener listener) {
        switch (eventID) {
            case TEST_SPECTRA:
                logger.info("Start test/spectra signal for connection: " + connectionID);
                testSpectra(connectionID, listener);
                break;
            case DTMF:
                logger.info("Start DTMF detector for connection: " + connectionID);
                this.detectDTMF(connectionID, params, listener);
                break;
        }
    }
*/
}
