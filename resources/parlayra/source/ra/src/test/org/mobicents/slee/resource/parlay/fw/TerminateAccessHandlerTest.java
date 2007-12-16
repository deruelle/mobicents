
package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.fw.TerminateAccessEvent;
import org.mobicents.slee.resource.parlay.fw.access.TSMBean;
import org.mobicents.slee.resource.parlay.fw.access.TerminateAccessHandler;

/**
 *
 **/
public class TerminateAccessHandlerTest extends TestCase {

    TerminateAccessHandler handler = null;
    
    MockControl tsmBeanControl = null;
    
    TSMBean mockTSMBean = null;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        tsmBeanControl = MockControl.createControl(TSMBean.class);
        
        mockTSMBean = (TSMBean)tsmBeanControl.getMock();
        
        handler = new TerminateAccessHandler(mockTSMBean, "termination text", "NULL", new byte[0]);
    }


    public void testRun() {
        
        mockTSMBean.fireTerminateAccess(new TerminateAccessEvent("termination text", "NULL", new byte[0]));
        tsmBeanControl.replay();
        
        handler.run();
        
        tsmBeanControl.verify();
    }

}
