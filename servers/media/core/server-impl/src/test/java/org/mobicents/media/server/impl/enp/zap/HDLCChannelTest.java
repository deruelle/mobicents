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

package org.mobicents.media.server.impl.enp.zap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kulikov
 */
public class HDLCChannelTest implements HDLCReader {

    private HDLCChannel ch;
    private byte[] data;
    private int byteCount = 0;
    
    public HDLCChannelTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        ch = new HDLCChannel();
        ch.setReader(this);
        data = new byte[256];
        byteCount = 0;
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of offer method, of class HDLCChannel.
     */
    @Test
    public void testBitsAlignedToByte() {
        byte[] stream = new byte[]{(byte)0x7e, 0x01, 0x01, 0x00, (byte)0x37, (byte)0x62, (byte)0x7e, 
        0x01, 0x01, 0x00, (byte)0x7e};
        
        byte[] payload = new byte[] {0x01, 0x01, 0x00, (byte)0x37, (byte)0x62, 0x01, 0x01, 0x00};
        
        for (int i = 0; i < stream.length; i++) {
            ch.offer(stream[i]);
        }
        
        if (data == null) {
            fail("Data not received");
        }
        
        for (int i = 0; i < payload.length; i++) {
            assertEquals(data[i], payload[i]);
        }
        
    }

    /**
     * Test of offer method, of class HDLCChannel.
     */
    @Test
    public void testBitsNotAlignedToByte() {
        byte[] stream = new byte[]{(byte)0xB7, (byte)0xE0, 0x18, 0x00, 0x18, 0x07, (byte)0xe9, 
        (byte)0xB7, (byte)0xE0, 0x18, 0x00, 0x18, 0x07, (byte)0xe9};
        
        byte[] payload = new byte[] {0x01, (byte)0x80, 0x01, (byte)0x80, (byte)0x9b, 
        0x01, (byte)0x80, 0x01, (byte)0x80};
        
        for (int i = 0; i < stream.length; i++) {
            ch.offer(stream[i]);
        }
        
        if (data == null) {
            fail("Data not received");
        }
        
        assertEquals(payload.length, byteCount);
        for (int i = 0; i < payload.length; i++) {
            assertEquals(data[i], payload[i]);
        }
        
    }
    
    public void receive(byte[] buffer, int len) {
        System.arraycopy(buffer, 0, data, byteCount, len);
        byteCount += len;
    }


}