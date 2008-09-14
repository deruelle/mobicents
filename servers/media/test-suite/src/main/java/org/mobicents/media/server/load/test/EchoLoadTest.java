package org.mobicents.media.server.load.test;

import jain.protocol.ip.mgcp.CreateProviderException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.mobicents.media.server.load.test.gui.RunnableImpl;
import org.mobicents.mgcp.stack.JainMgcpStackImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class EchoLoadTest {

	public static final int ECHO_LOAD_TEST = 1;
	public static final int ANNOUNCEMENT_LOAD_TEST = 2;

	private Logger logger = Logger.getLogger(EchoLoadTest.class);

	private int numberOfUA = -1;

	private List<ScheduledFuture<?>> listOfFuture = new ArrayList<ScheduledFuture<?>>();

	private List<JainMgcpStackImpl> listOfJainMgcpStackImpl = new ArrayList<JainMgcpStackImpl>();
	private ScheduledThreadPoolExecutor scheduler;

	private int successfulTask = -1;
	private int errorTask = -1;

	private InetAddress clientMachineIPAddress;
	private String jbossBindAddress;
	private int serverMGCPStackPort = 0;
	private int clientMGCPStackPort = 0;
	private String audioFileToPlay = null;

	private int taskCompletedSuccessfully = 0;
	private int taskCompletedFailure = 0;

	private int testIdentifier = 0;

	public EchoLoadTest(int testIdentifier) {
		this.testIdentifier = testIdentifier;
	}

	public static void main(String args[]) {
		EchoLoadTest test = new EchoLoadTest(EchoLoadTest.ANNOUNCEMENT_LOAD_TEST);
		test.setNumberOfUA(1);
		try {
			InetAddress clientMachineIPAddress = InetAddress.getByName("127.0.0.1");
			test.setClientMachineIPAddress(clientMachineIPAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		test.setJbossBindAddress("127.0.0.1");
		test.setServerMGCPStackPort(2727);
		test.setClientMGCPStackPort(6727);
		test.setAudioFileToPlay("");
		test.test();
	}

	public void test() {

		System.out.println("test() Starting");

		int numberOfUAtemp = getNumberOfUA();

		setScheduler((ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(numberOfUAtemp * 2));

		for (int count = 1; count <= numberOfUAtemp; count++) {
			try {
				JainMgcpStackImpl stack = new JainMgcpStackImpl(clientMachineIPAddress, this.getClientMGCPStackPort()
						+ count);
				listOfJainMgcpStackImpl.add(stack);
				JainMgcpStackProviderImpl provider = (JainMgcpStackProviderImpl) stack.createProvider();

				Runnable ua = null;
				switch (testIdentifier) {
				case EchoLoadTest.ECHO_LOAD_TEST:
//					ua = new UA(count, clientMachineIPAddress, jbossBindAddress, serverMGCPStackPort, audioFileToPlay,
//							provider, this);
					ua = new UA();
					break;

				case EchoLoadTest.ANNOUNCEMENT_LOAD_TEST:
					ua = new AnnouncementUA(count, clientMachineIPAddress, jbossBindAddress, serverMGCPStackPort,
							provider, this);
					break;
				}

				final ScheduledFuture<?> future = getScheduler().scheduleWithFixedDelay(ua, 0, 1, TimeUnit.SECONDS);

				listOfFuture.add(future);

				// Let us go to sleep for 100 ms
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (CreateProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} // end of for loop

		System.out.println("test() returning");

	}

	public void add() {
		System.out.println("add() called");
		try {
			Runnable ua = null;
			numberOfUA++;

			JainMgcpStackImpl stack = new JainMgcpStackImpl(clientMachineIPAddress, this.getClientMGCPStackPort()
					+ numberOfUA);
			listOfJainMgcpStackImpl.add(stack);
			JainMgcpStackProviderImpl provider;

			provider = (JainMgcpStackProviderImpl) stack.createProvider();

			switch (testIdentifier) {
			case EchoLoadTest.ECHO_LOAD_TEST:
//				ua = new UA(numberOfUA, clientMachineIPAddress, jbossBindAddress, serverMGCPStackPort, audioFileToPlay,
//						provider, this);
				ua = new UA();
				break;

			case EchoLoadTest.ANNOUNCEMENT_LOAD_TEST:
				ua = new AnnouncementUA(numberOfUA, clientMachineIPAddress, jbossBindAddress, serverMGCPStackPort,
						provider, this);
				break;
			}
			
			final ScheduledFuture<?> future = getScheduler().scheduleWithFixedDelay(ua, 0, 1, TimeUnit.SECONDS);

			listOfFuture.add(future);
			
		} catch (CreateProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			numberOfUA--;
		}

	}

	public void reduce() {
		System.out.println("reduce() called");

		for (ScheduledFuture<?> future : listOfFuture) {
			if (future.isCancelled()) {
				System.out.println(" This future is Cancelled already " + future);
				continue;

			} else {
				boolean result = future.cancel(false);
				System.out.println("Future " + future + " cancel result = " + result);
				numberOfUA--;
				break;
			}
		}

	}

	public void cancel() {
		for (ScheduledFuture<?> future : listOfFuture) {
			boolean result = future.cancel(false);
			System.out.println("Future " + future + " cancel result = " + result);
		}
		getScheduler().shutdown();

		// TODO : Should we wait for sometime before closing the MGCP Stack?
		for (JainMgcpStackImpl stack : listOfJainMgcpStackImpl) {
			stack.close();
		}

	}

	public int getNumberOfUA() {
		return numberOfUA;
	}

	public void setNumberOfUA(int numberOfUA) {
		this.numberOfUA = numberOfUA;
	}

	public ScheduledThreadPoolExecutor getScheduler() {
		return scheduler;
	}

	private void setScheduler(ScheduledThreadPoolExecutor scheduler) {
		this.scheduler = scheduler;
	}

	public int getSuccessfulTask() {
		return successfulTask;
	}

	public void setSuccessfulTask(int successfulTask) {
		this.successfulTask = successfulTask;
	}

	public int getErrorTask() {
		return errorTask;
	}

	public void setErrorTask(int errorTask) {
		this.errorTask = errorTask;
	}

	public InetAddress getClientMachineIPAddress() {
		return clientMachineIPAddress;
	}

	public void setClientMachineIPAddress(InetAddress clientMachineIPAddress) {
		this.clientMachineIPAddress = clientMachineIPAddress;
	}

	public String getJbossBindAddress() {
		return jbossBindAddress;
	}

	public void setJbossBindAddress(String jbossBindAddress) {
		this.jbossBindAddress = jbossBindAddress;
	}

	public int getServerMGCPStackPort() {
		return serverMGCPStackPort;
	}

	public void setServerMGCPStackPort(int serverMGCPStackPort) {
		this.serverMGCPStackPort = serverMGCPStackPort;
	}

	public String getAudioFileToPlay() {
		return audioFileToPlay;
	}

	public void setAudioFileToPlay(String audioFileToPlay) {
		this.audioFileToPlay = audioFileToPlay;
	}

	public int getTaskCompletedSuccessfully() {
		return taskCompletedSuccessfully;
	}

	public synchronized void addTaskCompletedSuccessfully() {
		this.taskCompletedSuccessfully++;
	}

	public int getTaskCompletedFailure() {
		return taskCompletedFailure;
	}

	public synchronized void addTaskCompletedFailure() {
		this.taskCompletedFailure++;
	}

	public int getClientMGCPStackPort() {
		return clientMGCPStackPort;
	}

	public void setClientMGCPStackPort(int clientMGCPStackPort) {
		this.clientMGCPStackPort = clientMGCPStackPort;
	}
}
