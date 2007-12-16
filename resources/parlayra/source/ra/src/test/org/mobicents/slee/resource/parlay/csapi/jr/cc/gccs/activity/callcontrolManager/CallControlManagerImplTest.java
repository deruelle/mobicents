
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolManager;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.gccs.IpCall;
import org.csapi.cc.gccs.IpCallControlManager;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection;
import org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.ParlayServiceActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.TpCallActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManagerImpl;
import org.mobicents.slee.resource.parlay.fw.FwSession;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.mobicents.slee.resource.parlay.session.ServiceSession;
import org.mobicents.slee.resource.parlay.util.ResourceIDFactory;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;
import org.omg.PortableServer.POA;

/**
 *
 **/
public class CallControlManagerImplTest extends TestCase {
    
    MockControl fwSessionControl;
    
    FwSession mockFwSession;
    
    MockControl ipCallControlManagerControl;
    
    IpCallControlManager mockIpCallControlManager;
    
    CallControlManagerImpl callControlManagerImpl;
    
    MockControl gccsListenerControl;
    
    GccsListener mockGccsListener;
    
    MockControl activityManagerControl;
    
    ActivityManager mockActivityManager;
    
    MockControl ipCallControl;
    
    IpCall mockIpCall;

    TpServiceIdentifier serviceIdentifier;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        fwSessionControl = MockControl.createControl(FwSession.class);
        
        mockFwSession = (FwSession)fwSessionControl.getMock(); 
        
        ipCallControlManagerControl = MockControl.createControl(IpCallControlManager.class);
        
        mockIpCallControlManager = (IpCallControlManager)ipCallControlManagerControl.getMock(); 
        
        gccsListenerControl = MockControl.createControl(GccsListener.class);
        
        mockGccsListener = (GccsListener)gccsListenerControl.getMock(); 
        
        activityManagerControl = MockControl.createControl(ActivityManager.class);
        
        mockActivityManager = (ActivityManager)activityManagerControl.getMock(); 
        
        ipCallControl = MockControl.createControl(IpCall.class);
        
        mockIpCall = (IpCall)ipCallControl.getMock();
        
        serviceIdentifier = new TpServiceIdentifier(2);   
        
