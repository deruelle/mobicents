package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;

import junit.framework.TestCase;

import org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier;

/**
 * 
 * Class Description for TpCallLegActivityHandleTest
 */
public class TpCallLegActivityHandleTest extends TestCase {

    public void testGetCallLegIdentifier() {
        TpCallLegIdentifier identifier = new TpCallLegIdentifier(1,2);
        
        TpCallLegActivityHandle activityHandle = new TpCallLegActivityHandle(
                identifier);

        assertEquals(identifier, activityHandle.getCallLegIdentifier());
    }

    /*
     * Class under test for boolean equals(Object)
     */
    public void testEqualsObject() {
        TpCallLegIdentifier identifier1 = new TpCallLegIdentifier(1,2);
        
        TpCallLegIdentifier identifier2 = new TpCallLegIdentifier(2,3);
        
        TpCallLegActivityHandle activityHandle1 = new TpCallLegActivityHandle(
                identifier1);

        TpCallLegActivityHandle activityHandle2 = new TpCallLegActivityHandle(
                identifier2);

        TpCallLegActivityHandle activityHandle3 = new TpCallLegActivityHandle(
                identifier1);

        assertTrue(activityHandle1.equals(activityHandle3));

        assertTrue(!activityHandle1.equals(activityHandle2));
    }
    
    public void testToString() {
        TpCallLegIdentifier identifier = new TpCallLegIdentifier(1,2);
        
        TpCallLegActivityHandle activityHandle = new TpCallLegActivityHandle(
                identifier);
        
        activityHandle.toString();
        
    }
    
    public void testHashCode() {
        TpCallLegIdentifier identifier1 = new TpCallLegIdentifier(1,2);
        
        TpCallLegIdentifier identifier2 = new TpCallLegIdentifier(2,3);
        
        TpCallLegActivityHandle activityHandle1 = new TpCallLegActivityHandle(
                identifier1);

        TpCallLegActivityHandle activityHandle2 = new TpCallLegActivityHandle(
                identifier2);

        TpCallLegActivityHandle activityHandle3 = new TpCallLegActivityHandle(
                identifier1);

        assertTrue(activityHandle1.hashCode() == activityHandle3.hashCode());
    }

}