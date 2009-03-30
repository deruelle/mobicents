package org.mobicents.javax.media.mscontrol.mediagroup;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.PlayerEvent;
import javax.media.mscontrol.resource.MediaEvent;
import javax.media.mscontrol.resource.MediaEventListener;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.RTC;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.MediaObjectState;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.jsr309.mgcp.MgcpWrapper;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;

public class PlayerImpl implements Player {

	private static Logger logger = Logger.getLogger(PlayerImpl.class);
	protected MediaGroupImpl mediaGroup = null;
	protected CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>> mediaEventListenerList = new CopyOnWriteArrayList<MediaEventListener<? extends MediaEvent<?>>>();

	protected MediaSessionImpl mediaSession = null;
	protected MgcpWrapper mgcpWrapper = null;

	protected PlayerImpl(MediaGroupImpl mediaGroup, MgcpWrapper mgcpWrapper) throws MsControlException {
		this.mediaGroup = mediaGroup;
		this.mediaSession = (MediaSessionImpl) mediaGroup.getMediaSession();
		this.mgcpWrapper = mgcpWrapper;
	}

	public void play(URI[] arg0, RTC[] arg1, Parameters arg2) throws MsControlException {

		if (MediaObjectState.JOINED.equals(this.mediaGroup.getState())) {

		} else {
			throw new MsControlException(this.mediaGroup.getURI() + " Container is not joined to any other container");
		}
	}

	public void play(URI arg0, RTC[] arg1, Parameters arg2) throws MsControlException {
		this.play(new URI[] { arg0 }, arg1, arg2);
	}

	public MediaGroup getContainer() {
		return this.mediaGroup;
	}

	public void stop() {
		// TODO Auto-generated method stub

	}

	public void addListener(MediaEventListener<PlayerEvent> listener) {
		this.mediaEventListenerList.add(listener);
	}

	public void removeListener(MediaEventListener<PlayerEvent> listener) {
		this.mediaEventListenerList.remove(listener);
	}

	public MediaSession getMediaSession() {
		return this.mediaGroup.getMediaSession();
	}

	private class NtfyTx implements Runnable, JainMgcpExtendedListener {
		private int tx = -1;
		private RequestIdentifier reqId = null;
		private PlayerImpl player = null;
		private URI[] files = null;

		NtfyTx(PlayerImpl player, URI[] files) {
			this.player = player;
			this.files = files;
		}

		public void run() {
			this.tx = mgcpWrapper.getUniqueTransactionHandler();
			try {

				mgcpWrapper.addListnere(this.tx, this);
				CallIdentifier callId = mediaSession.getCallIdentifier();

				EndpointIdentifier endpointID = new EndpointIdentifier(mediaGroup.getEndpoint(), mgcpWrapper
						.getPeerIp()
						+ ":" + mgcpWrapper.getPeerPort());

				reqId = mgcpWrapper.getUniqueRequestIdentifier();

				NotificationRequest notificationRequest = new NotificationRequest(this, endpointID, reqId);
				ConnectionIdentifier connId = mediaGroup.thisConnId;

				EventName[] signalRequests = new EventName[files.length];
				int count = 0;
				for (URI uri : files) {
					String filePath = uri.toString();
					signalRequests[count] = new EventName(PackageName.Announcement, MgcpEvent.ann.withParm(filePath),
							connId);
					count++;
				}

				notificationRequest.setSignalRequests(signalRequests);

				RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };

				RequestedEvent[] requestedEvents = {
						new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.oc, connId),
								actions),
						new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.of, connId),
								actions) };
				
				notificationRequest.setRequestedEvents(requestedEvents);
				notificationRequest.setTransactionHandle(this.tx);
				
				mgcpWrapper.sendMgcpEvents(new JainMgcpEvent[] { notificationRequest });

			} catch (Exception e) {
				logger.error(e);
			}

		}

		public void transactionEnded(int arg0) {
			// TODO Auto-generated method stub

		}

		public void transactionRxTimedOut(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void transactionTxTimedOut(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void processMgcpCommandEvent(JainMgcpCommandEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void processMgcpResponseEvent(JainMgcpResponseEvent arg0) {
			// TODO Auto-generated method stub

		}

	}

}
