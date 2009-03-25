package org.mobicents.javax.media.mscontrol;

import java.io.Serializable;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.JoinableContainer;
import javax.media.mscontrol.JoinableStream;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.StatusEventListener;
import javax.media.mscontrol.JoinableStream.StreamType;

import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public abstract class AbstractJoinableContainer implements JoinableContainer {

	private final String id = (new UID()).toString();
	protected MediaSessionImpl mediaSession = null;
	protected CopyOnWriteArrayList<StatusEventListener> statusEventListenerList = new CopyOnWriteArrayList<StatusEventListener>();

	protected AudioJoinableStream audioJoinableStream = null;
	protected ConcurrentHashMap<AbstractJoinableContainer, Direction> abstractJoinableContainerList = new ConcurrentHashMap<AbstractJoinableContainer, Direction>();
	protected String endpoint = null;
	protected JainMgcpStackProviderImpl jainMgcpStackProviderImpl;

	protected int maxJoinees = 1;

	public AbstractJoinableContainer(MediaSessionImpl mediaSession,
			JainMgcpStackProviderImpl jainMgcpStackProviderImpl, int maxJoinees, String endpointName) {
		this.mediaSession = mediaSession;
		this.jainMgcpStackProviderImpl = jainMgcpStackProviderImpl;
		this.maxJoinees = maxJoinees;
		this.endpoint = endpointName;
	}

	protected String getId() {
		return this.id;
	}

	public JoinableStream getJoinableStream(StreamType value) throws MsControlException {
		if (value.equals(StreamType.audio)) {
			return audioJoinableStream;
		}
		return null;
	}

	public JoinableStream[] getJoinableStreams() throws MsControlException {
		return new JoinableStream[] { audioJoinableStream };
	}

	public Joinable[] getJoinees() throws MsControlException {
		Joinable[] j = new Joinable[abstractJoinableContainerList.size()];
		int count = 0;
		for (AbstractJoinableContainer a : abstractJoinableContainerList.keySet()) {
			j[count] = a;
			count++;
		}
		return j;
	}

	public Joinable[] getJoinees(Direction direction) throws MsControlException {
		List<AbstractJoinableContainer> absJoinableContList = new ArrayList<AbstractJoinableContainer>();
		for (AbstractJoinableContainer key : abstractJoinableContainerList.keySet()) {
			Direction d = abstractJoinableContainerList.get(key);
			if (d.equals(direction) || d.equals(Direction.DUPLEX)) {
				absJoinableContList.add(key);
			}
		}

		Joinable[] j = new Joinable[absJoinableContList.size()];
		int count = 0;
		for (AbstractJoinableContainer a : absJoinableContList) {
			j[count] = a;
			count++;
		}

		return j;
	}

	public void join(Direction direction, Joinable other) throws MsControlException {

	}

	public void joinInitiate(Direction direction, Joinable other, Serializable context) throws MsControlException {
		if (other.equals(this)) {
			throw new MsControlException("Container cannot join to itself");
		}

		if (this.audioJoinableStream == null) {
			this.audioJoinableStream = new AudioJoinableStream(this);
		}
		AbstractJoinableContainer absJoiConOther = (AbstractJoinableContainer) other;
		if (absJoiConOther.audioJoinableStream == null) {
			absJoiConOther.audioJoinableStream = new AudioJoinableStream(absJoiConOther);
		}
		this.audioJoinableStream.joinInitiate(direction, absJoiConOther.audioJoinableStream, context);
	}

	public void unjoin(Joinable other) throws MsControlException {

	}

	public void unjoinInitiate(Joinable other, Serializable context) throws MsControlException {
		if (this.audioJoinableStream == null) {
			throw new MsControlException("No stream present with this container for unjoin");
		}
		AbstractJoinableContainer absJoiConOther = (AbstractJoinableContainer) other;
		if (absJoiConOther.audioJoinableStream == null) {
			throw new MsControlException("No stream present with other container for unjoin");
		}

		this.audioJoinableStream.unjoinInitiate(absJoiConOther.audioJoinableStream, context);
	}

	public void addListener(StatusEventListener listener) {
		statusEventListenerList.add(listener);
	}

	public MediaSession getMediaSession() {
		return this.mediaSession;
	}

	public void removeListener(StatusEventListener listener) {
		statusEventListenerList.remove(listener);
	}

	public CopyOnWriteArrayList<StatusEventListener> getStatusEventListenerList() {
		return this.statusEventListenerList;
	}

}
