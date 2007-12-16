
package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

import org.csapi.fw.P_INVALID_SERVICE_TOKEN;
import org.csapi.fw.P_INVALID_SIGNATURE;
import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.fw.access.TSMBean;
import org.mobicents.slee.resource.parlay.fw.application.IpAppServiceAgreementManagementImpl;
import org.mobicents.slee.resource.parlay.fw.application.SABean;
import org.mobicents.slee.resource.parlay.fw.application.TerminateServiceAgreementHandler;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.omg.PortableServer.POA;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

/**
 *
 **/
public class IpAppServiceAgreementManagementImplTest extends TestCase {

    IpAppServiceAgreementManagementImpl appServiceAgreementManagementImpl = null;
    
    POA poa = null;
    
    MockControl saBeanControl = null;
    
    SABean mockSaBean = null;
    
    MockControl tsmBeanControl = null;
    
    TSMBean mockTSMBean = null;
    
    MockControl executorControl = null;
    
    Executor mockExecutor = null;
    
    Executor executor = null;
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        saBeanControl = MockControl.createControl(SABean.class);
        
        mockSaBean = (SABean)saBeanControl.getMock();
        
        tsmBeanControl = MockControl.createControl(TSMBean.class);
        
        mockTSMBean = (TSMBean)tsmBeanControl.getMock();
        
        executorControl = MockControl.createControl(Executor.class);
        
        mockExecutor = (Executor)executorControl.getMock();
        
        executor = new QueuedExecutor();
        
        poa = new POAStub();
        appServiceAgreementManagementImpl = new IpAppServiceAgreementManagementImpl(poa);
        appServiceAgreementManagementImpl.setSABean(mockSaBean);

    }


    public void test_create() {
        assertNotNull(appServiceAgreementManagementImpl._create(poa));
    }

    public void testSignServiceAgreement() {
        mockSaBean.getServiceAgreementMonitor();
        saBeanControl.setReturnValue(new Object());
        mockSaBean.putServiceTokenSigningAlgorithm("ui123", "NULL");
        mockSaBean.setIsAgreementSigned(true);
        saBeanControl.replay();
        appServiceAgreementManagementImpl.signServiceAgreement("ui123", "agreement text","NULL");
        
        saBeanControl.verify();
    }

    public void testTerminateServiceAgreement() throws P_INVALID_SERVICE_TOKEN, P_INVALID_SIGNATURE, InterruptedException {

        saBeanControl.reset();
        
        mockSaBean.verifyDigitalSignature("termination text", "ui123", "NULL", new byte[0]);
        saBeanControl.setMatcher(MockControl.ALWAYS_MATCHER);
        saBeanControl.setReturnValue(true);
        
            
        mockSaBean.getTSMBean();
        saBeanControl.setReturnValue(mockTSMBean);
        
        //mockSaBean.fireTerminateServiceAgreement(new TerminateServiceAgreementEvent("ui123", "termination text"));
        //saBeanControl.setMatcher(MockControl.ALWAYS_MATCHER);
        
        mockSaBean.removeSigningAlgorithm("ui123");
        saBeanControl.setReturnValue("NULL");
        
        saBeanControl.replay();
        
        mockTSMBean.getEventsQueue();
        tsmBeanControl.setReturnValue(mockExecutor);
        tsmBeanControl.replay();
        
        mockExecutor.execute(new TerminateServiceAgreementHandler(mockSaBean, "ui123", "termination text"));
        executorControl.setMatcher(MockControl.ALWAYS_MATCHER);
        executorControl.replay();
        
        appServiceAgreementManagementImpl.terminateServiceAgreement("ui123", "termination text", new byte[0]);
        
        saBeanControl.verify();
        tsmBeanControl.verify();
        executorControl.verify();
        
    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
        assertNotNull(appServiceAgreementManagementImpl.toString());
    }

    public void testCleanup() {
        appServiceAgreementManagementImpl.cleanup();
    }

}
