package org.mobicents.slee.resource.parlay.util;

import junit.framework.TestCase;

/**
 *
 * Class Description for ConvertTest
 */
public class ConvertTest extends TestCase {

    /*
     * Class under test for String toHexString(byte)
     */
    public void testToHexStringbyte() {
        assertEquals("09", Convert.toHexString(Byte.parseByte("09")));
    }

    /*
     * Class under test for String toHexString(byte[])
     */
    public void testToHexStringbyteArray() {
        assertEquals("00000009", Convert.toHexString(new byte[]{ 0, 0, 0, 9}));
        assertEquals("09090909", Convert.toHexString(new byte[]{ 9, 9, 9, 9}));
        assertEquals("0F0F0F0F", Convert.toHexString(new byte[]{ 15, 15, 15, 15}));
        assertEquals("FFFFFFFF", Convert.toHexString(new byte[]{ -1, -1, -1, -1}));
    }

    public void testFromHexString() {
        byte[] result = Convert.fromHexString("00000009");
        assertEquals(4, result.length);
        assertEquals(9, result[result.length-1]);
        
        result = Convert.fromHexString("09");
        assertEquals(1, result.length);
        assertEquals(9, result[result.length-1]);
        
        try {
            result = Convert.fromHexString("9");
            fail();
        }
        catch(Exception e) {
            
        }
    }

    public void testAssertEquals() {
        byte[] a = new byte[] { 1, 2, 3 ,4 };
        byte[] b = new byte[] { 1, 2, 3 ,4 };
        byte[] c = new byte[] { 1, 2, 3 };
        byte[] d = new byte[4];
        byte[] e = null;
        byte[] f = new byte[] { 1, 2, 3 ,5 };
        
        assertEquals(true, Convert.assertEquals(a,b));
        assertEquals(false, Convert.assertEquals(a,c));
        assertEquals(false, Convert.assertEquals(a,d));
        assertEquals(false, Convert.assertEquals(a,e));
        assertEquals(false, Convert.assertEquals(a,f));
    }

    public void testToByteArray() {
        byte[] zero = Convert.toByteArray(0);
        byte[] one = Convert.toByteArray(1);
        byte[] two = Convert.toByteArray(2);
        byte[] max = Convert.toByteArray(Integer.MAX_VALUE);
        byte[] min = Convert.toByteArray(Integer.MIN_VALUE);
        byte[] minusone = Convert.toByteArray(-1);
        
        assertEquals(4, zero.length);
        assertEquals(4, one.length);
        assertEquals(4, two.length);
        assertEquals(4, max.length);
        assertEquals(4, min.length);
        assertEquals(4, minusone.length);
    }

}
