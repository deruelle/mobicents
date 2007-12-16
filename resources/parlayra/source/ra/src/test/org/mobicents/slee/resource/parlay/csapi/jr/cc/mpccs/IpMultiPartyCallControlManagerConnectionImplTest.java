package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;


import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.cc.TpNotificationRequested;
import org.csapi.cc.TpNotificationRequestedSetEntry;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCallStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;

/**
 * 
 * Class Description for IpMultiPartyCallControlManagerConnectionImplTest
 */
public class IpMultiPartyCallControlManagerConnectionImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        multiPartyCallControlManagerControl = MockControl
                .createControl(MultiPartyCallControlManager.class);

        multiPartyCallControlManager = (MultiPartyCallControlManager) multiPartyCallControlManagerControl
                .getMock();

        connectionImpl = new IpMultiPartyCallControlManagerConnectionImpl(
                multiPartyCallControlManager);
    }

    MultiPartyCallControlManager multiPartyCallControlManager;

    MockControl multiPartyCallControlManagerControl;

    IpMultiPartyCallControlManagerConnectionImpl connectionImpl;
    
    public void testIpMultiPartyCallControlManagerConnectionImpl() {

        try {
            connectionImpl = new IpMultiPartyCallControlManagerConnectionImpl(null);
            fail();
        }
        catch (Exception e) {

        }
    }

    public void testGetMultiPartyCallConnection() {
        MultiPartyCall multiPartyCall = new MultiPartyCallStub();
        IpMultiPartyCallConnection multiPartyCallConnection = new IpMultiPartyCallConnectionImpl(
                multiPartyCall);

        TpMultiPartyCallIdentifier callIdentifier = new TpMultiPartyCallIdentifier(
                MpccsTestData.CALL_ID, MpccsTestData.CALL_SESSION_ID);

        try {
            multiPartyCallControlManager
                    .getIpMultiPartyCallConnection(callIdentifier);
            multiPartyCallControlManagerControl
                    .setReturnValue(multiPartyCallConnection);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();
        try {
            IpMultiPartyCallConnection result = connectionImpl
                    .getIpMultiPartyCallConnection(callIdentifier);
            assertNotNull(result);
            assertEquals(multiPartyCallConnection, result);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testCreateCall() {

        TpMultiPartyCallIdentifier callIdentifier = new TpMultiPartyCallIdentifier(
                MpccsTestData.CALL_ID, MpccsTestData.CALL_SESSION_ID);

        try {
            multiPartyCallControlManager.createCall();
            multiPartyCallControlManagerControl.setReturnValue(callIdentifier);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();

        try {
            TpMultiPartyCallIdentifier result = connectionImpl.createCall();

            assertEquals(callIdentifier, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testCreateNotification() {

        try {
            multiPartyCallControlManager
                    .createNotification(MpccsTestData.TP_CALL_NOTIFCATION_REQUEST);
            multiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.ASSIGNMENT_ID);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();

        try {
            int result = connectionImpl
                    .createNotification(MpccsTestData.TP_CALL_NOTIFCATION_REQUEST);

            assertEquals(MpccsTestData.ASSIGNMENT_ID, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testEnableNotifications() {

        try {
            multiPartyCallControlManager
                    .enableNotifications();
            multiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.ASSIGNMENT_ID);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();

        try {
            int result = connectionImpl
                    .enableNotifications();

            assertEquals(MpccsTestData.ASSIGNMENT_ID, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testDestroyNotification() {

        try {
            multiPartyCallControlManager
                    .destroyNotification(MpccsTestData.ASSIGNMENT_ID);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();

        try {
            connectionImpl.destroyNotification(MpccsTestData.ASSIGNMENT_ID);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testDisableNotifications() {

        try {
            multiPartyCallControlManager
                    .disableNotifications();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();

        try {
            connectionImpl.disableNotifications();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testChangeNotification() {

        try {
            multiPartyCallControlManager.changeNotification(
                    MpccsTestData.ASSIGNMENT_ID,
                    MpccsTestData.TP_CALL_NOTIFCATION_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();

        try {
            connectionImpl.changeNotification(MpccsTestData.ASSIGNMENT_ID,
                    MpccsTestData.TP_CALL_NOTIFCATION_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testGetNotification() {

        try {
            multiPartyCallControlManager.getNotification();
            multiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.TP_NOTIFCATION_REQUESTED_ARRAY);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();

        try {
            TpNotificationRequested[] result = connectionImpl.getNotification();

            assertEquals(MpccsTestData.TP_NOTIFCATION_REQUESTED_ARRAY, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testGetNextNotification() {

        try {
            multiPartyCallControlManager.getNextNotification(true);
            multiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.TP_NOTIFICATION_REQUESTED_SET_ENTRY);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();

        try {
            TpNotificationRequestedSetEntry result = connectionImpl.getNextNotification(true);

            assertEquals(MpccsTestData.TP_NOTIFICATION_REQUESTED_SET_ENTRY, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testSetCallLoadControl() {

        try {
            multiPartyCallControlManager.setCallLoadControl(
                    MpccsTestData.DURATION,
                    MpccsTestData.TP_CALL_LOAD_CONTROL_MECHANISM,
                    MpccsTestData.TP_CALL_TREATMENT,
                    MpccsTestData.TP_ADDRESS_RANGE);

            multiPartyCallControlManagerControl
                    .setReturnValue(MpccsTestData.ASSIGNMENT_ID);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.replay();

        try {
            int result = connectionImpl.setCallLoadControl(
                    MpccsTestData.DURATION,
                    MpccsTestData.TP_CALL_LOAD_CONTROL_MECHANISM,
                    MpccsTestData.TP_CALL_TREATMENT,
                    MpccsTestData.TP_ADDRESS_RANGE);

            assertEquals(MpccsTestData.ASSIGNMENT_ID, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

    public void testClose() {
        multiPartyCallControlManagerControl.replay();

        try {
            connectionImpl.closeConnection();
        }
        catch (Exception e) {
            fail();
        }

        multiPartyCallControlManagerControl.verify();
    }

}