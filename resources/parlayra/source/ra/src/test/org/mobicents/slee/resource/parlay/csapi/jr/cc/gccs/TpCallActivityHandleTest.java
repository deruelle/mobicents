
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs;

import org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier;

import junit.framework.TestCase;

/**
 *
 **/
public class TpCallActivityHandleTest extends TestCase {

    public void testHashCode() {
        TpCallActivityHandle callActivityHandle = new TpCallActivityHandle(GccsTestData.SLEE_TP_CALL_IDENTIFIER);

        assertEquals(1, callActivityHandle.hashCode());

    }

    public void testGetCallIdentifier() {
        TpCallActivityHandle callActivityHandle = new TpCallActivityHandle(GccsTestData.SLEE_TP_CALL_IDENTIFIER);
        
        assertEquals(GccsTestData.SLEE_TP_CALL_IDENTIFIER, callActivityHandle.getCallIdentifier());
    }

    /*
     * Class under test for boolean equals(Object)
     */
    public void testEqualsObject() {
        TpCallActivityHandle callActivityHandle = new TpCallActivityHandle(GccsTestData.SLEE_TP_CALL_IDENTIFIER);
         
        callActivityHandle.equals(new TpCallActivityHandle(new TpCallIdentifier(0,1)));
    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
        TpCallActivityHandle callActivityHandle = new TpCallActivityHandle(GccsTestData.SLEE_TP_CALL_IDENTIFIER);
        
        assertEquals("TpCallActivityHandle:callIdentifier,org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier@1;", callActivityHandle.toString());

    }

}
