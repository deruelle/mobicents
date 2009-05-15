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
package org.mobicents.media.server.impl.resource.cnf;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;

/**
 *
 * @author Oleg Kulikov
 */
public class MixerOutput extends AbstractSource {

    public MixerOutput() {
        super("MixerOutput");
    }

    protected void push(Buffer buffer) {
        if (otherParty != null) {
//            print(buffer);
            otherParty.receive(buffer);
        }
    }

    private void print(Buffer buffer) {
        int len = buffer.getLength();
        int offset = buffer.getOffset();
        
        byte[] data = (byte[])buffer.getData();
        for (int i =offset; i < len; i++) {
            System.out.print(data[i] + " ");
        }
            System.out.println("");
    }
    
    public void start() {
    }

    public void stop() {
    }

    public Format[] getFormats() {
        return AudioMixer.formats;
    }
}
