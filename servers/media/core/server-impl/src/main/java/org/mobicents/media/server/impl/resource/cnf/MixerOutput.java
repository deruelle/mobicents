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
package org.mobicents.media.server.impl.resource.cnf;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;

/**
 * Implements output stream of the audio mixer.
 * 
 * @author Oleg Kulikov
 */
public class MixerOutput extends AbstractSource {

    private AudioMixer mixer;
    
    /**
     * Creates new ouput stream
     * @param mixer
     */
    public MixerOutput(AudioMixer mixer) {
        super("AudioMixer[Output]");
        this.mixer = mixer;
    }
    
    @Override
    public void evolve(Buffer buffer, long timestamp, long sequenceNumber) {
        mixer.evolve(buffer, timestamp, sequenceNumber);
    }    

    private boolean isSilence(Buffer buffer) {
        byte[] data = (byte[]) buffer.getData();
        for (int i = 0; i < buffer.getLength(); i++) {
            if (data[i + buffer.getOffset()] != 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.MediaSource#getFormats() 
     */
    public Format[] getFormats() {
        return AudioMixer.formats;
    }

}
