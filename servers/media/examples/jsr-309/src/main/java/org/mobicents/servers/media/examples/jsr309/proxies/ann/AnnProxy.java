package org.mobicents.servers.media.examples.jsr309.proxies.ann;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

import javax.media.mscontrol.JoinEvent;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.StatusEvent;
import javax.media.mscontrol.StatusEventListener;
import javax.media.mscontrol.Joinable.Direction;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.NetworkConnectionEvent;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.MediaEventListener;
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

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.servers.media.examples.jsr309.CallProxy;
import org.mobicents.servers.media.examples.jsr309.Jsr309Example;

/**
 * 
 * @author amit bhayani
 *
 */
public class AnnProxy extends CallProxy {
	private static Logger logger = Logger.getLogger(AnnProxy.class);

	private AnnProxyState state = AnnProxyState.INITIAL;
	// This file should be present on server on exact same address
	public static final String HELLO_WORLD = "file://home/abhayani/workarea/mobicents/svn/trunk/servers/media/examples/jsr-309/audio/ann.wav";
	NetworkConnection myNetworkConnection = null;
	MediaGroup medGrp = null;
	Player player = null;

	public AnnProxy(Jsr309Example example) {
		super(example);
	}

	public void processDialogTerminated(DialogTerminatedEvent dte) {
		jsr309Example.removeCallProxy(dte.getDialog().getCallId());
	}

	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processRequest(RequestEvent requestEvent) {
		String method = requestEvent.getRequest().getMethod();

		Request request = requestEvent.getRequest();
		if (requestEvent.getServerTransaction() != null
				&& !((method.compareTo(Request.ACK) == 0) || (method.compareTo(Request.BYE) == 0))) {
			// its rtr?
			logger.error("Received retransmission message, message: " + requestEvent.getRequest());
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
					super.mediaSession = this.msControlFactory.createMediaSession();

					// TODO Using MediaSessionImpl till JSr 309 adds method
					// createNetworkConnection()
					myNetworkConnection = ((MediaSessionImpl) this.mediaSession).createNetworkConnection();

					MediaEventListener<NetworkConnectionEvent> netConnList = new MediaEventListener<NetworkConnectionEvent>() {

						public void onEvent(NetworkConnectionEvent anEvent) {

							if (Error.e_OK.equals(anEvent.getError())) {
								if (NetworkConnection.ev_Modify.equals(anEvent.getEventID())) {
									if (logger.isDebugEnabled()) {
										logger.debug("UA connected to PR endpoint " + anEvent.toString());
									}

									try {
										final String localSdp = myNetworkConnection.getRawLocalSessionDescription();

										StatusEventListener statusEvtList = new StatusEventListener() {

											public void onEvent(StatusEvent event) {
												if (event.getError().equals(Error.e_OK)) {
													if (JoinEvent.ev_Joined.equals(event.getEventID())) {
														if (logger.isDebugEnabled()) {
															logger.debug("Join successful " + event);
														}

														try {
															Response inviteResponse = jsr309Example.getMessageFactory()
																	.createResponse(Response.OK,
																			actingTransaction.getRequest());
															ContentTypeHeader ctp = jsr309Example.getHeaderFactory()
																	.createContentTypeHeader("application", "sdp");
															inviteResponse.setContent(localSdp, ctp);
															ToHeader tHeader = (ToHeader) inviteResponse
																	.getHeader(ToHeader.NAME);
															tHeader.setTag(this.hashCode() + "");

															SipURI contactURI = jsr309Example.getAddressFactory()
																	.createSipURI(
																			"SimpleUser",
																			jsr309Example.getStackAddress() + ":"
																					+ jsr309Example.getPort());

															ContactHeader ch = jsr309Example.getHeaderFactory()
																	.createContactHeader(
																			jsr309Example.getAddressFactory()
																					.createAddress(contactURI));

															inviteResponse.addHeader(ch);

															actingTransaction.sendResponse(inviteResponse);

															state = AnnProxyState.SENT_INVITE_200;

														} catch (ParseException e) {
															logger.error(e);
														} catch (SipException e) {
															logger.error(e);
														} catch (InvalidArgumentException e) {
															logger.error(e);
														}

														try {
															player = medGrp.getPlayer();
															URI audioFile = new URI(HELLO_WORLD);
														} catch (MsControlException e) {
															logger.error(e);
														} catch (URISyntaxException e) {
															logger.error(e);
														}

													} else {
														if (logger.isDebugEnabled()) {
															logger.debug("UnJoin successful " + event);
														}
													}

												} else {
													logger.error("Join failed " + event);
												}
											}
										};
										myNetworkConnection.addListener(statusEvtList);
										medGrp = ((MediaSessionImpl) mediaSession).createMediaGroup();
										myNetworkConnection.join(Direction.DUPLEX, medGrp);
									} catch (MsControlException msEx) {
										logger.error(msEx);
									}
								}
							} else {

							}
						}
					};
					byte[] sdp = request.getRawContent();
					myNetworkConnection.addListener(netConnList);
					myNetworkConnection.modify("$", new String(sdp));

				} catch (Exception e) {
					logger.error(e);
				}
			} else {
				logger.error("Received wrong message, message: " + requestEvent.getRequest());
			}
			break;

		case SENT_INVITE_200:
			// here we await for ACK
			if (method.compareTo(Request.ACK) == 0) {
				// Its ok, we do nothing?
				this.state = AnnProxyState.RECEIVED_ACK;
			} else {
				logger.error("Received wrong message on state: " + this.state + ", message: "
						+ requestEvent.getRequest());
			}
			break;
		case RECEIVED_ACK:

			// here we wait for BYE
			if (method.compareTo(Request.BYE) != 0) {
				// FIXME: add more
				logger.error("Received wrong message on state: " + this.state + ", message: "
						+ requestEvent.getRequest());
				return;
			}

			if (super.actingTransaction != null
					&& super.actingTransaction.getRequest().getMethod().compareTo(Request.BYE) == 0) {
				// rtr
				return;
			}
			this.player.stop();
			this.myNetworkConnection.release();
			try {
				Response byeResponse = jsr309Example.getMessageFactory().createResponse(Response.OK,
						super.actingTransaction.getRequest());
				super.actingTransaction.sendResponse(byeResponse);
				this.state = AnnProxyState.SENT_BYE_200;
			} catch (Exception e) {
				logger.error(e);
				this.state = AnnProxyState.TERMINATED;
			}
			break;
		}

	}

	public void processResponse(ResponseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

}
