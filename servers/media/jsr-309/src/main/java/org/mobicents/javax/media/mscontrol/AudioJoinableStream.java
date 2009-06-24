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
import java.util.concurrent.ConcurrentHashMap;

import javax.media.mscontrol.join.JoinEvent;
import javax.media.mscontrol.join.Joinable;
import javax.media.mscontrol.join.JoinableContainer;
import javax.media.mscontrol.join.JoinableStream;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.join.TooManyJoineesException;
import javax.media.mscontrol.MediaErr;
import javax.media.mscontrol.resource.ResourceContainer;

import org.apache.log4j.Logger;
import org.mobicents.jsr309.mgcp.MgcpWrapper;
import org.mobicents.jsr309.mgcp.Provider;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;

/**
 * 
 * @author amit bhayani
 *
 */
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
	protected ConcurrentHashMap<AudioJoinableStream, ConnectionIdentifier> audJoinStrVsConnIdMap = new ConcurrentHashMap<AudioJoinableStream, ConnectionIdentifier>();

	protected int maxJoinees = 1;
	private MgcpWrapper mgcpWrapper = null;
	private MediaSessionImpl mediaSession = null;

	public AudioJoinableStream(AbstractJoinableContainer container) {
		this.container = container;
		this.mediaSession = (MediaSessionImpl) container.getMediaSession();
		this.mgcpWrapper = container.mgcpWrapper;
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

	protected ConnectionIdentifier getConnectionIdentifier(AudioJoinableStream audioJoiStreamOther) {
		ConnectionIdentifier connId = null;
		connId = audJoinStrVsConnIdMap.get(audioJoiStreamOther);
		return connId;
	}

	public Joinable[] getJoinees() throws MsControlException {
		this.container.checkState();

		Joinable[] j = new Joinable[audJoinStrVsDirMap.size()];
		int count = 0;
		for (AudioJoinableStream a : audJoinStrVsDirMap.keySet()) {
			j[count] = a;
			count++;
		}
		return j;
	}

	public Joinable[] getJoinees(Direction direction) throws MsControlException {
		this.container.checkState();
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

		this.container.checkState();

		AudioJoinableStream audioJoiStreamOther = (AudioJoinableStream) other;

		audioJoiStreamOther.container.checkState();

		if (other.equals(this)) {
			throw new MsControlException("Stream cannot join to itself");
		}

		Direction thisDir = audJoinStrVsDirMap.get(audioJoiStreamOther);
		Runnable tx = null;
		if (thisDir != null) {
			// This is existing join. May be change the direction
			if (!thisDir.equals(direction)) {
				ConnectionIdentifier thisConnId = getConnectionIdentifier(audioJoiStreamOther);
				ConnectionIdentifier otherConnId = audioJoiStreamOther.getConnectionIdentifier(this);
				tx = new ModifyTx(this, thisConnId, direction, audioJoiStreamOther, otherConnId);
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
				throw new TooManyJoineesException("Already joined " + this.maxJoinees
						+ " times. Unjoin first and then call join");
			}

			if (audioJoiStreamOther.audJoinStrVsDirMap.size() == audioJoiStreamOther.maxJoinees) {
				throw new TooManyJoineesException("Other already joined " + audioJoiStreamOther.maxJoinees
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
		if (!this.container.state.equals(MediaObjectState.RELEASED)) {
			AudioJoinableStream audioJoiStreamOther = (AudioJoinableStream) other;

			if (!audioJoiStreamOther.container.state.equals(MediaObjectState.RELEASED)) {
				ConnectionIdentifier thisConnId = getConnectionIdentifier(audioJoiStreamOther);

				if (thisConnId == null) {
					throw new MsControlException("This stream is not connected to other stream");
				}

				ConnectionIdentifier otherConnId = audioJoiStreamOther.getConnectionIdentifier(this);

				// This should never happen. If This is connected to Other,
				// Other has to be connected to This, else its an error/leak
				// state
				if (otherConnId == null) {
					throw new MsControlException(
							"Other stream is not connected to this stream. This is Error/Leak condition");
				}

				if (logger.isDebugEnabled()) {
					logger.debug("UnJoinTx() with thisConnId = " + thisConnId + " and otherConnId = " + otherConnId);
				}
				Runnable tx = new UnJoinTx(this, thisConnId, audioJoiStreamOther, otherConnId);
				Provider.submit(tx);
			}

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
			this.thisTx = mgcpWrapper.getUniqueTransactionHandler();
			this.otherTx = mgcpWrapper.getUniqueTransactionHandler();
			try {
				mgcpWrapper.addListnere(this.thisTx, this);

				EndpointIdentifier thisEndID = new EndpointIdentifier(this.thisAudJoiStr.container.endpoint,
						mgcpWrapper.getPeerIp() + ":" + mgcpWrapper.getPeerPort());

				DeleteConnection thisDLCX = new DeleteConnection(this, callId, thisEndID, this.thisConnId);
				thisDLCX.setTransactionHandle(this.thisTx);

				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { thisDLCX });

			} catch (Exception e) {
				logger.error("Exception ", e);
				mgcpWrapper.removeListener(this.thisTx);

				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.UNJOINED, false, MediaErr.UNKNOWN_ERROR, e.getMessage());
				container.updateUnjoined(joinEvent, this.thisConnId, this.otherConnId, this.otherAudJoiStr.container,
						true);
				return;
			}

			try {
				mgcpWrapper.addListnere(this.otherTx, this);
				EndpointIdentifier otherEndID = new EndpointIdentifier(this.otherAudJoiStr.container.endpoint,
						mgcpWrapper.getPeerIp() + ":" + mgcpWrapper.getPeerPort());
				DeleteConnection otherDLCX = new DeleteConnection(this, callId, otherEndID, this.otherConnId);
				otherDLCX.setTransactionHandle(this.otherTx);

				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { otherDLCX });
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
			mgcpWrapper.removeListener(jainMgcpComdEve.getTransactionHandle());
			if (noOfRespReceived == 2) {
				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.UNJOINED, false, MediaErr.TIMEOUT, this.errorTxt);
				container.updateUnjoined(joinEvent, this.thisConnId, this.otherConnId, this.otherAudJoiStr.container,
						true);
			}

		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {
		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent respoEve) {
			int respoTx = respoEve.getTransactionHandle();
			noOfRespReceived++;
			switch (respoEve.getObjectIdentifier()) {
			case Constants.RESP_DELETE_CONNECTION:
				processDeleteConnectionResponse((DeleteConnectionResponse) respoEve);
				break;
			default:
				logger.warn(" This RESPONSE is unexpected " + respoEve);
				this.errorTxt += " Unexpected response";
				this.error = true;
				mgcpWrapper.removeListener(respoTx);
				if (noOfRespReceived == 2) {

					JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
							this.thisAudJoiStr, JoinEvent.JOINED, false, MediaErr.UNKNOWN_ERROR, this.errorTxt);
					container.updateUnjoined(joinEvent, this.thisConnId, this.otherConnId,
							this.otherAudJoiStr.container, true);
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
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				if (noOfRespReceived == 2) {
					if (!this.error) {
						this.thisAudJoiStr.audJoinStrVsConnIdMap.remove(this.otherAudJoiStr);
						this.otherAudJoiStr.audJoinStrVsConnIdMap.remove(this.thisAudJoiStr);

						this.thisAudJoiStr.audJoinStrVsDirMap.remove(this.otherAudJoiStr);
						this.otherAudJoiStr.audJoinStrVsDirMap.remove(this.thisAudJoiStr);

						joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
								this.thisAudJoiStr, JoinEvent.UNJOINED, true);
						container.updateUnjoined(joinEvent, this.thisConnId, this.otherConnId,
								this.otherAudJoiStr.container, false);
					} else {
						joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
								this.thisAudJoiStr, JoinEvent.UNJOINED, false, MediaErr.UNKNOWN_ERROR, this.errorTxt);
						container.updateUnjoined(joinEvent, this.thisConnId, this.otherConnId,
								this.otherAudJoiStr.container, true);

					}
				}

				break;
			default:
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				this.error = true;
				this.errorTxt += " - " + responseEvent.getReturnCode().getComment();
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				if (noOfRespReceived == 2) {
					joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
							this.thisAudJoiStr, JoinEvent.UNJOINED, false, MediaErr.UNKNOWN_ERROR, this.errorTxt);
					container.updateUnjoined(joinEvent, this.thisConnId, this.otherConnId,
							this.otherAudJoiStr.container, true);
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
		private ConnectionIdentifier otherConnId = null;
		private int tx = -1;

		ModifyTx(AudioJoinableStream thisAudJoiStr, ConnectionIdentifier thisConnId, Direction thisDir,
				AudioJoinableStream otherAudJoiStr, ConnectionIdentifier otherConnId) {
			this.thisAudJoiStr = thisAudJoiStr;
			this.thisConnId = thisConnId;
			this.thisDir = thisDir;
			this.otherAudJoiStr = otherAudJoiStr;
			this.otherConnId = otherConnId;
		}

		public void run() {
			try {
				this.tx = mgcpWrapper.getUniqueTransactionHandler();
				mgcpWrapper.addListnere(this.tx, this);
				CallIdentifier callId = mediaSession.getCallIdentifier();
				EndpointIdentifier thisEndID = new EndpointIdentifier(this.thisAudJoiStr.container.endpoint,
						mgcpWrapper.getPeerIp() + ":" + mgcpWrapper.getPeerPort());
				ModifyConnection modifyConnection = new ModifyConnection(this, callId, thisEndID, this.thisConnId);

				modifyConnection.setMode(getConnectionMode(this.thisDir));
				modifyConnection.setTransactionHandle(this.tx);
				modifyConnection.setNotifiedEntity(mgcpWrapper.getDefaultNotifiedEntity());
				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { modifyConnection });

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
			mgcpWrapper.removeListener(arg0.getTransactionHandle());
			JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
					this.thisAudJoiStr, JoinEvent.JOINED, false, MediaErr.TIMEOUT,
					"No response from MGW. Tx timed out " + arg0.toString());
			container.updateJoined(joinEvent, this.thisConnId, this.otherConnId, this.otherAudJoiStr.container, true);
		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {

		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {

			switch (jainmgcpresponseevent.getObjectIdentifier()) {

			case Constants.RESP_MODIFY_CONNECTION:
				processMofiyConnectionResponse((ModifyConnectionResponse) jainmgcpresponseevent);
				break;
			default:
				mgcpWrapper.removeListener(jainmgcpresponseevent.getTransactionHandle());
				logger.warn(" This RESPONSE is unexpected " + jainmgcpresponseevent);
				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.JOINED, false, MediaErr.UNKNOWN_ERROR,
						"Received unexpected Response " + jainmgcpresponseevent.toString());
				container.updateJoined(joinEvent, this.thisConnId, this.otherConnId, this.otherAudJoiStr.container,
						true);
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
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
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
						this.thisAudJoiStr, JoinEvent.JOINED, true);
				container.updateJoined(joinEvent, this.thisConnId, this.otherConnId, this.otherAudJoiStr.container,
						false);

				break;
			default:
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);

				joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.JOINED, false, MediaErr.UNKNOWN_ERROR, responseEvent
								.getReturnCode().getComment());
				container.updateJoined(joinEvent, this.thisConnId, this.otherConnId, this.otherAudJoiStr.container,
						true);

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
			this.tx = mgcpWrapper.getUniqueTransactionHandler();
			try {

				mgcpWrapper.addListnere(this.tx, this);
				CallIdentifier callId = mediaSession.getCallIdentifier();
				EndpointIdentifier endpointID = new EndpointIdentifier(this.thisAudJoiStr.container.endpoint,
						mgcpWrapper.getPeerIp() + ":" + mgcpWrapper.getPeerPort());
				EndpointIdentifier secondEndpointID = new EndpointIdentifier(this.otherAudJoiStr.container.endpoint,
						mgcpWrapper.getPeerIp() + ":" + mgcpWrapper.getPeerPort());

				CreateConnection createConnection = new CreateConnection(this, callId, endpointID,
						ConnectionMode.SendRecv);

				createConnection.setSecondEndpointIdentifier(secondEndpointID);
				createConnection.setMode(getConnectionMode(this.thisDir));
				createConnection.setTransactionHandle(this.tx);
				createConnection.setNotifiedEntity(mgcpWrapper.getDefaultNotifiedEntity());
				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { createConnection });
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
			mgcpWrapper.removeListener(jainMgcpCommandEvent.getTransactionHandle());
			JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
					this.thisAudJoiStr, JoinEvent.JOINED, false, MediaErr.TIMEOUT,
					" Request timedout. No response from MGW");
			container.updateJoined(joinEvent, null, null, this.otherAudJoiStr.container, true);
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
				mgcpWrapper.removeListener(jainmgcpresponseevent.getTransactionHandle());
				logger.warn(" This RESPONSE is unexpected " + jainmgcpresponseevent);
				JoinEvent joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.JOINED, false, MediaErr.UNKNOWN_ERROR, jainmgcpresponseevent
								.getReturnCode().getComment());
				container.updateJoined(joinEvent, null, null, this.otherAudJoiStr.container, true);
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
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());

				EndpointIdentifier thisEndpointIdentifier = responseEvent.getSpecificEndpointIdentifier();
				this.thisAudJoiStr.container.endpoint = thisEndpointIdentifier.getLocalEndpointName();

				ConnectionIdentifier thisConnId = responseEvent.getConnectionIdentifier();
				this.thisAudJoiStr.audJoinStrVsConnIdMap.put(this.otherAudJoiStr, thisConnId);

				EndpointIdentifier otherEndpointIdentifier = responseEvent.getSecondEndpointIdentifier();
				this.otherAudJoiStr.container.endpoint = otherEndpointIdentifier.getLocalEndpointName();

				ConnectionIdentifier otherConnId = responseEvent.getSecondConnectionIdentifier();
				this.otherAudJoiStr.audJoinStrVsConnIdMap.put(this.thisAudJoiStr, otherConnId);

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
						this.thisAudJoiStr, JoinEvent.JOINED, true);
				container.updateJoined(joinEvent, thisConnId, otherConnId, this.otherAudJoiStr.container, false);

				break;
			case ReturnCode.ENDPOINT_INSUFFICIENT_RESOURCES:
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				logger.warn("joinInitiate() executed un-successfully for this.endpoint = "
						+ this.thisAudJoiStr.container.endpoint + " this.other.endpoint = "
						+ this.otherAudJoiStr.container.endpoint + " " + responseEvent.getReturnCode().getComment());
				joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.JOINED, false, MediaErr.RESOURCE_UNAVAILABLE, responseEvent
								.getReturnCode().getComment());
				container.updateJoined(joinEvent, null, null, this.otherAudJoiStr.container, true);
				break;
			default:
				mgcpWrapper.removeListener(responseEvent.getTransactionHandle());
				logger.error(" SOMETHING IS BROKEN = " + responseEvent);
				joinEvent = new JoinEventImpl((ResourceContainer) container, this, this.otherAudJoiStr,
						this.thisAudJoiStr, JoinEvent.JOINED, false, MediaErr.UNKNOWN_ERROR, responseEvent
								.getReturnCode().getComment());
				container.updateJoined(joinEvent, null, null, this.otherAudJoiStr.container, true);
				break;

			}
		}
	}
}
