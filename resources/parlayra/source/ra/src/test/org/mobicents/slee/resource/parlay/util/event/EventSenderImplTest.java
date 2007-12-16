package org.mobicents.slee.resource.parlay.util.event;

import javax.slee.Address;
import javax.slee.AddressPlan;
import javax.slee.facilities.EventLookupFacility;
import javax.slee.resource.ActivityIsEndingException;
import javax.slee.resource.SleeEndpoint;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallAbortedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.slee.resource.parlay.ParlayResourceAdaptor;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.TpMultiPartyCallActivityHandle;

/**
 * 
 * Class Description for EventSenderImplTest
 */
public class EventSenderImplTest extends TestCase {

    EventLookupFacility eventLookupFacility;

    SleeEndpoint sleeEndpoint;

    Address defaultAddress;

    MockControl sleeEndpointControl;

    MockControl eventLookupControl;

    EventSenderImpl eventSenderImpl;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        sleeEndpointControl = MockControl.createControl(SleeEndpoint.class);

        eventLookupControl = MockControl
                .createControl(EventLookupFacility.class);

        eventLookupFacility = (EventLookupFacility) eventLookupControl
                .getMock();

        sleeEndpoint = (SleeEndpoint) sleeEndpointControl.getMock();

        defaultAddress = new Address(AddressPlan.SIP, "user@domain.com");

        eventSenderImpl = new EventSenderImpl(eventLookupFacility,
                sleeEndpoint, defaultAddress);

    }

    public void testEventSenderImpl() {

        eventSenderImpl = new EventSenderImpl(eventLookupFacility,
                sleeEndpoint, defaultAddress);
    }

    public void testSendEvent() {
        final int EVENT_ID = 1;
        final int ERROR_EVENT_ID = -1;

        final TpMultiPartyCallIdentifier callIdentifier = new TpMultiPartyCallIdentifier(
                MpccsTestData.CALL_ID, MpccsTestData.CALL_SESSION_ID);

        final CallAbortedEvent callAbortedEvent = new CallAbortedEvent(
                new TpServiceIdentifier(1), callIdentifier);

        try {
            eventLookupFacility.getEventID(callAbortedEvent.getClass()
                    .getName(), ParlayResourceAdaptor.VENDOR,
                    ParlayResourceAdaptor.VERSION);

            eventLookupControl.setReturnValue(EVENT_ID);

            sleeEndpoint
                    .fireEvent(new TpMultiPartyCallActivityHandle(
                            callIdentifier), callAbortedEvent, EVENT_ID,
                            defaultAddress);

            eventLookupControl.replay();

            sleeEndpointControl.replay();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        eventSenderImpl.sendEvent(callAbortedEvent,
                new TpMultiPartyCallActivityHandle(callIdentifier));

        eventLookupControl.verify();

        sleeEndpointControl.verify();
        
        eventLookupControl.reset();
        
        sleeEndpointControl.reset();

        try {
            eventLookupFacility.getEventID(callAbortedEvent.getClass()
                    .getName(), ParlayResourceAdaptor.VENDOR,
                    ParlayResourceAdaptor.VERSION);

            eventLookupControl.setReturnValue(ERROR_EVENT_ID);

            eventLookupControl.replay();

            sleeEndpointControl.replay();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        eventSenderImpl.sendEvent(callAbortedEvent,
                new TpMultiPartyCallActivityHandle(callIdentifier));

        eventLookupControl.verify();

        sleeEndpointControl.verify();
        
        eventLookupControl.reset();
        
        sleeEndpointControl.reset();

        try {
            eventLookupFacility.getEventID(callAbortedEvent.getClass()
                    .getName(), ParlayResourceAdaptor.VENDOR,
                    ParlayResourceAdaptor.VERSION);

            eventLookupControl.setReturnValue(EVENT_ID);

            sleeEndpoint
                    .fireEvent(new TpMultiPartyCallActivityHandle(
                            callIdentifier), callAbortedEvent, EVENT_ID,
                            defaultAddress);
            
            sleeEndpointControl.setThrowable(new ActivityIsEndingException("Ending"));

            eventLookupControl.replay();

            sleeEndpointControl.replay();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        eventSenderImpl.sendEvent(callAbortedEvent,
                new TpMultiPartyCallActivityHandle(callIdentifier));

        eventLookupControl.verify();

        sleeEndpointControl.verify();
    }

    public void testGetEventLookupFacility() {
        assertEquals(eventLookupFacility, eventSenderImpl.getEventLookupFacility());
    }

    public void testGetSleeEndpoint() {
        assertEquals(sleeEndpoint, eventSenderImpl.getSleeEndpoint());
    }

}