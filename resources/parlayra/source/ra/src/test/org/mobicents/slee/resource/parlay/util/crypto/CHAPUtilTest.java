
package org.mobicents.slee.resource.parlay.util.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mobicents.slee.resource.parlay.util.Convert;

import junit.framework.TestCase;

/**
 *
 **/
public class CHAPUtilTest extends TestCase {

    byte[] challenge = {0x61, 0x62, 0x63};
    
    byte identifier = 0x64; 
    byte[] responseName = {0x61, 0x62, 0x63};
    CHAPUtil util = null;
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        util = new CHAPUtil();

    }

    public void testGenerateCHAPRequestPacket() {
        
        byte[] result1 = util.generateCHAPRequestPacket(challenge);
        assertNotNull(result1);
        
        assertTrue((result1[0] == 1));
        
        byte[] result2 = util.generateCHAPRequestPacket(challenge);
        
        assertEquals(result1.length, result2.length);
        
    }

    public void testGenerateCHAPResponsePacket() {
        
        byte[] result1 = util.generateCHAPResponsePacket(identifier, challenge, responseName);
        assertNotNull(result1);
        
        assertTrue((result1[0] == 2));
        
        byte[] result2 = util.generateCHAPResponsePacket(identifier, challenge, responseName);
        
        assertEquals(result1.length, result2.length);
    }

    public void testGenerateMD5HashChallenge() {
        
        try {
            byte[] result1 = util.generateMD5HashChallenge(identifier, "shared secret", challenge);
            assertNotNull(result1);
            byte[] result2 = util.generateMD5HashChallenge(identifier, "shared secret", challenge);
            
            assertTrue(Convert.assertEquals(result1, result2));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGetMd5Digest() {
        
        try {
            MessageDigest result = util.getMd5Digest();
            assertEquals("MD5", result.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            fail();
        }
    }

}
