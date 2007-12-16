package org.mobicents.slee.resource.parlay.util.corba;

import java.io.IOException;

import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;

import junit.framework.TestCase;

/**
 * 
 * Class Description for POAFactoryTest
 */
public class POAFactoryTest extends TestCase {

    POAFactory factory;

    ORBHandler handler;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        try {
            handler = ORBHandler.getInstance();
            handler.init();
        }
        catch (UserException e) {
            e.printStackTrace();
            fail();
        }
        catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        factory = new POAFactory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();

        handler.shutdown();
    }

    public void testCreatePOA() {
        try {
            POA poa = POAFactory.createPOA(handler.getRootPOA(), "Test", handler
                    .getRootPOA().the_POAManager(), PolicyFactory
                    .createTransientPoaPolicies(handler.getRootPOA()));
            
            assertNotNull(poa);
        }
        catch (AdapterAlreadyExists e) {
            e.printStackTrace();
            fail();
        }
        catch (InvalidPolicy e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDestroyPOA() {
        try {
            POA poa = POAFactory.createPOA(handler.getRootPOA(), "Test", handler
                    .getRootPOA().the_POAManager(), PolicyFactory
                    .createTransientPoaPolicies(handler.getRootPOA()));
            
            POAFactory.destroyPOA(poa);
        }
        catch (AdapterAlreadyExists e) {
            e.printStackTrace();
            fail();
        }
        catch (InvalidPolicy e) {
            e.printStackTrace();
            fail();
        }
    }

}