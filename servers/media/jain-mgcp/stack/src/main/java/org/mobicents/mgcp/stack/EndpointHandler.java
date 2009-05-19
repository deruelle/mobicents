/**
 * Start time:14:39:50 2008-11-21<br>
 * Project: mobicents-media-server-controllers<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.mgcp.stack;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.ModifyConnection;
import jain.protocol.ip.mgcp.message.ModifyConnectionResponse;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.handlers.EndpointHandlerManager;
import org.mobicents.mgcp.stack.handlers.TransactionHandlerManagement;

/**
 * Start time:14:39:50 2008-11-21<br>
 * Project: mobicents-media-server-controllers<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class EndpointHandler {

	protected static final Logger logger = Logger.getLogger(EndpointHandler.class);

	protected TreeSet<ConnectionIdentifier> connectionIds = new TreeSet<ConnectionIdentifier>(
			new Comparator<ConnectionIdentifier>() {

				public int compare(ConnectionIdentifier o1, ConnectionIdentifier o2) {

					if (o1 == null)
						return -1;
					if (o2 == null)
						return 1;
					return o1.toString().compareTo(o2.toString());

				}
			});
	/**
	 * EndpointWide notification requests
	 */
	// protected Set<String> subscribedEvents = new HashSet<String>();
	// protected BlockingQueue<Runnable> ourQueue = new
	// LinkedBlockingQueue<Runnable>();
	// protected ThreadPoolQueueExecutor executor = new
	// ThreadPoolQueueExecutor(1,
	// 1, ourQueue);
	protected ThreadPoolQueueExecutor executor = null;
	protected List<TransactionHandlerManagement> ongoingTransactions = new ArrayList<TransactionHandlerManagement>();
	protected EndpointHandlerManager stack = null;
	protected String endpointId = null;
	protected String fakeId = null;
	protected boolean useFakeId = false;
	protected Set<RequestedEvent> requestedEvents = new TreeSet<RequestedEvent>(new Comparator<RequestedEvent>() {

		public int compare(RequestedEvent o1, RequestedEvent o2) {

			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return o1.toString().compareTo(o2.toString());

		}
	});

	public EndpointHandler(EndpointHandlerManager jainMgcpStackImpl, String endpointId) {
		this.endpointId = endpointId;
		this.stack = jainMgcpStackImpl;
		this.fakeId = new UID().toString();
		this.executor = this.stack.getNextExecutor();
	}

	public String getFakeId() {
		return fakeId;
	}

	/**
	 * Method called once tx has been created
	 * 
	 * @param handler
	 */
	public void addTransactionHandler(TransactionHandlerManagement handler) {
		this.ongoingTransactions.add(handler);
		handler.setEndpointHandler(this);
	}

	public void scheduleTransactionHandler(TransactionHandlerManagement th) {

		this.executor.execute(th);
	}

	/**
	 * Should be called when reqeust command is delivered - either sent or
	 * received.
	 * 
	 * @param commandEvent
	 * @param handler
	 */
	public void commandDelivered(JainMgcpCommandEvent commandEvent, TransactionHandlerManagement handler) {

		// FIXME: Is there any real check we have to do here?
		// doEndChecks();
	}

	/**
	 * Should be called when response command is delivered - either sent or
	 * received.<br>
	 * This method should take care of initializing another endpoint handler for
	 * cases like CRCX with wildcard and actual name of endpoint in response
	 * 
	 * @param commandEvent
	 * @param event
	 * @param handler
	 */
	public void commandDelivered(JainMgcpCommandEvent commandEvent, JainMgcpResponseEvent event,
			TransactionHandlerManagement handler) {

		if (commandEvent instanceof CreateConnection) {
			CreateConnection ccRequest = (CreateConnection) commandEvent;
			CreateConnectionResponse ccResponse = (CreateConnectionResponse) event;

			int responseCode = ccResponse.getReturnCode().getValue();
			MgcpResponseType type = MgcpResponseType.getResponseTypeFromCode(responseCode);

			// This is local/we have to handle

			switch (type) {

			case SuccessResponse:

				EndpointIdentifier specificEndpointId = ccResponse.getSpecificEndpointIdentifier();
				if (specificEndpointId != null && this.endpointId.compareTo(specificEndpointId.toString()) == 0) {

					// On Success we have to add ConnectionId, possibly process
					// NotificationRequest like, since it can have
					// NotificationRequest embeded :]
					this.connectionIds.add(ccResponse.getConnectionIdentifier());

					if (ccRequest.getNotificationRequestParms() != null) {
						processRequestedEvents(ccRequest.getNotifiedEntity(), ccRequest.getNotificationRequestParms()
								.getRequestedEvents());
					}
				} else if (isAnyOfWildcard(endpointId)) {
					// This means that client asked to get connection on any of
					// endpoints, we have to change mapping
					// this.ongoingTransactions.remove(handler);
					// EndpointHandler validEndpointHandler = this.stack
					// .getEndpointHandler(specificEndpointId.toString());
					this.endpointId = specificEndpointId.toString();
					EndpointHandler concurrent = this.stack.switchMapping(this.fakeId, specificEndpointId.toString());
					if (concurrent != null) {
						// This could mean that in a mean while someone created
						// this, we should use it.
						this.ongoingTransactions.remove(handler);
						concurrent.addTransactionHandler(handler);
						concurrent.commandDelivered(commandEvent, event, handler);
					} else {
						this.commandDelivered(commandEvent, event, handler);
					}
				} else {
					// ? This is error, this means that endpoitID does not match
					// and
					// possibly is AllOfWildcad - *, which is not permited
					logger.error("Wrong endpoitn id, local: " + endpointId + ", fakeId: " + fakeId
							+ ", id in response: " + specificEndpointId);
				}

				break;
			case TransientError:
			case PermanentError:
				// Remove handler from ongoing transaction and cleaning will
				// happen TransactionHandler.release(true) is called

			default:
				break;
			}

		} else if (commandEvent instanceof NotificationRequest) {
			// FIXME: there can be wildcard notficatoion request = allof ??
			// what should we do than?
			NotificationRequest nRequest = (NotificationRequest) commandEvent;
			NotificationRequestResponse nrResponse = (NotificationRequestResponse) event;
			int responseCode = nrResponse.getReturnCode().getValue();
			MgcpResponseType type = MgcpResponseType.getResponseTypeFromCode(responseCode);

			switch (type) {
			case ProvisionalResponse:
				return;
			case SuccessResponse:
				if (this.endpointId.equals(nRequest.getEndpointIdentifier().toString())) {
					// On success we have to add subscription info
					processRequestedEvents(nRequest.getNotifiedEntity(), nRequest.getRequestedEvents());
				} else if (isWildCardEndpointName(nRequest.getEndpointIdentifier().toString())) {
					// FIXME: do nothing? Or should we create wildcard named EH?
					// Its
					// not mentioned - but this should be also
				}
				break;
			case TransientError:
			case PermanentError:

			default:
				break;
			}

		} else if (commandEvent instanceof ModifyConnection) {
			// FIXME: there can be wildcard notficatoion request = allof ??
			// what should we do than?
			ModifyConnection mcRequest = (ModifyConnection) commandEvent;
			ModifyConnectionResponse mcResponse = (ModifyConnectionResponse) event;
			int responseCode = mcResponse.getReturnCode().getValue();
			MgcpResponseType type = MgcpResponseType.getResponseTypeFromCode(responseCode);

			switch (type) {
			case ProvisionalResponse:
				return;
			case SuccessResponse:
				if (this.endpointId.equals(mcRequest.getEndpointIdentifier().toString())) {
					// On success we have to check for embeded NR
					// processRequestedEvents(mcRequest.getNotifiedEntity(),
					// mcRequest.getRequestedEvents());
					if (mcRequest.getNotificationRequestParms() != null) {
						processRequestedEvents(mcRequest.getNotifiedEntity(), mcRequest.getNotificationRequestParms()
								.getRequestedEvents());
					}
				} else {
					logger.error("Wrong EndpoiontId on " + event.getClass().getSimpleName()
							+ " event. This should be set to valid EId, this EId: " + this.endpointId);
				}
				break;
			case TransientError:
			case PermanentError:

			default:
				break;
			}

		} else if (commandEvent instanceof DeleteConnection) {
			// FIXME: there can be wildcard notficatoion request = allof ??
			// what should we do than?
			DeleteConnection dcRequest = (DeleteConnection) commandEvent;
			DeleteConnectionResponse dcResponse = (DeleteConnectionResponse) event;
			int responseCode = dcResponse.getReturnCode().getValue();
			MgcpResponseType type = MgcpResponseType.getResponseTypeFromCode(responseCode);

			switch (type) {
			case ProvisionalResponse:
				return;
			case SuccessResponse:
				if (this.endpointId.equals(dcRequest.getEndpointIdentifier().toString())) {
					// On success we have to:
					// * check for embeded NR
					// * delete connection
					// * delete all connections
					// processRequestedEvents(mcRequest.getNotifiedEntity(),
					// mcRequest.getRequestedEvents());
					// http://tools.ietf.org/html/rfc3435#section-2.3.7
					if (dcRequest.getNotificationRequestParms() != null) {
						processRequestedEvents(null, dcRequest.getNotificationRequestParms().getRequestedEvents());
					}

					// http://tools.ietf.org/html/rfc3435#section-2.3.7 or
					// http://tools.ietf.org/html/rfc3435#section-2.3.8
					if (dcRequest.getConnectionIdentifier() != null) {
						// deletes specific connection
						this.connectionIds.remove(dcRequest.getConnectionIdentifier());
						if (logger.isDebugEnabled()) {
							logger.debug("Removing connection:" + dcRequest.getConnectionIdentifier() + " From:"
									+ Arrays.toString(this.connectionIds.toArray()) + " ------ " + this);
						}

					} else {
						// 2.3.9
						this.connectionIds.clear();
					}
				} else {
					logger.error("Wrong EndpoiontId on " + event.getClass().getSimpleName()
							+ " event. This should be set to valid EId, this EId: " + this.endpointId);
				}
				break;
			case TransientError:
			case PermanentError:

			default:
				break;
			}

		}

		doEndChecks();
	}

	protected void processRequestedEvents(NotifiedEntity entity, RequestedEvent[] rEvents) {
		// FIXME: there is only single list of those? New one overwrites
		// previous?

		// RequestedEvents is not incremental list, it acts as setter?
		this.requestedEvents.clear();
		if (rEvents != null) {
			for (RequestedEvent re : rEvents) {
				this.requestedEvents.add(re);
			}

		}

	}

	/**
	 * Called when localy initiated transaction times out
	 * 
	 * @param commandEvent
	 * @param transactionHandler
	 */
	public void processTxTimeout(JainMgcpCommandEvent commandEvent, TransactionHandlerManagement transactionHandler) {

		transactionHandler.clearEndpointHandler();
		doEndChecks();
	}

	/**
	 * Called when remotely created tx times out
	 * 
	 * @param commandEvent
	 * @param transactionHandler
	 */
	public void processRxTimeout(JainMgcpCommandEvent commandEvent, TransactionHandlerManagement transactionHandler) {
		// TODO Auto-generated method stub
		transactionHandler.clearEndpointHandler();
		doEndChecks();

	}

	public void transactionHandlerDeleted(TransactionHandlerManagement th) {
		this.ongoingTransactions.remove(th);
		th.setEndpointHandler(null);
		doEndChecks();
	}

	/**
	 * Should be called in case something changes - connection list change,
	 * subscription list change, tx termination/completition
	 */
	public void doEndChecks() {
		if (this.connectionIds.size() == 0 && this.ongoingTransactions.size() == 0
		// && this.requestedEvents.size() == 0
		// FIXME: This can cause a leak if someone does not unregister
		// Oleg, Amit?
		) {
			try {
				if (isWildCardEndpointName(this.endpointId)) {
					this.stack.removeEndpointHandler(this.fakeId);
				} else {
					this.stack.removeEndpointHandler(this.endpointId);
				}
			} finally {
				// We have a pool now, we dont kill them :}
				// this.executor.shutdownNow();
			}
		}
	}

	// For now simple detection, no range wildcard detection
	public static boolean isWildCardEndpointName(String endpointId) {

		return isAnyOfWildcard(endpointId) || isAllOfWildcard(endpointId);
	}

	public static boolean isAnyOfWildcard(String endpointId) {
		boolean flag = endpointId.contains("$");; 
		return flag;
	}

	public static boolean isAllOfWildcard(String endpointId) {
		return endpointId.contains("*");
	}

	public String toString() {

		return this.getClass().getSimpleName() + this.hashCode() + " - EId: " + this.endpointId
				+ ", Subscribed events: " + Arrays.toString(this.requestedEvents.toArray()) + ", connectionIds: "
				+ Arrays.toString(this.connectionIds.toArray()) + ", Handlers: "
				+ Arrays.toString(this.ongoingTransactions.toArray());

	}

	public void setUseFake(boolean b) {
		this.useFakeId = b;

	}

}
