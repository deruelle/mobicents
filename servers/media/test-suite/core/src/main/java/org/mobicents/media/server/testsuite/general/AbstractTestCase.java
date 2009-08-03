/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.testsuite.general;

import jain.protocol.ip.mgcp.CreateProviderException;
import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Notify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sdp.Attribute;
import javax.sdp.SdpFactory;

import org.mobicents.media.server.testsuite.general.file.FileUtils;
import org.mobicents.media.server.testsuite.general.rtp.RtpSocketFactory;
import org.mobicents.media.server.testsuite.general.rtp.RtpSocketFactoryImpl;
import org.mobicents.media.server.testsuite.gui.ext.CallStateTableModel;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 * 
 * @author baranowb
 */
public abstract class AbstractTestCase implements JainMgcpExtendedListener, Runnable, Serializable {

	protected transient Logger logger = Logger.getLogger(this.getClass().getName());
	private TestState testState = TestState.Stoped;
	public transient final static String _CASE_FILE = "testcase.bin";
	public transient final static String _COLLECTIVE_CASE_FILE="graph.txt";
	public static final String _LINE_SEPARATOR;
	static {
		String lineSeparator = System.getProperty("line.separator");
		_LINE_SEPARATOR = lineSeparator;
	}

	public static final int _TURN_OFF_BOUNDRY = -1;
	// Yes, it would be good thing to ser
	protected transient SdpFactory sdpFactory;
	protected transient CallDisplayInterface callDisplay;
	protected Map<Long, AbstractCall> callSequenceToCall;
	
	// We mix view, but this is easier to achieve perf with that.
	protected transient CallStateTableModel model;
	// protected part - some variables that we might use.
	protected InetAddress clientTestNodeAddress;
	protected InetAddress serverJbossBindAddress;
	
	
	
	// timestamp :), its used for files
	protected long testTimesTamp = System.currentTimeMillis();
	protected transient File testDumpDirectory;
	// Call creators
	protected transient final ScheduledExecutorService executors;
	protected transient ScheduledFuture callCreatorTask;
	protected transient ScheduledFuture gracefulStopTask;
	// Timer guard:
	protected transient final ScheduledExecutorService timeGuard;
	// Some getters
	// Some stats
	protected long ongoingCallNumber;
	protected long errorCallNumber;
	protected long completedCallNumber;
	protected long totalCalls;
	protected long maxErrorCallNumber;

	
	protected transient RtpSocketFactory socketFactory;
	
	//FIXME: this will go into MGCP test case, will be removed from here
	// some mgcp magic
	protected transient JainMgcpStackImpl stack;
	protected transient JainMgcpStackProviderImpl provider;
	// We need this to map TXID to Call :)
	protected transient Map<Integer, AbstractCall> mgcpTransactionToProxy = new HashMap<Integer, AbstractCall>();
	protected transient Map<String, AbstractCall> requestIdIdToProxy = new HashMap<String, AbstractCall>();
	
	public AbstractTestCase() {
		this.callSequenceToCall = new HashMap<Long, AbstractCall>();
		// model = new CallStateTableModel(this.callSequenceToCall);
		AbstractCall.resetSequence();
                NamedThreadFactory executorsThreadFactory = new NamedThreadFactory("ExecutorsTestCaseFactory");
                NamedThreadFactory timeGuardThreadFactory = new NamedThreadFactory("GuardThreadFactoryTestCaseFactory");
                executors = Executors.newScheduledThreadPool(3,executorsThreadFactory);
                timeGuard = Executors.newScheduledThreadPool(5,timeGuardThreadFactory);

	}

