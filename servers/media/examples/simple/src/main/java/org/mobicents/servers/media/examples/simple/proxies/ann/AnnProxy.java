package org.mobicents.servers.media.examples.simple.proxies.ann;

import java.text.ParseException;
import java.util.logging.Level;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.NotifyResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EmbeddedRequest;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import javax.management.Notification;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.mobicents.servers.media.examples.simple.CallProxy;
import org.mobicents.servers.media.examples.simple.SimplExample;
import org.mobicents.servers.media.examples.simple.proxies.echo.EchoProxyState;

public class AnnProxy extends CallProxy {

	private AnnProxyState state = AnnProxyState.INITIAL;
	public static final String HELLO_WORLD = "";

	public AnnProxy(SimplExample mainDish) {
		super(mainDish);

		super.callIdentifier = new CallIdentifier("" + this.hashCode());
		super.endpointName = "media/test/trunk/Announcement/$";
	}

	public void processMgcpCommandEvent(JainMgcpCommandEvent mgcpCommand) {

		switch (this.state) {
		case SENT_INVITE_200:
			if(mgcpCommand instanceof Notify)
			{
				//here we could do something, instead we respond with 200 and remove
				Notify notify = (Notify) mgcpCommand;
				super.mainDish.removeCallProxy(notify.getRequestIdentifier());
				ReturnCode rc= ReturnCode.Transaction_Executed_Normally;
				NotifyResponse notifyResponse = new NotifyResponse(this,rc);
				notifyResponse.setTransactionHandle(notify.getTransactionHandle());
				super.mgcpProvider.sendMgcpEvents(new JainMgcpEvent[]{notifyResponse});
			}
			break;
		default:
			if (this.logger.isLoggable(Level.SEVERE)) {
				logger.severe("Received message on wrong state: " + this.state + ", message: " + mgcpCommand);
			}
		}

	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent mgcpResponse) {

		int code = mgcpResponse.getReturnCode().getValue();
		switch (this.state) {
		case SENT_CRCX:
			// here we wait for answer, we need to send 200 to invite
			if (mgcpResponse instanceof CreateConnectionResponse) {
				CreateConnectionResponse ccr = (CreateConnectionResponse) mgcpResponse;
				if (99 < code && code < 200) {
					// its provisional
				} else if (199 < code && code < 300) {
					// its success
					this.endpointIndentifier = ccr.getSpecificEndpointIdentifier();
					ConnectionDescriptor cd = ccr.getLocalConnectionDescriptor();
					try {
						Response inviteResponse = super.mainDish.getMessageFactory().createResponse(Response.OK, super.actingTransaction.getRequest());
						ContentTypeHeader ctp = super.mainDish.getHeaderFactory().createContentTypeHeader("application", "sdp");
						inviteResponse.setContent(cd.toString(), ctp);
						ToHeader tHeader = (ToHeader) inviteResponse.getHeader(ToHeader.NAME);
						tHeader.setTag(this.hashCode() + "");
						SipURI contactURI = super.mainDish.getAddressFactory().createSipURI("SimpleUser", super.mainDish.getStackAddress() + ":" + super.mainDish.getPort());
						ContactHeader ch = super.mainDish.getHeaderFactory().createContactHeader(super.mainDish.getAddressFactory().createAddress(contactURI));
						inviteResponse.addHeader(ch);

						super.actingTransaction.sendResponse(inviteResponse);
						super.mainDish.removeCallProxy(mgcpResponse);
						this.state = AnnProxyState.SENT_INVITE_200;
						super.actingTransaction = null;

						RequestIdentifier ri = new RequestIdentifier("" + (Math.random()));

						NotificationRequest notificationRequest = new NotificationRequest(this, super.endpointIndentifier, ri);
						EventName[] signalRequests = { new EventName(PackageName.Announcement, MgcpEvent.ann.withParm(HELLO_WORLD), ccr.getConnectionIdentifier()) };
						notificationRequest.setSignalRequests(signalRequests);

						RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };
						RequestedEvent[] requestedEvents = { new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.oc, ccr.getConnectionIdentifier()), actions),
								new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.of, ccr.getConnectionIdentifier()), actions) };

						notificationRequest.setRequestedEvents(requestedEvents);
						// notificationRequest.setTransactionHandle(mgcpProvider.getUniqueTransactionHandler());

						NotifiedEntity notifiedEntity = new NotifiedEntity(super.mainDish.getMgcpClientAddress().getHostAddress(), super.mainDish.getMgcpClientAddress().getHostAddress(),
								super.mainDish.getMgcpClientPort());
						notificationRequest.setNotifiedEntity(notifiedEntity);

