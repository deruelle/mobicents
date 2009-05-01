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
package org.mobicents.media.server.impl.events.line;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;

/**
 *
 * @author Oleg Kulikov
 */
public class CongestionToneGen extends AbstractSource {

    private final static AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
    private final static Format[] SUPPORTED_FORMATS = new Format[]{LINEAR};
    private final static int F1 = 480;
    private final static int F2 = 680;
    private final static int PERIOD = 12;
    private boolean isSignal = true;    //0,25 ms buffer
    private byte[] signal = new byte[320];
    private TimerImpl timer = new TimerImpl();
    private int seq = 0;

    public CongestionToneGen() {
    	super("CongestionToneGen");
        double A = Short.MAX_VALUE / 2;
        int k = 0;
        for (int i = 0; i < 160; i++) {
            short t = (short) (A * (Math.sin(2 * Math.PI * F1 * i / 8000) + Math.sin(2 * Math.PI * F2 * i / 8000)));
            signal[k++] = (byte) (t);
            signal[k++] = (byte) (t >> 8);
        }
    }

    public void start() {
        
    }

    public void stop() {
    }

    public Format[] getFormats() {
        return SUPPORTED_FORMATS;
    }
    
    private boolean isSwitched() {
        int div = (int)(seq / PERIOD);
        return seq - (div * PERIOD) == 0;
    }
    private class Generator implements Runnable {

        public void run() {
            if (otherParty != null) {
                if (isSwitched()) {
                    isSignal = !isSignal;
                }
                byte[] data = new byte[320];
                Buffer buffer = new Buffer();
                if (isSignal) {
                    System.arraycopy(signal, 0, data, 0, signal.length);
                }
                buffer.setData(data);
                buffer.setDuration(20);
                buffer.setFormat(LINEAR);
                buffer.setLength(320);
                buffer.setSequenceNumber(seq);
                buffer.setTimeStamp(20 * seq);
            }
        }

        public void started() {
        }

        public void ended() {
        }
    }
}
