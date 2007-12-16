package org.mobicents.slee.resource.parlay;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.slee.resource.ActivityHandle;

import org.csapi.cc.TpCallEventInfo;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.EventReportResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.TpMultiPartyCallActivityHandle;

import junit.framework.TestCase;

/**
 * 
 * Class Description for ParlayMarshalerTest
 */
public class ParlayMarshalerTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        serviceIdentifier = new TpServiceIdentifier(100);

        multiPartyCallIdentifier = new TpMultiPartyCallIdentifier(
                MpccsTestData.CALL_ID, MpccsTestData.CALL_SESSION_ID);

        callLegIdentifier = new TpCallLegIdentifier(MpccsTestData.CALL_LEG_ID,
                MpccsTestData.CALL_LEG_SESSION_ID);

        multiPartyCallActivityHandle = new TpMultiPartyCallActivityHandle(
                multiPartyCallIdentifier);

        callEventInfo = MpccsTestData.TP_CALL_EVENT_INFO;

        event = new EventReportResEvent(serviceIdentifier,
                multiPartyCallIdentifier, callLegIdentifier, callEventInfo);

        marshaler = new ParlayMarshaler();
    }

    EventReportResEvent event;

    TpCallEventInfo callEventInfo;

    TpServiceIdentifier serviceIdentifier;

    TpMultiPartyCallIdentifier multiPartyCallIdentifier;

    TpCallLegIdentifier callLegIdentifier;

    TpMultiPartyCallActivityHandle multiPartyCallActivityHandle;

    ParlayMarshaler marshaler;

    public void testMarshalEvent() {
        try {
            ByteBuffer byteBuffer = marshaler.marshalEvent(event, 1);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testUnmarshalEvent() {
        try {
            ByteBuffer byteBuffer = marshaler.marshalEvent(event, 1);
            Object o = marshaler.unmarshalEvent(byteBuffer, 1);

            assertTrue(o instanceof EventReportResEvent);

            EventReportResEvent event2 = (EventReportResEvent) o;

            assertEquals(event.getService(), event2.getService());

            assertEquals(event.getTpCallLegIdentifier(), event2
                    .getTpCallLegIdentifier());

            assertEquals(event.getTpMultiPartyCallIdentifier(), event2
                    .getTpMultiPartyCallIdentifier());

            assertEquals(event.getEventInfo().CallEventTime, event2
                    .getEventInfo().CallEventTime);

            assertEquals(event.getEventInfo().CallEventType.value(), event2
                    .getEventInfo().CallEventType.value());

            assertEquals(event.getEventInfo().CallMonitorMode.value(), event2
                    .getEventInfo().CallMonitorMode.value());

            assertEquals(event.getEventInfo().AdditionalCallEventInfo
                    .discriminator().value(),
                    event2.getEventInfo().AdditionalCallEventInfo
                            .discriminator().value());
        }
        catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testMarshalHandle() {
        try {
            ByteBuffer byteBuffer = marshaler
                    .marshalHandle(multiPartyCallActivityHandle);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testUnmarshalHandle() {
        try {
            ByteBuffer byteBuffer = marshaler
                    .marshalHandle(multiPartyCallActivityHandle);

            ActivityHandle activityHandle = marshaler
                    .unmarshalHandle(byteBuffer);

            assertTrue(activityHandle instanceof TpMultiPartyCallActivityHandle);

            TpMultiPartyCallActivityHandle multiPartyCallActivityHandle2 = (TpMultiPartyCallActivityHandle) activityHandle;

            assertEquals(multiPartyCallActivityHandle, multiPartyCallActivityHandle2);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

}