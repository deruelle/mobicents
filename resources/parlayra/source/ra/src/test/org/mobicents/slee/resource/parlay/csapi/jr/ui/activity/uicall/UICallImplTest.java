package org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall;

import javax.slee.resource.ResourceException;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.TpCommonExceptions;
import org.csapi.ui.IpUICall;
import org.csapi.ui.P_ID_NOT_FOUND;
import org.csapi.ui.P_ILLEGAL_ID;
import org.csapi.ui.P_ILLEGAL_RANGE;
import org.csapi.ui.P_INVALID_COLLECTION_CRITERIA;
import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListener;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManager;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;

import junit.framework.TestCase;

/**
 * There is some duplication between the test classes for the UIGenericImpl and
 * UICallImpl activities. There is inheritance for the implementations but not
 * here in the testers, so need to be careful to keep them in line.
 */

public class UICallImplTest extends TestCase {
    MockControl uiManagerControl;

    UIManager uiManagerMock;

    MockControl activityManagerControl;

    ActivityManager activityManagerMock;

    MockControl eventListenerControl;

    UiListener eventListenerMock;

    MockControl ipUICallControl;

    IpUICall ipUICallMock;

    UICallImpl uICallImpl;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        uiManagerControl = MockControl.createControl(UIManager.class);
        uiManagerMock = (UIManager) uiManagerControl.getMock();

        activityManagerControl = MockControl
                .createControl(ActivityManager.class);
        activityManagerMock = (ActivityManager) activityManagerControl
                .getMock();

