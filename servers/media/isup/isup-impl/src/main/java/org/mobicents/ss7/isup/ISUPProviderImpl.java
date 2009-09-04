/**
 * Start time:09:18:14 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.ss7.isup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.mobicents.ss7.SS7PayloadListener;
import org.mobicents.ss7.SS7Provider;
import org.mobicents.ss7.isup.message.ISUPMessage;
import org.mobicents.ss7.isup.message.InitialAddressMessage;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;

/**
 * Start time:09:18:14 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
class ISUPProviderImpl implements ISUPProvider, SS7PayloadListener {

	private SS7Provider transportProvider;
	private final List<ISUPListener> listeners = new ArrayList<ISUPListener>();
	private ISUPStackImpl stack;
	private ISUPMessageFactory messageFactory;
	private ConcurrentHashMap transactionMap = new ConcurrentHashMap();

	/**
	 * @param provider
	 * @param stackImpl
	 * @param messageFactoryImpl
	 * 
	 */
	public ISUPProviderImpl(SS7Provider provider, ISUPStackImpl stackImpl, ISUPMessageFactoryImpl messageFactoryImpl) {
		this.transportProvider = provider;
		this.stack = stackImpl;
		this.messageFactory = messageFactoryImpl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPProvider#getTransportProvider()
	 */
	public SS7Provider getTransportProvider() {

		return transportProvider;
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
	void setTransportProvider(SS7Provider mtp) {
		this.transportProvider = mtp;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.ss7.isup.ISUPProvider#createClientTransaction(org.mobicents
	 * .ss7.isup.message.ISUPMessage)
	 */
	public ISUPClientTransaction createClientTransaction(ISUPMessage msg) throws TransactionAlredyExistsException, IllegalArgumentException {
		TransactionKey key = msg.generateTransactionKey();
		if (this.transactionMap.containsKey(key)) {
			throw new TransactionAlredyExistsException("Transaction already exists for key: " + key);
		}
		ISUPClientTransactionImpl ctx = new ISUPClientTransactionImpl(msg, this, this.stack);
		this.transactionMap.put(msg.generateTransactionKey(), ctx);

		return ctx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.ss7.isup.ISUPProvider#createServerTransaction(org.mobicents
	 * .ss7.isup.message.ISUPMessage)
	 */
	public ISUPServerTransaction createServerTransaction(ISUPMessage msg) throws TransactionAlredyExistsException, IllegalArgumentException {
		TransactionKey key = msg.generateTransactionKey();
		if (this.transactionMap.containsKey(key)) {
			throw new TransactionAlredyExistsException("Transaction already exists for key: " + key);
		}
		ISUPServerTransactionImpl stx = new ISUPServerTransactionImpl(msg, this, this.stack);
		this.transactionMap.put(msg.generateTransactionKey(), stx);

		return stx;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPProvider#getMessageFactory()
	 */
	public ISUPMessageFactory getMessageFactory() {
		return this.messageFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.ss7.isup.ISUPProvider#sendMessage(org.mobicents.ss7.isup
	 * .ISUPTransaction)
	 */
	public void sendMessage(ISUPTransaction msg) throws ParameterRangeInvalidException, IOException {
		// TODO Auto-generated method stub

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
	 * @see org.mobicents.ss7.SS7PayloadListener#receivedMessage(byte[])
	 */
	public void receivedMessage(byte[] message) {
		DeliveryHandler dh = new DeliveryHandler(message);
		// possibly we should do here check on timer
		this.stack.getExecutors().schedule(dh, 0, TimeUnit.MICROSECONDS);

	}

	private final class DeliveryHandler implements Runnable {

		private byte[] payload;

		public DeliveryHandler(byte[] payload) {
			super();
			this.payload = payload;

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

				switch (commandCode) {
				case ISUPMessage._MESSAGE_CODE_IAM:
					InitialAddressMessageImpl IAM = new InitialAddressMessageImpl(this);
					IAM.decodeElement(payload);

					for (int li = 0; li < listeners.size(); li++) {
						ISUPListener listener = listeners.get(li);
						if (listener != null) {
							try {
								listener.onIAM(IAM);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					break;
				case ISUPMessage._MESSAGE_CODE_ACM:
					AddressCompleteMessageImpl ACM = new AddressCompleteMessageImpl(this);
					ACM.decodeElement(payload);

					for (int li = 0; li < listeners.size(); li++) {
						ISUPListener listener = listeners.get(li);
						if (listener != null) {
							try {
								listener.onACM(ACM);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					break;
				case ISUPMessage._MESSAGE_CODE_REL:
					ReleaseMessageImpl REL = new ReleaseMessageImpl(this);
					REL.decodeElement(payload);

					for (int li = 0; li < listeners.size(); li++) {
						ISUPListener listener = listeners.get(li);
						if (listener != null) {
							try {
								listener.onREL(REL);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					break;
				case ISUPMessage._MESSAGE_CODE_RLC:
					ReleaseCompleteMessageImpl RLC = new ReleaseCompleteMessageImpl(this);
					RLC.decodeElement(payload);

					for (int li = 0; li < listeners.size(); li++) {
						ISUPListener listener = listeners.get(li);
						if (listener != null) {
							try {
								listener.onRLC(RLC);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					break;
				default:
					throw new IllegalArgumentException("Not supported comamnd code: " + commandCode);
				}

			} catch (Exception e) {
				// FIXME: what should we do here? send back?
				e.printStackTrace();
			}
		}
	}

	// FIXME: should we wait here to get all messages?

	void onTransactionTimeout(ISUPClientTransaction tx) {
		for (ISUPListener l : this.listeners) {
			try {
				l.onTransactionTimeout(tx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.transactionMap.remove(tx.getOriginalMessage().generateTransactionKey());

	}

	void onTransactionTimeout(ISUPServerTransaction tx) {
		for (ISUPListener l : this.listeners) {
			try {
				l.onTransactionTimeout(tx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.transactionMap.remove(tx.getOriginalMessage().generateTransactionKey());
	}

	void onTransactionEnded(ISUPClientTransaction tx) {
		for (ISUPListener l : this.listeners) {
			try {
				l.onTransactionEnded(tx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.transactionMap.remove(tx.getOriginalMessage().generateTransactionKey());
	}

	void onTransactionEnded(ISUPServerTransaction tx) {
		for (ISUPListener l : this.listeners) {
			try {
				l.onTransactionEnded(tx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.transactionMap.remove(tx.getOriginalMessage().generateTransactionKey());
	}

}
