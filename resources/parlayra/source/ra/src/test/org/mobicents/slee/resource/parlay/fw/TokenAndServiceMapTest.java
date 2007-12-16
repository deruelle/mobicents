
package org.mobicents.slee.resource.parlay.fw;

import org.csapi.IpService;
import org.mobicents.slee.resource.parlay.session.IpServiceStub;

import junit.framework.TestCase;

/**
 *
 **/
public class TokenAndServiceMapTest extends TestCase {

    TokenAndServiceMap tokenAndServiceMap = null;
    
    IpService ipService = new IpServiceStub();
    String serviceToken = "123";
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        tokenAndServiceMap = new TokenAndServiceMap();
    }

    /*
     * Class under test for void put(String, IpService)
     */
    public void testPutStringIpService() {
        
        tokenAndServiceMap.put(serviceToken, ipService);
        
        assertEquals(ipService, tokenAndServiceMap.get(serviceToken));
    }

    /*
     * Class under test for void put(IpService, String)
     */
    public void testPutIpServiceString() {
        tokenAndServiceMap.put(ipService, serviceToken);
        
        assertEquals(serviceToken, tokenAndServiceMap.get(ipService));
    }


    /*
     * Class under test for IpService remove(String)
     */
    public void testRemoveString() {
        
        assertNull(tokenAndServiceMap.remove(serviceToken));
        
        tokenAndServiceMap.put(ipService, serviceToken);
        
        assertEquals(ipService, tokenAndServiceMap.remove(serviceToken));
        
        
    }

    /*
     * Class under test for String remove(IpService)
     */
    public void testRemoveIpService() {
        
        assertNull(tokenAndServiceMap.remove(ipService));
        
        tokenAndServiceMap.put(ipService, serviceToken);
        
        assertEquals(serviceToken, tokenAndServiceMap.remove(ipService));
    }

    public void testTokens() {
        assertEquals(0, tokenAndServiceMap.tokens().length);
        
        tokenAndServiceMap.put(ipService, serviceToken);
        
        assertEquals(1, tokenAndServiceMap.tokens().length);
        
    }

    public void testClear() {

        tokenAndServiceMap.put(ipService, serviceToken);
        
        assertEquals(1, tokenAndServiceMap.tokens().length);
        
        tokenAndServiceMap.clear();
        assertEquals(0, tokenAndServiceMap.tokens().length);
    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
        
        assertNotNull(tokenAndServiceMap.toString());
    }

}
