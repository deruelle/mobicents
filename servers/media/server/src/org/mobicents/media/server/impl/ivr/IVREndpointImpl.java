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

package org.mobicents.media.server.impl.ivr;

import javax.media.DataSink;
import javax.media.Processor;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import org.mobicents.media.server.impl.ann.AnnEndpointImpl;

import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 */
public class IVREndpointImpl extends AnnEndpointImpl {

    private Processor recorder;
    private DataSink dataSink;
    
    protected DataSource inputStream;
    
    private AudioFormat audioFormat = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private String mediaType = FileTypeDescriptor.WAVE;
    
    private transient Logger logger = Logger.getLogger(IVREndpointImpl.class);
    
    /** Creates a new instance of IVREndpointImpl */
    public IVREndpointImpl(String localName) {
        super(localName);
    }
    
        
}
