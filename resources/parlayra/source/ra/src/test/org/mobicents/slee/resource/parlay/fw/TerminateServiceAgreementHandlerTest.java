
package org.mobicents.slee.resource.parlay.fw;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.fw.TerminateServiceAgreementEvent;
import org.mobicents.slee.resource.parlay.fw.application.SABean;
import org.mobicents.slee.resource.parlay.fw.application.TerminateServiceAgreementHandler;

import junit.framework.TestCase;

/**
 *
 **/
public class TerminateServiceAgreementHandlerTest extends TestCase {

    MockControl saBeanControl = null;
    
    SABean mockSaBean = null;
    
    TerminateServiceAgreementHandler handler = null;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        saBeanControl = MockControl.createControl(SABean.class);
        
        mockSaBean = (SABean)saBeanControl.getMock();
        
        handler = new TerminateServiceAgreementHandler(mockSaBean, "ui123", "terminatation text");
        
    }


    public void testRun() {
        mockSaBean.fireTerminateServiceAgreement(new TerminateServiceAgreementEvent("ui123", "terminatation text"));
        saBeanControl.setMatcher(MockControl.ALWAYS_MATCHER);
        saBeanControl.replay();
        
        handler.run();
        
        saBeanControl.verify();
    }

}
