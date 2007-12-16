package org.mobicents.slee.resource.parlay.util.activity;

import javax.slee.resource.ActivityHandle;
import javax.slee.resource.SleeEndpoint;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.TpMultiPartyCallActivityHandle;

/**
 * 
 * Class Description for ActivityManagerImplTest
 */
public class ActivityManagerImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        sleeEndpointControl = MockControl.createControl(SleeEndpoint.class);
        sleeEndpoint = (SleeEndpoint) sleeEndpointControl.getMock();

        activityManagerImpl = new ActivityManagerImpl(sleeEndpoint);
    }

    SleeEndpoint sleeEndpoint;

    MockControl sleeEndpointControl;

    final TpMultiPartyCallIdentifier activity1 = new TpMultiPartyCallIdentifier(
            MpccsTestData.CALL_ID, MpccsTestData.CALL_SESSION_ID);

    final TpMultiPartyCallIdentifier activity2 = new TpMultiPartyCallIdentifier(
            MpccsTestData.CALL_ID, MpccsTestData.CALL_SESSION_ID + 1);

    final ActivityHandle activityHandle1 = new TpMultiPartyCallActivityHandle(
            activity1);

    final ActivityHandle activityHandle2 = new TpMultiPartyCallActivityHandle(
            activity2);

    ActivityManagerImpl activityManagerImpl;

    public void testActivityEnded() {
        activityManagerImpl.activityEnded(activityHandle1);
    }

    public void testActivityUnreferenced() {
        activityManagerImpl.activityUnreferenced(activityHandle1);
    }

    public void testQueryLiveness() {
        activityManagerImpl.queryLiveness(activityHandle1);
    }

    public void testGetActivity() {
        activityManagerImpl.add(activityHandle1, activity1);
        activityManagerImpl.add(activityHandle2, activity2);

        assertEquals(activity1, activityManagerImpl
                .getActivity(activityHandle1));

        assertEquals(activity2, activityManagerImpl
                .getActivity(activityHandle2));
    }

    public void testGetActivityHandle() {
        activityManagerImpl.add(activityHandle1, activity1);
        activityManagerImpl.add(activityHandle2, activity2);

        assertEquals(activityHandle1, activityManagerImpl
                .getActivityHandle(activity1));

        assertEquals(activityHandle2, activityManagerImpl
                .getActivityHandle(activity2));
    }

    public void testActivityStarted() {
        try {
            sleeEndpoint.activityStarted(activityHandle1);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        sleeEndpointControl.replay();

        activityManagerImpl.activityStarted(activityHandle1);

        sleeEndpointControl.verify();
    }

    public void testActivityStartedSuspended() {
        try {
            sleeEndpoint.activityStartedSuspended(activityHandle1);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        sleeEndpointControl.replay();

        activityManagerImpl.activityStartedSuspended(activityHandle1);

        sleeEndpointControl.verify();
    }

    public void testActivityEnding() {
        try {
            sleeEndpoint.activityEnding(activityHandle1);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        sleeEndpointControl.replay();

        activityManagerImpl.activityEnding(activityHandle1);

        sleeEndpointControl.verify();
    }

    public void testAdd() {
        activityManagerImpl.add(activityHandle1, activity1);
        activityManagerImpl.add(activityHandle2, activity2);
    }

    public void testRemove() {
        activityManagerImpl.add(activityHandle1, activity1);
        activityManagerImpl.remove(activityHandle1, activity1);
    }

}