package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.fw.TerminateAccessEvent;

/**
 *
 * Class Description for TerminateAccessEventTest
 */
public class TerminateAccessEventTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        mockControl = MockControl.createControl(FwSession.class);
        
        source = (FwSession)mockControl.getMock();
        
        event = new TerminateAccessEvent(TERMINATION_TEXT, SIGNING_ALGORITHM, DIGITAL_SIGNATURE);
    }
    
    
    static final byte[] DIGITAL_SIGNATURE = new byte[0];
    
    static final String TERMINATION_TEXT = "You're terminated.";
    
    static final String SIGNING_ALGORITHM = "NULL";

    
    FwSession source;
    
    MockControl mockControl;
    
    TerminateAccessEvent event;

    public void testGetTerminationText() {
        
        assertEquals(TERMINATION_TEXT, event.getTerminationText());
    }

    public void testGetDigitalSignature() {
        
        assertEquals(DIGITAL_SIGNATURE, event.getDigitalSignature());
    }
    
    public void testGetSigningAlgorithm() {
        
        assertEquals(SIGNING_ALGORITHM, event.getSigningAlgorithm());
    }

}
