/*
 * File Name     : JainMgcpStackImpl.java
 *
 * The JAIN MGCP API implementaion.
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
package org.mobicents.mgcp.stack;

import jain.protocol.ip.mgcp.CreateProviderException;
import jain.protocol.ip.mgcp.DeleteProviderException;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.JainMgcpStack;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.handlers.EndpointHandlerManager;

/**
 * 
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class JainMgcpStackImpl extends Thread implements JainMgcpStack, EndpointHandlerManager {

	private static final String propertiesFileName = "mgcp-stack.properties";
	private String protocolVersion = "1.0";
	protected int port = 2727;
	private DatagramSocket socket;
	private boolean stopped = true;
	private int executorTableSize = 80;
	private int executorQueueSize = -1;
	private int incomingDataBufferSize = -1;
	private ThreadPoolQueueExecutor[] executors = null;
	private int executorPosition = 0;
	private Logger logger = Logger.getLogger(JainMgcpStackImpl.class);
	// protected ExecutorService jainMgcpStackImplPool =
	// Executors.newFixedThreadPool(50,new
	// JainMgcpStackImpl.ThreadFactoryImpl());

	// For now we have only one provider/delete prvider method wont work.
	public JainMgcpStackProviderImpl provider;
	/**
	 * holds current active transactions (RFC 3435 [$3.2.1.2]: for tx sent &
	 * received).
	 * 
	 */
	private ConcurrentHashMap<Integer, TransactionHandler> localTransactions = new ConcurrentHashMap<Integer, TransactionHandler>();
	private ConcurrentHashMap<Integer, Integer> remoteTxToLocalTxMap = new ConcurrentHashMap<Integer, Integer>();

	private ConcurrentHashMap<Integer, TransactionHandler> completedTransactions = new ConcurrentHashMap<Integer, TransactionHandler>();
	// private ConcurrentSkipListMap<String, EndpointHandler> endpointHandlers =
	// new ConcurrentSkipListMap<String, EndpointHandler>(new
	// StringComparator());
	private SortedMap<String, EndpointHandler> endpointHandlers = Collections
			.synchronizedSortedMap(new TreeMap<String, EndpointHandler>(new StringComparator()));

	// Queue part
	protected LinkedList<PacketRepresentation> rawQueue = new LinkedList<PacketRepresentation>();
	protected ThreadPoolQueueExecutor eventSchedulerExecutor = null;
	protected MessageHandler messageHandler = null;

	// protected Timer tt=new Timer();

	/** Creates a new instance of JainMgcpStackImpl */
	public JainMgcpStackImpl(InetAddress localAddress, int port) {

		this.port = port;

		initExecutors();

		if (socket == null) {
			while (true) {
				try {
					InetSocketAddress bindAddress = new InetSocketAddress(localAddress, port);
					socket = new DatagramSocket(bindAddress);
					logger.info("Jain Mgcp stack bound to IP " + localAddress + " and UDP port " + this.port);
					break;
				} catch (SocketException e) {
					logger.error("Failed to bound to local port " + this.port + ". Caused by", e);
					if (this.port != port + 10) {
						this.port++;
					} else {
						throw new RuntimeException("Failed to find a local port to bound stack");
					}
				}
			}
		}

		stopped = false;
		if (logger.isDebugEnabled()) {
			logger.debug("Starting main thread " + this);
		}

		this.provider = new JainMgcpStackProviderImpl(this);
		this.messageHandler = new MessageHandler(this);
		this.eventSchedulerExecutor.execute(new EventSchedulerTask());
		start();
		// tt.scheduleAtFixedRate(new TimerTask(){

		// @Override
		// public void run() {
		// int count=0;
		// for(EndpointHandler eh:endpointHandlers.values())
		// {
		// logger.info("==["+(count++)+"]--"+socket.getLocalAddress()+":"+socket.getLocalPort()+"-->
		// "+eh);
		// }
		// }}, 10000, 10000);
	}

	private void readProperties() {

		try {
			Properties props = new Properties();
			InputStream is = this.getClass().getResourceAsStream(this.propertiesFileName);
			if (is == null) {
				logger.error("Failed to locate properties file, using default values");
				return;
			}

			String val = null;
			val = props.getProperty("executorTableSize", "" + executorTableSize);
			this.executorTableSize = Integer.parseInt(val);
			val = null;

			val = props.getProperty("executorQueueSize", "" + executorQueueSize);
			this.executorQueueSize = Integer.parseInt(val);
			val = null;

			val = props.getProperty("incomingDataBufferSize", "" + incomingDataBufferSize);
			this.incomingDataBufferSize = Integer.parseInt(val);
			val = null;

		} catch (Exception e) {
			logger.error("Failed to read properties file due to some error, using defualt values!!!!");
		}

	}

	private void initExecutors() {
		this.executors = new ThreadPoolQueueExecutor[this.executorTableSize];
		for (int i = 0; i < this.executors.length; i++) {
			ThreadFactoryImpl th = new ThreadFactoryImpl();
			if (executorQueueSize > 0)
				this.executors[i] = new ThreadPoolQueueExecutor(1, 1, new LinkedBlockingQueue<Runnable>(
						executorQueueSize));
			else
				this.executors[i] = new ThreadPoolQueueExecutor(1, 1, new LinkedBlockingQueue<Runnable>());
			this.executors[i].setThreadFactory(th);
		}
		if (this.incomingDataBufferSize > 0)
			this.eventSchedulerExecutor = new ThreadPoolQueueExecutor(1, 1, new LinkedBlockingQueue<Runnable>(
					executorQueueSize));
		else
			this.eventSchedulerExecutor = new ThreadPoolQueueExecutor(1, 1, new LinkedBlockingQueue<Runnable>());

		ThreadFactoryImpl th = new ThreadFactoryImpl();
		th.setPriority(Thread.MAX_PRIORITY);
		eventSchedulerExecutor.setThreadFactory(th);

	}

	private void terminateExecutors() {
		this.eventSchedulerExecutor.shutdown();
		for (ThreadPoolQueueExecutor tpqe : this.executors) {
			if (tpqe != null) {
				tpqe.shutdown();
			}
		}
	}

	/**
	 * Closes the stack and it's underlying resources.
	 */
	public void close() {
		stopped = true;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Closing socket");
			}
			socket.close();
			// jainMgcpStackImplPool.shutdown();
			terminateExecutors();
		} catch (Exception e) {
			logger.warn("Could not gracefully close socket", e);
		}
	}

	public JainMgcpProvider createProvider() throws CreateProviderException {
		return this.provider;
	}

	public void deleteProvider(JainMgcpProvider provider) throws DeleteProviderException {
		// do nothing
	}

	public void setPort(int port) {
		throw new UnsupportedOperationException("this stack impl doesn't support port reconfiguring");
	}

	public int getPort() {
		return port;
	}

	public InetAddress getAddress() {
		if (this.socket != null) {
			return socket.getLocalAddress();
		} else {
			return null;
		}
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	protected synchronized void send(DatagramPacket packet) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Sending " + packet.getLength() + " bytes to " + packet.getAddress() + " port = "
						+ packet.getPort());
			}

			socket.send(packet);
		} catch (IOException e) {
			logger.error("I/O Exception uccured, caused by", e);
		}
	}

	public boolean isRequest(String header) {
		return header.matches("[\\w]{4}(\\s|\\S)*");
	}

	@Override
	public void run() {
		if (logger.isDebugEnabled()) {
			logger
					.debug("MGCP stack started successfully on " + socket.getLocalAddress() + ":"
							+ socket.getLocalPort());
		}

		byte[] buffer = new byte[86400];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		while (!stopped) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Waiting for packet delivery");
				}
				socket.receive(packet);
			} catch (IOException e) {
				if (stopped) {
					break;
				}
				logger.error("I/O exception occured:", e);
				continue;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Receive " + packet.getLength() + " bytes from " + packet.getAddress() + ":"
						+ packet.getPort());
			}

			// uses now the actual data length from the DatagramPacket
			// instead of the length of the byte[] buffer
			byte[] data = new byte[packet.getLength()];
			System.arraycopy(packet.getData(), 0, data, 0, data.length);

			// MessageHandler handler = new MessageHandler(this, data,
			// packet.getAddress(), packet.getPort());

			// jainMgcpStackImplPool.execute(handler);
			synchronized (rawQueue) {
				rawQueue.add(new PacketRepresentation(data, packet.getAddress(), packet.getPort()));
				rawQueue.notify();
			}

		}

		if (logger.isDebugEnabled()) {
			logger.debug("MGCP stack stopped gracefully");
		}
	}

	protected class EventSchedulerTask implements Runnable {

		boolean runSwitch = true;

		public void run() {

			synchronized (rawQueue) {
				while (runSwitch) {
					if (rawQueue.size() == 0) {
						try {
							rawQueue.wait();
						} catch (InterruptedException e) {

							e.printStackTrace();
							return;
						}
					}

					PacketRepresentation pr = rawQueue.remove();

					messageHandler.scheduleMessages(pr);

				}
			}

		}

		public boolean isRunSwitch() {
			return runSwitch;
		}

		public void setRunSwitch(boolean runSwitch) {
			this.runSwitch = runSwitch;
		}

	}

	public synchronized EndpointHandler getEndpointHandler(String endpointId, boolean useFakeOnWildcard) {

		EndpointHandler eh = null;
		String _endpointId = endpointId.intern();

		if (logger.isDebugEnabled()) {
			for (String key : endpointHandlers.keySet()) {
				logger.debug("-------------" + this.socket.getLocalAddress() + ":" + this.socket.getLocalPort()
						+ "--------------\n" + endpointId + "\n" + key + "\n" + (endpointId.equals(key)) + "\n"
						+ endpointHandlers.containsKey(_endpointId) + "\n---------------------------");
			}
		}
		// In case of fake we always create new EH
		if (useFakeOnWildcard) {
			eh = new EndpointHandler(this, _endpointId);
			eh.setUseFake(true);
			endpointHandlers.put(eh.getFakeId(), eh);
		} else if (!endpointHandlers.containsKey(_endpointId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Adding endpoint handler on:" + this.socket.getLocalAddress() + ":"
						+ this.socket.getLocalPort() + ", using fakeId[" + useFakeOnWildcard + "]  -  " + _endpointId);
			}
			eh = new EndpointHandler(this, _endpointId);
			endpointHandlers.put(_endpointId, eh);

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Fetching endpoint handler on: " + this.socket.getLocalAddress() + ":"
						+ this.socket.getLocalPort() + "  -  " + _endpointId);
			}
			eh = endpointHandlers.get(_endpointId);
		}
		int count = 0;
		if (logger.isDebugEnabled())
			for (String key : endpointHandlers.keySet()) {
				logger.debug("----AA--[" + (count++) + "]-------" + this.socket.getLocalAddress() + ":"
						+ this.socket.getLocalPort() + "-------------- " + key + ": " + endpointHandlers.get(key));
			}

		return eh;
	}

	public synchronized void removeEndpointHandler(String endpointId) {
		EndpointHandler eh = this.endpointHandlers.remove(endpointId.intern());
		if (logger.isDebugEnabled()) {
			logger.debug("Removing EH" + this.socket.getLocalAddress() + ":" + this.socket.getLocalPort() + ": for:"
					+ endpointId + " = " + eh);
		}

	}

	public synchronized EndpointHandler switchMapping(String fakeId, String specificEndpointId) {
		EndpointHandler eh = this.endpointHandlers.get(specificEndpointId);

		if (eh == null) {
			// Well this means we are first, noone has return this before us so
			// we do the switch
			eh = this.endpointHandlers.remove(fakeId);
			this.endpointHandlers.put(specificEndpointId, eh);
			eh.setUseFake(false);
			eh = null;
		}
		int count = 0;
		if (logger.isDebugEnabled())
			for (String key : endpointHandlers.keySet()) {
				logger.debug("----AS--[" + (count++) + "]-------" + this.socket.getLocalAddress() + ":"
						+ this.socket.getLocalPort() + "-------------- " + key + ": " + endpointHandlers.get(key));
			}

		return eh;
	}

	public ThreadPoolQueueExecutor getNextExecutor() {
		return this.executors[(this.executorPosition++) % this.executorTableSize];
	}

	public Map<Integer, TransactionHandler> getLocalTransactions() {
		return localTransactions;
	}

	public Map<Integer, Integer> getRemoteTxToLocalTxMap() {
		return remoteTxToLocalTxMap;
	}

	public Map<Integer, TransactionHandler> getCompletedTransactions() {
		return completedTransactions;
	}

	protected class StringComparator implements Comparator<String> {

		public int compare(String o1, String o2) {
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return o1.compareTo(o2);
		}
	}

	static class ThreadFactoryImpl implements ThreadFactory {

		final ThreadGroup group;
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;
		protected int priority = Thread.NORM_PRIORITY;

		ThreadFactoryImpl() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = "JainMgcpStackImpl-FixedThreadPool-" + "thread-";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 5);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

	}

	/*
	 * public TransactionHandler getLocalTransaction(Integer valueOf) { return
	 * this.loaclTransactions.get(valueOf); }
	 * 
	 * public void addLocalTransaction(Integer localTID,TransactionHandler th) {
	 * this.loaclTransactions.put(localTID, th); }
	 * 
	 * public Integer getLocalTXIDFromRemoteTXID(Integer remoteTxIdIntegere) {
	 * return this.remoteTxToLocalTxMap.get(remoteTxIdIntegere); }
	 * 
	 * public TransactionHandler getCompletedTransaction(Integer
	 * remoteTxIdIntegere) { return this.responseTx.get(remoteTxIdIntegere); }
	 * 
	 * public TransactionHandler removeCompletedTx(Integer key) { return
	 * this.responseTx.remove(key); }
	 * 
	 * public void removeLocalTransaction(Integer valueOf) {
	 * this.loaclTransactions.remove(valueOf);
	 *  }
	 * 
	 * public void removeRemoteTransaction(Integer valueOf) {
	 * this.remoteTxToLocalTxMap.remove(valueOf);
	 *  }
	 * 
	 * public void addCompletedTransaction(Integer valueOf, TransactionHandler
	 * transactionHandler) { this.responseTx.put(valueOf,transactionHandler);
	 *  }
	 * 
	 * public void addRemoteTxIDToLocalTxID(Integer remoteTxID, Integer
	 * localTxID) { this.remoteTxToLocalTxMap.put(remoteTxID, localTxID);
	 *  }
	 */
}
