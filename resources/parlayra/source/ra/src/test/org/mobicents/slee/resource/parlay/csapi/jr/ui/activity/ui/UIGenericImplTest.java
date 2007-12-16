package org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.TpCommonExceptions;
import org.csapi.ui.IpUI;
import org.csapi.ui.P_ID_NOT_FOUND;
import org.csapi.ui.P_ILLEGAL_ID;
import org.csapi.ui.P_ILLEGAL_RANGE;
import org.csapi.ui.P_INVALID_COLLECTION_CRITERIA;
import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListener;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManager;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;

public class UIGenericImplTest extends TestCase { 
    
    MockControl uiManagerControl;
    UIManager uiManagerMock;
    
    MockControl activityManagerControl;
    ActivityManager activityManagerMock;
    
    MockControl eventListenerControl;    
    UiListener eventListenerMock;
    
    MockControl ipUIControl;
    IpUI ipUIMock;
    
    UIGenericImpl uIGenericImpl;
    
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        uiManagerControl = MockControl.createControl(UIManager.class);
        uiManagerMock = (UIManager) uiManagerControl.getMock();
        
        activityManagerControl = MockControl.createControl(ActivityManager.class);
        activityManagerMock = (ActivityManager) activityManagerControl.getMock(); 
        
        eventListenerControl = MockControl.createControl(UiListener.class);
        eventListenerMock = (UiListener) eventListenerControl.getMock();
        // Equals is not implemented fully in the events so until it is will be using dummy ' null' events   
        eventListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        
        ipUIControl = MockControl.createControl(IpUI.class);
        ipUIMock = (IpUI) ipUIControl.getMock();
        
