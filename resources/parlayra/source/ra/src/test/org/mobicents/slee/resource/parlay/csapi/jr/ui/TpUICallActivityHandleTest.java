package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import junit.framework.TestCase;

import org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier;

public class TpUICallActivityHandleTest extends TestCase {

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.TpUICallActivityHandle.hashCode()'
     */
    public void testHashCode() {
        TpUICallActivityHandle uiCallActivityHandle = new TpUICallActivityHandle(UiTestData.SLEE_TP_UICALL_IDENTIFIER);
 
        assertEquals(1, uiCallActivityHandle.hashCode());
    }

 
    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.TpUICallActivityHandle.getUICallIdentifier()'
     */
    public void testGetUICallIdentifier() {
        TpUICallActivityHandle uiCallActivityHandle = new TpUICallActivityHandle(UiTestData.SLEE_TP_UICALL_IDENTIFIER);
        
        assertEquals(UiTestData.SLEE_TP_UICALL_IDENTIFIER, uiCallActivityHandle.getUICallIdentifier());
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.TpUICallActivityHandle.equals(Object)'
     */
    public void testEqualsObject() {
        TpUICallActivityHandle uiCallActivityHandle = new TpUICallActivityHandle(UiTestData.SLEE_TP_UICALL_IDENTIFIER);
        
        uiCallActivityHandle.equals(new TpUICallActivityHandle(new TpUICallIdentifier(0,1)));
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.TpUICallActivityHandle.toString()'
     */
    public void testToString() {
     TpUICallActivityHandle uiCallActivityHandle = new TpUICallActivityHandle(UiTestData.SLEE_TP_UICALL_IDENTIFIER);
        
        assertEquals("TpUICallActivityHandle:uiCallIdentifier,org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier@1;", uiCallActivityHandle.toString());

    }

}
