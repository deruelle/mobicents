
package org.mobicents.slee.resource.parlay.fw;

import java.security.Security;

import junit.framework.TestCase;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.csapi.fw.TpAuthDomain;
import org.csapi.fw.TpDomainID;
import org.csapi.fw.TpProperty;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.fw.TerminateAccessEvent;
import org.mobicents.slee.resource.parlay.fw.access.AuthenticationHandler;
import org.mobicents.slee.resource.parlay.fw.access.TSMBeanConstants;
import org.mobicents.slee.resource.parlay.fw.access.TSMBeanException;
import org.mobicents.slee.resource.parlay.fw.access.TSMBeanImpl;
import org.mobicents.slee.resource.parlay.fw.access.TSMBeanListener;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.mobicents.slee.resource.parlay.util.corba.ORBHandler;

/**
 *
 **/
public class TSMBeanImplTest extends TestCase {

    TSMBeanImpl tsmBean = null;
    
    MockControl fwSessionControl = null;
    
    FwSession mockFwSession = null;
    
    FwSessionProperties fwProperties = null;
    
    TpDomainID domainID = null;

    org.omg.PortableServer.POA rootPoa = new POAStub();
    
    TSMBeanListener tsmBeanListener = null;
    
    MockControl tsmBeanListenerControl = null;
    
    AuthenticationHandler authHandler  = null;
    
    MockControl authHandlerControl = null;
    
    TpAuthDomain authDomain = null;
    
    TpProperty[] tpPropertySet = null;
    
    TpDomainID tpDomainID = null;
    
    IpAccessStub ipAccessStub = null;
    
    TerminateAccessEvent taEvent = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        Security.addProvider(new BouncyCastleProvider());
        
        System.setProperty("com.sun.CORBA.POA.ORBServerId", "1232456");
        System.setProperty("com.sun.CORBA.POA.ORBPersistentServerPort", "1080");
        
        fwSessionControl = MockControl.createControl(FwSession.class);
        
        tsmBeanListenerControl = MockControl.createControl(TSMBeanListener.class);
        
        authHandlerControl = MockControl.createControl(AuthenticationHandler.class);
        
        mockFwSession = (FwSession)fwSessionControl.getMock();
        
        tsmBeanListener = (TSMBeanListener)tsmBeanListenerControl.getMock();
        
        authHandler =(AuthenticationHandler)authHandlerControl.getMock();
        
        fwProperties = new FwSessionProperties();
        
        domainID = new TpDomainID();
        domainID.ClientAppID("P_CLIENT_APPLICATION");
        fwProperties.setDomainID(domainID);
        
        fwProperties.setAuthenticationSequence(AuthenticationSequence.getAuthenticationSequence("TWO_WAY"));
        fwProperties.setFwParlayVersion("P_PARLAY_4");
        fwProperties.setIpInitialIOR("IOR:000000000000003c49444c3a6f72672f63736170692f66772f66775f6163636573732f74727573745f616e645f73656375726974792f4970496e697469616c3a312e300000000001000000000000006600010200000000076575636c6964000017d50000000000193a3e023331066677506f61000c4970496e697469616c4f6964000000000000020000000100000018000000000001000100000000000101040000000100010109000000060000000600000000000b");
        
        
        tpPropertySet = new TpProperty[0];
        
        tpDomainID = new TpDomainID();
        tpDomainID.ClientAppID("CLIENT_APP");
        
        tsmBean = new TSMBeanImpl(mockFwSession, fwProperties);
        
        ipAccessStub = new IpAccessStub();
        