	public AbstractTestCase(CallDisplayInterface cdi) throws IllegalStateException, SocketException, IOException {
		setCallDisplay(cdi);
		model = new CallStateTableModel(this.callSequenceToCall);
		AbstractCall.resetSequence();
                 NamedThreadFactory executorsThreadFactory = new NamedThreadFactory("ExecutorsTestCaseFactory");
                NamedThreadFactory timeGuardThreadFactory = new NamedThreadFactory("GuardThreadFactoryTestCaseFactory");
                executors = Executors.newScheduledThreadPool(3,executorsThreadFactory);
                timeGuard = Executors.newScheduledThreadPool(5,timeGuardThreadFactory);
                this.init();
                
                
	}

	private void init() throws SocketException, IOException
	{
		if(this.socketFactory==null)
		{
			this.socketFactory = new RtpSocketFactoryImpl();
			this.socketFactory.setTimer(new TimerImpl());
			//this.socketFactory.setPortRange("5000-10000");
			
		}
		
		if(this.socketFactory!=null)
		{
			
			this.socketFactory.setBindAddress(this.callDisplay.getLocalAddress());
			this.socketFactory.setFormatMap(this.callDisplay.getCodecs());
			this.socketFactory.start();
		}
	}
	
	
	
	protected void incrementOngoignCall() {
		this.ongoingCallNumber++;
		this.totalCalls++;
	}

	protected void decrementOngoingCall() {
		this.ongoingCallNumber--;
	}

	protected void incrementErrorCall() {
		this.errorCallNumber++;
	}

	protected void incrementCompletedCall() {

		this.completedCallNumber++;
	}

	public long getTestTimeStamp() {
		return this.testTimesTamp;
	}

	public InetAddress getClientTestNodeAddress() {
		return this.clientTestNodeAddress;
	}

	public InetAddress getServerJbossBindAddress() {
		return this.serverJbossBindAddress;
	}

	public CallDisplayInterface getCallDisplayInterface() {
		return this.callDisplay;
	}

	public AbstractCall getCallBySequence(Long seq) {

		return this.callSequenceToCall.get(seq);
	}

	public void callStateChanged(AbstractCall c) {

		CallState callState = c.getState();

		if (callState == CallState.INITIAL) {

			this.incrementOngoignCall();

		} else if (callState == CallState.ENDED) {
			this.decrementOngoingCall();
			this.incrementCompletedCall();
			// as soon as we end one call, we should try to start another.
			this.checkForCallInit();
		} else if (callState == CallState.IN_ERROR) {
			this.decrementOngoingCall();
			this.incrementErrorCall();
			// as soon as we end one call, we should try to start another.
			this.checkForCallInit();
		}

		// System.err.println("updateCallView:"+this.ongoingCallNumber);
		this.callDisplay.updateCallView();
		// this is forterm;
		if (this.testState == TestState.Terminating) {
			if (getOngoingCallNumber() == 0) {
				this.stop(false);
			}
		}
	}

	public CallStateTableModel getTableModel() {
		return this.model;
	}

	public long getCompletedCallNumber() {

		return this.completedCallNumber;
	}

	public long getErrorCallNumber() {
		return this.errorCallNumber;
	}

	public long getOngoingCallNumber() {

		return this.ongoingCallNumber;
	}

	public void setMaxErrorCallNumber(long v) {
		this.maxErrorCallNumber = v;
	}

	public long getTotalCallNumber() {
		return this.totalCalls;
	}

	
	
