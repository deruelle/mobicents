
package org.mobicents.slee.resource.parlay.fw;

import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.csapi.TpCommonExceptions;
import org.csapi.fw.P_ACCESS_DENIED;
import org.csapi.fw.P_INVALID_AGREEMENT_TEXT;
import org.csapi.fw.P_INVALID_SERVICE_ID;
import org.csapi.fw.P_INVALID_SERVICE_TOKEN;
import org.csapi.fw.P_INVALID_SIGNATURE;
import org.csapi.fw.P_INVALID_SIGNING_ALGORITHM;
import org.csapi.fw.P_SERVICE_ACCESS_DENIED;
import org.csapi.fw.TpSignatureAndServiceMgr;
import org.csapi.fw.fw_application.service_agreement.IpServiceAgreementManagement;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.fw.TerminateServiceAgreementEvent;
import org.mobicents.slee.resource.parlay.fw.access.TSMBean;
import org.mobicents.slee.resource.parlay.fw.application.SABeanException;
import org.mobicents.slee.resource.parlay.fw.application.SABeanImpl;
import org.mobicents.slee.resource.parlay.fw.application.SABeanListener;
import org.mobicents.slee.resource.parlay.session.IpServiceStub;
import org.mobicents.slee.resource.parlay.util.corba.ORBHandler;

/**
 *
 **/
public class SABeanImplTest extends TestCase {

    SABeanImpl saBean = null;
    
    MockControl tsmBeanControl = null;
    
    TSMBean mockTSMBean = null;
    
    MockControl serviceAgreementControl = null;
    
    IpServiceAgreementManagement mockServiceAgreementmanagement = null;
    
    SABeanListener saBeanListener = null;
    
    ORBHandler orbHandler = null;
    
    TpSignatureAndServiceMgr sigServMgr = null;
    
    FwSessionProperties fwproperties = null;
    
    byte[] digitalSignature = {0x63};
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        
        System.setProperty("com.sun.CORBA.POA.ORBServerId", "1232456");
        System.setProperty("com.sun.CORBA.POA.ORBPersistentServerPort", "1080");
        tsmBeanControl = MockControl.createControl(TSMBean.class);
        mockTSMBean = (TSMBean)tsmBeanControl.getMock();
        
        serviceAgreementControl = MockControl.createControl(IpServiceAgreementManagement.class);
        
        mockServiceAgreementmanagement = (IpServiceAgreementManagement)serviceAgreementControl.getMock();
        
        saBeanListener = new SABeanListenerStub();
        
        orbHandler = ORBHandler.getInstance();
        
        orbHandler.init();
        
        while (!orbHandler.getIsServerReady()) {
        
        }
        
        IpServiceStub serviceStub = new IpServiceStub();
        
        sigServMgr = new TpSignatureAndServiceMgr(digitalSignature, serviceStub);
        