        taEvent = new TerminateAccessEvent("termination text", "NULL", new byte[0]);
    }

    /*Tests initialisation, authentication, discover, requestAccess, endAccess, shutdown*/
    public void testAll() throws TSMBeanException {

        mockFwSession.getRootPOA();
        fwSessionControl.setReturnValue(rootPoa);
        mockFwSession.getRootPOA();
        fwSessionControl.setReturnValue(rootPoa);
       
        fwSessionControl.replay();

        tsmBean.initialize();
        
        tsmBean.setAuthHandler(authHandler);
        
        authHandler.selectAuthenticationMechanism("P_OSA_MD5");
        authHandlerControl.setReturnValue("P_OSA_MD5");
        
        authHandler.challenge("P_OSA_MD5");
        
        authHandler.authenticationSucceeded();
        
        authHandler.initiateAuthenticationWithVersion(authDomain,"P_PARLAY_4");

        authHandler.requestAccess(null);
        authHandlerControl.setReturnValue(ipAccessStub);
        authHandler.cleanup();
        authHandlerControl.replay();
        
        tsmBean.setTpAuthDomain(authDomain);
        tsmBean.setFwAuthenticationSucceeded(true);
        tsmBean.authenticate();
        
        createSABean();
        
        obtainDiscoveryInterface();
        
        releaseDiscoveryInterface();
        
        endAccess();
        
        tsmBean.destroySABean();
        
        tsmBean.shutdown();
       
        fwSessionControl.verify();

        authHandlerControl.verify();
        
    }

    
    public void testGetState() {
        assertEquals(TSMBeanConstants.IDLE_STATE, tsmBean.getState());
    }   
    
    public void addTSMBeanListener() {

        tsmBean.addTSMBeanListener(tsmBeanListener);
    }
    
    public void removeTSMBeanListener() {

        tsmBean.removeTSMBeanListener(tsmBeanListener);
    }

    
    private void createSABean() throws TSMBeanException {
        tsmBean.setState(TSMBeanConstants.ACTIVE_STATE);
        tsmBean.createSABean();
        
        tsmBean.destroySABean();
    }
    
    private void endAccess() throws TSMBeanException {

        tsmBean.setState(TSMBeanConstants.ACTIVE_STATE);
        tsmBean.endAccess(tpPropertySet);
    }
    
    
    public void testGetClientDomain() {
        assertEquals("CLIENT_APP",tsmBean.getClientDomain(tpDomainID));
        
    }
    
    public void testGetClientID() {
        assertEquals("P_CLIENT_APPLICATION",tsmBean.getClientID());
        
    }
    
    public void testGetEncryptionMethod() {
        assertNull(tsmBean.getEncryptionMethod());
    }
    
    public void testGetEventsQueue() {
        assertNull(tsmBean.getEventsQueue());
    }
    
    public void testGetSABean() {
        assertNull(tsmBean.getSABean());
    }
    
    private void obtainDiscoveryInterface() throws TSMBeanException {
        tsmBean.setState(TSMBeanConstants.ACTIVE_STATE);
        tsmBean.obtainDiscoveryInterface();
    }
    
    private void releaseDiscoveryInterface() throws TSMBeanException {
        tsmBean.setState(TSMBeanConstants.ACTIVE_STATE);
        tsmBean.releaseDiscoveryInterface();
    }
    
    public void testSetClientDomainID() {
        tsmBean.setClientDomainID(domainID);       
        
        assertEquals(tsmBean.getClientDomainID(), domainID);
    }
    
    public void testFireTerminateAccess() {
        tsmBeanListener.terminateAccess(taEvent);
        tsmBeanListenerControl.replay();
        tsmBean.addTSMBeanListener(tsmBeanListener);
        tsmBean.fireTerminateAccess(taEvent);
        tsmBean.removeTSMBeanListener(tsmBeanListener);
        tsmBeanListenerControl.verify();
    }
    
    public void testGetFwProperties() {
        tsmBean.setFwProperties(fwProperties);
        assertEquals(fwProperties, tsmBean.getFwProperties());
        
    }
    
    public void testSetFwPorperties() {
        tsmBean.setFwProperties(fwProperties);
        
        assertEquals(fwProperties, tsmBean.getFwProperties());
    }
    
    public void testGetOrbHandler() {
        ORBHandler o = tsmBean.getOrbHandler();
        assertNull(o);
    }
    
    public void testAbortAuthentication() throws TSMBeanException {
        fwProperties.setFwParlayVersion("P_PARLAY_4");

        mockFwSession.getRootPOA();
        fwSessionControl.setReturnValue(rootPoa);
        mockFwSession.getRootPOA();
        fwSessionControl.setReturnValue(rootPoa);
       
        fwSessionControl.replay();

        tsmBean.initialize();
        
        tsmBean.setAuthHandler(authHandler);

        authHandler.initiateAuthenticationWithVersion(authDomain, "P_PARLAY_4");
        
        authHandler.selectAuthenticationMechanism("P_OSA_MD5");
        authHandlerControl.setThrowable(new TSMBeanException("Bad Authenticate"));
        authHandler.abortAuthentication();

        authHandler.cleanup();
        authHandler.cleanup();
        authHandlerControl.replay();
        
        tsmBean.setTpAuthDomain(authDomain);
        tsmBean.setFwAuthenticationSucceeded(true);
        try {
            tsmBean.authenticate();
        } catch (Exception e) {
            System.out.println("expected exception");
        }

        
        tsmBean.shutdown();
       
        fwSessionControl.verify();

        authHandlerControl.verify();
        
    }
    
    /*Tests initialisation, authentication, discover, requestAccess, endAccess, shutdown*/
    public void testAllP3() throws TSMBeanException {
        fwProperties.setFwParlayVersion("P_PARLAY_3");

        mockFwSession.getRootPOA();
        fwSessionControl.setReturnValue(rootPoa);
        mockFwSession.getRootPOA();
        fwSessionControl.setReturnValue(rootPoa);
       
        fwSessionControl.replay();

        tsmBean.initialize();
        
        tsmBean.setAuthHandler(authHandler);
        
        authHandler.authenticationSucceeded();
        
        authHandler.initiateAuthentication(authDomain);
        
        authHandler.selectEncryptionMethod("NULL,P_RSA_512,P_RSA_1024");
        authHandlerControl.setReturnValue("NULL");
        
        authHandler.authenticate("NULL");

        authHandler.requestAccess(null);
        authHandlerControl.setReturnValue(ipAccessStub);
        authHandler.cleanup();
        authHandlerControl.replay();
        
        tsmBean.setTpAuthDomain(authDomain);
        tsmBean.setFwAuthenticationSucceeded(true);
        tsmBean.authenticate();
        
        createSABean();
        
        obtainDiscoveryInterface();
        
        releaseDiscoveryInterface();
        
        endAccess();
        
        tsmBean.shutdown();
       
        fwSessionControl.verify();

        authHandlerControl.verify();
        
    }
    
}
