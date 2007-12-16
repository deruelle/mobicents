
package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.fw.access.IpClientAPILevelAuthenticationImpl;
import org.mobicents.slee.resource.parlay.fw.access.TSMBean;
import org.mobicents.slee.resource.parlay.fw.access.TSMBeanConstants;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.mobicents.slee.resource.parlay.util.crypto.CHAPUtil;
import org.omg.PortableServer.POA;

/**
 *
 **/
public class IpClientAPILevelAuthenticationImplTest extends TestCase {

    IpClientAPILevelAuthenticationImpl clientAPILevelAuthenticationImpl = null;
    
    POA poa = null;
    
    CHAPUtil chapUtil = null;
    
    MockControl tsmBeanControl = null;
    
    TSMBean mockTSMBean = null;
    
    byte[] challenge = {0x61, 0x62, 0x63, 0x61, 0x62, 0x63, 0x61, 0x62, 0x63, 0x61, 0x62, 0x63, 0x61, 0x62, 0x63, 0x61, 0x62, 0x63};
    
    FwSessionProperties props = null;
    
     /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        clientAPILevelAuthenticationImpl = new IpClientAPILevelAuthenticationImpl();
        
        poa = new POAStub();
        
        chapUtil = new CHAPUtil();
        
        tsmBeanControl = MockControl.createControl(TSMBean.class);
        
        mockTSMBean = (TSMBean)tsmBeanControl.getMock();
        
        clientAPILevelAuthenticationImpl.setTSMBean(mockTSMBean);
        
        props = new FwSessionProperties();
    }

    /*
     * Class under test for void IpClientAPILevelAuthenticationImpl(org.omg.PortableServer.POA)
     */
    public void testIpClientAPILevelAuthenticationImplPOA() {
        clientAPILevelAuthenticationImpl = new IpClientAPILevelAuthenticationImpl(poa);
        assertEquals(poa, clientAPILevelAuthenticationImpl._default_POA());
    }


    public void test_create() {
        assertNotNull(IpClientAPILevelAuthenticationImpl._create(poa));
    }

    public void testAuthenticate() {
        
        mockTSMBean.getAuthenticationMonitor();
        tsmBeanControl.setReturnValue(new Object());
        mockTSMBean.getEncryptionMethod();
        tsmBeanControl.setReturnValue(TSMBeanConstants.NULL_AUTH);
        
        
        tsmBeanControl.replay();
        clientAPILevelAuthenticationImpl.authenticate(challenge);
        tsmBeanControl.verify();
    }

    public void testChallenge() {
        
        mockTSMBean.getAuthenticationMonitor();
        tsmBeanControl.setReturnValue(new Object());
        
        mockTSMBean.getFwProperties();
        tsmBeanControl.setReturnValue(props, 3);
        
        
        tsmBeanControl.replay();
        assertNotNull(clientAPILevelAuthenticationImpl.challenge(chapUtil.generateCHAPRequestPacket(challenge)));
        tsmBeanControl.verify();
    }

    public void testAbortAuthentication() {
        tsmBeanControl.replay();
        clientAPILevelAuthenticationImpl.abortAuthentication();
        tsmBeanControl.verify();
    }

    public void testAuthenticationSucceeded() {
        
        mockTSMBean.getAuthenticationMonitor();
        tsmBeanControl.setReturnValue(new Object(),2);
        mockTSMBean.setFwAuthenticationSucceeded(true);
        tsmBeanControl.replay();
        clientAPILevelAuthenticationImpl.authenticationSucceeded();
        tsmBeanControl.verify();
    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
        assertNotNull(clientAPILevelAuthenticationImpl.toString());
    }

    public void testCleanup() {
        clientAPILevelAuthenticationImpl.cleanup();
    }

}
