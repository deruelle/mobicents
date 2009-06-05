package org.mobicents.media.server.ctrl.mgcp.test.dtmf;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mobicents.media.server.ctrl.mgcp.test.Connection;
import org.mobicents.media.server.ctrl.mgcp.test.MgcpMicrocontainerTest;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;

/**
 * 
 * @author amit bhayani
 * 
 */
public class DtmfDetectionTestCase extends MgcpMicrocontainerTest implements JainMgcpExtendedListener {

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

	private EventName[] events;

	public DtmfDetectionTestCase(String name) {
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
		
		events = null;
		Thread.currentThread().sleep(2000);
	}

	private void requestDtmfDet(MgcpEvent digit) {
		NotificationRequest notificationRequest = new NotificationRequest(this, rxConnection.getEndpoint(), recordingID);

		RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };

		RequestedEvent[] requestedEvents = { new RequestedEvent(new EventName(PackageName.Dtmf, digit, rxConnection
				.getId()), actions) };
		notificationRequest.setRequestedEvents(requestedEvents);

		NotifiedEntity notifiedEntity = new NotifiedEntity(caIPAddress.getHostName(), caIPAddress.getHostAddress(),
				caStack.getPort());
		notificationRequest.setNotifiedEntity(notifiedEntity);

		notificationRequest.setTransactionHandle(caProvider.getUniqueTransactionHandler());

		super.caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { notificationRequest });
	}

	private void requestDtmfGen(MgcpEvent digit) {
		NotificationRequest notificationRequest = new NotificationRequest(this, txConnection.getEndpoint(), playbackID);

		EventName[] signalRequests = { new EventName(PackageName.Dtmf, digit, txConnection.getId()) };

		notificationRequest.setSignalRequests(signalRequests);
		NotifiedEntity notifiedEntity = new NotifiedEntity(caIPAddress.getHostName(), caIPAddress.getHostAddress(),
				caStack.getPort());
		notificationRequest.setNotifiedEntity(notifiedEntity);
		notificationRequest.setTransactionHandle(caProvider.getUniqueTransactionHandler());

		caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { notificationRequest });
	}

	@Test
	public void testDtmf0() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf0);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf0);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf0));
		Thread.currentThread().sleep(2000);
	}

	@Test
	public void testDtmf1() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf1);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf1);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf1));
		Thread.currentThread().sleep(2000);
	}

	@Test
	public void testDtmf2() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf2);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf2);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf2));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmf3() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf3);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf3);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf3));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmf4() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf4);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf4);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf4));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmf5() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf5);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf5);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf5));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmf6() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf6);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf6);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf6));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmf7() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf7);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf7);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf7));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmf8() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf8);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf8);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf8));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmf9() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmf9);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmf9);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmf9));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmfA() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmfA);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmfA);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmfA));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmfB() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmfB);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmfB);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmfB));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmfC() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmfC);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmfC);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmfC));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmfD() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmfD);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmfD);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmfD));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmfStar() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmfStar);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmfStar);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmfStar));
		Thread.currentThread().sleep(2000);
	}
	
	@Test
	public void testDtmfHash() throws Exception {
		this.requestDtmfDet(MgcpEvent.dtmfHash);

		Thread.currentThread().sleep(1000);

		this.requestDtmfGen(MgcpEvent.dtmfHash);

		oc = false;
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertEquals(true, checkEvent(MgcpEvent.dtmfHash));
		Thread.currentThread().sleep(2000);
	}

	@Override
	public void tearDown() throws Exception {
		deleteConnectionConnection(rxConnection);
		deleteConnectionConnection(txConnection);

		// Let us wait for DLCX Response to arrive
		Thread.currentThread().sleep(2000);

		super.tearDown();
	}

	private boolean checkEvent(MgcpEvent exp) {
		if (events == null) {
			return false;
		}

		if (events.length != 1) {
			return false;
		}

		return events[0].getEventIdentifier().getName().equals(exp.getName());
	}

	public void processMgcpCommandEvent(JainMgcpCommandEvent event) {
		System.out.println("******** CMD ***********");
		int msg = event.getObjectIdentifier();

		if (msg != Constants.CMD_NOTIFY) {
			return;
		}

		Notify ntfy = (Notify) event;
		events = ntfy.getObservedEvents();

		if (!oc) {
			oc = true;
			semaphore.release();
		}
	}

	@Override
	public void processMgcpResponseEvent(JainMgcpResponseEvent event) {
		super.processMgcpResponseEvent(event);
		response = event;
	}
}
