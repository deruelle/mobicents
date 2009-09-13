package org.mobicents.media.server.ctrl.mgcp.test.ann;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotifyResponse;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mobicents.jain.protocol.ip.mgcp.pkg.AUMgcpEvent;
import org.mobicents.jain.protocol.ip.mgcp.pkg.AUPackage;
import org.mobicents.media.server.ctrl.mgcp.test.Connection;
import org.mobicents.media.server.ctrl.mgcp.test.MgcpMicrocontainerTest;

/**
 * 
 * @author amit bhayani
 * 
 */
public class AnnTestCase extends MgcpMicrocontainerTest {

    private final static String AAP = "/mobicents/media/aap/$";
    private final static String IVR = "/mobicents/media/IVR/$";
    
    private Connection rxConnection;
    private Connection txConnection;
    private URL url = null;
    private Semaphore semaphore;
    
    private RequestIdentifier playbackID;
    private RequestIdentifier recordingID;
    
    private JainMgcpResponseEvent response;
    private boolean oc = false;
    
    public AnnTestCase(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        semaphore = new Semaphore(0);
        
        txConnection = createConnection(AAP, ConnectionMode.SendOnly, null);
        rxConnection = createConnection(IVR, ConnectionMode.RecvOnly, txConnection.getLocalSdp());

        modifyConnection(txConnection, rxConnection.getLocalSdp());
        
        playbackID = new RequestIdentifier("1");
        recordingID = new RequestIdentifier("2");
    }

    @Override
    public void tearDown() throws Exception {
        deleteConnectionConnection(rxConnection);
        deleteConnectionConnection(txConnection);
        
        Thread.sleep(2000);
        
        super.tearDown();
    }
    
    private void startPlayer(String url) throws Exception {
        EndpointIdentifier endpointID = new EndpointIdentifier(AAP, "127.0.0.1");
        NotificationRequest rqnt = new NotificationRequest(this, txConnection.getEndpoint(), playbackID);

        EventName[] signalRequests = {new EventName(PackageName.Announcement, MgcpEvent.ann.withParm(url), null)};
        rqnt.setSignalRequests(signalRequests);

        RequestedAction[] actions = new RequestedAction[]{RequestedAction.NotifyImmediately};

        RequestedEvent[] requestedEvents = {new RequestedEvent(new EventName(PackageName.Announcement,
            MgcpEvent.oc, null), actions)
        };
        
        rqnt.setRequestedEvents(requestedEvents);
        NotifiedEntity notifiedEntity = new NotifiedEntity(caIPAddress.getHostName(), caIPAddress.getHostAddress(), caStack.getPort());
        rqnt.setNotifiedEntity(notifiedEntity);

        rqnt.setTransactionHandle(caProvider.getUniqueTransactionHandler());

        response = null;
        caProvider.sendMgcpEvents(new JainMgcpCommandEvent[]{rqnt});                
        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        
        if (response == null) {
            throw new Exception("Time out");
        }
        
        if (response.getReturnCode().getValue() != ReturnCode.TRANSACTION_EXECUTED_NORMALLY) {
            throw new Exception("Could not start player:" + response.getReturnCode().getComment());
        }
    }

    private void startRecording(String url) throws Exception {
        EndpointIdentifier endpointID = new EndpointIdentifier(IVR, "127.0.0.1");
        NotificationRequest rqnt = new NotificationRequest(this, rxConnection.getEndpoint(), recordingID);

        EventName[] signalRequests = {new EventName(AUPackage.AU, AUMgcpEvent.aupr.withParm(url), null)};
        rqnt.setSignalRequests(signalRequests);

        NotifiedEntity notifiedEntity = new NotifiedEntity(caIPAddress.getHostName(), caIPAddress.getHostAddress(), caStack.getPort());
        rqnt.setNotifiedEntity(notifiedEntity);

        rqnt.setTransactionHandle(caProvider.getUniqueTransactionHandler());

        response = null;
        caProvider.sendMgcpEvents(new JainMgcpCommandEvent[]{rqnt});
        semaphore.tryAcquire(5, TimeUnit.SECONDS);
        
        if (response == null) {
            throw new Exception("Time out");
        }
        
        if (response.getReturnCode().getValue() != ReturnCode.TRANSACTION_EXECUTED_NORMALLY) {
            throw new Exception("Could not start player:" + response.getReturnCode().getComment());
        }
    }
    
    
    @Test
    public void testSimpleTransmission() throws Exception {        
        Thread.currentThread().sleep(1000);
        url = AnnTestCase.class.getClassLoader().getResource("org/mobicents/media/server/ctrl/mgcp/test/ann/8kulaw.wav");
        String path = url.getPath();
        path = path.substring(0, path.lastIndexOf("/"));
        
        startRecording(path + "/test-record.wav");
        Thread.currentThread().sleep(1000);
        startPlayer("file:" + url.getPath());

        semaphore.tryAcquire(15, TimeUnit.SECONDS);
        
        assertEquals(true, oc);
        File file = new File(path + "/test-record.wav");
        
        assertEquals(true, file.exists());
        file.delete();
        
        //netx run on event
        playbackID = new RequestIdentifier("10");
        recordingID = new RequestIdentifier("20");
        response = null;
        oc = false;
        
        System.out.println(">>>>> Staring recording 2");
        startRecording(path + "/test-record.wav");
        System.out.println(">>>>> SLIPPING");
        Thread.currentThread().sleep(1000);
        
        System.out.println(">>>>> Staring player 2");
        startPlayer("file:" + url.getPath());

        semaphore.tryAcquire(15, TimeUnit.SECONDS);
        
        assertEquals(true, oc);
        file = new File(path + "/test-record.wav");
        
        assertEquals(true, file.exists());
  
    }

    //@Override
    public void processMgcpCommandEvent(JainMgcpCommandEvent event) {
        if (event.getObjectIdentifier() == Constants.CMD_NOTIFY) {
            oc = true;
            
            NotifyResponse res = new NotifyResponse(this, ReturnCode.Transaction_Executed_Normally);
            res.setTransactionHandle(event.getTransactionHandle());
            caProvider.sendMgcpEvents(new JainMgcpEvent[]{res});
            
            System.out.println("RELEASING SEMAPHORE");
            semaphore.release();
        }
    }

    @Override
    public void processMgcpResponseEvent(JainMgcpResponseEvent event) {
        super.processMgcpResponseEvent(event);
        response = event;
//        semaphore.release();
    }
}
