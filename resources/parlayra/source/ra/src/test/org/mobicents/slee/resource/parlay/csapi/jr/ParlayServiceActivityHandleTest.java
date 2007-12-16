package org.mobicents.slee.resource.parlay.csapi.jr;

import junit.framework.TestCase;

import org.mobicents.csapi.jr.slee.TpServiceIdentifier;

/**
 * 
 * Class Description for ParlayServiceActivityHandleTest
 */
public class ParlayServiceActivityHandleTest extends TestCase {

    public void testGetServiceIdentifier() {
        TpServiceIdentifier identifier = new TpServiceIdentifier(1);

        ParlayServiceActivityHandle activityHandle = new ParlayServiceActivityHandle(
                identifier);

        assertEquals(identifier, activityHandle.getServiceIdentifier());
    }
    
    public void testEquals() {
        TpServiceIdentifier identifier1 = new TpServiceIdentifier(1);
        TpServiceIdentifier identifier2 = new TpServiceIdentifier(2);

        ParlayServiceActivityHandle activityHandle1 = new ParlayServiceActivityHandle(
                identifier1);

        ParlayServiceActivityHandle activityHandle2 = new ParlayServiceActivityHandle(
                identifier2);

        ParlayServiceActivityHandle activityHandle3 = new ParlayServiceActivityHandle(
                identifier1);

        assertEquals(activityHandle1, activityHandle3);

        assertTrue(activityHandle1 != activityHandle2);
        
    }
    
    public void testHashCode() {
        TpServiceIdentifier identifier1 = new TpServiceIdentifier(1);

        ParlayServiceActivityHandle activityHandle1 = new ParlayServiceActivityHandle(
                identifier1);

        ParlayServiceActivityHandle activityHandle2 = new ParlayServiceActivityHandle(
                identifier1);
        
        assertEquals(activityHandle1.hashCode(), activityHandle2.hashCode());
        
    }
    
    public void testToString() {
        TpServiceIdentifier identifier = new TpServiceIdentifier(1);

        ParlayServiceActivityHandle activityHandle = new ParlayServiceActivityHandle(
                identifier);
        
        activityHandle.toString();
        
    }

}