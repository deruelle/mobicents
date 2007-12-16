package org.mobicents.slee.resource.parlay.util.corba;

import java.io.IOException;

import org.omg.CORBA.UserException;

import junit.framework.TestCase;

/**
 * 
 * Class Description for PolicyFactoryTest
 */
public class PolicyFactoryTest extends TestCase {

    PolicyFactory factory;

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

        factory = new PolicyFactory();
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

    public void testCreatePeristentPoaPolicies() {
        org.omg.CORBA.Policy[] policies = PolicyFactory
                .createPeristentPoaPolicies(handler.getRootPOA());
    }

    public void testCreateTransientPoaPolicies() {
        org.omg.CORBA.Policy[] policies = PolicyFactory
                .createTransientPoaPolicies(handler.getRootPOA());
    }

}