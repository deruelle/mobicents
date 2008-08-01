/*
 * MsSignalDetectorImpl.java
 *
 * The Simple Media API RA
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

package org.mobicents.mscontrol.impl;

import java.rmi.server.UID;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.mobicents.media.server.impl.common.events.EventCause;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.UnknownSignalException;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsResourceListener;
import org.mobicents.mscontrol.MsSignalGenerator;

/**
 * 
 * @author Oleg Kulikov
 */
public class MsSignalGeneratorImpl implements MsSignalGenerator, NotificationListener {

	private Endpoint endpoint;
	private String endpointName;
	private MsProvider provider;

	private String id = (new UID()).toString();

	private ArrayList<MsResourceListener> listeners = new ArrayList<MsResourceListener>();

	private boolean released = false;

	/** Creates a new instance of MsSignalDetectorImpl */
	public MsSignalGeneratorImpl(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	public MsSignalGeneratorImpl(MsProvider provider, String endpointName) {
		this.provider = provider;
		this.endpointName = endpointName;
		listeners.addAll(provider.getResourceListeners());
	}

	public String getID() {
		return id;
	}

	public void setResourceStateIdle() {
		MsNotifyEventImpl evt = new MsNotifyEventImpl(this, EventID.DTMF, EventCause.NORMAL,
				"Created new MsSignalGenerator");
		for (MsResourceListener listener : listeners) {
			listener.resourceCreated(evt);
		}
	}

	public void apply(EventID signalID, String[] params) {
		new Thread(new PlayTx(this, signalID, params)).start();
	}

	public void update(NotifyEvent event) {
		if (!released) {
			MsNotifyEventImpl evt = new MsNotifyEventImpl(this, event.getID(), event.getCause(), event.getMessage());
			sendEvent(evt);
		}
	}

	private void sendEvent(MsNotifyEvent evt) {
		for (MsResourceListener listener : listeners) {
			listener.update(evt);
		}
	}

	public void addResourceListener(MsResourceListener listener) {
		listeners.add(listener);
	}

	public void removeResourceListener(MsResourceListener listener) {
		listeners.remove(listener);
	}

	public void release() {
		released = true;

		MsNotifyEventImpl evt = new MsNotifyEventImpl(this, EventID.INVALID, EventCause.NORMAL,
				"Inavlidated MsSignalGenerator");
		for (MsResourceListener listener : listeners) {
			listener.resourceInvalid(evt);
		}
	}

	public String toString() {
		return "SignalGenerator[" + id + "]";
	}

	private class PlayTx implements Runnable {
		private EventID signalID;
		private String[] params;
		private MsSignalGeneratorImpl generator;

		public PlayTx(MsSignalGeneratorImpl generator, EventID signalID, String[] params) {
			this.generator = generator;
			this.signalID = signalID;
			this.params = params;
		}

		public void run() {
			// if endpoint is not known yet
			if (endpoint == null) {
				try {
					endpoint = EndpointQuery.find(endpointName);
				} catch (NamingException ex) {
					MsNotifyEvent error = new MsNotifyEventImpl(generator, EventID.FAIL, EventCause.FACILITY_FAILURE,
							ex.getMessage());
					sendEvent(error);
				} catch (ResourceUnavailableException ex) {
					MsNotifyEvent error = new MsNotifyEventImpl(generator, EventID.FAIL, EventCause.FACILITY_FAILURE,
							ex.getMessage());
					sendEvent(error);
				}
			}

			try {
				endpoint.play(signalID, params, null, generator, false, false);
			} catch (UnknownSignalException ex) {
				MsNotifyEvent error = new MsNotifyEventImpl(generator, EventID.FAIL, EventCause.FACILITY_FAILURE, ex
						.getMessage());
				sendEvent(error);
			}
		}
	}
}
