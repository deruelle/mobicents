package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpNotificationRequested;
import org.csapi.cc.TpNotificationRequestedSetEntry;
import org.csapi.cc.mpccs.IpMultiPartyCallControlManager;
import org.csapi.cc.mpccs.TpMultiPartyCallIdentifier;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection;
import org.mobicents.slee.resource.parlay.csapi.jr.ParlayServiceActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.TpMultiPartyCallActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCallImpl;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCallStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManagerImpl;
import org.mobicents.slee.resource.parlay.fw.FwSession;
import org.mobicents.slee.resource.parlay.session.ORBStub;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.mobicents.slee.resource.parlay.session.ServiceSession;
import org.mobicents.slee.resource.parlay.util.ResourceIDFactory;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

/**
 * 
 * Class Description for MultiPartyCallControlManagerImplTest
 */
public class MultiPartyCallControlManagerImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        mpccsListenerControl = MockControl.createControl(MpccsListener.class);

        mpccsListener = (MpccsListener) mpccsListenerControl.getMock();

        activityManagerControl = MockControl
                .createControl(ActivityManager.class);

        activityManager = (ActivityManager) activityManagerControl.getMock();

        ipMultiPartyCallControlManagerControl = MockControl
                .createControl(IpMultiPartyCallControlManager.class);

        ipMultiPartyCallControlManager = (IpMultiPartyCallControlManager) ipMultiPartyCallControlManagerControl
                .getMock();

        fwSessionControl = MockControl.createControl(FwSession.class);

        fwSession = (FwSession) fwSessionControl.getMock();

        multiPartyCallControlManagerImpl = new MultiPartyCallControlManagerImpl(
                serviceIdentifier, ipMultiPartyCallControlManager, fwSession,
                activityManager, mpccsListener);
    }

    MultiPartyCallControlManagerImpl multiPartyCallControlManagerImpl;

    final TpServiceIdentifier serviceIdentifier = new TpServiceIdentifier(1);

    MpccsListener mpccsListener;
    MockControl mpccsListenerControl;

    ActivityManager activityManager;
    MockControl activityManagerControl;

    IpMultiPartyCallControlManager ipMultiPartyCallControlManager;
    MockControl ipMultiPartyCallControlManagerControl;

    FwSession fwSession;
    MockControl fwSessionControl;

    public void testMultiPartyCallControlManagerImpl() {
        assertEquals(ipMultiPartyCallControlManager,
                multiPartyCallControlManagerImpl
                        .getIpMultiPartyCallControlManager());
    }

    public void testInit() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        POA rootPOA = new POAStub();
        

        // GetrootPoa called many times create policies x 1, createPOA x 2 for
        // each POA
        fwSession.getRootPOA();
        fwSessionControl.setReturnValue(rootPOA, 7);

         
            // Can't compare callback for equality as reference is internal to
            // class
            // so just make sure method is called
            ipMultiPartyCallControlManager.setCallback(null);
            ipMultiPartyCallControlManagerControl
                    .setMatcher(MockControl.ALWAYS_MATCHER);
         

        ipMultiPartyCallControlManagerControl.replay();

        fwSessionControl.replay();

         
            multiPartyCallControlManagerImpl.init();
         

        ipMultiPartyCallControlManagerControl.verify();

        fwSessionControl.verify();
    }

    private void init() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        POA rootPOA = new POAStub();
        

        // GetrootPoa called many times create policies x 1, createPOA x 2 for
        // each POA
        fwSession.getRootPOA();
        fwSessionControl.setReturnValue(rootPOA, 7);

         
            // Can't compare callback for equality as reference is internal to
            // class
            // so just make sure method is called
            ipMultiPartyCallControlManager.setCallback(null);
            ipMultiPartyCallControlManagerControl
                    .setMatcher(MockControl.ALWAYS_MATCHER);

            activityManager.add(new ParlayServiceActivityHandle(
                    serviceIdentifier), serviceIdentifier);
            activityManager.activityStartedSuspended(new ParlayServiceActivityHandle(
                    serviceIdentifier));
         

        activityManagerControl.replay();

        ipMultiPartyCallControlManagerControl.replay();

        fwSessionControl.replay();

        
            multiPartyCallControlManagerImpl.init();
         

        activityManagerControl.verify();

        ipMultiPartyCallControlManagerControl.verify();

        fwSessionControl.verify();
    }

    public void testGetType() {
        assertEquals(ServiceSession.MultiPartyCallControl,
                multiPartyCallControlManagerImpl.getType());
    }

    public void testGetTpServiceIdentifier() {
        assertEquals(serviceIdentifier, multiPartyCallControlManagerImpl
                .getTpServiceIdentifier());
    }

    public void testDestroy() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        multiPartyCallControlManagerImpl.destroy();

        ipMultiPartyCallControlManagerControl.verify();

        fwSessionControl.verify();
    }



    public void testGetIpAppMultiPartyCallControlManagerImpl() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        assertNull(multiPartyCallControlManagerImpl
                .getIpAppMultiPartyCallControlManagerImpl());
        init();
        assertNotNull(multiPartyCallControlManagerImpl
                .getIpAppMultiPartyCallControlManagerImpl());
    }

    public void testGetIpAppMultiPartyCallImpl() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        assertNull(multiPartyCallControlManagerImpl
                .getIpAppMultiPartyCallImpl());
        init();
        assertNotNull(multiPartyCallControlManagerImpl
                .getIpAppMultiPartyCallImpl());
    }

    public void testGetIpMultiPartyCallControlManager() {
        assertEquals(ipMultiPartyCallControlManager,
                multiPartyCallControlManagerImpl
                        .getIpMultiPartyCallControlManager());
    }

    public void testGetIpAppCallLegPOA() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        assertNull(multiPartyCallControlManagerImpl.getIpAppCallLegPOA());
        init();
        assertNotNull(multiPartyCallControlManagerImpl.getIpAppCallLegPOA());
    }

    public void testGetMultiPartyCall() {
        MultiPartyCall multiPartyCall = new MultiPartyCallImpl(
                multiPartyCallControlManagerImpl, null, null,
                MpccsTestData.CALL_LEG_SESSION_ID, activityManager,
                mpccsListener, null);

        multiPartyCallControlManagerImpl.addMultiPartyCall(
                MpccsTestData.CALL_SESSION_ID, multiPartyCall);

        assertEquals(multiPartyCall, multiPartyCallControlManagerImpl
                .getMultiPartyCall(MpccsTestData.CALL_SESSION_ID));

        assertEquals(multiPartyCall, multiPartyCallControlManagerImpl
                .removeMultiPartyCall(MpccsTestData.CALL_SESSION_ID));

        assertNull(multiPartyCallControlManagerImpl
                .getMultiPartyCall(MpccsTestData.CALL_SESSION_ID));
    }

    public void testCreateCall() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        ipMultiPartyCallControlManagerControl.reset();

        activityManagerControl.reset();

        TpMultiPartyCallIdentifier callIdentifier = new TpMultiPartyCallIdentifier(
                MpccsTestData.IP_MULTI_PARTY_CALL,
                MpccsTestData.CALL_SESSION_ID);
         
            ipMultiPartyCallControlManager
                    .createCall(multiPartyCallControlManagerImpl
                            .getIpAppMultiPartyCall());
            ipMultiPartyCallControlManagerControl
                    .setReturnValue(callIdentifier);

            activityManager
                    .add(
                            new TpMultiPartyCallActivityHandle(
                                    new org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier(
                                            ResourceIDFactory.getCurrentID() + 1,
                                            MpccsTestData.CALL_SESSION_ID)),
                            new org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier(
                                    ResourceIDFactory.getCurrentID() + 1,
                                    MpccsTestData.CALL_SESSION_ID));
            activityManager
                    .activityStartedSuspended(new TpMultiPartyCallActivityHandle(
                            new org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier(
                                    ResourceIDFactory.getCurrentID() + 1,
                                    MpccsTestData.CALL_SESSION_ID)));
         

        activityManagerControl.replay();
        ipMultiPartyCallControlManagerControl.replay();

         
            org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier result = multiPartyCallControlManagerImpl
                    .createCall();

            assertEquals(callIdentifier.CallSessionID, result
                    .getCallSessionID());

            assertEquals(result, multiPartyCallControlManagerImpl
                    .getMultiPartyCall(MpccsTestData.CALL_SESSION_ID)
                    .getTpMultiPartyCallIdentifier());
         

        activityManagerControl.verify();

        ipMultiPartyCallControlManagerControl.verify();

    }

    public void testCreateNotification() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException, P_INVALID_EVENT_TYPE, P_INVALID_CRITERIA {
        init();

        ipMultiPartyCallControlManagerControl.reset();
         
            ipMultiPartyCallControlManager.createNotification(
                    multiPartyCallControlManagerImpl
                            .getIpAppMultiPartyCallControlManager(),
                    MpccsTestData.TP_CALL_NOTIFCATION_REQUEST);
            ipMultiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.ASSIGNMENT_ID);
         

        ipMultiPartyCallControlManagerControl.replay();

         
            int result = multiPartyCallControlManagerImpl
                    .createNotification(MpccsTestData.TP_CALL_NOTIFCATION_REQUEST);

            assertEquals(MpccsTestData.ASSIGNMENT_ID, result);
         

        ipMultiPartyCallControlManagerControl.verify();
    }

    public void testDestroyNotification() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException, P_INVALID_ASSIGNMENT_ID {
        init();

        ipMultiPartyCallControlManagerControl.reset();
         
            ipMultiPartyCallControlManager
                    .destroyNotification(MpccsTestData.ASSIGNMENT_ID);
         

        ipMultiPartyCallControlManagerControl.replay();

       
            multiPartyCallControlManagerImpl
                    .destroyNotification(MpccsTestData.ASSIGNMENT_ID);
         

        ipMultiPartyCallControlManagerControl.verify();
    }

    public void testChangeNotification() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException, P_INVALID_ASSIGNMENT_ID, P_INVALID_EVENT_TYPE, P_INVALID_CRITERIA {
        init();

        ipMultiPartyCallControlManagerControl.reset();

        
            ipMultiPartyCallControlManager.changeNotification(
                    MpccsTestData.ASSIGNMENT_ID,
                    MpccsTestData.TP_CALL_NOTIFCATION_REQUEST);
        

        ipMultiPartyCallControlManagerControl.replay();

         
            multiPartyCallControlManagerImpl.changeNotification(
                    MpccsTestData.ASSIGNMENT_ID,
                    MpccsTestData.TP_CALL_NOTIFCATION_REQUEST);
        

        ipMultiPartyCallControlManagerControl.verify();
    }

    public void testGetNotification() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        ipMultiPartyCallControlManagerControl.reset();
         
            ipMultiPartyCallControlManager.getNotification();
            ipMultiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.TP_NOTIFCATION_REQUESTED_ARRAY);
         

        ipMultiPartyCallControlManagerControl.replay();

        
            TpNotificationRequested[] result = multiPartyCallControlManagerImpl
                    .getNotification();

            assertEquals(MpccsTestData.TP_NOTIFCATION_REQUESTED_ARRAY, result);
         

        ipMultiPartyCallControlManagerControl.verify();
    }

    public void testSetCallLoadControl() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException, P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN {
        init();

        ipMultiPartyCallControlManagerControl.reset();
        
            ipMultiPartyCallControlManager.setCallLoadControl(
                    MpccsTestData.DURATION,
                    MpccsTestData.TP_CALL_LOAD_CONTROL_MECHANISM,
                    MpccsTestData.TP_CALL_TREATMENT,
                    MpccsTestData.TP_ADDRESS_RANGE);

            ipMultiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.ASSIGNMENT_ID);
         

        ipMultiPartyCallControlManagerControl.replay();

        
            int result = multiPartyCallControlManagerImpl.setCallLoadControl(
                    MpccsTestData.DURATION,
                    MpccsTestData.TP_CALL_LOAD_CONTROL_MECHANISM,
                    MpccsTestData.TP_CALL_TREATMENT,
                    MpccsTestData.TP_ADDRESS_RANGE);

            assertEquals(MpccsTestData.ASSIGNMENT_ID, result);
        

        ipMultiPartyCallControlManagerControl.verify();
    }

    public void testEnableNotifications() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        ipMultiPartyCallControlManagerControl.reset();
        
            ipMultiPartyCallControlManager
                    .enableNotifications(multiPartyCallControlManagerImpl
                            .getIpAppMultiPartyCallControlManager());
            ipMultiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.ASSIGNMENT_ID);
         

        ipMultiPartyCallControlManagerControl.replay();

        
            int result = multiPartyCallControlManagerImpl.enableNotifications();

            assertEquals(MpccsTestData.ASSIGNMENT_ID, result);
        

        ipMultiPartyCallControlManagerControl.verify();
    }

    public void testDisableNotifications() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        ipMultiPartyCallControlManagerControl.reset();
     
            ipMultiPartyCallControlManager.disableNotifications();
       

        ipMultiPartyCallControlManagerControl.replay();

      
            multiPartyCallControlManagerImpl.disableNotifications();
        

        ipMultiPartyCallControlManagerControl.verify();
    }

    public void testGetNextNotification() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        ipMultiPartyCallControlManagerControl.reset();
       
            ipMultiPartyCallControlManager.getNextNotification(true);
            ipMultiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.TP_NOTIFICATION_SET_ENTRY);
         

        ipMultiPartyCallControlManagerControl.replay();

        
            TpNotificationRequestedSetEntry result = multiPartyCallControlManagerImpl
                    .getNextNotification(true);

            assertEquals(MpccsTestData.TP_NOTIFICATION_SET_ENTRY, result);
      

        ipMultiPartyCallControlManagerControl.verify();
    }

    public void testReportNotification() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();


        mpccsListener.onReportNotificationEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallControlManagerImpl.reportNotification(
                MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER,
                MpccsTestData.TP_SLEE_CALL_LEG_IDENTIFIER_ARRAY,
                MpccsTestData.TP_CALL_NOTIFICATION_INFO,
                MpccsTestData.ASSIGNMENT_ID);

        mpccsListenerControl.verify();
        activityManagerControl.verify();
    }

    public void testCallAborted() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        // Activity destroyed

        multiPartyCallControlManagerImpl.addMultiPartyCall(
                MpccsTestData.CALL_SESSION_ID, new MultiPartyCallStub());

        activityManagerControl.reset();
        activityManager.remove(MpccsTestData.callActivityHandle,
                MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER);
        activityManager.activityEnding(MpccsTestData.callActivityHandle);
        activityManagerControl.replay();

        mpccsListener.onCallAbortedEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallControlManagerImpl
                .callAborted(MpccsTestData.CALL_SESSION_ID);

        mpccsListenerControl.verify();
        activityManagerControl.verify();
    }

    public void testManagerInterrupted() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        mpccsListener.onManagerInterruptedEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallControlManagerImpl.managerInterrupted();

        mpccsListenerControl.verify();
    }

    public void testManagerResumed() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        mpccsListener.onManagerResumedEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallControlManagerImpl.managerResumed();

        mpccsListenerControl.verify();
    }

    public void testCallOverloadEncountered() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        mpccsListener.onCallOverloadEncounteredEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallControlManagerImpl
                .callOverloadEncountered(MpccsTestData.ASSIGNMENT_ID);

        mpccsListenerControl.verify();
    }

    public void testCallOverloadCeased() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        mpccsListener.onCallOverloadCeasedEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallControlManagerImpl
                .callOverloadCeased(MpccsTestData.ASSIGNMENT_ID);

        mpccsListenerControl.verify();
    }

    public void testGetMultiPartyCallConnection() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        init();

        multiPartyCallControlManagerImpl.addMultiPartyCall(
                MpccsTestData.CALL_SESSION_ID, new MultiPartyCallStub());
        
      
            IpMultiPartyCallConnection callConnection = multiPartyCallControlManagerImpl.getIpMultiPartyCallConnection(MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER);
            
            assertNotNull(callConnection);
       
    }

    public void testClose() throws ResourceException {
      
            multiPartyCallControlManagerImpl.closeConnection();
       
    }

}