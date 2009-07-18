/**
 * Start time:09:18:14 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.mobicents.isup.messages.AddressCompleteMessage;
import org.mobicents.isup.messages.ISUPMessage;
import org.mobicents.isup.messages.InitialAddressMessage;
import org.mobicents.isup.messages.ReleaseCompleteMessage;
import org.mobicents.isup.messages.ReleaseMessage;
import org.mobicents.mtp.MTPPayloadListener;
import org.mobicents.mtp.MTPProvider;

/**
 * Start time:09:18:14 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
class ISUPProviderImpl implements ISUPProvider, MTPPayloadListener {

	private MTPProvider transportProvider;
	private final List<ISUPListener> listeners = new ArrayList<ISUPListener>();
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(8);

	/**
	 * 	
	 */
	public ISUPProviderImpl() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPProvider#getTransportProvider()
	 */
	public Object getTransportProvider() {

		return transportProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.ISUPProvider#sendMessage(org.mobicents.isup.messages
	 * .ISUPMessage)
	 */
	public void sendMessage(ISUPMessage msg) throws ParameterRangeInvalidException, IOException {
		if (msg.hasAllMandatoryParameters()) {
			byte[] encoded = msg.encodeElement();
			transportProvider.sendData(encoded);

		} else {
			throw new ParameterRangeInvalidException("Message does not have all madnatory parameters");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.mtp.MTPPayloadListener#dataTransfered(byte[])
	 */
	public void dataTransfered(byte[] conveyedMessage) {

		DeliveryHandler dh = new DeliveryHandler(conveyedMessage, false);
		this.executor.schedule(dh, 0, TimeUnit.MICROSECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.mtp.MTPPayloadListener#dataTimedOut(byte[])
	 */
	public void dataTimedOut(byte[] conveyedMessage) {
		DeliveryHandler dh = new DeliveryHandler(conveyedMessage, true);
		this.executor.schedule(dh, 0, TimeUnit.MICROSECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.ISUPProvider#addListener(org.mobicents.isup.ISUPListener
	 * )
	 */
	public void addListener(ISUPListener listener) {
		listeners.add(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.ISUPProvider#removeListener(org.mobicents.isup.
	 * ISUPListener)
	 */
	public void removeListener(ISUPListener listener) {
		listeners.remove(listener);

	}

	/**
	 * @param mtp
	 */
	void setTransportProvider(MTPProvider mtp) {
		this.transportProvider = mtp;

	}

	private final class DeliveryHandler implements Runnable {

		private byte[] payload;
		private boolean timedOut = false;

		public DeliveryHandler(byte[] payload, boolean timedOut) {
			super();
			this.payload = payload;
			this.timedOut = timedOut;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// byte[] should be only isup message, first byte is command code
			try {
				int commandCode = payload[0];

				ISUPMessage msg;
				switch (commandCode) {
				case InitialAddressMessage._MESSAGE_CODE:
					msg = new InitialAddressMessage(this, payload);
					break;
				case AddressCompleteMessage._MESSAGE_CODE:
					msg = new AddressCompleteMessage(this, payload);
					break;
				case ReleaseMessage._MESSAGE_CODE:
					msg = new ReleaseMessage(this, payload);
					break;
				case ReleaseCompleteMessage._MESSAGE_CODE:
					msg = new ReleaseCompleteMessage(this, payload);
					break;
				default:
					throw new IllegalArgumentException("Not supported comamnd code: " + commandCode);
				}

				if (msg != null) {
					for (int li = 0; li < listeners.size(); li++) {
						ISUPListener listener = listeners.get(li);
						if (listener != null) {
							if (timedOut) {
								listener.onMessageTimeout(msg);
							} else {
								listener.onMessage(msg);
							}

						}
					}

				}
			} catch (Exception e) {
				// FIXME: what should we do here? send back?
				e.printStackTrace();
			}
		}
	}

}
