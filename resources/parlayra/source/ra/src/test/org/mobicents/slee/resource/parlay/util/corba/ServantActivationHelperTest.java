package org.mobicents.slee.resource.parlay.util.corba;

import java.io.IOException;

import junit.framework.TestCase;

import org.csapi.cc.mpccs.IpAppCallLeg;
import org.csapi.cc.mpccs.IpAppCallLegHelper;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.IpAppCallLegImpl;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;

/**
 * 
 * Class Description for ServantActivationHelperTest
 */
public class ServantActivationHelperTest extends TestCase {

    ServantActivationHelper helper;

    IpAppCallLegImpl servant;

    ORBHandler handler;
    
    POA poa;

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

        helper = new ServantActivationHelper();
        
        poa = POAFactory.createPOA(handler.getRootPOA(), "Test", handler
                .getRootPOA().the_POAManager(), PolicyFactory
                .createPeristentPoaPolicies(handler.getRootPOA()));

        servant = new IpAppCallLegImpl(null, handler.getRootPOA(), null);
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

    public void testActivateServantWithID() {
        try {
            org.omg.CORBA.Object obj = ServantActivationHelper
                    .activateServantWithID(poa, servant,
                            new byte[] { 1 });

            IpAppCallLeg appCallLeg = IpAppCallLegHelper.narrow(obj);
            assertNotNull(appCallLeg);
        }
        catch (UserException e) {
            e.printStackTrace();
            fail();
        }
    }

    /*
     * Class under test for void deactivateServant(Servant)
     */
    public void testDeactivateServantServant() {
        try {
            org.omg.CORBA.Object obj = ServantActivationHelper
                    .activateServantWithID(poa, servant,
                            new byte[] { 1 });
            assertNotNull(obj);
            ServantActivationHelper.deactivateServant(servant);
        }
        catch (UserException e) {
            e.printStackTrace();
            fail();
        }
    }

    /*
     * Class under test for void deactivateServant(POA, byte[])
     */
    public void testDeactivateServantPOAbyteArray() {
        try {
            org.omg.CORBA.Object obj = ServantActivationHelper
                    .activateServantWithID(poa, servant,
                            new byte[] { 1 });
            assertNotNull(obj);
            ServantActivationHelper.deactivateServant(poa,
                    new byte[] { 1 });
        }
        catch (UserException e) {
            e.printStackTrace();
            fail();
        }
    }

}