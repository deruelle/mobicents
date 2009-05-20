/**
 * Start time:16:00:15 2008-11-24<br>
 * Project: mobicents-media-server-controllers<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.mgcp.stack.test.endpointhandler;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

import org.mobicents.mgcp.stack.EndpointHandler;
import org.mobicents.mgcp.stack.EndpointHandlerFactory;
import org.mobicents.mgcp.stack.ThreadPoolQueueExecutor;
import org.mobicents.mgcp.stack.handlers.EndpointHandlerManager;
import org.mobicents.mgcp.stack.handlers.TransactionHandlerManagement;
import org.mobicents.mgcp.stack.test.MessageFlowHarness;

/**
 * Start time:16:00:15 2008-11-24<br>
 * Project: mobicents-media-server-controllers<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class LifeCycleStateTest extends MessageFlowHarness {

	// protected EndpointHandler testSubject=null;
	protected LocalEndpointHandlerManager proxyManager = null;
	// Is this correct?
	protected EndpointIdentifier wildcarded = new EndpointIdentifier("/media/truink/$", "127.0.0.1:2729");
	protected EndpointIdentifier specific = new EndpointIdentifier("/media/truink/enp-1", "127.0.0.1:2729");

	/**
	 * 
	 */
	public LifeCycleStateTest() {
		super("LifeCycleStateTest");
	}

	/**
	 * 
	 * @param name
	 */
	public LifeCycleStateTest(String name) {
		super(name);

	}

	@Override
	public void setUp() throws Exception {

		super.setUp();
		this.proxyManager = new LocalEndpointHandlerManager();
		// this.testSubject=proxyManager.getEndpointHandler(wildcarded);

	}

	@Override
	public void tearDown() throws Exception {

		super.tearDown();
	}

	protected class LocalEndpointHandlerManager implements EndpointHandlerManager {

		private EndpointHandlerFactory ehFactory = new EndpointHandlerFactory(5, this);
		protected int creationConunt = 0;
		protected TreeMap<String, EndpointHandler> hanlers = new TreeMap<String, EndpointHandler>(
				new Comparator<String>() {

					public int compare(String o1, String o2) {
						if (o1 == null)
							return -1;
						if (o2 == null)
							return 1;
						return o1.compareTo(o2);
					}
				});

		public void removeEndpointHandler(String endpointId) {

			if (this.hanlers.containsKey(endpointId)) {
				this.hanlers.remove(endpointId);
			}

		}

		public int getCreationConunt() {
			return creationConunt;
		}

		public String[] getPresentHandlers() {
			return this.hanlers.keySet().toArray(new String[hanlers.size()]);
		}

		public EndpointHandler getEndpointHandler(String endpointId, boolean useFakeOnWildcard) {
			try {
				EndpointHandler eh = null;
				if (useFakeOnWildcard) {
					// System.err.println("==== 1= ");
					eh = ehFactory.allocate(endpointId);
					eh.setUseFake(true);
					hanlers.put(eh.getFakeId(), eh);
					creationConunt++;
				} else if (this.hanlers.get(endpointId) != null) {
					// System.err.println("==== 2= ");
					return this.hanlers.get(endpointId);
				} else {
					eh = ehFactory.allocate(endpointId);
					this.hanlers.put(endpointId, eh);
					creationConunt++;
					// System.err.println("==== 3= ");

				}
				return eh;
			} finally {

				// System.err.println(hanlers.keySet());
			}
		}

		public ThreadPoolQueueExecutor getNextExecutor() {
			return new ThreadPoolQueueExecutor(1, 1, new ArrayBlockingQueue<Runnable>(5));
		}

		public synchronized EndpointHandler switchMapping(String fakeId, String specificEndpointId) {
			EndpointHandler eh = this.hanlers.get(specificEndpointId);

			if (eh == null) {
				// Well this means we are first, noone has return this before us
				// so we do the switch
				eh = this.hanlers.remove(fakeId);
				this.hanlers.put(specificEndpointId, eh);
				eh.setUseFake(false);
				eh = null;
			}
			return eh;
		}
	}

	protected class LocalTransactionHandler implements TransactionHandlerManagement, Runnable {

		protected EndpointHandler eh = null;
		protected List<PerformAction> actions = new ArrayList<PerformAction>();

		public void clearEndpointHandler() {
			this.eh.transactionHandlerDeleted(this);
			this.eh = null;

		}

		public void setEndpointHandler(EndpointHandler endpointHandler) {
			this.eh = endpointHandler;

		}

		public void run() {
			PerformAction ap = actions.remove(0);
			ap.perform();
		}

		/**
		 * This simulates actions - receive req/resp, send req/resp
		 * 
		 * @param action
		 */
		public void addAction(PerformAction action) {
			this.actions.add(action);
		}

	}

	protected abstract class PerformAction {
		protected LocalTransactionHandler lth = null;

		public abstract void perform();
	}

	protected class ReceiveCommand extends PerformAction {

		public ReceiveCommand(JainMgcpCommandEvent commandEvent, LocalTransactionHandler lth) {
			super();
			this.commandEvent = commandEvent;
			super.lth = lth;
		}

		protected JainMgcpCommandEvent commandEvent = null;

		@Override
		public void perform() {
			this.lth.eh.commandDelivered(commandEvent, lth);

		}

	}

	protected class ReceiveResponse extends PerformAction {

		public ReceiveResponse(JainMgcpCommandEvent commandEvent, JainMgcpResponseEvent responseEvent,
				LocalTransactionHandler lth) {
			super();
			this.commandEvent = commandEvent;
			this.responseEvent = responseEvent;
			super.lth = lth;
		}

		protected JainMgcpCommandEvent commandEvent = null;
		protected JainMgcpResponseEvent responseEvent = null;

		@Override
		public void perform() {

			this.lth.eh.commandDelivered(commandEvent, responseEvent, lth);

		}

	}

	public void testCRCXWithSpecificId() throws Exception {
		// We simulate send CRCX and simulate receival of response, both with
		// specific Id
		try {
			// 1. create EH, we sent CCR with fixed name
			EndpointHandler testSubject = this.proxyManager.getEndpointHandler(this.specific.toString(), false);
			LocalTransactionHandler lth = new LocalTransactionHandler();
			testSubject.addTransactionHandler(lth);

			CallIdentifier ci = new CallIdentifier("214");

			CreateConnection cc = new CreateConnection(this, ci, this.specific, ConnectionMode.SendRecv);
			ReceiveCommand rc = new ReceiveCommand(cc, lth);
			lth.addAction(rc);

			testSubject.scheduleTransactionHandler(lth);
			// lth.run();
			sleep(1000);

			// 2. we send response
			testSubject = this.proxyManager.getEndpointHandler(this.specific.toString(), false);
			ConnectionIdentifier cid = new ConnectionIdentifier("123");
			CreateConnectionResponse response = new CreateConnectionResponse(cc.getSource(),
					ReturnCode.Transaction_Executed_Normally, cid);

			// response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			response.setSpecificEndpointIdentifier(specific);
			ReceiveResponse rr = new ReceiveResponse(cc, response, lth);
			lth.addAction(rr);
			testSubject.scheduleTransactionHandler(lth);
			// lth.run();

			sleep(1000);
			// 3. destroy lth
			lth.clearEndpointHandler();
			sleep(1000);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (this.proxyManager.getCreationConunt() != 1) {
				this.fail("Creation count to high: " + this.proxyManager.getCreationConunt());
				return;
			}

			if (this.proxyManager.getPresentHandlers().length != 1) {
				System.err.println("To0 many handlers: " + Arrays.toString(this.proxyManager.getPresentHandlers()));
				this.fail("To0 many handlers: " + Arrays.toString(this.proxyManager.getPresentHandlers()));
				return;
			}
		}
	}

	public void testCRCX_DLCXWithSpecificIds() throws Exception {
		// We simulate send CRCX and simulate receival of response, both with
		// specific Id
		try {
			// 1. create EH, we sent CCR with fixed name
			// System.out.println("1. ");
			EndpointHandler testSubject = this.proxyManager.getEndpointHandler(this.specific.toString(), false);
			LocalTransactionHandler lth = new LocalTransactionHandler();
			testSubject.addTransactionHandler(lth);

			CallIdentifier ci = new CallIdentifier("214");

			CreateConnection cc = new CreateConnection(this, ci, this.specific, ConnectionMode.SendRecv);
			ReceiveCommand rc = new ReceiveCommand(cc, lth);
			lth.addAction(rc);
			testSubject.scheduleTransactionHandler(lth);
			// lth.run();

			sleep(500);

			// 2. we send response
			// System.out.println("2. ");
			testSubject = this.proxyManager.getEndpointHandler(this.specific.toString(), false);
			ConnectionIdentifier cid = new ConnectionIdentifier("123");
			CreateConnectionResponse response = new CreateConnectionResponse(cc.getSource(),
					ReturnCode.Transaction_Executed_Normally, cid);

			// response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			response.setSpecificEndpointIdentifier(specific);
			ReceiveResponse rr = new ReceiveResponse(cc, response, lth);
			lth.addAction(rr);
			testSubject.scheduleTransactionHandler(lth);
			// lth.run();

			sleep(500);

			// 3. destroy lth
			// System.out.println("3. ");
			lth.clearEndpointHandler();
			sleep(500);

			// 4. send delete
			// System.out.println("4. ");
			testSubject = this.proxyManager.getEndpointHandler(this.specific.toString(), false);
			lth = new LocalTransactionHandler();
			testSubject.addTransactionHandler(lth);
			DeleteConnection deleteConnection = new DeleteConnection(this, this.specific);
			deleteConnection.setConnectionIdentifier(response.getConnectionIdentifier());

			rc = new ReceiveCommand(deleteConnection, lth);
			lth.addAction(rc);
			testSubject.scheduleTransactionHandler(lth);
			// lth.run();

			sleep(500);

			// 5. receive response
			// System.out.println("5. ");
			DeleteConnectionResponse dlcxResponse = new DeleteConnectionResponse(response.getSource(),
					ReturnCode.Transaction_Executed_Normally);
			rr = new ReceiveResponse(deleteConnection, dlcxResponse, lth);
			lth.addAction(rr);
			testSubject.scheduleTransactionHandler(lth);
			// lth.run();
			sleep(500);
			// 6. destroy lth
			// System.out.println("6. ");
			lth.clearEndpointHandler();

		} finally {
			if (this.proxyManager.getCreationConunt() != 1) {
				System.err.println("Creation count to high: " + this.proxyManager.getCreationConunt());
				this.fail("Creation count to high: " + this.proxyManager.getCreationConunt());
				return;
			}

			if (this.proxyManager.getPresentHandlers().length != 0) {
				System.err.println("To many handlers: " + Arrays.toString(this.proxyManager.getPresentHandlers()));
				this.fail("To many handlers: " + Arrays.toString(this.proxyManager.getPresentHandlers()));
				return;
			}
		}
	}

	public void testCRCXWithWildcardId() throws Exception {
		// We simulate send CRCX and simulate receival of response, both with
		// specific Id
		try {
			// 1. create EH, we sent CCR with fixed name
			EndpointHandler testSubject = this.proxyManager.getEndpointHandler(this.wildcarded.toString(), true);
			LocalTransactionHandler lth = new LocalTransactionHandler();
			testSubject.addTransactionHandler(lth);

			CallIdentifier ci = new CallIdentifier("214");

			CreateConnection cc = new CreateConnection(this, ci, this.wildcarded, ConnectionMode.SendRecv);
			ReceiveCommand rc = new ReceiveCommand(cc, lth);
			lth.addAction(rc);

			// testSubject.scheduleTransactionHandler(lth);
			lth.run();
			sleep(500);

			// 2. we send response, use wildcarded handler, send specific id in
			// response
			// testSubject =
			// this.proxyManager.getEndpointHandler(this.wildcarded
			// .toString());
			ConnectionIdentifier cid = new ConnectionIdentifier("123");
			CreateConnectionResponse response = new CreateConnectionResponse(cc.getSource(),
					ReturnCode.Transaction_Executed_Normally, cid);

			// response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			response.setSpecificEndpointIdentifier(specific);
			ReceiveResponse rr = new ReceiveResponse(cc, response, lth);
			lth.addAction(rr);
			// testSubject.scheduleTransactionHandler(lth);
			lth.run();

			sleep(500);
			// 3. destroy lth
			lth.clearEndpointHandler();
			sleep(500);

		} finally {
			if (this.proxyManager.getCreationConunt() != 1) {
				this.fail("Creation count to high: " + this.proxyManager.getCreationConunt());
				return;
			}

			if (this.proxyManager.getPresentHandlers().length != 1) {
				System.err.println("To0 many handlers: " + Arrays.toString(this.proxyManager.getPresentHandlers()));
				this.fail("To0 many handlers: " + Arrays.toString(this.proxyManager.getPresentHandlers()));
				return;
			}
		}
	}

	public void testCRCX_DLCXWithWildcardId() throws Exception {
		// We simulate send CRCX and simulate receival of response, both with
		// specific Id
		try {
			// 1. create EH, we sent CCR with fixed name
			EndpointHandler testSubject = this.proxyManager.getEndpointHandler(this.wildcarded.toString(), true);
			LocalTransactionHandler lth = new LocalTransactionHandler();
			testSubject.addTransactionHandler(lth);

			CallIdentifier ci = new CallIdentifier("214");

			CreateConnection cc = new CreateConnection(this, ci, this.wildcarded, ConnectionMode.SendRecv);
			ReceiveCommand rc = new ReceiveCommand(cc, lth);
			lth.addAction(rc);

			// testSubject.scheduleTransactionHandler(lth);
			lth.run();
			sleep(500);

			// 2. we send response, use wildcarded handler, send specific id in
			// response
			// testSubject =
			// this.proxyManager.getEndpointHandler(this.wildcarded
			// .toString());
			ConnectionIdentifier cid = new ConnectionIdentifier("123");
			CreateConnectionResponse response = new CreateConnectionResponse(cc.getSource(),
					ReturnCode.Transaction_Executed_Normally, cid);

			// response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			response.setSpecificEndpointIdentifier(specific);
			ReceiveResponse rr = new ReceiveResponse(cc, response, lth);
			lth.addAction(rr);
			// testSubject.scheduleTransactionHandler(lth);
			lth.run();

			sleep(500);
			// 3. destroy lth
			lth.clearEndpointHandler();
			sleep(500);
			// 4. send delete
			// System.out.println("4. ");
			testSubject = this.proxyManager.getEndpointHandler(this.specific.toString(), false);
			lth = new LocalTransactionHandler();
			testSubject.addTransactionHandler(lth);
			DeleteConnection deleteConnection = new DeleteConnection(this, this.specific);
			deleteConnection.setConnectionIdentifier(response.getConnectionIdentifier());

			rc = new ReceiveCommand(deleteConnection, lth);
			lth.addAction(rc);
			testSubject.scheduleTransactionHandler(lth);
			// lth.run();

			sleep(500);

			// 5. receive response
			// System.out.println("5. ");
			DeleteConnectionResponse dlcxResponse = new DeleteConnectionResponse(response.getSource(),
					ReturnCode.Transaction_Executed_Normally);
			rr = new ReceiveResponse(deleteConnection, dlcxResponse, lth);
			lth.addAction(rr);
			testSubject.scheduleTransactionHandler(lth);
			// lth.run();
			sleep(500);
			// 6. destroy lth
			// System.out.println("6. ");
			lth.clearEndpointHandler();

		} finally {
			if (this.proxyManager.getCreationConunt() != 1) {
				this.fail("Creation count to high: " + this.proxyManager.getCreationConunt());
				return;
			}

			if (this.proxyManager.getPresentHandlers().length != 0) {
				System.err.println("To many handlers: " + Arrays.toString(this.proxyManager.getPresentHandlers()));
				this.fail("To many handlers: " + Arrays.toString(this.proxyManager.getPresentHandlers()));
				return;
			}
		}
	}

	/*
	 * public void _testCRCX_DLCX_PLUS_NT_REQUESTWithWildcardId() throws
	 * Exception { // We simulate send CRCX and simulate receival of response,
	 * both with // specific Id try { // 1. create EH, we sent CCR with fixed
	 * name EndpointHandler testSubject = this.proxyManager
	 * .getEndpointHandler(this.wildcarded.toString()); LocalTransactionHandler
	 * lth = new LocalTransactionHandler();
	 * testSubject.addTransactionHandler(lth);
	 * 
	 * CallIdentifier ci = new CallIdentifier("214");
	 * 
	 * CreateConnection cc = new CreateConnection(this, ci, this.wildcarded,
	 * ConnectionMode.SendRecv); ReceiveCommand rc = new ReceiveCommand(cc,
	 * lth); lth.addAction(rc);
	 *  // testSubject.scheduleTransactionHandler(lth); lth.run(); sleep(500);
	 *  // 2. we send response, use wildcarded handler, send specific id in //
	 * response testSubject =
	 * this.proxyManager.getEndpointHandler(this.wildcarded .toString());
	 * ConnectionIdentifier cid = new ConnectionIdentifier("123");
	 * CreateConnectionResponse response = new CreateConnectionResponse(cc
	 * .getSource(), ReturnCode.Transaction_Executed_Normally, cid);
	 *  //
	 * response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
	 * response.setSpecificEndpointIdentifier(specific); ReceiveResponse rr =
	 * new ReceiveResponse(cc, response, lth); lth.addAction(rr); //
	 * testSubject.scheduleTransactionHandler(lth); lth.run();
	 * 
	 * sleep(500); // 3. destroy lth lth.clearEndpointHandler(); sleep(500); //
	 * 4. send delete // System.out.println("4. "); testSubject =
	 * this.proxyManager.getEndpointHandler(this.specific .toString()); lth =
	 * new LocalTransactionHandler(); testSubject.addTransactionHandler(lth);
	 * DeleteConnection deleteConnection = new DeleteConnection(this,
	 * this.specific); deleteConnection.setConnectionIdentifier(response
	 * .getConnectionIdentifier());
	 * 
	 * RequestedAction[] actions = new RequestedAction[] {
	 * RequestedAction.NotifyImmediately };
	 * 
	 * RequestedEvent[] requestedEvents = { new RequestedEvent(new
	 * EventName(PackageName.Dtmf, MgcpEvent.dtmf0,
	 * response.getConnectionIdentifier()), actions), new RequestedEvent(new
	 * EventName( PackageName.Announcement, MgcpEvent.of,
	 * response.getConnectionIdentifier()), actions) };
	 * 
	 * NotificationRequestParms parms=new NotificationRequestParms(new
	 * RequestIdentifier("3")); parms.setRequestedEvents(requestedEvents);
	 * deleteConnection.setNotificationRequestParms(parms);
	 * 
	 * rc = new ReceiveCommand(deleteConnection, lth); lth.addAction(rc);
	 * testSubject.scheduleTransactionHandler(lth); // lth.run();
	 * 
	 * sleep(500);
	 *  // 5. receive response // System.out.println("5. ");
	 * DeleteConnectionResponse dlcxResponse = new DeleteConnectionResponse(
	 * response.getSource(), ReturnCode.Transaction_Executed_Normally); rr = new
	 * ReceiveResponse(deleteConnection, dlcxResponse, lth); lth.addAction(rr);
	 * testSubject.scheduleTransactionHandler(lth); // lth.run(); sleep(500); //
	 * 6. destroy lth // System.out.println("6. "); lth.clearEndpointHandler();
	 *  } finally { if (this.proxyManager.getCreationConunt() != 2) {
	 * this.fail("Creation count to high: " +
	 * this.proxyManager.getCreationConunt()); return; }
	 * 
	 * if (this.proxyManager.getPresentHandlers().length != 1) {
	 * System.err.println("To many handlers: " +
	 * Arrays.toString(this.proxyManager .getPresentHandlers())); this.fail("To
	 * many handlers: " + Arrays.toString(this.proxyManager
	 * .getPresentHandlers())); return; } } }
	 * 
	 * public void _testCRCX_PLUS_NT_REQUEST_DLCXWithWildcardId() throws
	 * Exception { // We simulate send CRCX and simulate receival of response,
	 * both with // specific Id try { // 1. create EH, we sent CCR with fixed
	 * name EndpointHandler testSubject = this.proxyManager
	 * .getEndpointHandler(this.wildcarded.toString()); LocalTransactionHandler
	 * lth = new LocalTransactionHandler();
	 * testSubject.addTransactionHandler(lth);
	 * 
	 * CallIdentifier ci = new CallIdentifier("214");
	 * 
	 * CreateConnection cc = new CreateConnection(this, ci, this.wildcarded,
	 * ConnectionMode.SendRecv); RequestedAction[] actions = new
	 * RequestedAction[] { RequestedAction.NotifyImmediately };
	 * 
	 * RequestedEvent[] requestedEvents = { new RequestedEvent(new
	 * EventName(PackageName.Dtmf, MgcpEvent.dtmf0, null), actions), new
	 * RequestedEvent(new EventName( PackageName.Announcement, MgcpEvent.of,
	 * null), actions) };
	 * 
	 * NotificationRequestParms parms=new NotificationRequestParms(new
	 * RequestIdentifier("3")); parms.setRequestedEvents(requestedEvents);
	 * cc.setNotificationRequestParms(parms); ReceiveCommand rc = new
	 * ReceiveCommand(cc, lth); lth.addAction(rc);
	 *  // testSubject.scheduleTransactionHandler(lth); lth.run(); sleep(500);
	 *  // 2. we send response, use wildcarded handler, send specific id in //
	 * response testSubject =
	 * this.proxyManager.getEndpointHandler(this.wildcarded .toString());
	 * ConnectionIdentifier cid = new ConnectionIdentifier("123");
	 * CreateConnectionResponse response = new CreateConnectionResponse(cc
	 * .getSource(), ReturnCode.Transaction_Executed_Normally, cid);
	 *  //
	 * response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
	 * response.setSpecificEndpointIdentifier(specific); ReceiveResponse rr =
	 * new ReceiveResponse(cc, response, lth); lth.addAction(rr); //
	 * testSubject.scheduleTransactionHandler(lth); lth.run();
	 * 
	 * sleep(500); // 3. destroy lth lth.clearEndpointHandler(); sleep(500); //
	 * 4. send delete // System.out.println("4. "); testSubject =
	 * this.proxyManager.getEndpointHandler(this.specific .toString()); lth =
	 * new LocalTransactionHandler(); testSubject.addTransactionHandler(lth);
	 * DeleteConnection deleteConnection = new DeleteConnection(this,
	 * this.specific); deleteConnection.setConnectionIdentifier(response
	 * .getConnectionIdentifier());
	 * 
	 * 
	 * 
	 * rc = new ReceiveCommand(deleteConnection, lth); lth.addAction(rc);
	 * testSubject.scheduleTransactionHandler(lth); // lth.run();
	 * 
	 * sleep(500);
	 *  // 5. receive response // System.out.println("5. ");
	 * DeleteConnectionResponse dlcxResponse = new DeleteConnectionResponse(
	 * response.getSource(), ReturnCode.Transaction_Executed_Normally); rr = new
	 * ReceiveResponse(deleteConnection, dlcxResponse, lth); lth.addAction(rr);
	 * testSubject.scheduleTransactionHandler(lth); // lth.run(); sleep(500); //
	 * 6. destroy lth // System.out.println("6. "); lth.clearEndpointHandler();
	 *  } finally { if (this.proxyManager.getCreationConunt() != 2) {
	 * this.fail("Creation count to high: " +
	 * this.proxyManager.getCreationConunt()); return; }
	 * 
	 * if (this.proxyManager.getPresentHandlers().length != 1) {
	 * System.err.println("To many handlers: " +
	 * Arrays.toString(this.proxyManager .getPresentHandlers())); this.fail("To
	 * many handlers: " + Arrays.toString(this.proxyManager
	 * .getPresentHandlers())); return; } } }
	 */

}
