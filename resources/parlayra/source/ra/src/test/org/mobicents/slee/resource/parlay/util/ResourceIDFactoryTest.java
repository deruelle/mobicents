package org.mobicents.slee.resource.parlay.util;

import junit.framework.TestCase;

/**
 *
 * Class Description for ResourceIDFactoryTest
 */
public class ResourceIDFactoryTest extends TestCase {
    
    public void testResourceIDFactory() {
        ResourceIDFactory factory = new ResourceIDFactory();
    }

    public void testGetNextID() {
        int currentID = ResourceIDFactory.getCurrentID();
        int nextID = ResourceIDFactory.getNextID();
        
        assertEquals(currentID + 1, nextID);
        assertEquals(nextID, ResourceIDFactory.getCurrentID());
    }
}
