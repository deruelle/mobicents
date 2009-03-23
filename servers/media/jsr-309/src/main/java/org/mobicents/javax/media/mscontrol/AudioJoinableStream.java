package org.mobicents.javax.media.mscontrol;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConflictingParameterException;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.io.Serializable;
import java.util.TooManyListenersException;

import javax.media.mscontrol.JoinEvent;
import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.JoinableContainer;
import javax.media.mscontrol.JoinableStream;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.StatusEvent;
import javax.media.mscontrol.StatusEventListener;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.ResourceContainer;

import org.apache.log4j.Logger;
import org.mobicents.jsr309.mgcp.Provider;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class AudioJoinableStream implements JoinableStream {

	private static final Logger logger = Logger.getLogger(AudioJoinableStream.class);
	private AbstractJoinableContainer container = null;
	protected AudioJoinableStream other = null;
	protected Joinable[] joineesArray = new Joinable[1];
	protected Direction direction = null;
	protected String endpoint = null;
	private JainMgcpStackProviderImpl jainMgcpStackProviderImpl = null;
	private MediaSessionImpl mediaSession = null;

	public AudioJoinableStream(AbstractJoinableContainer container, String endpoint,
			JainMgcpStackProviderImpl jainMgcpStackProviderImpl) {
		this.container = container;
		this.endpoint = endpoint;
		this.mediaSession = (MediaSessionImpl) container.getMediaSession();
	}

	public JoinableContainer getContainer() {

		return this.container;
	}

	public StreamType getType() {
		return StreamType.audio;
	}

	public Joinable[] getJoinees() throws MsControlException {
		return joineesArray;
	}

	public Joinable[] getJoinees(Direction direction) throws MsControlException {
		Direction joineeDirection = this.other.getDirection();
		if (joineeDirection == direction || joineeDirection == Direction.DUPLEX) {
			return joineesArray;
		}

		return null;
	}

	public void join(Direction direction, Joinable other) throws MsControlException {

	}

	public void joinInitiate(Direction direction, Joinable other, Serializable context) throws MsControlException {
		if (this.other == null) {
			this.direction = direction;
			this.other = (AudioJoinableStream) other;
			this.joineesArray[0] = other;

			if (direction == Direction.RECV) {
				this.other.direction = Direction.SEND;

				// other.join(Direction.SEND, this);
			} else if (direction == Direction.SEND) {
				this.other.direction = Direction.RECV;
				// other.join(Direction.RECV, this);
			} else {
				this.other.direction = Direction.DUPLEX;
				// other.join(Direction.DUPLEX, this);
			}
			this.other.joineesArray[0] = this;
			this.other.other = this;

			Runnable tx = new JoinTx(this, this.endpoint, this.other.endpoint);
			Provider.submit(tx);
		} else {
			throw new MsControlException("This Joinable is already joined to other with Direction " + this.direction
					+ " Call unjoin first and then join again");
		}
	}

	public void unjoin(Joinable other) throws MsControlException {

	}

	public void unjoinInitiate(Joinable other, Serializable context) throws MsControlException {

	}

	private void clean() {
		if (this.other != null) {

			this.other.direction = null;
			this.other.joineesArray[0] = null;
			this.other.other = null;

			this.direction = null;
			this.other = null;
			this.joineesArray[0] = null;
		}
	}

	protected void update(StatusEvent anEvent) {
		for (StatusEventListener s : this.container.getStatusEventListenerList()) {
			s.onEvent(anEvent);
		}
	}

	public Direction getDirection() {
		return this.direction;
	}

	private class JoinTx implements Runnable, JainMgcpExtendedListener, Serializable {

		private String endpointA = null;
		private String endpointB = null;
		private JoinableStream thisJoinable = null;

		private int tx = -1;

		JoinTx(JoinableStream thisJoinable, String endpointA, String endpointB) {
			this.endpointA = endpointA;
			this.endpointB = endpointB;
			this.thisJoinable = thisJoinable;
		}

		public void run() {
			this.tx = jainMgcpStackProviderImpl.getUniqueTransactionHandler();
			try {

				jainMgcpStackProviderImpl.addJainMgcpListener(this);
				CallIdentifier callId = mediaSession.getCallIdentifier();
				EndpointIdentifier endpointID = new EndpointIdentifier(this.endpointA,
						MsControlFactoryImpl.mgcpStackPeerIp + ":" + MsControlFactoryImpl.mgcpStackPeerPort);

				EndpointIdentifier secondEndpointID = new EndpointIdentifier(this.endpointB,
						MsControlFactoryImpl.mgcpStackPeerIp + ":" + MsControlFactoryImpl.mgcpStackPeerPort);

				CreateConnection createConnection = new CreateConnection(this, callId, endpointID,
						ConnectionMode.SendRecv);

				createConnection.setSecondEndpointIdentifier(secondEndpointID);
				createConnection.setTransactionHandle(tx);
				jainMgcpStackProviderImpl.sendMgcpEvents(new JainMgcpEvent[] { createConnection });
			} catch (TooManyListenersException e) {
				e.printStackTrace();
			} catch (ConflictingParameterException e) {
				e.printStackTrace();
			}
		}

		public void transactionEnded(int arg0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Successfully completed Tx = " + arg0);
			}
		}

		public void transactionRxTimedOut(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void transactionTxTimedOut(JainMgcpCommandEvent jainMgcpCommandEvent) {
			logger.error("No response from MGW. Tx timed out for MGCP Tx " + this.tx + " For Command sent "
					+ jainMgcpCommandEvent.toString());
			jainMgcpStackProviderImpl.removeJainMgcpListener(this);			
			JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, other, this.thisJoinable,
					JoinEvent.ev_Joined, Error.e_Timeout, " Request timedout. No response from MGW");
			update(joinEvent);
			clean();
		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {
			if (jainmgcpresponseevent.getTransactionHandle() != this.tx) {
				return;
			}

			// TODO : Depending on Response we get fire corresponding JSR 309
			// events here
			switch (jainmgcpresponseevent.getObjectIdentifier()) {
			case Constants.RESP_CREATE_CONNECTION:
				processCreateConnectionResponse((CreateConnectionResponse) jainmgcpresponseevent);
				break;
			default:
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);				
				logger.warn(" This RESPONSE is unexpected " + jainmgcpresponseevent);
				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, other, this.thisJoinable,
						JoinEvent.ev_Joined, Error.e_System, jainmgcpresponseevent.getReturnCode().getComment());
				update(joinEvent);
				clean();
				break;

			}
		}

		private void processCreateConnectionResponse(CreateConnectionResponse responseEvent) {
			logger.debug(" processCreateConnectionResponse() ");
			ReturnCode returnCode = responseEvent.getReturnCode();
			JoinEvent joinEvent = null;
			switch (returnCode.getValue()) {
			case ReturnCode.TRANSACTION_BEING_EXECUTED:
				// do nothing
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction " + this.tx + "is being executed. Response received = " + responseEvent);
				}
				break;
			case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
				if (logger.isDebugEnabled()) {
					logger.debug("joinInitiate() executed successfully for this.endpoint = " + endpoint
							+ " this.other.endpoint = " + other.endpoint);
				}
				EndpointIdentifier thisEndpointIdentifier = responseEvent.getSpecificEndpointIdentifier();
				if(thisEndpointIdentifier != null){
					endpoint = thisEndpointIdentifier.getLocalEndpointName();
				}
				
				EndpointIdentifier otherEndpointIdentifier = responseEvent.getSecondEndpointIdentifier();
				if(otherEndpointIdentifier != null){
					other.endpoint = otherEndpointIdentifier.getLocalEndpointName();
				}
				
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);
				joinEvent = new JoinEventImpl((ResourceContainer) container, this, other, this.thisJoinable,
						JoinEvent.ev_Joined, Error.e_OK, responseEvent.getReturnCode().getComment());
				update(joinEvent);

				break;
			case ReturnCode.ENDPOINT_INSUFFICIENT_RESOURCES:
				logger
						.warn("joinInitiate() executed unsuccessfully for this.endpoint = " + endpoint
								+ " this.other.endpoint = " + other.endpoint + " "
								+ responseEvent.getReturnCode().getComment());
				joinEvent = new JoinEventImpl((ResourceContainer) container, this, other, this.thisJoinable,
						JoinEvent.ev_Joined, Error.e_System, responseEvent.getReturnCode().getComment());
				update(joinEvent);
				clean();
				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				joinEvent = new JoinEventImpl((ResourceContainer) container, this, other, this.thisJoinable,
						JoinEvent.ev_Joined, Error.e_System, responseEvent.getReturnCode().getComment());
				update(joinEvent);
				clean();
				break;

			}

		}

	}

}