						super.mainDish.addCallProxy(ri, this);
						super.mainDish.addCallProxy(notificationRequest, this);
						super.mgcpProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { notificationRequest });

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					try {
						// its error always?
						super.mainDish.removeCallProxy(mgcpResponse);
						Response inviteResponse = super.mainDish.getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, super.actingTransaction.getRequest());
						ContentTypeHeader ctp = super.mainDish.getHeaderFactory().createContentTypeHeader("text", "plain");
						inviteResponse.setContent(mgcpResponse.toString(), ctp);
						ToHeader tHeader = (ToHeader) inviteResponse.getHeader(ToHeader.NAME);
						tHeader.setTag(this.hashCode() + "");
						SipURI contactURI = super.mainDish.getAddressFactory().createSipURI("SimpleUser", super.mainDish.getStackAddress() + ":" + super.mainDish.getPort());
						ContactHeader ch = super.mainDish.getHeaderFactory().createContactHeader(super.mainDish.getAddressFactory().createAddress(contactURI));
						inviteResponse.addHeader(ch);
						super.actingTransaction.sendResponse(inviteResponse);

						super.mainDish.removeCallProxy(mgcpResponse);
						this.state = AnnProxyState.TERMINATED;
						super.actingTransaction = null;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {

				if (this.logger.isLoggable(Level.SEVERE)) {
					logger.severe("Received wrong message, it should be CRCX response, state: " + this.state + ", message: " + mgcpResponse);
				}
			}
			break;
			
		case SENT_DLCX:
			if (mgcpResponse instanceof DeleteConnectionResponse) {
				if (99 < code && code < 200) {
					// its provisional
				} else if (199 < code && code < 300) {
					// its success
					try {
						Response byeResponse = this.mainDish.getMessageFactory().createResponse(Response.OK, super.actingTransaction.getRequest());
						super.actingTransaction.sendResponse(byeResponse);
						this.state = AnnProxyState.SENT_BYE_200;
					} catch (Exception e) {
						e.printStackTrace();
						this.state = AnnProxyState.TERMINATED;
					}
				} else {
					// its error always?
					super.mainDish.removeCallProxy(mgcpResponse);
					this.state = AnnProxyState.TERMINATED;
					try {
						Response byeResponse = this.mainDish.getMessageFactory().createResponse(Response.OK, super.actingTransaction.getRequest());
						super.actingTransaction.sendResponse(byeResponse);
						this.state = AnnProxyState.TERMINATED;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				if (this.logger.isLoggable(Level.SEVERE)) {
					logger.severe("Received wrong message, it should be CRCX response, state: " + this.state + ", message: " + mgcpResponse);
				}
			}

			break;
			
			
		case SENT_INVITE_200:
			if (mgcpResponse instanceof NotificationRequestResponse) {

				// We do nothing, we just wait for notify on end
				super.mainDish.removeCallProxy(mgcpResponse);
			} else {

				if (this.logger.isLoggable(Level.SEVERE)) {
					logger.severe("Received wrong message, it should be CRCX response, state: " + this.state + ", message: " + mgcpResponse);
				}
			}
			break;
		default:
			if (this.logger.isLoggable(Level.SEVERE)) {
				logger.severe("Received message on wrong state: " + this.state + ", message: " + mgcpResponse);
			}
		}

	}

	public void processDialogTerminated(DialogTerminatedEvent dte) {
		super.mainDish.removeCallProxy(dte.getDialog().getCallId());

	}

	public void processIOException(IOExceptionEvent io) {
		// TODO Auto-generated method stub

	}

	public void processRequest(RequestEvent requestEvent) {

		String method = requestEvent.getRequest().getMethod();

		Request request = requestEvent.getRequest();
		if (requestEvent.getServerTransaction() != null && !((method.compareTo(Request.ACK) == 0) || (method.compareTo(Request.BYE) == 0))) {
			// its rtr?
			if (this.logger.isLoggable(Level.SEVERE)) {
				logger.severe("Received retransmission message, message: " + requestEvent.getRequest());
			}
			return;
		}
		switch (this.state) {

		case INITIAL:
			if (method.compareTo(Request.INVITE) == 0) {
				try {
					ServerTransaction st = super.sipProvider.getNewServerTransaction(request);
					super.actingDialog = super.sipProvider.getNewDialog(st);
					super.actingTransaction = st;
					super.actingDialog.terminateOnBye(true);

					EndpointIdentifier ei = new EndpointIdentifier(super.endpointName, this.mgcpServerAddress.getHostAddress() + ":" + this.mgcpServerPort);

					CreateConnection crcx = new CreateConnection(this, this.callIdentifier, ei, ConnectionMode.SendRecv);

					this.mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { crcx });
					this.mainDish.addCallProxy(crcx, this);
					this.state = AnnProxyState.SENT_CRCX;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (this.logger.isLoggable(Level.SEVERE)) {
					logger.severe("Received wrong message, message: " + requestEvent.getRequest());
				}
			}
			break;

		case SENT_INVITE_200:
			// here we await for ACK
			if (method.compareTo(Request.ACK) == 0) {
				// Its ok, we do nothing?
				this.state = AnnProxyState.RECEIVED_ACK;
			} else {
				if (this.logger.isLoggable(Level.SEVERE)) {
					logger.severe("Received wrong message on state: " + this.state + ", message: " + requestEvent.getRequest());
				}
			}
			break;
		case SENT_BYE_200:
			// here we await for ACK
			if (method.compareTo(Request.ACK) == 0) {
				// Its ok, we do nothing?
				this.state = AnnProxyState.TERMINATED;
			} else {
				if (this.logger.isLoggable(Level.SEVERE)) {
					logger.severe("Received wrong message on state: " + this.state + ", message: " + requestEvent.getRequest());
				}
			}
			break;
		case RECEIVED_ACK:
			try {

				// here we wait for BYE
				if (method.compareTo(Request.BYE) != 0) {
					// FIXME: add more
					if (this.logger.isLoggable(Level.SEVERE)) {
						logger.severe("Received wrong message on state: " + this.state + ", message: " + requestEvent.getRequest());
					}
					return;
				}

				if (super.actingTransaction != null && super.actingTransaction.getRequest().getMethod().compareTo(Request.BYE) == 0) {
					// rtr
					return;
				}

				super.actingTransaction = requestEvent.getServerTransaction();
				// This shoudl remove all connections (actually there is one :))
				DeleteConnection dlcx = new DeleteConnection(this, this.callIdentifier, this.endpointIndentifier);

				this.mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { dlcx });
				this.mainDish.addCallProxy(dlcx, this);
				this.state = AnnProxyState.SENT_DLCX;

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case TERMINATED:
			if (this.logger.isLoggable(Level.SEVERE)) {
				logger.severe("Received message on TERMINATED state, message: " + requestEvent.getRequest());
			}
			break;

		default:
			if (this.logger.isLoggable(Level.SEVERE)) {
				logger.severe("Received wrong message on state: " + this.state + ", message: " + requestEvent.getRequest());
			}

		}

	}

	public void processResponse(ResponseEvent response) {
		// TODO Auto-generated method stub

	}

	public void processTimeout(TimeoutEvent te) {
		// TODO Auto-generated method stub

	}

	public void processTransactionTerminated(TransactionTerminatedEvent tte) {
		// TODO Auto-generated method stub

	}

	public void transactionEnded(int arg0) {
		// TODO Auto-generated method stub

	}

	public void transactionRxTimedOut(JainMgcpCommandEvent msg) {

		try {
			super.mainDish.removeCallProxy(msg);

			super.mainDish.removeCallProxy(msg);
			Response response = super.mainDish.getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, super.actingTransaction.getRequest());
			ContentTypeHeader ctp = super.mainDish.getHeaderFactory().createContentTypeHeader("text", "plain");
			response.setContent("TIMEDOUT\n" + msg.toString(), ctp);
			ToHeader tHeader = (ToHeader) response.getHeader(ToHeader.NAME);
			tHeader.setTag(this.hashCode() + "");
			SipURI contactURI = super.mainDish.getAddressFactory().createSipURI("SimpleUser", super.mainDish.getStackAddress() + ":" + super.mainDish.getPort());
			ContactHeader ch = super.mainDish.getHeaderFactory().createContactHeader(super.mainDish.getAddressFactory().createAddress(contactURI));
			response.addHeader(ch);
			super.actingTransaction.sendResponse(response);

			super.mainDish.removeCallProxy(msg);
			this.state = AnnProxyState.TERMINATED;
			super.actingTransaction = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transactionTxTimedOut(JainMgcpCommandEvent msg) {
		try {

			super.mainDish.removeCallProxy(msg);
			Response response = super.mainDish.getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, super.actingTransaction.getRequest());
			ContentTypeHeader ctp = super.mainDish.getHeaderFactory().createContentTypeHeader("text", "plain");
			response.setContent("TIMEDOUT\n" + msg.toString(), ctp);
			ToHeader tHeader = (ToHeader) response.getHeader(ToHeader.NAME);
			tHeader.setTag(this.hashCode() + "");
			SipURI contactURI = super.mainDish.getAddressFactory().createSipURI("SimpleUser", super.mainDish.getStackAddress() + ":" + super.mainDish.getPort());
			ContactHeader ch = super.mainDish.getHeaderFactory().createContactHeader(super.mainDish.getAddressFactory().createAddress(contactURI));
			response.addHeader(ch);
			super.actingTransaction.sendResponse(response);

			super.mainDish.removeCallProxy(msg);
			this.state = AnnProxyState.TERMINATED;
			super.actingTransaction = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
