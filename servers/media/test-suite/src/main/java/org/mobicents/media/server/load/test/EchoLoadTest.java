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
	private String audioFileToPlay = null;
	
	private int taskCompletedSuccessfully = 0;
	private int taskCompletedFailure = 0;

	public static void main(String args[]) {
		EchoLoadTest test = new EchoLoadTest();
		


		test.setNumberOfUA(1);
		try {
			InetAddress clientMachineIPAddress = InetAddress.getByName("127.0.0.1");
			test.setClientMachineIPAddress(clientMachineIPAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		test.setJbossBindAddress("127.0.0.1");
		test.setServerMGCPStackPort(2727);
		test.setAudioFileToPlay("");
		
		test.test();
	}

	public void test() {

		System.out.println("test() Starting");

		int numberOfUAtemp = getNumberOfUA();

		setScheduler((ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(numberOfUAtemp * 2));

		for (int count = 1; count <= numberOfUAtemp; count++) {
			try {
				JainMgcpStackImpl stack = new JainMgcpStackImpl(2728 + count);
				listOfJainMgcpStackImpl.add(stack);
				JainMgcpStackProviderImpl provider = (JainMgcpStackProviderImpl)stack.createProvider();
				
				UA ua = new UA(count, clientMachineIPAddress, jbossBindAddress, serverMGCPStackPort,
						audioFileToPlay, provider, this);

				final ScheduledFuture<?> future = getScheduler().scheduleWithFixedDelay(ua, 0, 1, TimeUnit.SECONDS);

				listOfFuture.add(future);
			} catch (CreateProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println("test() returning");

	}

	public void add() {
		System.out.println("add() called");
		RunnableImpl runnableImpl = new RunnableImpl(1000 * 4);
		final ScheduledFuture<?> future = getScheduler().scheduleWithFixedDelay(runnableImpl, 0, 1, TimeUnit.SECONDS);

		listOfFuture.add(future);
		numberOfUA++;

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
		
		
		//TODO : Should we wait for sometime before closing the MGCP Stack?
		for(JainMgcpStackImpl stack : listOfJainMgcpStackImpl){
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
		logger.debug("The value of taskCompletedFailure =  "+taskCompletedFailure);
		this.taskCompletedFailure = this.taskCompletedFailure + 1; 
	}
}