        eventListenerControl = MockControl.createControl(UiListener.class);
        eventListenerMock = (UiListener) eventListenerControl.getMock();
        // Equals is not implemented fully in the events so until it is will be
        // using dummy ' null' events
        eventListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);

        ipUICallControl = MockControl.createControl(IpUICall.class);
        ipUICallMock = (IpUICall) ipUICallControl.getMock();

        uICallImpl = new UICallImpl(uiManagerMock, ipUICallMock,
                UiTestData.UI_CALL_SESSION_ID,
                UiTestData.SLEE_TP_UICALL_IDENTIFIER, activityManagerMock,
                eventListenerMock);

    }
 
  
    public void testMiscellaneousGetters() {
        assertEquals( UiTestData.TP_UICALL_ACTIVITY_HANDLE, uICallImpl.getActivityHandle());        
        assertEquals( eventListenerMock, uICallImpl.getEventListener());
        
    }
    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.recordMessageReq(TpUIInfo,
     * TpUIMessageCriteria)'
     */
    public void testRecordMessageReq() throws TpCommonExceptions, P_INVALID_NETWORK_STATE, P_ILLEGAL_ID, P_ID_NOT_FOUND, P_INVALID_CRITERIA, ResourceException, P_INVALID_SESSION_ID {
        ipUICallMock.recordMessageReq(UiTestData.UI_CALL_SESSION_ID,
                UiTestData.TP_UI_INFO,
                UiTestData.TP_UI_MESSAGE_CRITERIA);
        ipUICallControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        ipUICallControl.replay();

        activityManagerControl.reset();
        activityManagerControl.replay();

        assertEquals(UiTestData.ASSIGNMENT_ID, uICallImpl.recordMessageReq(UiTestData.TP_UI_INFO,
                UiTestData.TP_UI_MESSAGE_CRITERIA));

        activityManagerControl.verify();
        ipUICallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.deleteMessageReq(int)'
     */
    public void testDeleteMessageReq() throws P_ILLEGAL_ID, TpCommonExceptions, P_ID_NOT_FOUND, P_INVALID_SESSION_ID, ResourceException {
        ipUICallMock.deleteMessageReq(UiTestData.UI_CALL_SESSION_ID,
                UiTestData.MESSAGE_ID);
        ipUICallControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        ipUICallControl.replay();

        activityManagerControl.reset();
        activityManagerControl.replay();

        assertEquals(UiTestData.ASSIGNMENT_ID, uICallImpl.deleteMessageReq(UiTestData.MESSAGE_ID));

        activityManagerControl.verify();
        ipUICallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.abortActionReq(int)'
     */
    public void testAbortActionReq() throws P_INVALID_ASSIGNMENT_ID,
            TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {
        ipUICallMock.abortActionReq(UiTestData.UI_CALL_SESSION_ID,
                UiTestData.ASSIGNMENT_ID);
        ipUICallControl.setVoidCallable();
        ipUICallControl.replay();

        activityManagerControl.reset();
        activityManagerControl.replay();

        uICallImpl.abortActionReq(UiTestData.ASSIGNMENT_ID);

        activityManagerControl.verify();
        ipUICallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.recordMessageRes(int,
     * int, TpUIReport, int)'
     */
    public void testRecordMessageRes() {
        assertNotNull(uICallImpl.getIpUICall());
        
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onRecordMessageResEvent(null);
        eventListenerControl.replay();   
        
        
        uICallImpl.recordMessageRes(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT,
                UiTestData.MESSAGE_ID);
        
        eventListenerControl.verify();    
        uiManagerControl.verify();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.recordMessageErr(int,
     * int, TpUIError)'
     */
    public void testRecordMessageErr() {        
        
    assertNotNull(uICallImpl.getIpUICall());

        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();

        eventListenerMock.onRecordMessageErrEvent(null);
        eventListenerControl.replay();

        uICallImpl.recordMessageErr(UiTestData.UI_SESSION_ID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);

        eventListenerControl.verify();
        uiManagerControl.verify();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.deleteMessageRes(int,
     * TpUIReport, int)'
     */
    public void testDeleteMessageRes() {
        assertNotNull(uICallImpl.getIpUICall());
        
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onDeleteMessageResEvent(null);
        eventListenerControl.replay();   
        
        
        uICallImpl.deleteMessageRes(UiTestData.UI_SESSION_ID, UiTestData.TP_UI_REPORT, UiTestData.ASSIGNMENT_ID);
        
        eventListenerControl.verify();    
        uiManagerControl.verify();


    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.deleteMessageErr(int,
     * TpUIError, int)'
     */
    public void testDeleteMessageErr() {

        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onDeleteMessageErrEvent(null);
        eventListenerControl.replay();   
        
        
        uICallImpl.deleteMessageErr(UiTestData.UI_SESSION_ID, UiTestData.TP_UI_ERROR, UiTestData.ASSIGNMENT_ID);
        
        eventListenerControl.verify();    
        uiManagerControl.verify();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.abortActionRes(int,
     * int)'
     */
    public void testAbortActionRes() { 
        assertNotNull(uICallImpl.getIpUICall());
        
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onAbortActionResEvent(null);
        eventListenerControl.replay();   
        
        
        uICallImpl.abortActionRes(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID);
        
        eventListenerControl.verify();    
        uiManagerControl.verify();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.abortActionErr(int,
     * int, TpUIError)'
     */
    public void testAbortActionErr() {
        assertNotNull(uICallImpl.getIpUICall());
        
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onAbortActionErrEvent(null);
        eventListenerControl.replay();   
        
        
        uICallImpl.abortActionErr(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        
        eventListenerControl.verify();    
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.sendInfoRes(int,
     * int, TpUIReport)'
     */
   public void testSendInfoRes() { 
        
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onSendInfoResEvent(null);
        eventListenerControl.replay();
        
        uICallImpl.sendInfoRes(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT);
        
        eventListenerControl.verify();
        uiManagerControl.verify();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.sendInfoErr(int,
     * int, TpUIError)'
     */
    public void testSendInfoErr() {
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onSendInfoErrEvent(null);
        eventListenerControl.replay();
        
        uICallImpl.sendInfoErr(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        
        eventListenerControl.verify();
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.sendInfoAndCollectRes(int,
     * int, TpUIReport, String)'
     */
    public void testSendInfoAndCollectRes() {  
        assertNotNull(uICallImpl.getIpUICall());
        
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onSendInfoAndCollectResEvent(null);
        eventListenerControl.replay();        
        
        
        uICallImpl.sendInfoAndCollectRes(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT, UiTestData.COLLECTED_INFO);
        
        eventListenerControl.verify();    
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.sendInfoAndCollectErr(int,
     * int, TpUIError)'
     */
    public void testSendInfoAndCollectErr() {
        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        uiManagerControl.replay();
        
        eventListenerMock.onSendInfoAndCollectErrEvent(null);
        eventListenerControl.replay();   
        
        uICallImpl.sendInfoAndCollectErr(UiTestData.UI_SESSION_ID, UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);        
        eventListenerControl.verify();    
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallImpl.userInteractionFaultDetected(int,
     * TpUIFault)'
     */
    public void testUserInteractionFaultDetected() { 

        uiManagerMock.getTpServiceIdentifier();
        uiManagerControl.setReturnValue(UiTestData.SLEE_TP_SERVICE_IDENTIFIER);
        
        uiManagerMock.removeUI(UiTestData.UI_CALL_SESSION_ID);
        uiManagerControl.setReturnValue(UiTestData.UICALL_ACTIVITY_STUB);
        
        uiManagerControl.replay();
        
        
        activityManagerMock.remove(UiTestData.TP_UICALL_ACTIVITY_HANDLE,
                UiTestData.SLEE_TP_UICALL_IDENTIFIER);
        activityManagerMock.activityEnding(UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        activityManagerControl.replay();
        
        eventListenerMock.onUserInteractionFaultDetectedEvent(null);                           
        eventListenerControl.replay();
        
        uICallImpl.userInteractionFaultDetected(UiTestData.UI_CALL_SESSION_ID, UiTestData.TP_UI_FAULT);
        
        eventListenerControl.verify();
        uiManagerControl.verify();
        activityManagerControl.verify();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.closeConnection()'
     */
    public void testCloseConnection() throws ResourceException {
        uICallImpl.closeConnection();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.sendInfoReq(TpUIInfo,
     * String, TpUIVariableInfo[], int, int)'
     */
    public void testSendInfoReq() throws P_ILLEGAL_ID, P_INVALID_NETWORK_STATE,
            TpCommonExceptions, P_ID_NOT_FOUND, P_INVALID_SESSION_ID,
            ResourceException {

        ipUICallMock.sendInfoReq(UiTestData.UI_CALL_SESSION_ID,
                UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.REPEAT_INDICATOR, UiTestData.RESPONSE_REQUESTED);
        ipUICallControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        ipUICallControl.replay();

        activityManagerControl.reset();
        activityManagerControl.replay();

        assertEquals(UiTestData.ASSIGNMENT_ID, uICallImpl.sendInfoReq(
                UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.REPEAT_INDICATOR, UiTestData.RESPONSE_REQUESTED));

        activityManagerControl.verify();
        ipUICallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.sendInfoAndCollectReq(TpUIInfo,
     * String, TpUIVariableInfo[], TpUICollectCriteria, int)'
     */
    public void testSendInfoAndCollectReq() throws P_ILLEGAL_ID,
            P_INVALID_NETWORK_STATE, TpCommonExceptions, P_ILLEGAL_RANGE,
            P_ID_NOT_FOUND, P_INVALID_SESSION_ID,
            P_INVALID_COLLECTION_CRITERIA, ResourceException {
        ipUICallMock.sendInfoAndCollectReq(UiTestData.UI_CALL_SESSION_ID,
                UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.TP_UI_COLLECT_CRITERIA,
                UiTestData.RESPONSE_REQUESTED);
        ipUICallControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        ipUICallControl.replay();

        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();

        assertEquals(UiTestData.ASSIGNMENT_ID, uICallImpl
                .sendInfoAndCollectReq(UiTestData.TP_UI_INFO,
                        UiTestData.LANGUAGE,
                        UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                        UiTestData.TP_UI_COLLECT_CRITERIA,
                        UiTestData.RESPONSE_REQUESTED));

        activityManagerControl.verify();
        ipUICallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.sendInfoAndCollectReq(TpUIInfo,
     * String, TpUIVariableInfo[], TpUICollectCriteria, int)'
     */
    public void testSendInfoAndCollectReqWithParlayP_INVALID_NETWORK_STATEException()
            throws P_ILLEGAL_ID, TpCommonExceptions, P_ILLEGAL_RANGE,
            P_ID_NOT_FOUND, P_INVALID_SESSION_ID,
            P_INVALID_COLLECTION_CRITERIA, ResourceException {

        org.omg.CORBA.UserException EXPECTED_EX = new P_INVALID_NETWORK_STATE(
                "reason", "my extra info");
        try {
            ipUICallMock.sendInfoAndCollectReq(UiTestData.UI_CALL_SESSION_ID,
                    UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                    UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                    UiTestData.TP_UI_COLLECT_CRITERIA,
                    UiTestData.RESPONSE_REQUESTED);
        } catch (P_INVALID_NETWORK_STATE e) {
            fail("problem setting up mock");
        }
        ipUICallControl.setThrowable(EXPECTED_EX);
        ipUICallControl.replay();

        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();
        try {
            uICallImpl.sendInfoAndCollectReq(UiTestData.TP_UI_INFO,
                    UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                    UiTestData.TP_UI_COLLECT_CRITERIA,
                    UiTestData.RESPONSE_REQUESTED);
            fail();
        } catch (P_INVALID_NETWORK_STATE e) {
            assertEquals(EXPECTED_EX, e);
        }
        activityManagerControl.verify();
        ipUICallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.sendInfoAndCollectReq(TpUIInfo,
     * String, TpUIVariableInfo[], TpUICollectCriteria, int)'
     */
    public void testSendInfoAndCollectReqWithParlayP_INVALID_SESSION_IDException()
            throws P_ILLEGAL_ID, TpCommonExceptions, P_ILLEGAL_RANGE,
            P_ID_NOT_FOUND, P_INVALID_COLLECTION_CRITERIA, ResourceException,
            P_INVALID_NETWORK_STATE {

        try {
            ipUICallMock.sendInfoAndCollectReq(UiTestData.UI_CALL_SESSION_ID,
                    UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                    UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                    UiTestData.TP_UI_COLLECT_CRITERIA,
                    UiTestData.RESPONSE_REQUESTED);
        } catch (P_INVALID_SESSION_ID e) {
            fail("problem setting up mock");
        }
        ipUICallControl.setThrowable(new P_INVALID_SESSION_ID("reason",
                "my extra info"));
        ipUICallControl.replay();

        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();
        try {
            uICallImpl.sendInfoAndCollectReq(UiTestData.TP_UI_INFO,
                    UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                    UiTestData.TP_UI_COLLECT_CRITERIA,
                    UiTestData.RESPONSE_REQUESTED);
            fail();
        } catch (ResourceException e) {
            assertEquals(AbstractUIImpl.UI_NOT_VALID, e.getMessage());
        }
        activityManagerControl.verify();
        ipUICallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.release()'
     */
    public void testRelease() throws TpCommonExceptions, ResourceException,
            P_INVALID_SESSION_ID {

        final int SESS_ID = uICallImpl.getTpUICallIdentifier()
                .getUserInteractionSessionID();
        ipUICallMock.release(SESS_ID);
        uiManagerMock.removeUI(SESS_ID);
        uiManagerControl.setReturnValue(null);
        activityManagerControl.reset();

        activityManagerMock.remove(UiTestData.TP_UICALL_ACTIVITY_HANDLE,
                UiTestData.SLEE_TP_UICALL_IDENTIFIER);
        activityManagerMock
                .activityEnding(UiTestData.TP_UICALL_ACTIVITY_HANDLE);

        activityManagerControl.replay();
        ipUICallControl.replay();
        uiManagerControl.replay();

        uICallImpl.release();

        ipUICallControl.verify();
        uiManagerControl.verify();
        activityManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.setOriginatingAddress(String)'
     */
    public void testSetOriginatingAddress() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_INVALID_ADDRESS, ResourceException,
            P_INVALID_SESSION_ID {
        ipUICallMock.setOriginatingAddress(UiTestData.UI_CALL_SESSION_ID,
                UiTestData.ORIGIN);
        ipUICallControl.setVoidCallable();
        ipUICallControl.replay();

        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();

        uICallImpl.setOriginatingAddress(UiTestData.ORIGIN);

        activityManagerControl.verify();
        ipUICallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.AbstractUIImpl.getOriginatingAddress()'
     */
    public void testGetOriginatingAddress() throws P_INVALID_NETWORK_STATE,
            TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {
        ipUICallMock.getOriginatingAddress(UiTestData.UI_CALL_SESSION_ID);
        ipUICallControl.setReturnValue(UiTestData.ORIGIN);
        ipUICallControl.replay();

        // activity does NOT get deleted.
        activityManagerControl.reset();
        activityManagerControl.replay();

        assertEquals(UiTestData.ORIGIN, uICallImpl.getOriginatingAddress());

        activityManagerControl.verify();
        ipUICallControl.verify();
    }

}
