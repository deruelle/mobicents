package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.TpCommonExceptions;
import org.csapi.ui.P_ID_NOT_FOUND;
import org.csapi.ui.P_ILLEGAL_ID;
import org.csapi.ui.P_ILLEGAL_RANGE;
import org.csapi.ui.P_INVALID_COLLECTION_CRITERIA;
import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGeneric;

public class IpUIConnectionImplTest extends TestCase {
    IpUIConnectionImpl connectionImpl;

    MockControl uiGenericControl;

    UIGeneric uiGenericMock;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        uiGenericControl = MockControl.createControl(UIGeneric.class);
 
        uiGenericMock = (UIGeneric) uiGenericControl.getMock();

        connectionImpl = new IpUIConnectionImpl(uiGenericMock);
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIConnectionImpl.IpUIConnectionImpl(UIGeneric)'
     */
    public void testIpUIConnectionImpl() {
        try {
            connectionImpl = new IpUIConnectionImpl(null);
            fail("Create an connection with a null activity");
        } catch (IllegalArgumentException e) {
            // as expected
        }
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIConnectionImpl.closeConnection()'
     */
    public void testCloseConnection() throws ResourceException {
        uiGenericControl.replay();
        connectionImpl.closeConnection();
        uiGenericControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIConnectionImpl.sendInfoReq(TpUIInfo,
     * String, TpUIVariableInfo[], int, int)'
     */
    public void testSendInfoReq() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_ILLEGAL_ID, P_ID_NOT_FOUND,
            ResourceException {
        uiGenericMock.sendInfoReq(UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.REPEAT_INDICATOR, UiTestData.RESPONSE_REQUESTED);
        uiGenericControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        uiGenericControl.replay();

        int result = connectionImpl.sendInfoReq(UiTestData.TP_UI_INFO,
                UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.REPEAT_INDICATOR, UiTestData.RESPONSE_REQUESTED);
        assertEquals(UiTestData.ASSIGNMENT_ID, result);
        uiGenericControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIConnectionImpl.sendInfoAndCollectReq(TpUIInfo,
     * String, TpUIVariableInfo[], TpUICollectCriteria, int)'
     */
    public void testSendInfoAndCollectReq() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_ILLEGAL_ID, P_ID_NOT_FOUND,
            P_ILLEGAL_RANGE, P_INVALID_COLLECTION_CRITERIA, ResourceException {
        uiGenericMock.sendInfoAndCollectReq(UiTestData.TP_UI_INFO,
                UiTestData.LANGUAGE, UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.TP_UI_COLLECT_CRITERIA,
                UiTestData.RESPONSE_REQUESTED);
        uiGenericControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        uiGenericControl.replay();

        int result = connectionImpl.sendInfoAndCollectReq(
                UiTestData.TP_UI_INFO, UiTestData.LANGUAGE,
                UiTestData.TP_UI_VARIABLE_INFO_ARRAY,
                UiTestData.TP_UI_COLLECT_CRITERIA,
                UiTestData.RESPONSE_REQUESTED);
        assertEquals(UiTestData.ASSIGNMENT_ID, result);
        uiGenericControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIConnectionImpl.release()'
     */
    public void testRelease() throws TpCommonExceptions, ResourceException {
        uiGenericMock.release();
        uiGenericControl.setVoidCallable();
        uiGenericControl.replay();
        connectionImpl.release();
        uiGenericControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIConnectionImpl.setOriginatingAddress(String)'
     */
    public void testSetOriginatingAddress() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_INVALID_ADDRESS, ResourceException {
        uiGenericMock.setOriginatingAddress(UiTestData.ORIGIN);
        uiGenericControl.setVoidCallable();
        uiGenericControl.replay();
        connectionImpl.setOriginatingAddress(UiTestData.ORIGIN);
        uiGenericControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIConnectionImpl.getOriginatingAddress()'
     */
    public void testGetOriginatingAddress() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, ResourceException {
        uiGenericMock.getOriginatingAddress();
        uiGenericControl.setReturnValue(UiTestData.ORIGIN);
        uiGenericControl.replay();

        String result = connectionImpl.getOriginatingAddress();
        assertEquals(UiTestData.ORIGIN, result);
        uiGenericControl.verify();
    }

}
