/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.ivr;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.ann.AnnouncementSignal;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.events.EventCause;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.jmf.recorder.Recorder;
import org.mobicents.media.server.impl.jmf.recorder.RecorderEvent;
import org.mobicents.media.server.impl.jmf.recorder.RecorderListener;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author Oleg Kulikov
 */
public class PlayRecordSignal extends AnnouncementSignal implements RecorderListener {

	// private IVREndpointImpl endpoint;
	private String[] params;
	private BaseConnection connection;
	private NotificationListener listener;
	private AnnouncementSignal annSignal;
	private Recorder recorder;
	private Logger logger = Logger.getLogger(PlayRecordSignal.class);

	public PlayRecordSignal(IVREndpointImpl endpoint, NotificationListener listener, String params[]) {
		super(endpoint, listener, params);
		this.params = params;
		this.listener = listener;
		connection = endpoint.getConnections().iterator().next();
	}

	@Override
	public void start() {
		String announcement = params[0];
		String recordDir = ((IVREndpointImpl) endpoint).getRecordDir();
		String recordURL = null;

		if (recordDir != null) {
			String param1 = params[1];
			int index = param1.lastIndexOf("/");
			if (index > 0) {
				String folderStructure = param1.substring(0, index);

				java.io.File file = new java.io.File(new StringBuffer(recordDir).append("/").append(folderStructure)
						.toString());
				boolean fileCreationSuccess = file.mkdirs();
			}
			recordURL = recordDir + "/" + param1;
		} else {
			recordURL = params[1];
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Announcement url=" + announcement + ", record=" + recordURL);
		}

		if (announcement != null) {
			annSignal = new AnnouncementSignal(endpoint, listener, params);
			annSignal.start();
		}

		if (recordURL != null) {
			logger.info("Starting recording to, url=" + recordURL);
			recorder = new Recorder(((IVREndpointImpl) endpoint).mediaType);

			LocalSplitter splitter = (LocalSplitter) endpoint.getResource(MediaResourceType.AUDIO_SINK, connection
					.getId());
			recorder.start(recordURL, splitter.newBranch("Recorder"));
		}
	}

	@Override
	public void stop() {
		LocalSplitter splitter = (LocalSplitter) endpoint.getResource(MediaResourceType.AUDIO_SINK, connection.getId());
		if (splitter != null) {
			splitter.remove("Recorder");
		}

		if (recorder != null) {
			recorder.stop();
		}

		if (annSignal != null) {
			annSignal.stop();
		}
	}

	public void update(RecorderEvent evt) {
		switch (evt.getEventType()) {
		case STARTED:
			break;
		case STOP_BY_REQUEST:
			NotifyEvent notify = new NotifyEvent(this, EventID.COMPLETE, EventCause.NORMAL, "");
			sendEvent(notify);
			break;
		case FACILITY_ERROR:
			notify = new NotifyEvent(this, EventID.FAIL, EventCause.FACILITY_FAILURE, evt.getMessage());
			sendEvent(notify);
			break;
		}
	}
}
