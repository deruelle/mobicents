
package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

/**
 *
 **/
public class FwSessionFactoryTest extends TestCase {
    
    FwSessionFactory fwSessionFactory;
    
    FwSessionProperties fwSessionProperties;
    
    FwSession fwSession;

    protected void setUp() throws Exception {
        super.setUp();
        
        fwSessionProperties = new FwSessionProperties();
    }
    
    public void testCreateFwSession() {       
        fwSession = FwSessionFactory.createFwSession(fwSessionProperties);
        assertNotNull(fwSession);
        assertTrue(fwSession.getClass() == BypassedFwSession.class);
    }

}
