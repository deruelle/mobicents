package org.mobicents.javax.media.mscontrol.mediagroup;

import java.net.URI;

import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.MediaGroupConfig;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.Recorder;
import javax.media.mscontrol.mediagroup.signals.SignalDetector;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.Symbol;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.AbstractJoinableContainer;
import org.mobicents.javax.media.mscontrol.AudioJoinableStream;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class MediaGroupImpl extends AbstractJoinableContainer implements MediaGroup {
	public static Logger logger = Logger.getLogger(MediaGroupImpl.class);

	protected AudioJoinableStream audioJoinableStream = null;
	protected JainMgcpStackProviderImpl jainMgcpStackProviderImpl = null;

	private static final String LOOP_ENDPOINT_NAME = "media/test/trunk/Loopback/$";

	public MediaGroupImpl(MediaSessionImpl mediaSession, JainMgcpStackProviderImpl jainMgcpStackProviderImpl) {
		super(mediaSession, jainMgcpStackProviderImpl, 1, LOOP_ENDPOINT_NAME);
		this.jainMgcpStackProviderImpl = jainMgcpStackProviderImpl;
	}

	public Player getPlayer() throws MsControlException {
		return null;
	}

	public Recorder getRecorder() throws MsControlException {
		return null;
	}

	public SignalDetector getSignalDetector() throws MsControlException {
		return null;
	}

	public void stop() {

	}

	public void confirm() throws MsControlException {

	}

	public MediaGroupConfig getConfig() {
		return null;
	}

	public <R> R getResource(Class<R> arg0) throws MsControlException {
		return null;
	}

	public void triggerRTC(Symbol arg0) {

	}

	public Parameters createParameters() {
		return null;
	}

	public Parameters getParameters(Symbol[] arg0) {
		return null;
	}

	public URI getURI() {
		return null;
	}

	public void release() {

	}

	public void setParameters(Parameters arg0) {

	}

}
