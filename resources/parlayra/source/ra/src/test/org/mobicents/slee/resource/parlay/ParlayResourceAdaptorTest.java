package org.mobicents.slee.resource.parlay;

import java.util.Properties;

import javax.slee.Address;
import javax.slee.AddressPlan;
import javax.slee.facilities.EventLookupFacility;
import javax.slee.resource.BootstrapContext;
import javax.slee.resource.FailureReason;
import javax.slee.resource.SleeEndpoint;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;

/**
 * 
 * Class Description for ParlayResourceAdaptorTest
 */
public class ParlayResourceAdaptorTest extends TestCase {

    ParlayResourceAdaptor parlayResourceAdaptor;

    ParlayResourceAdaptorProperties adaptorProperties;

    BootstrapContext bootstrapContext;

    MockControl bootstrapContextControl;

    SleeEndpoint sleeEndpoint;

    MockControl sleeEndpointControl;

    EventLookupFacility eventLookupFacility;

    MockControl eventLookupFacilityControl;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        adaptorProperties = new ParlayResourceAdaptorProperties();
        adaptorProperties.setAuthenticationSequence("ONE_WAY");
        adaptorProperties.setDomainID("1234");
        adaptorProperties.setIpInitialIOR("");
        adaptorProperties.setIpInitialLocation("");
        adaptorProperties.setIpInitialURL("");
        adaptorProperties.setNamingServiceIOR("");

        // Can't test all functionality at the minute as the BootStrapContext
        // has dependencies on TransactionManager which is not available without
        // a
        // J2EE environment

        //        bootstrapContextControl =
        // MockControl.createControl(BootstrapContext.class);
        //        
        //        bootstrapContext =
        // (BootstrapContext)bootstrapContextControl.getMock();

        sleeEndpointControl = MockControl.createControl(SleeEndpoint.class);

        sleeEndpoint = (SleeEndpoint) sleeEndpointControl.getMock();

        eventLookupFacilityControl = MockControl
                .createControl(EventLookupFacility.class);

        eventLookupFacility = (EventLookupFacility) eventLookupFacilityControl
                .getMock();

        parlayResourceAdaptor = new ParlayResourceAdaptor();
    }

    /*
     * Class under test for void ParlayResourceAdaptor(Properties)
     */
    public void testParlayResourceAdaptorProperties() {
        Properties properties = new Properties();
        properties.setProperty(
                "org.mobicents.slee.resource.parlay.ipInitialIOR", "");
        properties.setProperty("org.mobicents.slee.resource.parlay.domainID",
                "1234");
        properties.setProperty(
                "org.mobicents.slee.resource.parlay.authenticationSequence",
                "ONE_WAY");
        properties.setProperty(
                "org.mobicents.slee.resource.parlay.ipInitialLocation", "");
        properties.setProperty(
                "org.mobicents.slee.resource.parlay.ipInitialURL", "");
        properties.setProperty(
                "org.mobicents.slee.resource.parlay.namingServiceIOR", "");

        parlayResourceAdaptor = new ParlayResourceAdaptor(properties);

    }

    /*
     * Class under test for void ParlayResourceAdaptor(String, String, String,
     * String, String, String)
     */
    public void testParlayResourceAdaptorStringStringStringStringStringString() {

        parlayResourceAdaptor = new ParlayResourceAdaptor("", "", "", "",
                "1234", "ONE_WAY");
    }

    /*
     * Class under test for void ParlayResourceAdaptor()
     */
    public void testParlayResourceAdaptor() {

        parlayResourceAdaptor = new ParlayResourceAdaptor();
    }

    public void testEntityCreated() {

        //        bootstrapContext.getSleeEndpoint();
        //        bootstrapContextControl.setReturnValue(sleeEndpoint);
        //        bootstrapContext.getEventLookupFacility();
        //        bootstrapContextControl.setReturnValue(eventLookupFacility);
        //        bootstrapContextControl.replay();
        //        
        //        try {
        //            parlayResourceAdaptor.entityCreated(bootstrapContext);
        //        }
        //        catch (ResourceException e) {
        //            e.printStackTrace();
        //            fail();
        //        }
        //        
        //        bootstrapContextControl.verify();
    }

    public void testEntityRemoved() {
        parlayResourceAdaptor.entityRemoved();
    }

    public void testEntityActivated() {
    }

    public void testEntityDeactivating() {
    }

    public void testEntityDeactivated() {
    }

    public void testEventProcessingSuccessful() {

        parlayResourceAdaptor.eventProcessingSuccessful(
                MpccsTestData.callActivityHandle,
                MpccsTestData.callAbortedEvent, 1, new Address(AddressPlan.SIP,
                        "user@domain.com"), 1);
    }

    public void testEventProcessingFailed() {

        parlayResourceAdaptor.eventProcessingFailed(
                MpccsTestData.callActivityHandle,
                MpccsTestData.callAbortedEvent, 1, new Address(AddressPlan.SIP,
                        "user@domain.com"), 1, FailureReason.EVENT_QUEUE_FULL);
    }

    public void testActivityEnded() {
    }

    public void testActivityUnreferenced() {
    }

    public void testQueryLiveness() {
    }

    public void testGetActivity() {
    }

    public void testGetActivityHandle() {
    }

    public void testGetSBBResourceAdaptorInterface() {
        assertNull(parlayResourceAdaptor.getSBBResourceAdaptorInterface(null));
    }

    public void testGetMarshaler() {
        assertNull(parlayResourceAdaptor.getMarshaler());
    }

    public void testServiceInstalled() {
        parlayResourceAdaptor.serviceInstalled("A", new int[] { 1, 2, 3 },
                new String[] { "a" });
    }

    public void testServiceUninstalled() {
        parlayResourceAdaptor.serviceUninstalled("A");
    }

    public void testServiceActivated() {
        parlayResourceAdaptor.serviceActivated("A");
    }

    public void testServiceDeactivated() {
        parlayResourceAdaptor.serviceDeactivated("A");
    }

}