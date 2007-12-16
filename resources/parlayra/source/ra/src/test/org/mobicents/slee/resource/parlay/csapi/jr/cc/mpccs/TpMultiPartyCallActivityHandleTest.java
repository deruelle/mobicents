package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;

import junit.framework.TestCase;

import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;

/**
 *
 * Class Description for TpMultiPartyCallActivityHandleTest
 */
public class TpMultiPartyCallActivityHandleTest extends TestCase {


    public void testGetMultiPartyCallIdentifier() {
        TpMultiPartyCallIdentifier identifier = new TpMultiPartyCallIdentifier(1,2);
        
        TpMultiPartyCallActivityHandle activityHandle = new TpMultiPartyCallActivityHandle(
                identifier);

        assertEquals(identifier, activityHandle.getMultiPartyCallIdentifier());
    }

    /*
     * Class under test for boolean equals(Object)
     */
    public void testEqualsObject() {
        TpMultiPartyCallIdentifier identifier1 = new TpMultiPartyCallIdentifier(1,2);

        TpMultiPartyCallIdentifier identifier2 = new TpMultiPartyCallIdentifier(1,3);

        TpMultiPartyCallActivityHandle activityHandle1 = new TpMultiPartyCallActivityHandle(
                identifier1);

        TpMultiPartyCallActivityHandle activityHandle2 = new TpMultiPartyCallActivityHandle(
                identifier2);

        TpMultiPartyCallActivityHandle activityHandle3 = new TpMultiPartyCallActivityHandle(
                identifier1);

        assertTrue(activityHandle1.equals(activityHandle3));

        assertTrue(!activityHandle1.equals(activityHandle2));
    }
    
    public void testToString() {
        TpMultiPartyCallIdentifier identifier = new TpMultiPartyCallIdentifier(1,2);
        
        TpMultiPartyCallActivityHandle activityHandle = new TpMultiPartyCallActivityHandle(
                identifier);
        
        activityHandle.toString();
        
    }
    
    public void testHashCode() {
        TpMultiPartyCallIdentifier identifier1 = new TpMultiPartyCallIdentifier(1,2);

        TpMultiPartyCallIdentifier identifier2 = new TpMultiPartyCallIdentifier(1,3);

        TpMultiPartyCallActivityHandle activityHandle1 = new TpMultiPartyCallActivityHandle(
                identifier1);

        TpMultiPartyCallActivityHandle activityHandle2 = new TpMultiPartyCallActivityHandle(
                identifier2);

        TpMultiPartyCallActivityHandle activityHandle3 = new TpMultiPartyCallActivityHandle(
                identifier1);

        assertTrue(activityHandle1.hashCode() == activityHandle3.hashCode());
        
    }

}
