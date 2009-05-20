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
package org.mobicents.media.server.impl.resource.zap;

/**
 *
 * @author kulikov
 */
public class HDLCChannel {

    public final static int STATE_OCTET_COUNTING = 0;
    public final static int STATE_DATA_READING = 1;
    
    private HDLCReader reader;
    private int state = STATE_OCTET_COUNTING;
    
    private byte[] data = new byte[279];    
    private int seg = 0;
    private int offset = 0;
    
    private int buff;
    private int[] stamp = new int[256];
    private int[] mask = new int[]{1, 3, 7, 15, 31, 63, 127, 255};

    private int[][][]ones = new int[8][256][2];    
    private int bits = 0;

    public HDLCChannel() {
        for (int i = 1; i < 7; i++) {
            int n = (int) Math.pow(2, i);
            for (int p = 0; p < n; p++) {
                int q = (p << (8 - i)) | (0x7e >> i);
                stamp[q] = i;
            }
        }
        
        for (int i = 1; i < 8; i++) {
            
        }
    }

    public void setReader(HDLCReader reader) {
        this.reader = reader;
    }
    
    public int getState() {
        return state;
    }

    public void offer(byte b) {
        if (b == 0x7e) {
            if (state == STATE_OCTET_COUNTING) {
                state = STATE_DATA_READING;
            } else {
                this.push();
            }
        } else  //there was no any indications of the boundary flag in the previous byte
        if (bits == 0) {
            //checking if flag indications is presented in this byte
            bits = stamp[b & 0xff];
            if (bits > 0) {
                //found the begining of the flag, the next one can contains 
                //the remainder part. let's save current byte in the local buffer
                buff = (buff << 8) | b;
            } else {
                //there is no flag's indications in the current byte too.
                //then it is a data. we are appending to a data array if state 
                //is reading data or ignore if state is octet counting
                if (state == STATE_DATA_READING) {
                    append(b, 8);
                }
            }
        } else {
            //the previous byte contains flag's indications
            //let's try to find ending part in the current byte
            int p = (b & 0xff) >> (8 - bits);
            int q = 0x7e & mask[bits - 1];

            if (p == q) {
                //yes, flag is detected
                //the action now depends from the state of the channel.
                //if state is octet counting, we should swicth state and append 
                //bits followed by the flag to the data stream. if state is data 
                //reading then this flag is frame boundary. we have to append
                //bits received before flag to the data stream, push data stream 
                //to the receiver and append bits followed by the flag to the stream
                switch (state) {
                    case STATE_OCTET_COUNTING :
                        append(b, bits);
                        state = STATE_DATA_READING;
                        break;
                    case STATE_DATA_READING :
                        buff >>= 8-bits;
                        append((byte)buff, bits);
                        push();
                        append(b, bits);
                        bits = 0;
                        break;
                }
            } else {
                //no, it is not a flag but the prevoius byte now local buffer
                //we should append it to the data stream.
                append((byte)buff, 8);
                append(b, 8);
            }
            bits = 0;
        }
    }

    /**
     * Appends last <code>count</code> bits to the data stream.
     * 
     * @param buff the the local buffer which contains bits to append
     * @param count the number of bits to append.
     */
    private void append(byte buff, int count) {
        int available = 8 - offset;
        if (count <= available) {
            data[seg] |= (buff & mask[count-1]) << (available - count);
            offset += count;
            
            if (offset == 8) {
                offset = 0;
                seg++;
            }
        } else {
            int dif = (count - available);
            data[seg] |= ((buff & mask[count-1]) >> dif);
            
            seg++;
            offset = 0;
            
            data[seg] = (byte)((buff & mask[count-1]) << (8 - dif));
            offset += dif;
        }
        
    }

    private void push() {
        if (offset != 0) {
            state = STATE_OCTET_COUNTING;
            return;
        }
        if (this.reader != null) {
            reader.receive(data, seg);
        }
        
        for (int i = 0; i < seg; i++) {
            data[i] = 0;
        }
        
        seg = 0; 
        offset = 0;                    
    }
    
    public static void main(String[] args) {
        byte[] stream = new byte[]{(byte)0x7e, 0x01, 0x01, 0x00, (byte)0x37, (byte)0x62, (byte)0x7e};
        HDLCChannel ch = new HDLCChannel();
        
        for (int i = 0; i < stream.length; i++) {
            ch.offer(stream[i]);
        }
    }
}
