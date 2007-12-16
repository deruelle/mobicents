package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.TpCommonExceptions;
import org.csapi.ui.P_ID_NOT_FOUND;
import org.csapi.ui.P_ILLEGAL_ID;
import org.csapi.ui.P_ILLEGAL_RANGE;
import org.csapi.ui.P_INVALID_COLLECTION_CRITERIA;
import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICall;

public class IpUICallConnectionImplTest extends TestCase {
    IpUICallConnectionImpl connectionImpl;

    MockControl uiCallControl;

    UICall uiCallMock;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        uiCallControl = MockControl.createControl(UICall.class);

        uiCallMock = (UICall) uiCallControl.getMock();

        connectionImpl = new IpUICallConnectionImpl(uiCallMock);
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUICallConnectionImpl.closeConnection()'
     */
    public void testCloseConnection() throws ResourceException {
        uiCallControl.replay();
        connectionImpl.closeConnection();
        uiCallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUICallConnectionImpl.sendInfoReq(TpUIInfo,
     * String, TpUIVariableInfo[], int, int)'
     */
    public void testSendInfoReq() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_ILLEGAL_ID, P_ID_NOT_FOUND,
            ResourceException {
        uiCallMock.sendInfoReq(UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.REPEAT_INDICATOR, UiTestData.RESPONSE_REQUESTED);
        uiCallControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        uiCallControl.replay();

        int result = connectionImpl.sendInfoReq(UiTestData.TP_UI_INFO,
                UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.REPEAT_INDICATOR, UiTestData.RESPONSE_REQUESTED);
        assertEquals(UiTestData.ASSIGNMENT_ID, result);
        uiCallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUICallConnectionImpl.sendInfoAndCollectReq(TpUIInfo,
     * String, TpUIVariableInfo[], TpUICollectCriteria, int)'
     */
    public void testSendInfoAndCollectReq() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_ILLEGAL_ID, P_ID_NOT_FOUND,
            P_ILLEGAL_RANGE, P_INVALID_COLLECTION_CRITERIA, ResourceException {
        uiCallMock.sendInfoAndCollectReq(UiTestData.TP_UI_INFO,
                UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.TP_UI_COLLECT_CRITERIA,
                UiTestData.RESPONSE_REQUESTED);
        uiCallControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        uiCallControl.replay();

        int result = connectionImpl.sendInfoAndCollectReq(
                UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.TP_UI_COLLECT_CRITERIA,
                UiTestData.RESPONSE_REQUESTED);
        assertEquals(UiTestData.ASSIGNMENT_ID, result);
        uiCallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUICallConnectionImpl.release()'
     */
    public void testRelease() throws TpCommonExceptions, ResourceException {
        uiCallMock.release();
        uiCallControl.setVoidCallable();
        uiCallControl.replay();
        connectionImpl.release();
        uiCallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUICallConnectionImpl.setOriginatingAddress(String)'
     */
    public void testSetOriginatingAddress() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_INVALID_ADDRESS, ResourceException {
        uiCallMock.setOriginatingAddress(UiTestData.ORIGIN);
        uiCallControl.setVoidCallable();
        uiCallControl.replay();
        connectionImpl.setOriginatingAddress(UiTestData.ORIGIN);
        uiCallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUICallConnectionImpl.getOriginatingAddress()'
     */
    public void testGetOriginatingAddress() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, ResourceException {
        uiCallMock.getOriginatingAddress();
        uiCallControl.setReturnValue(UiTestData.ORIGIN);
        uiCallControl.replay();

        String result = connectionImpl.getOriginatingAddress();
        assertEquals(UiTestData.ORIGIN, result);
        uiCallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUICallConnectionImpl.recordMessageReq(TpUIInfo,
     * TpUIMessageCriteria)'
     */
    public void testRecordMessageReq() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_ILLEGAL_ID, P_ID_NOT_FOUND,
            P_INVALID_CRITERIA, ResourceException {

        uiCallMock.recordMessageReq(UiTestData.TP_UI_INFO,
                UiTestData.TP_UI_MESSAGE_CRITERIA);
        uiCallControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        uiCallControl.replay();

        int result = connectionImpl.recordMessageReq(UiTestData.TP_UI_INFO,
                UiTestData.TP_UI_MESSAGE_CRITERIA);
        assertEquals(UiTestData.ASSIGNMENT_ID, result);
        uiCallControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUICallConnectionImpl.deleteMessageReq(int)'
     */
    public void testDeleteMessageReq() throws TpCommonExceptions, P_ILLEGAL_ID,
            P_ID_NOT_FOUND, ResourceException {
        uiCallMock.deleteMessageReq(UiTestData.MESSAGE_ID);
        uiCallControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        uiCallControl.replay();

        int result = connectionImpl.deleteMessageReq(UiTestData.MESSAGE_ID);
        assertEquals(UiTestData.ASSIGNMENT_ID, result);
        uiCallControl.verify();
    }

    /* 
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUICallConnectionImpl.abortActionReq(int)'
     */
    public void testAbortActionReq() throws TpCommonExceptions,
            P_INVALID_ASSIGNMENT_ID, ResourceException {
        uiCallMock.abortActionReq(UiTestData.ASSIGNMENT_ID);
        uiCallControl.setVoidCallable();
        uiCallControl.replay();

        connectionImpl.abortActionReq(UiTestData.ASSIGNMENT_ID);

        uiCallControl.verify();
    }

}