        callControlManagerImpl = new CallControlManagerImpl(mockFwSession, mockIpCallControlManager,
                mockGccsListener, mockActivityManager, serviceIdentifier);
    }

    public void testInit() {
        POA rootPOA = new POAStub();

        // GetrootPoa called many times create policies x 1, createPOA x 2 for
        // each POA
        mockFwSession.getRootPOA();
        fwSessionControl.setReturnValue(rootPOA, 5);
        
        
        
        try {
            mockIpCallControlManager.setCallback(null);
            ipCallControlManagerControl.setMatcher(MockControl.ALWAYS_MATCHER);
            
            mockActivityManager.add(new ParlayServiceActivityHandle(
                    serviceIdentifier), serviceIdentifier);
            mockActivityManager.activityStartedSuspended(new ParlayServiceActivityHandle(
                    serviceIdentifier));
            
            activityManagerControl.replay();
            ipCallControlManagerControl.replay();
            fwSessionControl.replay();
            
            callControlManagerImpl.init();
            
            activityManagerControl.verify();
            ipCallControlManagerControl.verify();
            fwSessionControl.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    public void testDestroy() {
        testInit();

        callControlManagerImpl.destroy();

        ipCallControlManagerControl.verify();

        fwSessionControl.verify();
    }

    public void testGetCall() {
        callControlManagerImpl.addCall(GccsTestData.SESSIONID, GccsTestData.CALL);
        assertEquals(GccsTestData.CALL, callControlManagerImpl.getCall(GccsTestData.SESSIONID));
    }

    public void testRemoveCall() {
        callControlManagerImpl.addCall(GccsTestData.SESSIONID, GccsTestData.CALL);
        assertEquals(GccsTestData.CALL, callControlManagerImpl.removeCall(GccsTestData.SESSIONID));
    }

    public void testCallAborted() {
        callControlManagerImpl.addCall(GccsTestData.SESSIONID, GccsTestData.CALL);

        callControlManagerImpl.callAborted(GccsTestData.SESSIONID);
    }

    public void testCallEventNotify() {
        
        activityManagerControl.reset();
        
        activityManagerControl.replay();
        
        mockGccsListener.onCallEventNotifiyEvent(null);
        gccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        ipCallControl.replay();

        callControlManagerImpl.callEventNotify(GccsTestData.SLEE_TP_CALL_IDENTIFIER, GccsTestData.TP_CALL_EVENT_INFO, GccsTestData.ASSIGNMENT_ID);

        activityManagerControl.verify();
        ipCallControl.verify();
        gccsListenerControl.verify();
    }

    public void testCallNotificationInterrupted() {
        
        mockGccsListener.onCallNotificationInterruptedEvent(null);
        gccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();       
        
        callControlManagerImpl.callNotificationInterrupted();

        gccsListenerControl.verify();
    }

    public void testCallNotificationContinued() {
        mockGccsListener.onCallNotificationContinuedEvent(null);
        gccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();       
        
        callControlManagerImpl.callNotificationContinued();

        gccsListenerControl.verify();
    }

    public void testCallOverloadEncountered() {
        mockGccsListener.onCallOverloadEncounteredEvent(null);
        gccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();       
        
        callControlManagerImpl.callOverloadEncountered(GccsTestData.ASSIGNMENT_ID);

        gccsListenerControl.verify();
    }

    public void testCallOverloadCeased() {
        mockGccsListener.onCallOverloadCeasedEvent(null);
        gccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();       
        
        callControlManagerImpl.callOverloadCeased(GccsTestData.ASSIGNMENT_ID);

        gccsListenerControl.verify();
    }

    public void testGetIpCallConnection() throws ResourceException {
        callControlManagerImpl.addCall(GccsTestData.SESSIONID, GccsTestData.CALL);
        
        IpCallConnection result = null;

        result = callControlManagerImpl.getIpCallConnection(GccsTestData.SLEE_TP_CALL_IDENTIFIER);
        
        assertNotNull(result);
    }

    public void testCreateCall() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException {
        testInit();

        ipCallControlManagerControl.reset();

        activityManagerControl.reset();

        org.csapi.cc.gccs.TpCallIdentifier callIdentifier = new org.csapi.cc.gccs.TpCallIdentifier(
                mockIpCall,
                GccsTestData.SESSIONID);
        
        mockIpCallControlManager
                .createCall(callControlManagerImpl
                        .getIpAppCall());
        ipCallControlManagerControl
                .setReturnValue(callIdentifier);

        mockActivityManager
                .add(
                        new TpCallActivityHandle(
                                new org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier(
                                        ResourceIDFactory.getCurrentID() + 1,
                                        GccsTestData.SESSIONID)),
                        new org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier(
                                ResourceIDFactory.getCurrentID() + 1,
                                GccsTestData.SESSIONID));
        mockActivityManager
                .activityStartedSuspended(new TpCallActivityHandle(
                        new TpCallIdentifier(
                                ResourceIDFactory.getCurrentID() + 1,
                                GccsTestData.SESSIONID)));


        activityManagerControl.replay();
        ipCallControlManagerControl.replay();

        org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier result = callControlManagerImpl
                .createCall();

        assertEquals(callIdentifier.CallSessionID, result
                .getCallSessionID());

        assertEquals(result, callControlManagerImpl
                .getCall(GccsTestData.SESSIONID)
                .getTpCallIdentifier());


        activityManagerControl.verify();

        ipCallControlManagerControl.verify();

    }

    public void testEnableCallNotification() throws P_INVALID_INTERFACE_TYPE, P_INVALID_EVENT_TYPE, TpCommonExceptions, P_INVALID_CRITERIA, ResourceException {
        testInit();

        ipCallControlManagerControl.reset();

        mockIpCallControlManager.enableCallNotification(
                callControlManagerImpl
                        .getIpAppCallControlManager(),
                GccsTestData.TP_CALL_EVENT_CRITERIA);
        ipCallControlManagerControl
                .setReturnValue(GccsTestData.ASSIGNMENT_ID);


        ipCallControlManagerControl.replay();

        int result = callControlManagerImpl
                .enableCallNotification(GccsTestData.TP_CALL_EVENT_CRITERIA);

        assertEquals(GccsTestData.ASSIGNMENT_ID, result);

        ipCallControlManagerControl.verify();
    }

    public void testDisableCallNotification() throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID, ResourceException {
        testInit();

        ipCallControlManagerControl.reset();
        
        mockIpCallControlManager.disableCallNotification(GccsTestData.ASSIGNMENT_ID);

        ipCallControlManagerControl.replay();

        callControlManagerImpl
                    .disableCallNotification(GccsTestData.ASSIGNMENT_ID);

        ipCallControlManagerControl.verify();
    }

    public void testSetCallLoadControl() throws TpCommonExceptions, P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN, ResourceException {
        testInit();

        ipCallControlManagerControl.reset();

        mockIpCallControlManager.setCallLoadControl(GccsTestData.DURATION, 
                GccsTestData.TP_CALL_LOAD_CONTROL_MECHANIM, 
                GccsTestData.TP_CALL_TREATMENT, 
                GccsTestData.TP_ADDRESS_RANGE);
        ipCallControlManagerControl.setReturnValue(101);


        ipCallControlManagerControl.replay();

        int result = callControlManagerImpl
                .setCallLoadControl(GccsTestData.DURATION, 
                        GccsTestData.TP_CALL_LOAD_CONTROL_MECHANIM, 
                        GccsTestData.TP_CALL_TREATMENT, 
                        GccsTestData.TP_ADDRESS_RANGE);
        
        assertEquals(101, result);

        ipCallControlManagerControl.verify();
    }

    public void testChangeCallNotification() throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID, P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE, ResourceException {
        testInit();

        ipCallControlManagerControl.reset();

        mockIpCallControlManager.changeCallNotification(GccsTestData.ASSIGNMENT_ID, 
                GccsTestData.TP_CALL_EVENT_CRITERIA);


        ipCallControlManagerControl.replay();

        callControlManagerImpl
                    .changeCallNotification(GccsTestData.ASSIGNMENT_ID, 
                            GccsTestData.TP_CALL_EVENT_CRITERIA);

        ipCallControlManagerControl.verify();
    }

    public void testGetCriteria() throws TpCommonExceptions, ResourceException {
        testInit();

        ipCallControlManagerControl.reset();
        
        mockIpCallControlManager.getCriteria();
        ipCallControlManagerControl.setReturnValue(GccsTestData.TP_CALL_EVENT_CRITERIA_RESULT_SET);

        ipCallControlManagerControl.replay();

        TpCallEventCriteriaResult[] result = callControlManagerImpl
                    .getCriteria();
            
        assertEquals(GccsTestData.TP_CALL_EVENT_CRITERIA_RESULT_SET, result);

        ipCallControlManagerControl.verify();
    }

    public void testGetTpServiceIdentifier() {
        TpServiceIdentifier result = callControlManagerImpl.getTpServiceIdentifier();
        assertNotNull(result);
        assertEquals(serviceIdentifier, result);
    }

    public void testGetType() {
        assertEquals(ServiceSession.GenericCallControl, callControlManagerImpl.getType());
    }

    public void testGetIpAppCallControlManagerImpl() {
        assertNull(callControlManagerImpl
                .getIpAppCallControlManagerImpl());
        testInit();
        assertNotNull(callControlManagerImpl
                .getIpAppCallControlManagerImpl());
    }

    public void testGetIpAppCallImpl() {
        assertNull(callControlManagerImpl
                .getIpAppCallImpl());
        testInit();
        assertNotNull(callControlManagerImpl
                .getIpAppCallImpl());
    }

    public void testGetIpCallControlManager() {
        assertEquals(mockIpCallControlManager,
                callControlManagerImpl
                        .getIpCallControlManager());
    }

}