        uIGenericImpl = new UIGenericImpl(uiManagerMock, ipUIMock, UiTestData.UI_SESSION_ID, UiTestData.SLEE_TP_UI_IDENTIFIER, activityManagerMock, eventListenerMock);
        
    }
    
 

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGenericImpl.sendInfoRes(int, int, TpUIReport)'
     */
    public void testSendInfoRes() { 
        
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onSendInfoResEvent(null);
        eventListenerControl.replay();
        
        uIGenericImpl.sendInfoRes(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT);
        
        eventListenerControl.verify();
        uiManagerControl.verify();

    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGenericImpl.sendInfoErr(int, int, TpUIError)'
     */
    public void testSendInfoErr() { 
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        
        eventListenerMock.onSendInfoErrEvent(null);
        eventListenerControl.replay();    
        
        uiManagerControl.replay();
        uIGenericImpl.sendInfoErr(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);        
        eventListenerControl.verify();
        uiManagerControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGenericImpl.sendInfoAndCollectRes(int, int, TpUIReport, String)'
     */
    public void testSendInfoAndCollectRes() {  
        assertNotNull(uIGenericImpl.getIpUI());
        
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onSendInfoAndCollectResEvent(null);
        eventListenerControl.replay();        
        uIGenericImpl.sendInfoAndCollectRes(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT, UiTestData.COLLECTED_INFO);
        
        eventListenerControl.verify();    
        uiManagerControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGenericImpl.sendInfoAndCollectErr(int, int, TpUIError)'
     */
    public void testSendInfoAndCollectErr() {
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onSendInfoAndCollectErrEvent(null);
        eventListenerControl.replay();   
        
        uIGenericImpl.sendInfoAndCollectErr(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);        
        eventListenerControl.verify();    
        uiManagerControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGenericImpl.userInteractionFaultDetected(int, TpUIFault)'
     */
    public void testUserInteractionFaultDetected() { 

        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        
        uiManagerMock.removeUI(UiTestData.UI_SESSION_ID);
        uiManagerControl.setReturnValue(UiTestData.UIGENERIC_ACTIVITY_STUB);
        
        uiManagerControl.replay();
        
        
        activityManagerMock.remove(UiTestData.TP_UI_ACTIVITY_HANDLE,
                UiTestData.SLEE_TP_UI_IDENTIFIER);
        activityManagerMock.activityEnding(UiTestData.TP_UI_ACTIVITY_HANDLE);
        activityManagerControl.replay();
        
        eventListenerMock.onUserInteractionFaultDetectedEvent(null);                           
        eventListenerControl.replay();
        
        uIGenericImpl.userInteractionFaultDetected(UiTestData.UI_SESSION_ID, UiTestData.TP_UI_FAULT);
        
        eventListenerControl.verify();
        uiManagerControl.verify();
        activityManagerControl.verify();

    }
 
    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.getActivityHandle()'
     * and 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.getEventListener()'
     */
    public void testMiscellaneousGetters() {
        assertEquals( UiTestData.TP_UI_ACTIVITY_HANDLE, uIGenericImpl.getActivityHandle());        
        assertEquals( eventListenerMock, uIGenericImpl.getEventListener());
        
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.closeConnection()'
     */
    public void testCloseConnection() throws ResourceException { 
        uIGenericImpl.closeConnection();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.sendInfoReq(TpUIInfo, String, TpUIVariableInfo[], int, int)'
     */
    public void testSendInfoReq() throws P_ILLEGAL_ID, P_INVALID_NETWORK_STATE, TpCommonExceptions, P_ID_NOT_FOUND, P_INVALID_SESSION_ID, ResourceException { 

        ipUIMock.sendInfoReq(UiTestData.UI_SESSION_ID, UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.REPEAT_INDICATOR, UiTestData.RESPONSE_REQUESTED);
        ipUIControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        ipUIControl.replay();
        

        activityManagerControl.reset();
        activityManagerControl.replay();
        
        assertEquals(UiTestData.ASSIGNMENT_ID, uIGenericImpl.sendInfoReq(UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.REPEAT_INDICATOR, UiTestData.RESPONSE_REQUESTED));
        
        activityManagerControl.verify(); 
        ipUIControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.sendInfoAndCollectReq(TpUIInfo, String, TpUIVariableInfo[], TpUICollectCriteria, int)'
     */
    public void testSendInfoAndCollectReq() throws P_ILLEGAL_ID, P_INVALID_NETWORK_STATE, TpCommonExceptions, P_ILLEGAL_RANGE, P_ID_NOT_FOUND, P_INVALID_SESSION_ID, P_INVALID_COLLECTION_CRITERIA, ResourceException { 
        ipUIMock.sendInfoAndCollectReq(UiTestData.UI_SESSION_ID, UiTestData.TP_UI_INFO,
                UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.TP_UI_COLLECT_CRITERIA,
                UiTestData.RESPONSE_REQUESTED);
        ipUIControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        ipUIControl.replay();
        
        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();
        
        assertEquals(UiTestData.ASSIGNMENT_ID, uIGenericImpl.sendInfoAndCollectReq(UiTestData.TP_UI_INFO,
                UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.TP_UI_COLLECT_CRITERIA,
                UiTestData.RESPONSE_REQUESTED));
        
        activityManagerControl.verify();
        ipUIControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.sendInfoAndCollectReq(TpUIInfo, String, TpUIVariableInfo[], TpUICollectCriteria, int)'
     */
    public void testSendInfoAndCollectReqWithParlayP_INVALID_NETWORK_STATEException() throws P_ILLEGAL_ID, TpCommonExceptions, P_ILLEGAL_RANGE, P_ID_NOT_FOUND, P_INVALID_SESSION_ID, P_INVALID_COLLECTION_CRITERIA, ResourceException {
        
        
        org.omg.CORBA.UserException EXPECTED_EX = new P_INVALID_NETWORK_STATE("reason", "my extra info");
        try {
            ipUIMock.sendInfoAndCollectReq(UiTestData.UI_SESSION_ID, UiTestData.TP_UI_INFO,
                    UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                    UiTestData.TP_UI_COLLECT_CRITERIA,
                    UiTestData.RESPONSE_REQUESTED);
        }  catch (P_INVALID_NETWORK_STATE e) {
            fail("problem setting up mock");
        } 
        ipUIControl.setThrowable(EXPECTED_EX);
        ipUIControl.replay();
        
        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();
        try {
            uIGenericImpl.sendInfoAndCollectReq(UiTestData.TP_UI_INFO,
                UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.TP_UI_COLLECT_CRITERIA,
                UiTestData.RESPONSE_REQUESTED);
            fail();
        } catch (P_INVALID_NETWORK_STATE e) {
            assertEquals(EXPECTED_EX, e);
        }
        activityManagerControl.verify();
        ipUIControl.verify();
    }
    
    
    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.sendInfoAndCollectReq(TpUIInfo, String, TpUIVariableInfo[], TpUICollectCriteria, int)'
     */
    public void testSendInfoAndCollectReqWithParlayP_INVALID_SESSION_IDException() throws P_ILLEGAL_ID, TpCommonExceptions, P_ILLEGAL_RANGE, P_ID_NOT_FOUND,  P_INVALID_COLLECTION_CRITERIA, ResourceException, P_INVALID_NETWORK_STATE {
        
 
        try {
            ipUIMock.sendInfoAndCollectReq(UiTestData.UI_SESSION_ID, UiTestData.TP_UI_INFO,
                    UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                    UiTestData.TP_UI_COLLECT_CRITERIA,
                    UiTestData.RESPONSE_REQUESTED);
        }  catch (P_INVALID_SESSION_ID e) {
            fail("problem setting up mock");
        } 
        ipUIControl.setThrowable(new P_INVALID_SESSION_ID("reason", "my extra info"));
        ipUIControl.replay();
        
        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();
        try {
            uIGenericImpl.sendInfoAndCollectReq(UiTestData.TP_UI_INFO,
                UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.TP_UI_COLLECT_CRITERIA,
                UiTestData.RESPONSE_REQUESTED);
            fail();
        } catch (ResourceException  e) {
            assertEquals(AbstractUIImpl.UI_NOT_VALID, e.getMessage());
        }
        activityManagerControl.verify();
        ipUIControl.verify();
    }
    
    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.release()'
     */
    public void testRelease() throws TpCommonExceptions, ResourceException, P_INVALID_SESSION_ID {
        
        final int SESS_ID = uIGenericImpl.getTpUIIdentifier().getUserInteractionSessionID();
        ipUIMock.release(SESS_ID);               
        uiManagerMock.removeUI(SESS_ID);    
        uiManagerControl.setReturnValue(null);
        activityManagerControl.reset();
        
        activityManagerMock.remove(UiTestData.TP_UI_ACTIVITY_HANDLE,
                UiTestData.SLEE_TP_UI_IDENTIFIER);
        activityManagerMock.activityEnding(UiTestData.TP_UI_ACTIVITY_HANDLE);


        activityManagerControl.replay();        
        ipUIControl.replay();
        uiManagerControl.replay();
        
        uIGenericImpl.release();
        
        ipUIControl.verify();
        uiManagerControl.verify();
        activityManagerControl.verify();
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.setOriginatingAddress(String)'
     */
    public void testSetOriginatingAddress() throws TpCommonExceptions, P_INVALID_NETWORK_STATE, P_INVALID_ADDRESS, ResourceException, P_INVALID_SESSION_ID { 
        ipUIMock.setOriginatingAddress(UiTestData.UI_SESSION_ID,  UiTestData.ORIGIN);
        ipUIControl.setVoidCallable();
        ipUIControl.replay();
        
        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();
        
        uIGenericImpl.setOriginatingAddress(UiTestData.ORIGIN);
        
        activityManagerControl.verify();
        ipUIControl.verify();                
    }

    /*
     * Test method for 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.getOriginatingAddress()'
     */
    public void testGetOriginatingAddress() throws P_INVALID_NETWORK_STATE, TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException { 
        ipUIMock.getOriginatingAddress(UiTestData.UI_SESSION_ID);
        ipUIControl.setReturnValue(UiTestData.ORIGIN);
        ipUIControl.replay();
        
        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();
        
        assertEquals(UiTestData.ORIGIN, uIGenericImpl.getOriginatingAddress());
        
        activityManagerControl.verify();
        ipUIControl.verify();
    }

    
    
}
