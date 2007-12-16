package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.fw.TerminateServiceAgreementEvent;

/**
 *
 * Class Description for TerminateServiceAgreementEventTest
 */
public class TerminateServiceAgreementEventTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        mockControl = MockControl.createControl(FwSession.class);
        
        source = (FwSession)mockControl.getMock();
        
        event = new TerminateServiceAgreementEvent(SERVICE_TOKEN, TERMINATION_TEXT);
    }
    static final String TERMINATION_TEXT = "You're terminated.";
    
    static final String SERVICE_TOKEN = "Token";
    
    FwSession source;
    
    MockControl mockControl;
    
    TerminateServiceAgreementEvent event;

    public void testGetTerminationText() {
        
        assertEquals(TERMINATION_TEXT, event.getTerminationText());
    }

    public void testGetServiceToken() {
        
        assertEquals(SERVICE_TOKEN, event.getServiceToken());
    }

}
