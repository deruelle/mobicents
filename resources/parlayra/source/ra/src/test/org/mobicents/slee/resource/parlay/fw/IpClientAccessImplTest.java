
package org.mobicents.slee.resource.parlay.fw;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.fw.TerminateAccessEvent;
import org.mobicents.slee.resource.TestThreadUtil;
import org.mobicents.slee.resource.parlay.fw.access.IpClientAccessImpl;
import org.mobicents.slee.resource.parlay.fw.access.TSMBean;
import org.mobicents.slee.resource.parlay.fw.application.SABean;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.omg.PortableServer.POA;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

import junit.framework.TestCase;

/**
 *
 **/
public class IpClientAccessImplTest extends TestCase {

    MockControl tsmBeanControl = null;
    
    TSMBean mockTSMbean = null;

    POA poa = null;
    
    IpClientAccessImpl ipClientAccessImpl = null;
    
    MockControl saBeanControl = null;
    
    SABean mockSaBean = null;
    
    Executor executor = null;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        
        tsmBeanControl = MockControl.createControl(TSMBean.class);
        
        mockTSMbean = (TSMBean)tsmBeanControl.getMock();
        
        poa = new POAStub();
        
        saBeanControl = MockControl.createControl(SABean.class);
        
        mockSaBean = (SABean)saBeanControl.getMock();
        
        ipClientAccessImpl  = new IpClientAccessImpl(poa, mockTSMbean);
        
        executor = new QueuedExecutor();
    }

    public void test_create() {
        assertNotNull(IpClientAccessImpl._create(poa, mockTSMbean));
    }

    public void testTerminateAccess() {
        ipClientAccessImpl.setTSMBean(mockTSMbean);
        
        mockTSMbean.getEventsQueue();
        tsmBeanControl.setReturnValue(executor);
        
        mockTSMbean.getSABean();
        tsmBeanControl.setReturnValue(mockSaBean);
        
        mockTSMbean.fireTerminateAccess(new TerminateAccessEvent("terminationText", "NULL", new byte[0]));
        tsmBeanControl.setMatcher(MockControl.ALWAYS_MATCHER);
        
        mockTSMbean.cleanup();
        tsmBeanControl.replay();
        
        mockSaBean.cleanup();
        
        saBeanControl.replay();
        
        ipClientAccessImpl.terminateAccess("terminationText", "NULL", new byte[0]);
        TestThreadUtil.pause();
        
        tsmBeanControl.verify();
        saBeanControl.verify();
    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
        assertNotNull(ipClientAccessImpl.toString());
    }


    /*
     * Class under test for org.omg.PortableServer.POA _default_POA()
     */
    public void test_default_POA() {
        assertEquals(poa, ipClientAccessImpl._default_POA());
    }

     
}
