package org.mobicents.slee.resource.parlay.util;

import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_SESSION_ID;

import junit.framework.TestCase;

public class ParlayExceptionUtilTest extends TestCase {
    private final String REASON = "my reason";

    private final String EXTRA_INFO = "my extra info";

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.util.ParlayExceptionUtil.stringify(P_INVALID_SESSION_ID)'
     */
    public void testStringifyWithAReason() {

        final P_INVALID_SESSION_ID ex = new P_INVALID_SESSION_ID(REASON, EXTRA_INFO);
        String stringifiedP_INVALID_SESSION_ID = ParlayExceptionUtil
                .stringify(ex);                 
        assertEquals("org.csapi.P_INVALID_SESSION_ID: IDL:org/csapi/P_INVALID_SESSION_ID:1.0"
                + REASON + ", ExtraInformation = [" + EXTRA_INFO + "]", stringifiedP_INVALID_SESSION_ID);                
    }
    public void testStringifyWithNullReason() {

        String stringifiedP_INVALID_SESSION_ID = ParlayExceptionUtil
                .stringify(new P_INVALID_SESSION_ID( EXTRA_INFO));                 
        assertEquals("org.csapi.P_INVALID_SESSION_ID, ExtraInformation = [" + EXTRA_INFO + "]", stringifiedP_INVALID_SESSION_ID);        
    }
    
    public void testStringifyP_INVALID_INTERFACE_TYPEWithNullReason() {

        final P_INVALID_INTERFACE_TYPE ex = new P_INVALID_INTERFACE_TYPE( EXTRA_INFO);
        
        String stringifiedP_INVALID_INTERFACE_TYPE = ParlayExceptionUtil
                .stringify(ex);                 
        
        assertEquals("org.csapi.P_INVALID_INTERFACE_TYPE, ExtraInformation = [" + EXTRA_INFO + "]", stringifiedP_INVALID_INTERFACE_TYPE);   
     
    }
     
}
