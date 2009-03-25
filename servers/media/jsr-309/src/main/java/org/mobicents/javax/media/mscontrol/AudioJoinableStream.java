package org.mobicents.javax.media.mscontrol;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.ModifyConnection;
import jain.protocol.ip.mgcp.message.ModifyConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConflictingParameterException;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.io.Serializable;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentHashMap;

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
	private final String id = (new UID()).toString();

	/*
	 * This is Map of Other AudioJoinableStream Vs this Direction i.e., this
	 * stream is connected with other audioStream(key) with Direction (value)
	 */
	protected ConcurrentHashMap<AudioJoinableStream, Direction> audJoinStrVsDirMap = new ConcurrentHashMap<AudioJoinableStream, Direction>();

	/*
	 * This Map of This ConnectionIdentifier (key) Vs Other
	 * AudioJoinableStream(value) i.e., this stream is connected with
	 * connectionid with other stream
	 */
	protected ConcurrentHashMap<ConnectionIdentifier, AudioJoinableStream> connIdVsAudJoinStrMap = new ConcurrentHashMap<ConnectionIdentifier, AudioJoinableStream>();

	protected int maxJoinees = 1;
	private JainMgcpStackProviderImpl jainMgcpStackProviderImpl = null;
	private MediaSessionImpl mediaSession = null;

	public AudioJoinableStream(AbstractJoinableContainer container) {
		this.container = container;
		this.mediaSession = (MediaSessionImpl) container.getMediaSession();
		this.jainMgcpStackProviderImpl = container.jainMgcpStackProviderImpl;
		this.maxJoinees = container.maxJoinees;
	}

	protected String getId() {
		return this.id;
	}

	public JoinableContainer getContainer() {

		return this.container;
	}

	public StreamType getType() {
		return StreamType.audio;
	}

	public Joinable[] getJoinees() throws MsControlException {
		Joinable[] j = new Joinable[audJoinStrVsDirMap.size()];
		int count = 0;
		for (AudioJoinableStream a : audJoinStrVsDirMap.keySet()) {
			j[count] = a;
			count++;
		}
		return j;
	}

	public Joinable[] getJoinees(Direction direction) throws MsControlException {
		List<AudioJoinableStream> audioJoiStreList = new ArrayList<AudioJoinableStream>();
		for (AudioJoinableStream key : audJoinStrVsDirMap.keySet()) {
			Direction d = audJoinStrVsDirMap.get(key);
			if (d.equals(direction) || d.equals(Direction.DUPLEX)) {
				audioJoiStreList.add(key);
			}
		}

		Joinable[] j = new Joinable[audioJoiStreList.size()];
		int count = 0;
		for (AudioJoinableStream a : audioJoiStreList) {
			j[count] = a;
			count++;
		}

		return j;
	}

	public void join(Direction direction, Joinable other) throws MsControlException {
		throw new MsControlException("Not supported yet. Use joinInitiate()");
	}

	public void joinInitiate(Direction direction, Joinable other, Serializable context) throws MsControlException {

		if (other.equals(this)) {
			throw new MsControlException("Stream cannot join to itself");
		}
		
		AudioJoinableStream audioJoiStreamOther = (AudioJoinableStream) other;

		Direction thisDir = audJoinStrVsDirMap.get(audioJoiStreamOther);
		Runnable tx = null;
		if (thisDir != null) {
			// This is existing join. May be change the direction
			if (!thisDir.equals(direction)) {
				ConnectionIdentifier thisConnId = null;
				for (ConnectionIdentifier cId : connIdVsAudJoinStrMap.keySet()) {
					AudioJoinableStream a = connIdVsAudJoinStrMap.get(cId);
					if (a.equals(audioJoiStreamOther)) {
						thisConnId = cId;
						break;
					}
				}

				tx = new ModifyTx(this, thisConnId, direction, audioJoiStreamOther);
			} else {
				// TODO Join already exist and user calls join again with same
				// Direction, is this error condition or just ignore?
				if (logger.isDebugEnabled()) {
					logger.debug("Requested to join already joined stream with same Direction again. Ignore");
				}
			}
		} else {
			// This is new Join
			if (audJoinStrVsDirMap.size() == this.maxJoinees) {
				throw new MsControlException("Already joined " + this.maxJoinees
						+ " times. Unjoin first and then call join");
			}

			if (audioJoiStreamOther.audJoinStrVsDirMap.size() == audioJoiStreamOther.maxJoinees) {
				throw new MsControlException("Other already joined " + audioJoiStreamOther.maxJoinees
						+ " times. Unjoin first and then call join");
			}

			tx = new JoinTx(this, direction, audioJoiStreamOther);
		}
		Provider.submit(tx);
	}

	public void unjoin(Joinable other) throws MsControlException {
		throw new MsControlException("Not supported yet. Use unjoinInitiate()");
	}

	public void unjoinInitiate(Joinable other, Serializable context) throws MsControlException {

		AudioJoinableStream audioJoiStreamOther = (AudioJoinableStream) other;

		ConnectionIdentifier thisConnId = null;
		ConnectionIdentifier otherConnId = null;

		for (ConnectionIdentifier cId : connIdVsAudJoinStrMap.keySet()) {
			AudioJoinableStream a = connIdVsAudJoinStrMap.get(cId);
			if (a.equals(audioJoiStreamOther)) {
				thisConnId = cId;
				break;
			}
		}

		for (ConnectionIdentifier cId : audioJoiStreamOther.connIdVsAudJoinStrMap.keySet()) {
			AudioJoinableStream a = audioJoiStreamOther.connIdVsAudJoinStrMap.get(cId);
			if (a.equals(this)) {
				otherConnId = cId;
				break;
			}
		}

		if (thisConnId == null || otherConnId == null) {
			throw new MsControlException("This stream is not connected to other stream");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("UnJoinTx() with thisConnId = " + thisConnId + " and otherConnId = " + otherConnId);
		}
		Runnable tx = new UnJoinTx(this, thisConnId, audioJoiStreamOther, otherConnId);
		Provider.submit(tx);

	}

	protected void update(StatusEvent anEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("Size of listener = " + this.container.getStatusEventListenerList().size());
		}
		for (StatusEventListener s : this.container.getStatusEventListenerList()) {
			logger.debug("listener instance = " + s);
			s.onEvent(anEvent);
		}
	}

	private ConnectionMode getConnectionMode(Direction direction) {
		ConnectionMode mode = null;
		switch (direction) {
		case DUPLEX:
			mode = ConnectionMode.SendRecv;
			break;
		case SEND:
			mode = ConnectionMode.SendOnly;
			break;
		case RECV:
			mode = ConnectionMode.RecvOnly;
			break;
		}
		return mode;
	}

	private class UnJoinTx implements Runnable, JainMgcpExtendedListener, Serializable {
		private AudioJoinableStream thisAudJoiStr = null;
		private AudioJoinableStream otherAudJoiStr = null;
		ConnectionIdentifier thisConnId = null;
		ConnectionIdentifier otherConnId = null;
		private int thisTx = -1;
		private int otherTx = -1;

		private volatile int noOfRespReceived = 0;
		private volatile boolean error = false;
		private volatile String errorTxt = null;

		UnJoinTx(AudioJoinableStream thisAudJoiStr, ConnectionIdentifier thisConnId,
				AudioJoinableStream otherAudJoiStr, ConnectionIdentifier otherConnId) {
			this.thisAudJoiStr = thisAudJoiStr;
			this.thisConnId = thisConnId;
			this.otherAudJoiStr = otherAudJoiStr;
			this.otherConnId = otherConnId;

		}

		public void run() {
			// TODO MMS MGCP Stack doesn't have support for Piggybacking :(
			// Till piggybacking is supported we will have to live with two
			// try{}catch(){} blocks

			CallIdentifier callId = mediaSession.getCallIdentifier();
			this.thisTx = jainMgcpStackProviderImpl.getUniqueTransactionHandler();
			this.otherTx = jainMgcpStackProviderImpl.getUniqueTransactionHandler();
			try {
				jainMgcpStackProviderImpl.addJainMgcpListener(this);
				EndpointIdentifier thisEndID = new EndpointIdentifier(this.thisAudJoiStr.container.endpoint,
						MsControlFactoryImpl.mgcpStackPeerIp + ":" + MsControlFactoryImpl.mgcpStackPeerPort);

				DeleteConnection thisDLCX = new DeleteConnection(this, callId, thisEndID, this.thisConnId);
				thisDLCX.setTransactionHandle(this.thisTx);

				jainMgcpStackProviderImpl.sendMgcpEvents(new JainMgcpEvent[] { thisDLCX });

			} catch (TooManyListenersException e) {
				logger.error("TooManyListenersException ", e);
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);

				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Unjoined, Error.e_System, e.getMessage());
				update(joinEvent);
				return;

			} catch (Exception e) {
				logger.error("Exception ", e);
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);

				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Unjoined, Error.e_System, e.getMessage());
				update(joinEvent);
				return;
			}

			try {
				EndpointIdentifier otherEndID = new EndpointIdentifier(this.otherAudJoiStr.container.endpoint,
						MsControlFactoryImpl.mgcpStackPeerIp + ":" + MsControlFactoryImpl.mgcpStackPeerPort);
				DeleteConnection otherDLCX = new DeleteConnection(this, callId, otherEndID, this.otherConnId);
				otherDLCX.setTransactionHandle(this.otherTx);

				jainMgcpStackProviderImpl.sendMgcpEvents(new JainMgcpEvent[] { otherDLCX });
			} catch (Exception e) {
				logger.error("Exception ", e);
				this.errorTxt += " - " + e.getMessage();
				this.noOfRespReceived++;
			}

		}

		public void transactionEnded(int arg0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Successfully completed Tx = " + arg0);
			}

		}

		public void transactionRxTimedOut(JainMgcpCommandEvent arg0) {
		}

		public void transactionTxTimedOut(JainMgcpCommandEvent jainMgcpComdEve) {
			this.error = true;
			this.errorTxt += " - No response from MGW. Tx timed out for DLCX Command sent "
					+ jainMgcpComdEve.toString();
			logger.error(this.errorTxt);
			noOfRespReceived++;
			if (noOfRespReceived == 2) {
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);
				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Unjoined, Error.e_Timeout, this.errorTxt);
				update(joinEvent);
			}

		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {
		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent respoEve) {
			int respoTx = respoEve.getTransactionHandle();
			if (respoTx != this.thisTx && respoTx != this.otherTx) {
				if (logger.isDebugEnabled()) {
					logger.debug("Received response for Tx = " + respoTx + " Exepected either " + this.thisTx + " or "
							+ this.otherTx);
				}
				return;
			}
			noOfRespReceived++;
			switch (respoEve.getObjectIdentifier()) {
			case Constants.RESP_DELETE_CONNECTION:
				processDeleteConnectionResponse((DeleteConnectionResponse) respoEve);
				break;
			default:
				logger.warn(" This RESPONSE is unexpected " + respoEve);
				this.errorTxt += " Unexpected response";
				this.error = true;
				if (noOfRespReceived == 2) {
					jainMgcpStackProviderImpl.removeJainMgcpListener(this);

					JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
							this.thisAudJoiStr, JoinEvent.ev_Joined, Error.e_System, this.errorTxt);
					update(joinEvent);
				}
				break;

			}
		}

		private void processDeleteConnectionResponse(DeleteConnectionResponse responseEvent) {
			ReturnCode returnCode = responseEvent.getReturnCode();
			JoinEvent joinEvent = null;
			switch (returnCode.getValue()) {
			case ReturnCode.TRANSACTION_BEING_EXECUTED:
				// do nothing
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction " + responseEvent.getTransactionHandle()
							+ "is being executed. Response received = " + responseEvent);
				}
				break;
			case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
				if (logger.isDebugEnabled()) {
					logger.debug("DLCX executed successfully for Tx = " + responseEvent.getTransactionHandle());
				}
				if (noOfRespReceived == 2) {
					jainMgcpStackProviderImpl.removeJainMgcpListener(this);
					if (!this.error) {
						this.thisAudJoiStr.connIdVsAudJoinStrMap.remove(this.thisConnId);
						this.otherAudJoiStr.connIdVsAudJoinStrMap.remove(this.otherConnId);

						this.thisAudJoiStr.audJoinStrVsDirMap.remove(this.otherAudJoiStr);
						this.otherAudJoiStr.audJoinStrVsDirMap.remove(this.thisAudJoiStr);

						joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
								this.thisAudJoiStr, JoinEvent.ev_Unjoined);
					} else {
						joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
								this.thisAudJoiStr, JoinEvent.ev_Unjoined, Error.e_System, this.errorTxt);

					}
					update(joinEvent);
				}

				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				this.error = true;
				this.errorTxt += " - " + responseEvent.getReturnCode().getComment();
				if (noOfRespReceived == 2) {
					jainMgcpStackProviderImpl.removeJainMgcpListener(this);
					joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
							this.thisAudJoiStr, JoinEvent.ev_Unjoined, Error.e_System, this.errorTxt);
					update(joinEvent);
				}
				break;

			}

		}
	}

	private class ModifyTx implements Runnable, JainMgcpExtendedListener, Serializable {
		private AudioJoinableStream thisAudJoiStr = null;
		private ConnectionIdentifier thisConnId = null;
		private Direction thisDir = null;
		private AudioJoinableStream otherAudJoiStr = null;
		private int tx = -1;

		ModifyTx(AudioJoinableStream thisAudJoiStr, ConnectionIdentifier thisConnId, Direction thisDir,
				AudioJoinableStream otherAudJoiStr) {
			this.thisAudJoiStr = thisAudJoiStr;
			this.thisConnId = thisConnId;
			this.thisDir = thisDir;
			this.otherAudJoiStr = otherAudJoiStr;
		}

		public void run() {
			try {
				this.tx = jainMgcpStackProviderImpl.getUniqueTransactionHandler();
				jainMgcpStackProviderImpl.addJainMgcpListener(this);
				CallIdentifier callId = mediaSession.getCallIdentifier();
				EndpointIdentifier thisEndID = new EndpointIdentifier(this.thisAudJoiStr.container.endpoint,
						MsControlFactoryImpl.mgcpStackPeerIp + ":" + MsControlFactoryImpl.mgcpStackPeerPort);
				ModifyConnection modifyConnection = new ModifyConnection(this, callId, thisEndID, this.thisConnId);

				modifyConnection.setMode(getConnectionMode(this.thisDir));
				modifyConnection.setTransactionHandle(this.tx);

				jainMgcpStackProviderImpl.sendMgcpEvents(new JainMgcpEvent[] { modifyConnection });

			} catch (TooManyListenersException e) {
				logger.error(e);
			} catch (Exception e) {
				logger.error(e);
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

		public void transactionTxTimedOut(JainMgcpCommandEvent arg0) {
			jainMgcpStackProviderImpl.removeJainMgcpListener(this);
			JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
					this.thisAudJoiStr, JoinEvent.ev_Joined, Error.e_Timeout, "No response from MGW. Tx timed out "
							+ arg0.toString());
			update(joinEvent);
		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {

		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {
			if (jainmgcpresponseevent.getTransactionHandle() != this.tx) {
				return;
			}

			switch (jainmgcpresponseevent.getObjectIdentifier()) {

			case Constants.RESP_MODIFY_CONNECTION:
				processMofiyConnectionResponse((ModifyConnectionResponse) jainmgcpresponseevent);
				break;
			default:
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);
				logger.warn(" This RESPONSE is unexpected " + jainmgcpresponseevent);
				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Joined, Error.e_System, "Received unexpected Response "
								+ jainmgcpresponseevent.toString());
				update(joinEvent);
				break;

			}
		}

		private void processMofiyConnectionResponse(ModifyConnectionResponse responseEvent) {
			logger.debug(" processMofiyConnectionResponse() ");

			JoinEvent joinEvent = null;
			ReturnCode returnCode = responseEvent.getReturnCode();

			switch (returnCode.getValue()) {
			case ReturnCode.TRANSACTION_BEING_EXECUTED:
				// do nothing
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction " + this.tx + "is being executed. Response received = " + responseEvent);
				}
				break;
			case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);
				if (logger.isDebugEnabled()) {
					logger.debug(" MDCX TRANSACTION_EXECUTED_NORMALLY for connectionIdentifier = " + this.thisConnId
							+ "endpointID = " + this.thisAudJoiStr.container.endpoint);
				}

				switch (this.thisDir) {
				case SEND:
					this.thisAudJoiStr.audJoinStrVsDirMap.put(this.otherAudJoiStr, Direction.SEND);
					this.otherAudJoiStr.audJoinStrVsDirMap.put(this.thisAudJoiStr, Direction.RECV);
					break;
				case RECV:
					this.thisAudJoiStr.audJoinStrVsDirMap.put(this.otherAudJoiStr, Direction.RECV);
					this.otherAudJoiStr.audJoinStrVsDirMap.put(this.thisAudJoiStr, Direction.SEND);
					break;
				case DUPLEX:
					this.thisAudJoiStr.audJoinStrVsDirMap.put(this.otherAudJoiStr, Direction.DUPLEX);
					this.otherAudJoiStr.audJoinStrVsDirMap.put(this.thisAudJoiStr, Direction.DUPLEX);
					break;
				}

				joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Joined);
				update(joinEvent);

				break;
			default:
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);

				joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Joined, Error.e_System, responseEvent.getReturnCode()
								.getComment());
				update(joinEvent);

				break;

			}

		}
	}

	private class JoinTx implements Runnable, JainMgcpExtendedListener, Serializable {

		private AudioJoinableStream thisAudJoiStr = null;
		private AudioJoinableStream otherAudJoiStr = null;
		private Direction thisDir = null;

		private int tx = -1;

		JoinTx(AudioJoinableStream thisAudJoiStr, Direction thisDir, AudioJoinableStream otherAudJoiStr) {
			this.thisAudJoiStr = thisAudJoiStr;
			this.thisDir = thisDir;
			this.otherAudJoiStr = otherAudJoiStr;
		}

		public void run() {
			this.tx = jainMgcpStackProviderImpl.getUniqueTransactionHandler();
			try {

				jainMgcpStackProviderImpl.addJainMgcpListener(this);
				CallIdentifier callId = mediaSession.getCallIdentifier();
				EndpointIdentifier endpointID = new EndpointIdentifier(this.thisAudJoiStr.container.endpoint,
						MsControlFactoryImpl.mgcpStackPeerIp + ":" + MsControlFactoryImpl.mgcpStackPeerPort);

				EndpointIdentifier secondEndpointID = new EndpointIdentifier(this.otherAudJoiStr.container.endpoint,
						MsControlFactoryImpl.mgcpStackPeerIp + ":" + MsControlFactoryImpl.mgcpStackPeerPort);

				CreateConnection createConnection = new CreateConnection(this, callId, endpointID,
						ConnectionMode.SendRecv);

				createConnection.setSecondEndpointIdentifier(secondEndpointID);
				createConnection.setMode(getConnectionMode(this.thisDir));
				createConnection.setTransactionHandle(this.tx);
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
			JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
					this.thisAudJoiStr, JoinEvent.ev_Joined, Error.e_Timeout, " Request timedout. No response from MGW");
			update(joinEvent);
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
				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Joined, Error.e_System, jainmgcpresponseevent.getReturnCode()
								.getComment());
				update(joinEvent);
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
					logger.debug("joinInitiate() executed successfully for this.endpoint = "
							+ responseEvent.getSpecificEndpointIdentifier() + " this.other.endpoint = "
							+ responseEvent.getSecondEndpointIdentifier());
				}
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);

				EndpointIdentifier thisEndpointIdentifier = responseEvent.getSpecificEndpointIdentifier();
				this.thisAudJoiStr.container.endpoint = thisEndpointIdentifier.getLocalEndpointName();

				ConnectionIdentifier thisConnId = responseEvent.getConnectionIdentifier();
				this.thisAudJoiStr.connIdVsAudJoinStrMap.put(thisConnId, this.otherAudJoiStr);

				EndpointIdentifier otherEndpointIdentifier = responseEvent.getSecondEndpointIdentifier();
				this.otherAudJoiStr.container.endpoint = otherEndpointIdentifier.getLocalEndpointName();

				ConnectionIdentifier otherConnId = responseEvent.getSecondConnectionIdentifier();
				this.otherAudJoiStr.connIdVsAudJoinStrMap.put(otherConnId, this.thisAudJoiStr);

				switch (this.thisDir) {
				case SEND:
					this.thisAudJoiStr.audJoinStrVsDirMap.put(this.otherAudJoiStr, Direction.SEND);
					this.otherAudJoiStr.audJoinStrVsDirMap.put(this.thisAudJoiStr, Direction.RECV);
					break;
				case RECV:
					this.thisAudJoiStr.audJoinStrVsDirMap.put(this.otherAudJoiStr, Direction.RECV);
					this.otherAudJoiStr.audJoinStrVsDirMap.put(this.thisAudJoiStr, Direction.SEND);
					break;
				case DUPLEX:
					this.thisAudJoiStr.audJoinStrVsDirMap.put(this.otherAudJoiStr, Direction.DUPLEX);
					this.otherAudJoiStr.audJoinStrVsDirMap.put(this.thisAudJoiStr, Direction.DUPLEX);
					break;
				}

				joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Joined);
				update(joinEvent);

				break;
			case ReturnCode.ENDPOINT_INSUFFICIENT_RESOURCES:
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);
				logger.warn("joinInitiate() executed un-successfully for this.endpoint = "
						+ this.thisAudJoiStr.container.endpoint + " this.other.endpoint = "
						+ this.otherAudJoiStr.container.endpoint + " " + responseEvent.getReturnCode().getComment());
				joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Joined, Error.e_System, responseEvent.getReturnCode()
								.getComment());
				update(joinEvent);
				break;
			default:
				jainMgcpStackProviderImpl.removeJainMgcpListener(this);
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.ev_Joined, Error.e_System, responseEvent.getReturnCode()
								.getComment());
				update(joinEvent);
				break;

			}

		}

	}

}
