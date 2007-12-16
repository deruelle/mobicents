package org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.TpCommonExceptions;
import org.csapi.ui.IpUIManager;
import org.csapi.ui.TpUIEventCriteriaResult;
import org.csapi.ui.TpUITargetObjectType;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.ui.CallLegUITarget;
import org.mobicents.csapi.jr.slee.ui.CallUITarget;
import org.mobicents.csapi.jr.slee.ui.MultiPartyCallUITarget;
import org.mobicents.csapi.jr.slee.ui.ReportEventNotificationEvent;
import org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier;
import org.mobicents.csapi.jr.slee.ui.TpUIIdentifier;
import org.mobicents.csapi.jr.slee.ui.TpUITargetObject;
import org.mobicents.slee.resource.parlay.csapi.jr.ParlayServiceActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.TpUIActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.TpUICallActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListener;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiTestData;
import org.mobicents.slee.resource.parlay.fw.FwSession;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.mobicents.slee.resource.parlay.session.ParlaySession;
import org.mobicents.slee.resource.parlay.session.ServiceSession;
import org.mobicents.slee.resource.parlay.util.ResourceIDFactory;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;
import org.omg.PortableServer.POA;


public class UIManagerImplTest extends TestCase {
    // class under test
    UIManagerImpl uiManagerImpl;

    final TpServiceIdentifier serviceIdentifier = new TpServiceIdentifier(2345);

    UiListener uiListenerMock;
    MockControl uiListenerControl;

    ActivityManager activityManagerMock;
    MockControl activityManagerControl;

    IpUIManager ipUIManagerMock;
    MockControl ipUIManagerControl;

    FwSession fwSessionMock;
    MockControl fwSessionControl;
    
