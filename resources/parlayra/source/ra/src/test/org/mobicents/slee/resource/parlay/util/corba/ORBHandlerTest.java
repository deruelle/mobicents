package org.mobicents.slee.resource.parlay.util.corba;

import java.io.IOException;
import java.util.Properties;

import org.omg.CORBA.UserException;

import junit.framework.TestCase;

/**
 *
 * Class Description for ORBHandlerTest
 */
public class ORBHandlerTest extends TestCase {
    
    ORBHandler handler;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        handler = ORBHandler.getInstance();
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        
        try {
            handler.shutdown();
        }
        catch(Exception e) {
            
        }
    }

    public void testGetInstance() {
        try {
            assertEquals(handler, ORBHandler.getInstance());
        }
        catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
    

    public void testInit() {
        try {
            handler.init();
        }
        catch (UserException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testRun() {
        // tested implicitly in init
    }

    public void testGetIsServerReady() {
        assertFalse(handler.getIsServerReady());
        
        testInit();
        
        assertTrue(handler.getIsServerReady());
    }

    public void testGetOrb() {
        assertNull(handler.getOrb());
        
        testInit();

        assertNotNull(handler.getOrb());
    }

    public void testGetRootPOA() {
        assertNull(handler.getRootPOA());
        
        testInit();

        assertNotNull(handler.getRootPOA());
    }

    public void testGetOrbProperties() {
        assertNotNull(handler.getOrbProperties());
    }

    public void testShutdown() {
        
        handler.shutdown();
        
        testInit();
        
        handler.shutdown();
    }

    public void testSetOrbProperties() {
        Properties properties = new Properties();
        handler.setOrbProperties(properties);
        assertEquals(properties, handler.getOrbProperties());
    }

}
