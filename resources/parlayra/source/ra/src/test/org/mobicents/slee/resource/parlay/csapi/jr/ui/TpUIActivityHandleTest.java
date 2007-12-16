
package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import junit.framework.TestCase;

import org.mobicents.csapi.jr.slee.ui.TpUIIdentifier;

/**
 *
 **/
public class TpUIActivityHandleTest extends TestCase {

    public void testHashCode() {
        TpUIActivityHandle uiActivityHandle = new TpUIActivityHandle(UiTestData.SLEE_TP_UI_IDENTIFIER);

        assertEquals(1, uiActivityHandle.hashCode());

    }

    public void testGetUIIdentifier() {
        TpUIActivityHandle uiActivityHandle = new TpUIActivityHandle(UiTestData.SLEE_TP_UI_IDENTIFIER);
        
        assertEquals(UiTestData.SLEE_TP_UI_IDENTIFIER, uiActivityHandle.getUIIdentifier());
    }

    /*
     * Class under test for boolean equals(Object)
     */
    public void testEqualsObject() {
        TpUIActivityHandle uiActivityHandle = new TpUIActivityHandle(UiTestData.SLEE_TP_UI_IDENTIFIER);
         
        uiActivityHandle.equals(new TpUIActivityHandle(new TpUIIdentifier(0,1)));
    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
        TpUIActivityHandle uiActivityHandle = new TpUIActivityHandle(UiTestData.SLEE_TP_UI_IDENTIFIER);
        
        assertEquals("TpUIActivityHandle:uiIdentifier,org.mobicents.csapi.jr.slee.ui.TpUIIdentifier@1;", uiActivityHandle.toString());

    }

}