    ParlaySession parlaySessionMock;
    MockControl parlaySessionControl;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() {

        fwSessionControl = MockControl.createControl(FwSession.class);
        fwSessionMock = (FwSession) fwSessionControl.getMock();

        ipUIManagerControl = MockControl.createControl(IpUIManager.class);
        ipUIManagerMock = (IpUIManager) ipUIManagerControl.getMock();

        uiListenerControl = MockControl.createControl(UiListener.class);
        uiListenerMock = (UiListener) uiListenerControl.getMock();

        activityManagerControl = MockControl
                .createControl(ActivityManager.class);
        activityManagerMock = (ActivityManager) activityManagerControl
                .getMock();

        parlaySessionControl = MockControl.createControl(ParlaySession.class);
        parlaySessionMock = (ParlaySession) parlaySessionControl.getMock();
                
        uiManagerImpl = new UIManagerImpl(parlaySessionMock, fwSessionMock, ipUIManagerMock,
                uiListenerMock, activityManagerMock, serviceIdentifier);
    }
 
    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.init()'
     */
    public void testInit() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions,
            ResourceException {
        POA rootPOA = new POAStub();

        // GetrootPoa called many times create policies x 1, createPOA x 2 for
        // each POA
        fwSessionMock.getRootPOA();
        fwSessionControl.setReturnValue(rootPOA, 7);

        // Can't compare callback for equality as reference is internal to
        // class so just make sure method is called
        ipUIManagerMock.setCallback(null);
        ipUIManagerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        
        activityManagerMock.add(new ParlayServiceActivityHandle(
                serviceIdentifier), serviceIdentifier);
        activityManagerMock.activityStartedSuspended(new ParlayServiceActivityHandle(
                serviceIdentifier));                        
        
        ipUIManagerControl.replay();
        fwSessionControl.replay();
        activityManagerControl.replay();
        
        uiManagerImpl.init();
        
        ipUIManagerControl.verify();
        fwSessionControl.verify();
        activityManagerControl.verify();
    }

  
     

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.destroy()'
     */
    public void testDestroy() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException { 

        testInit();
        assertNotNull(uiManagerImpl.getIpAppUIImpl());
        uiManagerImpl.addUI( UiTestData.SESSIONID, UiTestData.UIGENERIC_ACTIVITY_STUB);
        assertNotNull(uiManagerImpl.getUIGeneric(UiTestData.SESSIONID));
                
        uiManagerImpl.destroy();
                
        ipUIManagerControl.verify();
        fwSessionControl.verify();
        
        assertNull(uiManagerImpl.getIpAppUIImpl());
        assertNull(uiManagerImpl.getUIGeneric(UiTestData.SESSIONID));
        
    }

   

 
  

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.getUIGeneric(int)'
     */
    public void testGetUIGeneric() {  
        uiManagerImpl.addUI( UiTestData.SESSIONID, UiTestData.UIGENERIC_ACTIVITY_STUB);
        assertEquals(UiTestData.UIGENERIC_ACTIVITY_STUB,uiManagerImpl.getUIGeneric(UiTestData.SESSIONID));
        assertEquals(UiTestData.UIGENERIC_ACTIVITY_STUB,uiManagerImpl.removeUI(UiTestData.SESSIONID));
        assertEquals(null, uiManagerImpl.getUIGeneric(UiTestData.SESSIONID));
       
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.getUICall(int)'
     */
    public void testGetUICall() { 
 
        uiManagerImpl.addUI( UiTestData.SESSIONID, UiTestData.UICALL_ACTIVITY_STUB);
        assertEquals(UiTestData.UICALL_ACTIVITY_STUB,uiManagerImpl.getUICall(UiTestData.SESSIONID));
       
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.removeUI(int)'
     */
    public void testAddAndRemoveUI() { 
        //UIGENERIC
        uiManagerImpl.addUI( UiTestData.SESSIONID, UiTestData.UIGENERIC_ACTIVITY_STUB);
        assertEquals(UiTestData.UIGENERIC_ACTIVITY_STUB,uiManagerImpl.getUIGeneric(UiTestData.SESSIONID));
        assertEquals(UiTestData.UIGENERIC_ACTIVITY_STUB,uiManagerImpl.removeUI(UiTestData.SESSIONID));
        assertNull( uiManagerImpl.getUIGeneric(UiTestData.SESSIONID));
        
        //UICALL
        uiManagerImpl.addUI( UiTestData.SESSIONID, UiTestData.UICALL_ACTIVITY_STUB);
        assertEquals(UiTestData.UICALL_ACTIVITY_STUB,uiManagerImpl.getUICall(UiTestData.SESSIONID));
        assertEquals(UiTestData.UICALL_ACTIVITY_STUB,uiManagerImpl.removeUI(UiTestData.SESSIONID));
        assertNull( uiManagerImpl.getUICall(UiTestData.SESSIONID));

    }

 
 


    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.getIpUIConnection(TpUIIdentifier)'
     */
    public void testGetIpUIConnection() throws ResourceException {
        try {
            uiManagerImpl.getIpUIConnection(UiTestData.SLEE_TP_UI_IDENTIFIER);
            fail("should have got ex");
        } catch (ResourceException re) {
            // ignore
        }

        uiManagerImpl.addUI(UiTestData.SLEE_TP_UI_IDENTIFIER
                .getUserInteractionSessionID(), UiTestData.UIGENERIC_ACTIVITY_STUB);

        // get it twice to make sure doesnt get removed
        uiManagerImpl.getIpUIConnection(UiTestData.SLEE_TP_UI_IDENTIFIER);
        assertNotNull(uiManagerImpl
                .getIpUIConnection(UiTestData.SLEE_TP_UI_IDENTIFIER));
        try {
            uiManagerImpl.getIpUICallConnection(new TpUICallIdentifier(
                    UiTestData.SLEE_TP_UI_IDENTIFIER.getUIRefID(),
                    UiTestData.SLEE_TP_UI_IDENTIFIER
                            .getUserInteractionSessionID()));
            fail("it was not a TpUICallIdentifier that was stored");
        } catch (ClassCastException e) {
            // ignore
        }
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.getIpUICallConnection(TpUICallIdentifier)'
     */
    public void testGetIpUICallConnection() throws ResourceException { // TODO
  

        uiManagerImpl.addUI(UiTestData.SLEE_TP_UICALL_IDENTIFIER
                .getUserInteractionSessionID(), UiTestData.UICALL_ACTIVITY_STUB);

        // get it twice to make sure doesnt get removed
        uiManagerImpl.getIpUICallConnection(UiTestData.SLEE_TP_UICALL_IDENTIFIER);
        assertNotNull(uiManagerImpl
                .getIpUICallConnection(UiTestData.SLEE_TP_UICALL_IDENTIFIER));
        try {
            uiManagerImpl.getIpUIConnection(new TpUIIdentifier(
                    UiTestData.SLEE_TP_UICALL_IDENTIFIER.getUICallRefID(),
                    UiTestData.SLEE_TP_UICALL_IDENTIFIER
                            .getUserInteractionSessionID()));
            fail("it was not a TpUIIdentifier that was stored");
        } catch (ClassCastException e) {
            // ignore
        }
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.closeConnection()'
     */
    public void testCloseConnection() {  
        try {
            uiManagerImpl.closeConnection();
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
    
    }



    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.getTpServiceIdentifier()'
     */
    public void testGetTpServiceIdentifier() { 
        assertEquals(serviceIdentifier, uiManagerImpl.getTpServiceIdentifier());

    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.getType()'
     */
    public void testGetType() { 
        assertEquals(ServiceSession.UserInteraction,
                uiManagerImpl.getType());
   

    }


  

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.getIpUIManager()'
     */
    public void testGetIpUIManager() { 
            assertEquals(ipUIManagerMock, uiManagerImpl.getIpUIManager());
    }
 

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.userInteractionAborted(TpUIIdentifier)'
     */
    public void testUserInteractionAborted() {
        
        
        uiManagerImpl.addUI( UiTestData.UI_SESSION_ID, UiTestData.UIGENERIC_ACTIVITY_STUB);        
        assertNotNull (uiManagerImpl.getUIGeneric(UiTestData.UI_SESSION_ID));
        
        
        // The SLEE UI API Events dont properly implement equals() hence use of default matcher
        uiListenerMock.onUserInteractionAbortedEvent(null);        
        uiListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        uiListenerControl.setVoidCallable();
        
        activityManagerControl.reset();
        
        activityManagerMock.remove(UiTestData.UIGENERIC_ACTIVITY_STUB.getActivityHandle(),
                UiTestData.SLEE_TP_UI_IDENTIFIER);
        activityManagerMock.activityEnding(UiTestData.UIGENERIC_ACTIVITY_STUB.getActivityHandle());


        activityManagerControl.replay();        
        uiListenerControl.replay();

        uiManagerImpl.userInteractionAborted( new org.csapi.ui.TpUIIdentifier(null, UiTestData.UI_SESSION_ID));
        
        uiListenerControl.verify();
        activityManagerControl.verify();
        // Dont check that the activity has been deleted from the mgr activity; that is done by the activity which is stubbed out.   
        // assertNull (uiManagerImpl.getUIGeneric(UiTestData.SESSIONID));
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.reportNotification(TpUIIdentifier, TpUIEventInfo, int)'
     */
    public void testReportNotification() { 
        // The SLEE UI API Events dont properly implement equals() hence use of default matcher
        uiListenerMock.onReportNotificationEvent(null);
        uiListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        uiListenerControl.replay();
        
        uiManagerImpl.reportNotification(UiTestData.SLEE_TP_UI_IDENTIFIER, UiTestData.TP_UI_EVENT_INFO, UiTestData.ASSIGNMENT_ID);
        uiListenerControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.userInteractionNotificationInterrupted()'
     */
    public void testUserInteractionNotificationInterrupted() { 

        // The SLEE UI API Events dont properly implement equals() hence use of default matcher
        uiListenerMock.onUserInteractionNotificationInterruptedEvent(null);
        uiListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        uiListenerControl.replay();
        
        uiManagerImpl.userInteractionNotificationInterrupted();
        uiListenerControl.verify();

    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.userInteractionNotificationContinued()'
     */
    public void testUserInteractionNotificationContinued() { 
        // The SLEE UI API Events dont properly implement equals() hence use of default matcher
        uiListenerMock.onUserInteractionNotificationContinuedEvent(null);
        uiListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        uiListenerControl.replay();
        
        uiManagerImpl.userInteractionNotificationContinued();
        uiListenerControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.reportEventNotification(TpUIIdentifier, TpUIEventNotificationInfo, int)'
     */
    public void testReportEventNotification() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException { 
        testInit();
        // uiListenerMock.onReportEventNotificationEvent could equally well be null as we are using DefaultMatcher
        uiListenerMock.onReportEventNotificationEvent(new ReportEventNotificationEvent (UiTestData.SLEE_TP_SERVICE_IDENTIFIER, null, UiTestData.TP_UI_EVENT_NOTIFICATION_INFO, UiTestData.ASSIGNMENT_ID) );
        uiListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        uiListenerControl.replay();
        uiManagerImpl.reportEventNotification(UiTestData.SLEE_TP_UI_IDENTIFIER, UiTestData.TP_UI_EVENT_NOTIFICATION_INFO, UiTestData.ASSIGNMENT_ID);       
        uiListenerControl.verify();

    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.createUI(TpAddress)'
     */
    public void testSleeApiCreateUI() throws TpCommonExceptions, P_INVALID_NETWORK_STATE, ResourceException, P_INVALID_INTERFACE_TYPE {
        
        testInit();
        ipUIManagerControl.reset();
        activityManagerControl.reset();
        
         
        ipUIManagerMock.createUI(uiManagerImpl.getIpAppUI(), UiTestData.TP_ADDRESS);
        ipUIManagerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
 
        org.csapi.ui.TpUIIdentifier parlayUiIdentifier = new  org.csapi.ui.TpUIIdentifier(null, UiTestData.SESSIONID); 
        ipUIManagerControl.setReturnValue(parlayUiIdentifier);
        
        final TpUIIdentifier tpUIIdentifier = new TpUIIdentifier(ResourceIDFactory.getCurrentID() + 1, UiTestData.SESSIONID);        
        final  TpUIActivityHandle tpUIActivityHandle = new TpUIActivityHandle (tpUIIdentifier);
        
        activityManagerMock.add(tpUIActivityHandle , tpUIIdentifier);
        activityManagerMock.activityStartedSuspended(tpUIActivityHandle);
        
        ipUIManagerControl.replay();
        activityManagerControl.replay();
         
        TpUIIdentifier result = uiManagerImpl.createUI(UiTestData.TP_ADDRESS);
        
        ipUIManagerControl.verify();
        activityManagerControl.verify();
                
        assertEquals(tpUIIdentifier, result);
        assertEquals(tpUIIdentifier, uiManagerImpl.getUIGeneric(UiTestData.SESSIONID).getTpUIIdentifier());

    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.createUICall(TpUITargetObject)'
     */
    public void testSleeApiCreateUICall() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException, P_INVALID_NETWORK_STATE { 
        testInit();
        ipUIManagerControl.reset();
        activityManagerControl.reset();
        parlaySessionControl.reset();
        
        parlaySessionMock.getServiceSession(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        parlaySessionControl.setReturnValue(UiTestData.MPCC_SERVICE_SESSION);
       
        
 
        ipUIManagerMock.createUICall(uiManagerImpl.getIpAppUICall(), null);
        ipUIManagerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER); 
        org.csapi.ui.TpUICallIdentifier parlayUiCallIdentifier = new  org.csapi.ui.TpUICallIdentifier(null, UiTestData.SESSIONID); 
        ipUIManagerControl.setReturnValue(parlayUiCallIdentifier);
        
        final org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier tpUICallIdentifier = new org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier(ResourceIDFactory.getCurrentID() + 1, UiTestData.SESSIONID);        
        final  TpUICallActivityHandle tpUICallActivityHandle = new TpUICallActivityHandle (tpUICallIdentifier);
        
        activityManagerMock.add(tpUICallActivityHandle , tpUICallIdentifier);
        activityManagerMock.activityStartedSuspended(tpUICallActivityHandle);
        
        parlaySessionControl.replay();
        ipUIManagerControl.replay();
        activityManagerControl.replay();
        
        
         TpUITargetObject sleeTpUITargetObject = new TpUITargetObject();         
         sleeTpUITargetObject.setCallLegUITarget( new CallLegUITarget(UiTestData.SLEE_TP_SERVICE_IDENTIFIER,UiTestData.SLEE_TP_MULTIPARTYCALL_IDENTIFIER, UiTestData.SLEE_TP_CALLLEG_IDENTIFIER) );   
   
        org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier result = uiManagerImpl.createUICall(sleeTpUITargetObject);
        
        ipUIManagerControl.verify();
        activityManagerControl.verify();
                
        assertEquals(tpUICallIdentifier, result);
        assertEquals(tpUICallIdentifier, uiManagerImpl.getUICall(UiTestData.SESSIONID).getTpUICallIdentifier());
 
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.createNotification(TpUIEventCriteria)'
     */
    public void testCreateNotification() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, ResourceException, P_INVALID_CRITERIA { 
        
        ipUIManagerMock.createNotification(uiManagerImpl.getIpAppUIManager(),UiTestData.TP_UI_EVENT_CRITERIA_DEL_RCPT);
        ipUIManagerControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        ipUIManagerControl.replay();
        assertEquals(UiTestData.ASSIGNMENT_ID, uiManagerImpl.createNotification(UiTestData.TP_UI_EVENT_CRITERIA_DEL_RCPT) );
        ipUIManagerControl.verify();

    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.destroyNotification(int)'
     */
    public void testDestroyNotification() throws P_INVALID_ASSIGNMENT_ID, TpCommonExceptions, ResourceException {  
        ipUIManagerMock.destroyNotification(UiTestData.ASSIGNMENT_ID);        
        ipUIManagerControl.replay();
        uiManagerImpl.destroyNotification(UiTestData.ASSIGNMENT_ID);
        ipUIManagerControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.changeNotification(int, TpUIEventCriteria)'
     */
    public void testChangeNotification() throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID, P_INVALID_CRITERIA, ResourceException {  
        ipUIManagerMock.changeNotification(UiTestData.ASSIGNMENT_ID,UiTestData.TP_UI_EVENT_CRITERIA_DEL_RCPT);      
        ipUIManagerControl.replay();
        uiManagerImpl.changeNotification(UiTestData.ASSIGNMENT_ID,UiTestData.TP_UI_EVENT_CRITERIA_DEL_RCPT);
        ipUIManagerControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.getNotification()'
     */
    public void testGetNotification() throws TpCommonExceptions, ResourceException {  
        ipUIManagerMock.getNotification();
        ipUIManagerControl.setReturnValue(UiTestData.TP_UI_EVENT_CRITERIA_RESULT_ARRAY);
        ipUIManagerControl.replay();
        TpUIEventCriteriaResult[] result = uiManagerImpl.getNotification();
        ipUIManagerControl.verify();
        assertEquals(UiTestData.TP_UI_EVENT_CRITERIA_RESULT_ARRAY, result);
          
        
        
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.enableNotifications()'
     */
    public void testEnableNotifications() throws TpCommonExceptions, ResourceException { 
        ipUIManagerMock.enableNotifications(uiManagerImpl.getIpAppUIManager());
        ipUIManagerControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        ipUIManagerControl.replay();        
        assertEquals(UiTestData.ASSIGNMENT_ID, uiManagerImpl.enableNotifications() );        
        ipUIManagerControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManagerImpl.disableNotifications()'
     */
    public void testDisableNotifications() throws TpCommonExceptions, ResourceException {  
        ipUIManagerMock.disableNotifications( );
        
        ipUIManagerControl.replay();        
        uiManagerImpl.disableNotifications()  ;        
        ipUIManagerControl.verify();
   
    }
    
    public void  testGetCorbaUITargetObjectForGccsCall() throws ResourceException {
        parlaySessionMock.getServiceSession(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        parlaySessionControl.setReturnValue(UiTestData.GCCS_SERVICE_SESSION);
        parlaySessionControl.replay();
        
        TpUITargetObject sleeTpUITargetObject = new TpUITargetObject();

        sleeTpUITargetObject.setCallUITarget(new CallUITarget(UiTestData.SLEE_TP_SERVICE_IDENTIFIER, UiTestData.SLEE_TP_CALL_IDENTIFIER));
        
        org.csapi.ui.TpUITargetObject result = uiManagerImpl.getCorbaUITargetObject(sleeTpUITargetObject);
        
        assertEquals(TpUITargetObjectType.P_UI_TARGET_OBJECT_CALL, result.discriminator());
    
        assertEquals(UiTestData.GCCS_CALL.getParlayTpCallIdentifier().CallSessionID, result.Call().CallSessionID);
        assertEquals(UiTestData.GCCS_CALL.getParlayTpCallIdentifier().CallReference, result.Call().CallReference);
        
    }
    
    public void  testGetCorbaUITargetObjectForMpccCall() throws ResourceException {
     

        parlaySessionMock.getServiceSession(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        parlaySessionControl.setReturnValue(UiTestData.MPCC_SERVICE_SESSION);
        parlaySessionControl.replay();
        
        TpUITargetObject sleeTpUITargetObject = new TpUITargetObject();
        
        sleeTpUITargetObject.setMultiPartyCallUITarget(new MultiPartyCallUITarget( UiTestData.SLEE_TP_SERVICE_IDENTIFIER, UiTestData.SLEE_TP_MULTIPARTYCALL_IDENTIFIER));
        
       
        org.csapi.ui.TpUITargetObject result = uiManagerImpl.getCorbaUITargetObject(sleeTpUITargetObject);
        
        assertEquals(TpUITargetObjectType.P_UI_TARGET_OBJECT_MULTI_PARTY_CALL, result.discriminator());
        assertEquals(UiTestData.MPCC_CALL.getParlayTpMultiPartyCallIdentifier().CallSessionID, result.MultiPartyCall().CallSessionID);
        assertEquals(UiTestData.MPCC_CALL.getParlayTpMultiPartyCallIdentifier().CallReference, result.MultiPartyCall().CallReference);

    }
    public void  testGetCorbaUITargetObjectForCallLeg() throws ResourceException {

        parlaySessionMock.getServiceSession(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        parlaySessionControl.setReturnValue(UiTestData.MPCC_SERVICE_SESSION);
        parlaySessionControl.replay();
        
        TpUITargetObject sleeTpUITargetObject = new TpUITargetObject();
        sleeTpUITargetObject.setCallLegUITarget(new CallLegUITarget( UiTestData.SLEE_TP_SERVICE_IDENTIFIER, UiTestData.SLEE_TP_MULTIPARTYCALL_IDENTIFIER, UiTestData.SLEE_TP_CALLLEG_IDENTIFIER));
        
        org.csapi.ui.TpUITargetObject result = uiManagerImpl.getCorbaUITargetObject(sleeTpUITargetObject);
        
        assertEquals(TpUITargetObjectType.P_UI_TARGET_OBJECT_CALL_LEG, result.discriminator());
         
        assertEquals(UiTestData.MPCC_CALLLEG.getParlayTpCallLegIdentifier().CallLegSessionID, result.CallLeg().CallLegSessionID);
        assertEquals(UiTestData.MPCC_CALLLEG.getParlayTpCallLegIdentifier().CallLegReference, result.CallLeg().CallLegReference);
        
       
        
 
    }
        
    
}
