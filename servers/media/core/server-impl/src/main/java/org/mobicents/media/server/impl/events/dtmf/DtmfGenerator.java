/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */

package org.mobicents.media.server.impl.events.dtmf;

import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.dsp.Processor;
import org.mobicents.media.server.impl.resource.dtmf.InbandGeneratorImpl;
import org.mobicents.media.server.impl.resource.dtmf.Rfc2833GeneratorImpl;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author kulikov
 */
public class DtmfGenerator extends AbstractSource {

    public final static AudioFormat LINEAR_AUDIO = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
    private final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);
    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    
    private final static Format RFC2833_FORMATS[] = new Format[]{DTMF};
    private final static Format[] INBAND_FORMATS = new Format[]{LINEAR_AUDIO, PCMA, PCMU, SPEEX, G729, GSM};
    private final static Format[] ALL_FORMATS = new Format[]{LINEAR_AUDIO, PCMA, PCMU, SPEEX, G729, GSM, DTMF};
    
    private InbandGeneratorImpl inband;
    private Rfc2833GeneratorImpl rfc2833;
    private Processor dsp;
    
    private DTMFMode mode = DTMFMode.AUTO;

    public  DtmfGenerator(String name) {
        super(name);
    }
    
    public  DtmfGenerator(BaseEndpoint endpoint) {
        super(endpoint.getLocalName());
        inband = new InbandGeneratorImpl("");
        rfc2833 = new Rfc2833GeneratorImpl("");
        dsp = new Processor("");
        dsp.getInput().connect(inband);
    }
    
    public void start() {
        if (mode == DTMFMode.INBAND) {
            inband.start();
        } else {
            rfc2833.start();
        }
    }

    public void stop() {
        if (mode == DTMFMode.INBAND) {
            inband.stop();
        } else {
            rfc2833.stop();
        }
    }

    @Override
    public void connect(MediaSink sink) {
        super.connect(sink);
/*        if (mode == DTMFMode.RFC2833) {
            sink.connect(rfc2833);
        } else if (mode == DTMFMode.INBAND) {
            sink.connect(dsp.getOutput());
        } else {
            sink.connect(this.rfc2833);
            sink.connect(dsp.getOutput());
        }
 */ 
    }

    public void configure(Format[] formats) {
        dsp.configure(new Format[] {LINEAR_AUDIO}, formats);
    }
    
    @Override
    public void disconnect(MediaSink sink) {
        dsp.getOutput().disconnect(sink);
        rfc2833.disconnect(sink);
        super.disconnect(sink);
    }
    
    public Format[] getFormats() {
        switch (mode) {
            case RFC2833 : return RFC2833_FORMATS;
            case INBAND : return INBAND_FORMATS;
            default : return ALL_FORMATS;
        }
    }

}