        fwproperties = new FwSessionProperties();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        
    }
    
    public void testSABeanImpl() {
        mockTSMBean.getFwProperties();
        tsmBeanControl.setReturnValue(new FwSessionProperties());
        tsmBeanControl.replay();
        
        saBean = new SABeanImpl(mockTSMBean) {
            /* (non-Javadoc)
             * @see org.mobicents.slee.resource.parlay.fw.application.SABeanImpl#generateDigitalSignature(java.lang.String, java.lang.String, java.lang.String)
             */
            public byte[] generateDigitalSignature(String text,
                    String serviceToken, String signingAlgorithm) {
                
                return digitalSignature;
            }
        };
        
        tsmBeanControl.verify();
    }
    
    public void testInitialise() throws SABeanException {

        testSABeanImpl();
        tsmBeanControl.reset();
        
        mockTSMBean.getFwProperties();
        tsmBeanControl.setReturnValue(new FwSessionProperties());
        
        
        mockTSMBean.getOrbHandler();
        tsmBeanControl.setReturnValue(orbHandler);

        tsmBeanControl.replay();
        
        saBean.initialise();
        tsmBeanControl.verify();

    }

    public void testSetServiceAgreementManagement() {
        testSABeanImpl();
        tsmBeanControl.reset();
        saBean.setServiceAgreementManagement(mockServiceAgreementmanagement);
        assertEquals(mockServiceAgreementmanagement, saBean.getServiceAgreementManagement());
    }


    public void testGetAppServiceAgreementManagement() {
        testSABeanImpl();
        tsmBeanControl.reset();
        assertNull(saBean.getAppServiceAgreementManagement());
    }

    

    public void testAddSABeanListener() {
        testSABeanImpl();
        tsmBeanControl.reset();
        saBean.addSABeanListener(saBeanListener);
    }
    
    public void testRemoveSABeanListener() {
        testSABeanImpl();
        tsmBeanControl.reset();
        saBean.removeSABeanListener(saBeanListener);
    }

    public void testSelectAndSignServiceAgreement() throws P_INVALID_SERVICE_ID, TpCommonExceptions, P_ACCESS_DENIED, P_SERVICE_ACCESS_DENIED, P_INVALID_SERVICE_TOKEN, P_INVALID_SIGNING_ALGORITHM, P_INVALID_AGREEMENT_TEXT, SABeanException {
        testSABeanImpl();
        tsmBeanControl.reset();
        saBean.setServiceAgreementManagement(mockServiceAgreementmanagement);

        mockServiceAgreementmanagement.selectService("ui123");
        serviceAgreementControl.setReturnValue("P_USER_INTERACTION");
        mockServiceAgreementmanagement.initiateSignServiceAgreement("P_USER_INTERACTION");
        mockServiceAgreementmanagement.signServiceAgreement("P_USER_INTERACTION", "serviceAgreementText","NULL");
        serviceAgreementControl.setReturnValue(sigServMgr);
        serviceAgreementControl.replay();
        saBean.setIsAgreementSigned(true);
        saBean.putServiceTokenSigningAlgorithm("P_USER_INTERACTION", "NULL");
        saBean.selectAndSignServiceAgreement("ui123", "serviceAgreementText");
        
        
        serviceAgreementControl.verify();
        
    }

    public void testTerminateServiceAgreement() throws TpCommonExceptions, P_INVALID_SIGNATURE, P_ACCESS_DENIED, P_INVALID_SERVICE_TOKEN, SABeanException {
        testSABeanImpl();
        tsmBeanControl.reset();

        mockServiceAgreementmanagement.terminateServiceAgreement("P_USER_INTERACTION", "termination text", digitalSignature);
        
        fwproperties.setAuthenticationSequence(AuthenticationSequence.getAuthenticationSequence("TWO_WAY"));
        serviceAgreementControl.replay();
        tsmBeanControl.replay();
        saBean.setServiceAgreementManagement(mockServiceAgreementmanagement);
        saBean.putServiceTokenSigningAlgorithm("P_USER_INTERACTION", "NULL");
        saBean.terminateServiceAgreement("P_USER_INTERACTION", "termination text");
        serviceAgreementControl.verify();
        tsmBeanControl.verify();

    }

    public void testFireTerminateServiceAgreement() {
        testSABeanImpl();
        tsmBeanControl.reset();
        saBean.addSABeanListener(saBeanListener);
        
        saBean.fireTerminateServiceAgreement(new TerminateServiceAgreementEvent("ui123","terminationText"));
    }



    public void testGetTSMBean() {
        testSABeanImpl();
        tsmBeanControl.reset();
        assertEquals(mockTSMBean, saBean.getTSMBean());
    }
    
    public void generateDigitalSignature() throws UnsupportedEncodingException {

        assertEquals("NULL".getBytes("ISO-8859-1"),saBean.generateDigitalSignature("text", "ui123", "NULL"));

    }

}
