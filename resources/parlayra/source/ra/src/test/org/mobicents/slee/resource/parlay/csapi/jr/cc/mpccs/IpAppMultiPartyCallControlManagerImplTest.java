package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;

import org.csapi.cc.mpccs.TpAppMultiPartyCallBack;
import org.csapi.cc.mpccs.TpAppMultiPartyCallBackRefType;
import org.easymock.MockControl;
import org.mobicents.slee.resource.TestThreadUtil;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManagerStub;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.omg.PortableServer.POA;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

import junit.framework.TestCase;

/**
 * 
 * Class Description for IpAppMultiPartyCallControlManagerImplTest
 */
public class IpAppMultiPartyCallControlManagerImplTest extends TestCase {

    IpAppMultiPartyCallControlManagerImpl appMultiPartyCallControlManagerImpl;

    MultiPartyCallControlManager callControlManagerMock;

    MockControl callControlManagerControl;

    POA poa;

    Executor executor;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        callControlManagerControl = MockControl
                .createControl(MultiPartyCallControlManager.class);
        callControlManagerMock = (MultiPartyCallControlManager) callControlManagerControl
                .getMock();

        poa = new POAStub();

        appMultiPartyCallControlManagerImpl = new IpAppMultiPartyCallControlManagerImpl(
                callControlManagerMock, poa,  new PooledExecutor() );
    }

    public void testDispose() {
        appMultiPartyCallControlManagerImpl.dispose();
        appMultiPartyCallControlManagerImpl.dispose();
    }

    /*
     * Class under test for POA _default_POA()
     */
    public void test_default_POA() {
        assertEquals(poa, appMultiPartyCallControlManagerImpl._default_POA());
    }

    public void testReportNotification() {
		executor = new PooledExecutor();
        
        IpAppMultiPartyCallControlManagerImpl appMultiPartyCallControlManagerImpl = new IpAppMultiPartyCallControlManagerImpl(
                new MultiPartyCallControlManagerStub(), poa, executor);
        
        callControlManagerMock.reportNotification(MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER,
                MpccsTestData.TP_SLEE_CALL_LEG_IDENTIFIER_ARRAY,
                MpccsTestData.TP_CALL_NOTIFICATION_INFO,
                MpccsTestData.ASSIGNMENT_ID);
        
        callControlManagerControl.replay();

        TpAppMultiPartyCallBack result = appMultiPartyCallControlManagerImpl
                .reportNotification(MpccsTestData.TP_CALL_IDENTIFIER,
                        MpccsTestData.TP_CALL_LEG_IDENTIFIER_ARRAY,
                        MpccsTestData.TP_CALL_NOTIFICATION_INFO,
                        MpccsTestData.ASSIGNMENT_ID);

        assertNotNull(result);

        assertEquals(TpAppMultiPartyCallBackRefType.P_APP_CALL_AND_CALL_LEG_CALLBACK
                .value(), result.discriminator().value());
        
        TestThreadUtil.pause();
    }

    public void testCallAborted() {

        callControlManagerMock.callAborted(MpccsTestData.CALL_SESSION_ID);
        
        callControlManagerControl.replay();

        appMultiPartyCallControlManagerImpl
                .callAborted(MpccsTestData.CALL_SESSION_ID);
        
        TestThreadUtil.pause();
        
        callControlManagerControl.verify();
    }

    public void testManagerInterrupted() {

        callControlManagerMock.managerInterrupted();
        
        callControlManagerControl.replay();

        appMultiPartyCallControlManagerImpl
                .managerInterrupted();
        
        TestThreadUtil.pause();
        
        callControlManagerControl.verify();
    }

    public void testManagerResumed() {

        callControlManagerMock.managerResumed();
        
        callControlManagerControl.replay();

        appMultiPartyCallControlManagerImpl
                .managerResumed();
        
        TestThreadUtil.pause();
        
        callControlManagerControl.verify();
    }

    public void testCallOverloadEncountered() {

        callControlManagerMock.callOverloadEncountered(MpccsTestData.ASSIGNMENT_ID);
        
        callControlManagerControl.replay();

        appMultiPartyCallControlManagerImpl
                .callOverloadEncountered(MpccsTestData.ASSIGNMENT_ID);
        
        TestThreadUtil.pause();
        
        callControlManagerControl.verify();
    }

    public void testCallOverloadCeased() {

        callControlManagerMock.callOverloadCeased(MpccsTestData.ASSIGNMENT_ID);
        
        callControlManagerControl.replay();

        appMultiPartyCallControlManagerImpl
                .callOverloadCeased(MpccsTestData.ASSIGNMENT_ID);
        
        TestThreadUtil.pause();
        
        callControlManagerControl.verify();
    }
 

}