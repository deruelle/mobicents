package org.mobicents.javax.media.mscontrol;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.JoinableContainer;
import javax.media.mscontrol.JoinableStream;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.StatusEventListener;
import javax.media.mscontrol.JoinableStream.StreamType;

public abstract class AbstractJoinableContainer implements JoinableContainer {
	
	protected MediaSessionImpl mediaSession = null;
	protected CopyOnWriteArrayList<StatusEventListener> statusEventListenerList = new CopyOnWriteArrayList<StatusEventListener>(); 
	
	public AbstractJoinableContainer(MediaSessionImpl mediaSession){
		this.mediaSession = mediaSession;
	}

	public JoinableStream getJoinableStream(StreamType value) throws MsControlException {		
		return null;
	}

	public JoinableStream[] getJoinableStreams() throws MsControlException {		
		return null;
	}

	public Joinable[] getJoinees() throws MsControlException {
		return null;
	}

	public Joinable[] getJoinees(Direction direction) throws MsControlException {
		return null;
	}

	public void join(Direction direction, Joinable other) throws MsControlException {

	}

	public void joinInitiate(Direction direction, Joinable other, Serializable context) throws MsControlException {

	}

	public void unjoin(Joinable other) throws MsControlException {

	}

	public void unjoinInitiate(Joinable other, Serializable context) throws MsControlException {

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
	
	public CopyOnWriteArrayList<StatusEventListener> getStatusEventListenerList(){
		return this.statusEventListenerList;
	}

}
