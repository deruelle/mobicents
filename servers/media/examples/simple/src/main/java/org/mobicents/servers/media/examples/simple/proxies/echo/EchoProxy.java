package org.mobicents.servers.media.examples.simple.proxies.echo;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;

import java.text.ParseException;
import java.util.logging.Level;

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

public class EchoProxy extends CallProxy {

	private EchoProxyState state = EchoProxyState.INITIAL;

	public EchoProxy(SimplExample mainDish) {
		super(mainDish);

		super.callIdentifier = new CallIdentifier("" + this.hashCode());
		super.endpointName = "media/test/trunk/Loopback/$";
	}

	public void processMgcpCommandEvent(JainMgcpCommandEvent mgcpCommand) {
		// TODO Auto-generated method stub

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
						this.state = EchoProxyState.SENT_INVITE_200;
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
						this.state = EchoProxyState.TERMINATED;
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
						this.state = EchoProxyState.SENT_BYE_200;
					} catch (Exception e) {
						e.printStackTrace();
						this.state = EchoProxyState.TERMINATED;
					}
				} else {
					// its error always?
					super.mainDish.removeCallProxy(mgcpResponse);
					this.state = EchoProxyState.TERMINATED;
					try {
						Response byeResponse = this.mainDish.getMessageFactory().createResponse(Response.OK, super.actingTransaction.getRequest());
						super.actingTransaction.sendResponse(byeResponse);
						this.state = EchoProxyState.TERMINATED;
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
			// here we await only INVITE
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
					this.state = EchoProxyState.SENT_CRCX;
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
				this.state = EchoProxyState.RECEIVED_ACK;
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
				this.state = EchoProxyState.TERMINATED;
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
				this.state = EchoProxyState.SENT_DLCX;

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
			Response inviteResponse = super.mainDish.getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, super.actingTransaction.getRequest());
			ContentTypeHeader ctp = super.mainDish.getHeaderFactory().createContentTypeHeader("text", "plain");
			inviteResponse.setContent("TIMEDOUT\n" + msg.toString(), ctp);
			ToHeader tHeader = (ToHeader) inviteResponse.getHeader(ToHeader.NAME);
			tHeader.setTag(this.hashCode() + "");
			SipURI contactURI = super.mainDish.getAddressFactory().createSipURI("SimpleUser", super.mainDish.getStackAddress() + ":" + super.mainDish.getPort());
			ContactHeader ch = super.mainDish.getHeaderFactory().createContactHeader(super.mainDish.getAddressFactory().createAddress(contactURI));
			inviteResponse.addHeader(ch);
			super.actingTransaction.sendResponse(inviteResponse);

			super.mainDish.removeCallProxy(msg);
			this.state = EchoProxyState.TERMINATED;
			super.actingTransaction = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transactionTxTimedOut(JainMgcpCommandEvent msg) {
		try {
			System.err.println();
			super.mainDish.removeCallProxy(msg);
			Response inviteResponse = super.mainDish.getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, super.actingTransaction.getRequest());
			ContentTypeHeader ctp = super.mainDish.getHeaderFactory().createContentTypeHeader("text", "plain");
			inviteResponse.setContent("TIMEDOUT\n" + msg.toString(), ctp);
			ToHeader tHeader = (ToHeader) inviteResponse.getHeader(ToHeader.NAME);
			tHeader.setTag(this.hashCode() + "");
			SipURI contactURI = super.mainDish.getAddressFactory().createSipURI("SimpleUser", super.mainDish.getStackAddress() + ":" + super.mainDish.getPort());
			ContactHeader ch = super.mainDish.getHeaderFactory().createContactHeader(super.mainDish.getAddressFactory().createAddress(contactURI));
			inviteResponse.addHeader(ch);
			super.actingTransaction.sendResponse(inviteResponse);

			super.mainDish.removeCallProxy(msg);
			this.state = EchoProxyState.TERMINATED;
			super.actingTransaction = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
