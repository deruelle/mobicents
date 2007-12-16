package org.mobicents.slee.resource.parlay.util.corba;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.csapi.cc.mpccs.IpMultiPartyCallControlManager;
import org.csapi.cc.mpccs.IpMultiPartyCallControlManagerHelper;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;

/**
 * 
 * Class Description for ServiceManagerFactoryTest
 */
public class ServiceManagerFactoryTest extends TestCase {

     

    IpMultiPartyCallControlManagerImpl servant;

    ORBHandler handler;

    POA poa;
    
    IpMultiPartyCallControlManager callControlManager;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        try {
            handler = ORBHandler.getInstance();
            handler.init();

            poa = POAFactory.createPOA(handler.getRootPOA(), "Test", handler
                    .getRootPOA().the_POAManager(), PolicyFactory
                    .createTransientPoaPolicies(handler.getRootPOA()));

            servant = new IpMultiPartyCallControlManagerImpl();
            
            org.omg.CORBA.Object obj = ServantActivationHelper
                    .activateServant(poa, servant);

            callControlManager = IpMultiPartyCallControlManagerHelper.narrow(obj);
        }
        catch (UserException e) {
            e.printStackTrace();
            fail();
        }
        catch (IOException e) {
            e.printStackTrace();
            fail();
        }

      
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        File file = new File("test");
        file.delete();

        handler.shutdown();
    }

    public void testWriteReference() {

        try {
            ServiceManagerFactory.writeReference(handler, callControlManager, "test");
            org.omg.CORBA.Object object = ServiceManagerFactory.readReference(handler, "test");
            
            callControlManager = IpMultiPartyCallControlManagerHelper.narrow(object);
            
            File file = new File("test");
            file.delete();
        }
        catch (CorbaUtilException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGetIpInterfaceFromFile() {

        try {
            ServiceManagerFactory.writeReference(handler, callControlManager, "test");
            org.omg.CORBA.Object object = ServiceManagerFactory.getIpInterfaceFromFile(handler, "test");
            
            callControlManager = IpMultiPartyCallControlManagerHelper.narrow(object);
            
        }
        catch (CorbaUtilException e) {
            e.printStackTrace();
            fail();
        } finally {
            File file = new File("test");
            file.delete();
        }
    }

    public void testLoadIpMultiPartyCallControlManager() {

        try {
            ServiceManagerFactory.writeReference(handler, callControlManager, "test");
            org.omg.CORBA.Object object = ServiceManagerFactory.loadIpMultiPartyCallControlManager(handler, "test");
            
            callControlManager = IpMultiPartyCallControlManagerHelper.narrow(object);
            
        }
        catch (CorbaUtilException e) {
            e.printStackTrace();
            fail();
        } finally {
            File file = new File("test");
            file.delete();
        }
    }

}