	public void stop(boolean onGracefull) {
		
		synchronized (this.testState) {
			switch (this.testState) {
			case Terminating:
				
				if (!onGracefull) {
					return;
				}
				try {
					
					if (this.provider != null) {
						try {
							this.provider.removeJainMgcpListener(this);
							this.stack.deleteProvider(this.provider);
							
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					
					if (this.stack != null) {
						try {
							this.stack.close();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					
					if (callCreatorTask != null) {

						callCreatorTask.cancel(true);
					}
					
					// FIXME: add more?

					try {
						for (AbstractCall call : this.callSequenceToCall.values()) {
							if (call.getState() == CallState.ESTABILISHED || call.getState() == CallState.INITIAL) {
								call.stop();
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						this.socketFactory.stop();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					// Now lets serialize.
					
					serialize();
					dumpSampleTraffic();
				} finally {
					this.testState = TestState.Stoped;
					this.gracefulStopTask = null;
                                        if(this.timeGuard!=null)
                                        {
                                            this.timeGuard.shutdownNow();
                                        }
                                        if(this.executors!=null)
                                        {
                                            this.executors.shutdownNow();
                                        }
                                        
				}
				break;
			case Running:
				
				this.testState = TestState.Terminating;
				
				// this.gracefulStopTask = this.
				// so we dont have to press stop twice, this is stupid.
				
				if (this.gracefulStopTask == null) {
					
					this.gracefulStopTask = this.executors.schedule(new GracefulStopTask(this), this.callDisplay.getCallDuration() + 1000, TimeUnit.MILLISECONDS);
				}
				break;

			default:
				
				break;

			}
		}

	}

	public void start() throws CreateProviderException, TooManyListenersException {
		try {

			stop(false);
			this.clientTestNodeAddress = InetAddress.getByName(this.callDisplay.getLocalAddress());
			this.serverJbossBindAddress = InetAddress.getByName(this.callDisplay.getRemoteAddress());

			this.stack = new JainMgcpStackImpl(this.clientTestNodeAddress, this.callDisplay.getLocalPort());

			this.provider = (JainMgcpStackProviderImpl) this.stack.createProvider();

			this.provider.addJainMgcpListener(this);
			testState = TestState.Running;
			onCPSChange();
		} catch (UnknownHostException ex) {
			Logger.getLogger(AbstractTestCase.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public TestState getTestState() {
		return this.testState;
	}

	public RtpSocketFactory getSocketFactory() {
		return socketFactory;
	}

	public void setCallDisplay(CallDisplayInterface cdi) throws IllegalStateException, SocketException, IOException {
		this.callDisplay = cdi;

		this.clientTestNodeAddress = InetAddress.getByName(this.callDisplay.getRemoteAddress());
		this.serverJbossBindAddress = InetAddress.getByName(this.callDisplay.getRemoteAddress());

		
		this.sdpFactory = SdpFactory.getInstance();
		this.testDumpDirectory = new File(cdi.getDefaultDataDumpDirectory(), "" + this.testTimesTamp);
		
		if (!this.testDumpDirectory.exists()) {

			if (!this.testDumpDirectory.mkdirs()) {
				throw new IllegalStateException("Failed to create dirs: " + this.testDumpDirectory);
			}
		} else {
			// This shoudl not happen, but just in case.
			if (this.testDumpDirectory.isDirectory() && this.testDumpDirectory.canWrite()) {

			} else {
				throw new IllegalStateException("Failed to validate dump dir, its either not writeable or is not a directory: " + this.testDumpDirectory);
			}
		}

		this.init();
		
	}

	// This method is used on loaded test case
	public void setCallDisplay(CallDisplayInterface cdi, File testDumpDirectory) throws UnknownHostException, IllegalStateException {
		this.callDisplay = cdi;
		this.sdpFactory = SdpFactory.getInstance();
		this.testDumpDirectory = testDumpDirectory;
		model = new CallStateTableModel(this.callSequenceToCall);
		for (AbstractCall call : this.callSequenceToCall.values()) {
			call.setDumpDir(testDumpDirectory);
		}

	}

	public void onCPSChange() {

		if (testState == TestState.Stoped) {
			return;
		}
		// we changed CPS.
		if (this.callCreatorTask != null) {
			this.callCreatorTask.cancel(true);
		}
		int cps = this.getCallDisplayInterface().getCPS();
		if (cps == 0) {
			return;
		}
		int delta = 1000 / this.getCallDisplayInterface().getCPS();
		// we use delta,delta, cause we dont want sudden rush in CPS
		this.callCreatorTask = this.executors.scheduleAtFixedRate(this, delta, delta, TimeUnit.MILLISECONDS);
		// this.run();

	}

	public void onCallLengthChange() {

	}

	public abstract AbstractCall getNewCall();

	Vector<Attribute> getSDPAttributes() {
		return this.callDisplay.getCodecs();
	}

	public SdpFactory getSdpFactory() {
		return this.sdpFactory;
	}

	public File getTestDumpDirectory() {
		return this.testDumpDirectory;
	}

	// run in which we create more calls :)
	public void run() {
		// For some twisted reason constructo does not work...
		// model.setCallData(this.callSequenceToCall);
		if (this.testState == TestState.Running) {

			if (this.maxErrorCallNumber != _TURN_OFF_BOUNDRY && this.errorCallNumber >= this.maxErrorCallNumber) {

			}

			if (this.callDisplay.getMaxConcurrentCalls() != _TURN_OFF_BOUNDRY && this.ongoingCallNumber >= this.callDisplay.getMaxConcurrentCalls()) {

				return;
			}
			if (this.callDisplay.getMaxCalls() != _TURN_OFF_BOUNDRY && this.totalCalls == this.callDisplay.getMaxCalls()) {

				this.stop(false);
				return;
			}
			try {

				// This creates call, which knows how to estabilish itself and
				// how long it should linger on as active.
				AbstractCall c = this.getNewCall();

				this.callSequenceToCall.put(c.getSequence(), c);
				callStateChanged(c);
				c.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checkForCallInit() {
		if (this.callDisplay.getMaxConcurrentCalls() != -1 && this.callDisplay.getCPS() > 0) {
			try {
				run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// some handy methods
	public JainMgcpStackProviderImpl getProvider() {
		return this.provider;
	}

	// Event handlers
	public void processMgcpCommandEvent(JainMgcpCommandEvent command) {

		// For now we dont care for reqeust sent from MMS
		if (command instanceof Notify) {
			Notify notify = (Notify) command;
			AbstractCall cp = getCall(notify.getRequestIdentifier().toString());

			if (cp != null) {
				cp.processMgcpCommandEvent(command);
			}
		}
	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent response) {
		try {

			AbstractCall cp = getCall(response);
			if (cp != null) {
				cp.processMgcpResponseEvent(response);
			} else {
				// System.err.println("NO CALL");
			}
		} catch (RuntimeException re) {
			re.printStackTrace();
		}

	}

	public void transactionEnded(int arg0) {
		AbstractCall cp = getCall(arg0);
		if (cp != null) {
			cp.transactionEnded(arg0);
		} else {
			logger.severe("No call proxy for txid: " + arg0);
		}

	}

	public void transactionRxTimedOut(JainMgcpCommandEvent commandTimedOut) {
		AbstractCall cp = getCall(commandTimedOut);
		if (cp != null) {
			cp.transactionRxTimedOut(commandTimedOut);
		} else {
			logger.severe("No call proxy for txid: " + commandTimedOut.getTransactionHandle() + " for timed out event");
		}

	}

	public void transactionTxTimedOut(JainMgcpCommandEvent commandTimeOut) {
		AbstractCall cp = getCall(commandTimeOut);
		if (cp != null) {
			cp.transactionTxTimedOut(commandTimeOut);
		} else {
			logger.severe("No call proxy for txid: " + commandTimeOut.getTransactionHandle() + " for timed out event2");
		}
	}

	// CALL MGMT
	protected AbstractCall getCall(JainMgcpEvent mgcpEvent) {
		return this.mgcpTransactionToProxy.get(mgcpEvent.getTransactionHandle());
	}

	protected AbstractCall getCall(int txID) {
		return this.mgcpTransactionToProxy.get(txID);
	}

	public void removeCall(JainMgcpEvent mgcpEvent) {
		this.removeCall(mgcpEvent.getTransactionHandle());
	}

	public void removeCall(int txID) {
		this.mgcpTransactionToProxy.remove(txID);
	}

	public void addCall(String ri, AbstractCall cp) {
		this.requestIdIdToProxy.put(ri, cp);

	}

	public void removeCall(String ri) {
		this.requestIdIdToProxy.remove(ri);
	}

	public AbstractCall getCall(String ri) {
		return this.requestIdIdToProxy.get(ri);
	}

	public void addCall(JainMgcpEvent mgcpEvent, AbstractCall cp) {

		this.mgcpTransactionToProxy.put(mgcpEvent.getTransactionHandle(), cp);
	}

	/**
	 * Custom deserialization is needed.
	 */
	private void readObject(ObjectInputStream aStream) throws IOException, ClassNotFoundException {
		aStream.defaultReadObject();

	}

	/**
	 * Perofrms all serialization actions
	 */
	protected void serialize() {
		FileUtils.serializeTestCase(this);
	}

	/**
	 * Custom serialization is needed.
	 */
	private void writeObject(ObjectOutputStream aStream) throws IOException {
		aStream.defaultWriteObject();

	}

	private void dumpSampleTraffic()
	{
		
		if(this.callSequenceToCall!=null && this.callSequenceToCall.size()>0)
		{
			int index0 = this.callSequenceToCall.size()/2;
			Iterator<Long> seqIterator= this.callSequenceToCall.keySet().iterator();
			while(index0>0)
			{

				//seqIterator.next();
				index0--;
			}
			
			
			AbstractCall call = null;
			while(call==null && seqIterator.hasNext())
			{
				Long seq = seqIterator.next();
				
				call = this.callSequenceToCall.get(seq);
				if(call.getState()==CallState.IN_ERROR || call.getState() == CallState.TIMED_OUT)
				{
					call = null;
					continue;
				}else
				{
					break;
				}
			}
			
			if(call!=null)
			{
				//on end file dumps traffic along with jitter to this file, saves a lot of place, allows us to make mooore calls
				File callDumpFile = call.getGraphDataFileName();
				
				FileInputStream inputChannel=null;
				FileOutputStream outputChannel=null;
				try {
					inputChannel = new FileInputStream(callDumpFile);
					outputChannel = new FileOutputStream(new File(this.getTestDumpDirectory(),_COLLECTIVE_CASE_FILE));
					outputChannel.write((this.getCallDisplayInterface().getCallDuration()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					outputChannel.write((this.getCallDisplayInterface().getCPS()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					outputChannel.write((this.getCallDisplayInterface().getMaxConcurrentCalls()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					outputChannel.write((this.getCallDisplayInterface().getMaxFailCalls()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					outputChannel.write((this.getErrorCallNumber()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					outputChannel.write((this.getCallDisplayInterface().getMaxCalls()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					outputChannel.write((this.getTotalCallNumber()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					
					
					//outputChannel.write((call.getCallID().toString().getBytes()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					outputChannel.write((call.getSequence()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					outputChannel.write((call.getAvgJitter()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					outputChannel.write((call.getPeakJitter()+AbstractTestCase._LINE_SEPARATOR).getBytes());
					
					while(inputChannel.available()>0)
					{
						byte[] b = new byte[inputChannel.available()];
						int read=inputChannel.read(b);
						//FIXME: should we add more 
						outputChannel.write(b,0,read);
					}
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(inputChannel!=null)
					{
						try {
							inputChannel.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
					if(outputChannel!=null)
					{
						try {
							outputChannel.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(inputChannel!=null)
					{
						try {
							inputChannel.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
					if(outputChannel!=null)
					{
						try {
							outputChannel.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}else
			{
				logger.severe("Failed to find call to add data to collective dump file.");
			}
			
		}
		
	}
	
	
	/**
	 * This method is called after stop, to dump case data.
	 */
	private class GracefulStopTask implements Runnable {

		private AbstractTestCase atc;

		public GracefulStopTask(AbstractTestCase atc) {
			super();
			this.atc = atc;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			atc.stop(true);

		}
	}

	
}
