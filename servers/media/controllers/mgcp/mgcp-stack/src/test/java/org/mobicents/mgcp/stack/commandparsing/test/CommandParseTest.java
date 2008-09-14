package org.mobicents.mgcp.stack.commandparsing.test;

import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;

import org.mobicents.mgcp.stack.JainMgcpStackImpl;
import org.mobicents.mgcp.stack.NotificationRequestHandler;
import org.mobicents.mgcp.stack.test.TestHarness;

public class CommandParseTest extends TestHarness {

	private JainMgcpStackImpl jainMgcpStack = null;
	InetAddress inetAddress;
	int port;

	public CommandParseTest() {
		super("CommandParseTest");
	}

	public void setUp() {

		try {
			inetAddress = InetAddress.getByName("127.0.0.1");
			jainMgcpStack = new JainMgcpStackImpl(inetAddress, 2729);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Could not setUp the CommandParseTest");
		}

	}

	public void tearDown() {
		jainMgcpStack.close();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testNotificationRequestDecode() {
		String command = "RQNT 1201 aaln/1@rgw-2567.whatever.net MGCP 1.0 \nN: ca@ca1.whatever.net:5678 \nX: 16AC \nR: l/hd(N) \nS: l/rg";
		System.out.println(command);

		NotificationRequest notificationCommand = null;
		NotificationRequestHandler rqntHandler = new NotificationRequestHandler(jainMgcpStack, inetAddress, port);
		try {
			notificationCommand = (NotificationRequest) rqntHandler.decodeCommand(command);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parsing of RQNT failed");
		}

		assertNotNull(" RQNT Command created", notificationCommand);
		assertEquals(new Integer(Constants.CMD_NOTIFICATION_REQUEST), new Integer(notificationCommand
				.getObjectIdentifier()));

		RequestIdentifier X = notificationCommand.getRequestIdentifier();
		assertNotNull(" RQNT Command RequestIdentifier", X);

		NotifiedEntity N = notificationCommand.getNotifiedEntity();
		assertNotNull(" RQNT NotifiedEntity", N);
		assertEquals(N.getLocalName(), "ca");
		assertEquals(N.getDomainName(), "ca1.whatever.net");
		assertEquals(N.getPortNumber(), 5678);

		RequestedEvent[] R = notificationCommand.getRequestedEvents();
		assertNotNull(" RQNT RequestedEvent[]", R);
		RequestedEvent requestedEvent = R[0];
		//requestedEvent.getEventName()

	}
}
