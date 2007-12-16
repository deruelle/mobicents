package org.mobicents.slee.resource.parlay.fw;

import java.io.IOException;

import junit.framework.TestCase;

import org.csapi.fw.TpDomainID;
import org.csapi.fw.TpProperty;
import org.csapi.fw.TpServiceProperty;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.fw.TerminateAccessEvent;
import org.mobicents.csapi.jr.slee.fw.TerminateServiceAgreementEvent;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsTestData;
import org.mobicents.slee.resource.parlay.fw.access.TSMBean;
import org.mobicents.slee.resource.parlay.fw.access.TSMBeanException;
import org.mobicents.slee.resource.parlay.fw.access.TSMBeanListener;
import org.mobicents.slee.resource.parlay.session.IpServiceStub;
import org.mobicents.slee.resource.parlay.session.SABeanStub;
import org.mobicents.slee.resource.parlay.util.corba.ORBHandler;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

/**
 * 
 * Class Description for FwSessionImplTest
 */
public class FwSessionImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        tsmBeanControl = MockControl.createControl(TSMBean.class);

        mockTSMBean = (TSMBean) tsmBeanControl.getMock();

        tpDomainID = new TpDomainID();
        tpDomainID.ClientAppID("1234");

        fwSessionProperties = new FwSessionProperties();
        fwSessionProperties.setAuthenticationSequence(AuthenticationSequence
                .getAuthenticationSequence("TWO_WAY"));
        fwSessionProperties.setDomainID(tpDomainID);
        fwSessionProperties.setIpInitialIOR("IOR:0000");

        fwSessionImpl = new FwSessionImpl(fwSessionProperties) {
            public void init() throws FwSessionException {
                this.tsmBean = mockTSMBean;
                this.tsmBean.addTSMBeanListener(tsmBeanListener);
            }
        };
        fwSessionImpl.init();

        fwSessionListener = new FwSessionListener() {

            public void terminateAccess(TerminateAccessEvent e) {
                System.out.println("Received TerminateAccessEvent");

            }

            public void terminateServiceAgreement(
                    TerminateServiceAgreementEvent e) {
                System.out.println("Received TerminateServiceAgreementEvent");

            }
        };

        tsmBeanListener = new TSMBeanListener() {

            public void terminateAccess(TerminateAccessEvent event) {

            }

        };
        TpProperty prop = new TpProperty();
        serviceProps[0] = prop;

        discoveryStub = new IpServiceDiscoveryStub();

        TpServiceProperty servprop = new TpServiceProperty();

        props[0] = servprop;
    }

    FwSessionImpl fwSessionImpl = null;

    FwSessionProperties fwSessionProperties = null;

    TpDomainID tpDomainID = null;

    FwSessionListener fwSessionListener = null;

    TSMBeanListener tsmBeanListener = null;

    MockControl tsmBeanControl;

    static TSMBean mockTSMBean;

    TpProperty[] serviceProps = new TpProperty[1];

    TpServiceProperty[] props = new TpServiceProperty[1];

    ServiceAndToken serviceAndToken = new ServiceAndToken(new IpServiceStub(),
            "service1");

    IpServiceDiscoveryStub discoveryStub = null;
    

    public void testInit() {
        try {

            fwSessionImpl.init();

        } catch (FwSessionException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    public void testAddFwSessionListener() {
        fwSessionImpl.addFwSessionListener(fwSessionListener);
    }

    public void testGetService() throws TSMBeanException, FwSessionException {

        tsmBeanControl.reset();

        mockTSMBean.obtainDiscoveryInterface();
        tsmBeanControl.setReturnValue(discoveryStub);

        mockTSMBean.getSABean();
        tsmBeanControl.setReturnValue(null);

        mockTSMBean.createSABean();
        tsmBeanControl.setReturnValue(new SABeanStub());

        tsmBeanControl.replay();

        fwSessionImpl.getService("P_CLIENT_APPLCIATION", props);
        tsmBeanControl.verify();

    }

    public void testRemoveFwSessionListener() {

        tsmBeanControl.reset();

        mockTSMBean.removeTSMBeanListener(fwSessionImpl);

        mockTSMBean.getSABean();
        tsmBeanControl.setReturnValue(new SABeanStub(), 2);

        tsmBeanControl.replay();
        fwSessionImpl.removeFwSessionListener(fwSessionListener);
        tsmBeanControl.verify();
    }

    public void testAuthenticate() throws TSMBeanException, FwSessionException {

        tsmBeanControl.reset();
        mockTSMBean.authenticate();

        tsmBeanControl.replay();

        fwSessionImpl.authenticate();
        tsmBeanControl.verify();

    }

    public void testEndAccess() throws TSMBeanException, FwSessionException {
        tsmBeanControl.reset();

        mockTSMBean.endAccess(serviceProps);
        tsmBeanControl.replay();

        fwSessionImpl.endAccess(serviceProps);

        tsmBeanControl.verify();

    }

    public void testReleaseService() throws FwSessionException {
        tsmBeanControl.reset();

        mockTSMBean.getSABean();
        tsmBeanControl.setReturnValue(new SABeanStub());
        tsmBeanControl.replay();

        fwSessionImpl.releaseService(serviceAndToken);

        tsmBeanControl.verify();

    }

    public void testShutdown() {
        tsmBeanControl.reset();

        mockTSMBean.getSABean();
        tsmBeanControl.setReturnValue(new SABeanStub());

        mockTSMBean.shutdown();
        tsmBeanControl.replay();

        fwSessionImpl.shutdown();

        tsmBeanControl.verify();
    }

    public void testGetFwSessionProperties() {
        tsmBeanControl.reset();
        assertEquals(fwSessionProperties, fwSessionImpl
                .getFwSessionProperties());
    }

    public void testGetOrb() throws IOException {
        tsmBeanControl.reset();
        ORBHandler orbHandler = null;

        orbHandler = ORBHandler.getInstance();

        mockTSMBean.getOrbHandler();

        tsmBeanControl.setReturnValue(orbHandler);
        tsmBeanControl.replay();

        ORB result = fwSessionImpl.getORB();

        assertEquals(orbHandler.getOrb(), result);
        tsmBeanControl.verify();
    }
    
    public void testGetRootPOA() throws IOException {
        
        tsmBeanControl.reset();
        ORBHandler orbHandler = null;
        orbHandler = ORBHandler.getInstance();

        mockTSMBean.getOrbHandler();

        tsmBeanControl.setReturnValue(orbHandler);
        tsmBeanControl.replay();

        POA result = fwSessionImpl.getRootPOA();

        assertEquals(orbHandler.getRootPOA(), result);
        tsmBeanControl.verify();
        
    }
    
    public void testTerminateAccess() {
        fwSessionImpl.addFwSessionListener(fwSessionListener);
        fwSessionImpl.terminateAccess(GccsTestData.terminateAccessEvent);
        
    }
    
    public void testTerminateSignServiceAgreement() {
        fwSessionImpl.addFwSessionListener(fwSessionListener);
        fwSessionImpl.terminateServiceAgreement(GccsTestData.terminateServiceAgreementEvent);
    }

}