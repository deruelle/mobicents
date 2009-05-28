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
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
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

    private enum TestState {

        Stoped, Terminating, Running;
    }
    private TestState testState = AbstractTestCase.TestState.Stoped;
    public transient final static String _CASE_FILE = "testcase.bin";
    //Yes, it would be good thing to ser
    protected transient SdpFactory sdpFactory;
    protected transient CallDisplayInterface callDisplay;
    protected Map<Long, AbstractCall> callSequenceToCall;
    //We mix view, but this is easier to achieve perf with that.
    protected transient CallStateTableModel model;
    //protected part - some variables that we might use.
    protected InetAddress clientTestNodeAddress;
    protected InetAddress serverJbossBindAddress;
    //some mgcp magic
    protected transient JainMgcpStackImpl stack;
    protected transient JainMgcpStackProviderImpl provider;
    //We need this to map TXID to Call :)
    protected transient Map<Integer, AbstractCall> mgcpTransactionToProxy = new HashMap<Integer, AbstractCall>();
    protected transient Map<String, AbstractCall> requestIdIdToProxy = new HashMap<String, AbstractCall>();
    //timestamp :), its used for files
    protected long testTimesTamp = System.currentTimeMillis();
    protected transient File testDumpDirectory;
    //Call creators
    protected transient final ScheduledExecutorService callCreator = Executors.newSingleThreadScheduledExecutor();
    protected transient ScheduledFuture callCreatorTask;
    //Timer guard:
    protected transient final ScheduledExecutorService timeGuard = Executors.newScheduledThreadPool(5);
    //Some getters
    //Some stats
    protected int ongoingCallNumber;
    protected int errorCallNumber;
    protected int completedCallNumber;
    protected int totalCalls;

    public AbstractTestCase() {
        this.callSequenceToCall = new HashMap<Long, AbstractCall>();
    //model = new CallStateTableModel(this.callSequenceToCall);

    }

    public AbstractTestCase(CallDisplayInterface cdi) throws UnknownHostException, IllegalStateException {
        setCallDisplay(cdi);
        model = new CallStateTableModel(this.callSequenceToCall);

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
        } else if (callState == CallState.IN_ERROR) {
            this.decrementOngoingCall();
            this.incrementErrorCall();
        }

        //System.err.println("updateCallView:"+this.ongoingCallNumber);
        this.callDisplay.updateCallView();
        //this is forterm;
        if(this.testState == TestState.Terminating)
        {
            if(getOngoingCallNumber() == 0)
            {
                this.stop();
            }
        }
    }

    public CallStateTableModel getTableModel() {
        return this.model;
    }

    public int getCompletedCallNumber() {

        return this.completedCallNumber;
    }

    public int getErrorCallNumber() {
        return this.errorCallNumber;
    }

    public int getOngoingCallNumber() {

        return this.ongoingCallNumber;
    }

    public void stop() {

        switch (this.testState) {
            case Terminating:
                try {
                    if (this.provider != null) {
                        try {
                            this.provider.removeJainMgcpListener(this);
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

                    //FIXME: add more?

                    try {
                        for (AbstractCall call : this.callSequenceToCall.values()) {
                            if (call.getState() == CallState.ESTABILISHED || call.getState() == CallState.INITIAL) {
                                call.stop();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    //Now lets serialize.
                    serialize();
                } finally {
                    this.testState = AbstractTestCase.TestState.Stoped;
                }
                break;
            case Running:
                this.testState = AbstractTestCase.TestState.Terminating;
                break;



        }

    }

    public void start() throws CreateProviderException, TooManyListenersException {
        try {

            stop();
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

    public void setCallDisplay(CallDisplayInterface cdi) throws UnknownHostException, IllegalStateException {
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
            //This shoudl not happen, but just in case.
            if (this.testDumpDirectory.isDirectory() && this.testDumpDirectory.canWrite()) {

            } else {
                throw new IllegalStateException("Failed to validate dump dir, its either not writeable or is not a directory: " + this.testDumpDirectory);
            }
        }

    }
    //This method is used on loaded test case
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
        //we changed CPS.
        if (this.callCreatorTask != null) {
            this.callCreatorTask.cancel(true);
        }
        int cps = this.getCallDisplayInterface().getCPS();
        if (cps == 0) {
            return;
        }
        int delta = 1000 / this.getCallDisplayInterface().getCPS();
        //we use delta,delta, cause we dont want sudden rush  in CPS
        this.callCreatorTask = this.callCreator.scheduleAtFixedRate(this, delta, delta, TimeUnit.MILLISECONDS);
    //this.run();

    }

    public void onCallLengthChange() {


    }

    public abstract AbstractCall getNewCall();

    Vector<Attribute> getSDPAttributes() {
        return this.callDisplay.getCodec();
    }

    public SdpFactory getSdpFactory() {
        return this.sdpFactory;
    }

    public File getTestDumpDirectory() {
        return this.testDumpDirectory;
    }

    //run in which we create more calls :)
    public void run() {
        //For some twisted reason constructo does not work...
        //model.setCallData(this.callSequenceToCall);
        if (this.testState == TestState.Running) {
            try {

                //This creates call, which knows how to estabilish itself and how long it should linger on as active.
                AbstractCall c = this.getNewCall();

                this.callSequenceToCall.put(c.getSequence(), c);
                callStateChanged(c);
                c.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //some handy methods
    public JainMgcpStackProviderImpl getProvider() {
        return this.provider;
    }

    //Event handlers
    public void processMgcpCommandEvent(JainMgcpCommandEvent command) {

        //For now we dont care for reqeust sent from MMS
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
            //System.err.println("NO CALL");
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

    //CALL MGMT
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
    /**
     * This method is called after stop, to dump case data.
     */
}
