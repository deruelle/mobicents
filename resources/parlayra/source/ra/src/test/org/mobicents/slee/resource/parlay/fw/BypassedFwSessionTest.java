package org.mobicents.slee.resource.parlay.fw;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.csapi.IpInterface;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddressRange;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallLoadControlMechanism;
import org.csapi.cc.TpCallNotificationRequest;
import org.csapi.cc.TpCallTreatment;
import org.csapi.cc.TpNotificationRequested;
import org.csapi.cc.TpNotificationRequestedSetEntry;
import org.csapi.cc.mpccs.IpAppMultiPartyCall;
import org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager;
import org.csapi.cc.mpccs.IpMultiPartyCallControlManagerHelper;
import org.csapi.cc.mpccs.IpMultiPartyCallControlManagerPOA;
import org.csapi.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.csapi.jr.slee.fw.TerminateAccessEvent;
import org.mobicents.csapi.jr.slee.fw.TerminateServiceAgreementEvent;
import org.mobicents.slee.resource.parlay.util.corba.CorbaUtilException;
import org.mobicents.slee.resource.parlay.util.corba.ORBHandler;
import org.mobicents.slee.resource.parlay.util.corba.ServiceManagerFactory;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * 
 * Class Description for BypassedFwSessionTest
 */
public class BypassedFwSessionTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        servant = new Servant();

        listener = new Listener();

        testProperties = new TestProperties();
        testProperties.setByPassFwEnabled(true);
        testProperties.setIpMultiPartyCallControlManagerFileName("test.ior");

        properties = new FwSessionProperties();

        fwSession = new BypassedFwSession(properties, testProperties);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        File file = new File("test.ior");

        file.delete();
    }

    BypassedFwSession fwSession;

    Listener listener;

    Servant servant;

    FwSessionProperties properties;

    TestProperties testProperties;

    public void testInit() throws FwSessionException {

        fwSession.init();

    }

    public void testAddFwSessionListener() {
        fwSession.addFwSessionListener(listener);
        fwSession.addFwSessionListener(listener);
    }

    public void testRemoveFwSessionListener() {
        fwSession.removeFwSessionListener(listener);
        fwSession.addFwSessionListener(listener);
        fwSession.removeFwSessionListener(listener);
    }

    public void testAuthenticate() throws FwSessionException {

        fwSession.authenticate();

    }

    public void testEndAccess() throws FwSessionException {

        fwSession.endAccess(null);

    }

    public void testReleaseService() throws FwSessionException {

        fwSession.releaseService(null);

    }

    public void testGetService() throws FwSessionException, ServantAlreadyActive, WrongPolicy, ServantNotActive, CorbaUtilException, IOException {

        fwSession.init();

        fwSession.getRootPOA()
                .activate_object(servant);

        org.omg.CORBA.Object object = fwSession.getRootPOA()
                .servant_to_reference(servant);

        ServiceManagerFactory.writeReference(ORBHandler.getInstance(), object,
                testProperties.getIpMultiPartyCallControlManagerFileName());
        
        ServiceAndToken serviceAndToken = fwSession.getService("P_MULTI_PARTY_CALL_CONTROL", null);
        
        IpMultiPartyCallControlManagerHelper.narrow(serviceAndToken.getIpService());
        
        File file = new File(testProperties.getIpMultiPartyCallControlManagerFileName());
        file.delete();

    }

    public void testShutdown() {
        fwSession.shutdown();
    }

    public void testGetORB() throws FwSessionException, IOException {
        assertEquals(null, fwSession.getORB());

        fwSession.init();

        assertEquals(ORBHandler.getInstance().getOrb(), fwSession.getORB());

    }

    public void testGetFwSessionProperties() {
        assertEquals(properties, fwSession.getFwSessionProperties());
    }

    public void testGetRootPOA() throws FwSessionException, IOException {
        assertEquals(null, fwSession.getRootPOA());

        fwSession.init();

        assertEquals(ORBHandler.getInstance().getRootPOA(), fwSession.getRootPOA());

    }

    private class Listener implements FwSessionListener {

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.fw.FwSessionListener#terminateAccess(org.mobicents.slee.resource.parlay.fw.access.TerminateAccessEvent)
         */
        public void terminateAccess(TerminateAccessEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.fw.FwSessionListener#terminateServiceAgreement(org.mobicents.slee.resource.parlay.fw.application.TerminateServiceAgreementEvent)
         */
        public void terminateServiceAgreement(
                TerminateServiceAgreementEvent event) {
            //  Auto-generated method stub

        }

    }

    private class Servant extends IpMultiPartyCallControlManagerPOA {

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#createCall(org.csapi.cc.mpccs.IpAppMultiPartyCall)
         */
        public TpMultiPartyCallIdentifier createCall(IpAppMultiPartyCall appCall)
                throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#createNotification(org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager,
         *      org.csapi.cc.TpCallNotificationRequest)
         */
        public int createNotification(
                IpAppMultiPartyCallControlManager appCallControlManager,
                TpCallNotificationRequest notificationRequest)
                throws P_INVALID_INTERFACE_TYPE, P_INVALID_EVENT_TYPE,
                TpCommonExceptions, P_INVALID_CRITERIA {
            //  Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#destroyNotification(int)
         */
        public void destroyNotification(int assignmentID)
                throws P_INVALID_ASSIGNMENT_ID, TpCommonExceptions {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#changeNotification(int,
         *      org.csapi.cc.TpCallNotificationRequest)
         */
        public void changeNotification(int assignmentID,
                TpCallNotificationRequest notificationRequest)
                throws P_INVALID_ASSIGNMENT_ID, P_INVALID_EVENT_TYPE,
                TpCommonExceptions, P_INVALID_CRITERIA {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#getNotification()
         */
        public TpNotificationRequested[] getNotification()
                throws TpCommonExceptions {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#setCallLoadControl(int,
         *      org.csapi.cc.TpCallLoadControlMechanism,
         *      org.csapi.cc.TpCallTreatment, org.csapi.TpAddressRange)
         */
        public int setCallLoadControl(int duration,
                TpCallLoadControlMechanism mechanism,
                TpCallTreatment treatment, TpAddressRange addressRange)
                throws TpCommonExceptions, P_INVALID_ADDRESS,
                P_UNSUPPORTED_ADDRESS_PLAN {
            //  Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#enableNotifications(org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager)
         */
        public int enableNotifications(
                IpAppMultiPartyCallControlManager appCallControlManager)
                throws TpCommonExceptions {
            //  Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#disableNotifications()
         */
        public void disableNotifications() throws TpCommonExceptions {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#getNextNotification(boolean)
         */
        public TpNotificationRequestedSetEntry getNextNotification(boolean reset)
                throws TpCommonExceptions {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
         */
        public void setCallback(IpInterface appInterface)
                throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.IpServiceOperations#setCallbackWithSessionID(org.csapi.IpInterface,
         *      int)
         */
        public void setCallbackWithSessionID(IpInterface appInterface,
                int sessionID) throws P_INVALID_INTERFACE_TYPE,
                TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

    }
}