/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */

package org.mobicents.media.server.impl.resource.ss7;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author kulikov
 */
public class Mtp2 implements Runnable {
    private final static int TIMEOUT_1 = 65000;
    private final static int TIMEOUT_2 = 5000;
    private final static int TIMEOUT_3 = 20000;
//    private final static int TIMER_T3 = 2000;
    private final static int TIMEOUT_4_NORMAL = 9500;
    private final static int TIMEOUT_4_EMERGENCY = 500;
    
    private final static int fcstab[] = new int[]{
        0x0000, 0x1189, 0x2312, 0x329b, 0x4624, 0x57ad, 0x6536, 0x74bf,
        0x8c48, 0x9dc1, 0xaf5a, 0xbed3, 0xca6c, 0xdbe5, 0xe97e, 0xf8f7,
        0x1081, 0x0108, 0x3393, 0x221a, 0x56a5, 0x472c, 0x75b7, 0x643e,
        0x9cc9, 0x8d40, 0xbfdb, 0xae52, 0xdaed, 0xcb64, 0xf9ff, 0xe876,
        0x2102, 0x308b, 0x0210, 0x1399, 0x6726, 0x76af, 0x4434, 0x55bd,
        0xad4a, 0xbcc3, 0x8e58, 0x9fd1, 0xeb6e, 0xfae7, 0xc87c, 0xd9f5,
        0x3183, 0x200a, 0x1291, 0x0318, 0x77a7, 0x662e, 0x54b5, 0x453c,
        0xbdcb, 0xac42, 0x9ed9, 0x8f50, 0xfbef, 0xea66, 0xd8fd, 0xc974,
        0x4204, 0x538d, 0x6116, 0x709f, 0x0420, 0x15a9, 0x2732, 0x36bb,
        0xce4c, 0xdfc5, 0xed5e, 0xfcd7, 0x8868, 0x99e1, 0xab7a, 0xbaf3,
        0x5285, 0x430c, 0x7197, 0x601e, 0x14a1, 0x0528, 0x37b3, 0x263a,
        0xdecd, 0xcf44, 0xfddf, 0xec56, 0x98e9, 0x8960, 0xbbfb, 0xaa72,
        0x6306, 0x728f, 0x4014, 0x519d, 0x2522, 0x34ab, 0x0630, 0x17b9,
        0xef4e, 0xfec7, 0xcc5c, 0xddd5, 0xa96a, 0xb8e3, 0x8a78, 0x9bf1,
        0x7387, 0x620e, 0x5095, 0x411c, 0x35a3, 0x242a, 0x16b1, 0x0738,
        0xffcf, 0xee46, 0xdcdd, 0xcd54, 0xb9eb, 0xa862, 0x9af9, 0x8b70,
        0x8408, 0x9581, 0xa71a, 0xb693, 0xc22c, 0xd3a5, 0xe13e, 0xf0b7,
        0x0840, 0x19c9, 0x2b52, 0x3adb, 0x4e64, 0x5fed, 0x6d76, 0x7cff,
        0x9489, 0x8500, 0xb79b, 0xa612, 0xd2ad, 0xc324, 0xf1bf, 0xe036,
        0x18c1, 0x0948, 0x3bd3, 0x2a5a, 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e,
        0xa50a, 0xb483, 0x8618, 0x9791, 0xe32e, 0xf2a7, 0xc03c, 0xd1b5,
        0x2942, 0x38cb, 0x0a50, 0x1bd9, 0x6f66, 0x7eef, 0x4c74, 0x5dfd,
        0xb58b, 0xa402, 0x9699, 0x8710, 0xf3af, 0xe226, 0xd0bd, 0xc134,
        0x39c3, 0x284a, 0x1ad1, 0x0b58, 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c,
        0xc60c, 0xd785, 0xe51e, 0xf497, 0x8028, 0x91a1, 0xa33a, 0xb2b3,
        0x4a44, 0x5bcd, 0x6956, 0x78df, 0x0c60, 0x1de9, 0x2f72, 0x3efb,
        0xd68d, 0xc704, 0xf59f, 0xe416, 0x90a9, 0x8120, 0xb3bb, 0xa232,
        0x5ac5, 0x4b4c, 0x79d7, 0x685e, 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a,
        0xe70e, 0xf687, 0xc41c, 0xd595, 0xa12a, 0xb0a3, 0x8238, 0x93b1,
        0x6b46, 0x7acf, 0x4854, 0x59dd, 0x2d62, 0x3ceb, 0x0e70, 0x1ff9,
        0xf78f, 0xe606, 0xd49d, 0xc514, 0xb1ab, 0xa022, 0x92b9, 0x8330,
        0x7bc7, 0x6a4e, 0x58d5, 0x495c, 0x3de3, 0x2c6a, 0x1ef1, 0x0f78
    };
    
    private final static int RX_TX_BUFF_SIZE = 16;
    
    private final static int FRAME_STATUS_INDICATION_O = 0;
    private final static int FRAME_STATUS_INDICATION_N = 1;
    private final static int FRAME_STATUS_INDICATION_E = 2;
    private final static int FRAME_STATUS_INDICATION_OS = 3;
    private final static int FRAME_STATUS_INDICATION_PO = 4;
    private final static int FRAME_STATUS_INDICATION_B = 5;
    private final static int FRAME_FISU = 6;
    
    private final static int MTP2_READY = 1;
    private final static int MTP2_INSERVICE = 2;
    private final static int MTP2_NOT_ALIGNED = 3;
    private final static int MTP2_ALIGNED = 4;
    private final static int MTP2_PROVING = 5;
    private final static int MTP2_OUT_OF_SERVICE = 0;
    
    public final static int NORMAL_AERM_THRESHOLD = 1;
    public final static int EMERGENCY_AERM_THRESHOLD = 1;
    
    private int state;
    private Mtp1 layer1;
    
    private int span;
    private int timeslot;
    
    private boolean started;
    
    private FastHDLC hdlc = new FastHDLC();
    private HdlcState rxState = new HdlcState();
    private HdlcState txState = new HdlcState();
        
    private int rxLen = 0;
    private int txLen = 0;
    private int txOffset = 0;
    
    private int sendBSN;
    private int sendBIB;
    private int sendFIB;
    private int lastSentSN = 0;
    
    private byte[] txBuffer = new byte[RX_TX_BUFF_SIZE];
    private byte[] rxBuffer = new byte[RX_TX_BUFF_SIZE];
    
    private int[] rxFrame = new int[279];
    private byte[] txFrame = new byte[279];

    private TxQueue txQueue = new TxQueue();
    
    private int doCRC = 0;
    private int rxCRC = 0xffff;
    private int txCRC = 0xffff;
    
    private int rxFSN = 127;
    private int rxBSN = 127;
    
    private int txFSN = 127;
    private int txBSN = 127;
    
    private int rxFIB = 1;
    private int rxBIB = 1;
    
    private int txFIB = 1;
    private int txBIB = 1;
    
    private String name;
    private Logger logger = Logger.getLogger(Mtp2.class);
    
    
    private final MTPThreadFactoryImpl threadFactory = new MTPThreadFactoryImpl();
    private final ScheduledExecutorService mtpThread = Executors.newSingleThreadScheduledExecutor(threadFactory);

    private final static MTPTimerThreadFactoryImpl timerThreadFactory = new MTPTimerThreadFactoryImpl();
    protected final static ScheduledExecutorService mtpTimer = Executors.newSingleThreadScheduledExecutor(timerThreadFactory);
    
    private int POLL_DELAY = 2;
    private ScheduledFuture poll;
    
    private int aermThreshold;
    
    private boolean emergency = false;
    
    private T1Action t1Action = new T1Action();
    private ScheduledFuture T1;
    
    private T2Action t2Action = new T2Action();
    private ScheduledFuture T2;

    private T3Action t3Action = new T3Action();
    private ScheduledFuture T3;

    private T4Action t4Action = new T4Action();
    private ScheduledFuture T4;
    
    private int TIMEOUT_4;
    private int cp;
    private boolean futureProving;
    
    private Mtp3 mtp3;
    
    public Mtp2(String name) {
        this.name = name;
        
        //init HDLC
        hdlc.fasthdlc_precalc();
        hdlc.fasthdlc_init(rxState);
        hdlc.fasthdlc_init(txState);
        
        //init error rate monitor
        aermThreshold = NORMAL_AERM_THRESHOLD;
    }

    /**
     * Assigns layer 1 implementation.
     * 
     * @param layer1 the implementation of MTP1 layer.
     */
    public void setLayer1(Mtp1 layer1) {
        this.layer1 = layer1;
    }
    
    protected void setLayer3(Mtp3 mtp3) {
        this.mtp3 = mtp3;
    }
    
    public int getSpan() {
        return span;
    }
    
    public void setSpan(int span) {
        this.span = span;
    }
    
    public int getTimeslot() {
        return timeslot;
    }
    
    public void setTimeslot(int timeslot) {
        this.timeslot = timeslot;
    }
    
    public void start() throws IOException {
//        String devName = "/dev/zap/" + (31*span - 29 + timeslot);
        layer1.open();
        started = true;
        logger.info("Link(" + name + ") is out of service now");
        
        poll = mtpThread.scheduleAtFixedRate(this, 0, POLL_DELAY, TimeUnit.MILLISECONDS);
        state = Mtp2.MTP2_OUT_OF_SERVICE;
        
        queueLSSU(FRAME_STATUS_INDICATION_OS);
        processTx();
        
        startInitialAlignment();
    }

    private void startInitialAlignment() {
        txOffset = 3;        
        logger.info("Starting initial alignment on link '" + name + "'");
        
        //starting T1 timer
        /*if (T1 != null && !T1.isCancelled()) {
            T1.cancel(false);
        }
        T1 = mtpTimer.schedule(t1Action, TIMEOUT_1, TimeUnit.MILLISECONDS);
        */
        //switch state
        state = MTP2_NOT_ALIGNED;
        
        //starting T2 timer
        if (T2 != null && !T2.isCancelled()) {
            T2.cancel(false);
        }
        T2 = mtpTimer.schedule(t2Action, TIMEOUT_2, TimeUnit.MILLISECONDS);        
    }
    
    public void queue(byte[] msg) {
        byte[] frame = new byte[3 + msg.length];
        
        frame[0] = (byte)(txBSN | (txBIB << 7));
        frame[1] = (byte)(txFSN | (txFIB << 7));
        frame[2] = (byte)msg.length;
        System.arraycopy(msg, 0, frame, 3, msg.length);
        txQueue.offer(frame);
    }
    
    private void queueLSSU(int indicator) { 
        byte[] frame = new byte[4];
        frame[0] = (byte)(txBSN | (txBIB << 7));
        frame[1] = (byte)(txFSN | (txFIB << 7));
        frame[2] = 1;
        frame[3] = (byte)indicator; 
        
        txQueue.offer(frame);
        if (logger.isTraceEnabled()) {
            logger.trace("Link(" + name + ") queue LSSU[" + indicator + ", fsn=" + lastSentSN + ", bsn=" + sendBSN + "]");
        }
    }

    private void queueFISU() {
        byte[] frame = new byte[3];

        frame[0] = (byte)(txBSN | (txBIB << 7));
        frame[1] = (byte)(txFSN | (txFIB << 7));
        frame[2] = 0;
        
        txQueue.offer(frame);
        if (logger.isTraceEnabled()) {
            logger.trace("Link(" + name + ") queue FISU[fsn=" + txFSN + ", bsn=" + txBSN + "]");
        }
    }
    
    private void queueNextFrame() {
        switch (state) {
            case MTP2_NOT_ALIGNED :
                queueLSSU(FRAME_STATUS_INDICATION_O);
                break;
            case Mtp2.MTP2_ALIGNED :
                queueLSSU(FRAME_STATUS_INDICATION_N);
                break;
            default :
                queueFISU();                
        }
    }
    
    public void stop() {
        started = false;
        poll.cancel(false);
        
        if (T2 != null) {
            T2.cancel(false);
        }
        
        if (T3 != null) {
            T3.cancel(false);
        }
        layer1.close();
    }

    public int PPP_FCS(int fcs, int c) {
        return ((fcs) >> 8) ^ fcstab[((fcs) ^ (c)) & 0xff];
    }
    
    private void processLssu(int fsn, int fib) throws IOException {
        int type = rxFrame[3] & 0x07;
        if (logger.isDebugEnabled()) {
            logger.debug("Link " + name + ", state=" + state + " process LSSU(" + type +")");
        }
        switch (state) {
            case MTP2_NOT_ALIGNED :
                switch (type) {
                    case FRAME_STATUS_INDICATION_O :
                    case FRAME_STATUS_INDICATION_N :
                        T2.cancel(false);
                        if (!emergency) {
                            TIMEOUT_4 = TIMEOUT_4_NORMAL;
                        } else {
                            TIMEOUT_4 = TIMEOUT_4_EMERGENCY;
                        }
                        T3 = mtpTimer.schedule(t3Action, TIMEOUT_3, TimeUnit.MILLISECONDS);
                        state = MTP2_ALIGNED;
                        break;
                    case FRAME_STATUS_INDICATION_E :
                        if (!emergency) {
                            TIMEOUT_4 = TIMEOUT_4_EMERGENCY;
                        } else {
                            TIMEOUT_4 = TIMEOUT_4_EMERGENCY;
                        }
                        break;
                    case FRAME_STATUS_INDICATION_OS :
                        logger.info("Link(" + name + ") Receive link status indication 'OS', Link out of service now");
                        T2.cancel(false);
                        state = MTP2_OUT_OF_SERVICE;
                        startInitialAlignment();
                        break;
                    default :
                        queueFISU();
                }
                break;
            case MTP2_ALIGNED :
                switch (type) {
                    case FRAME_STATUS_INDICATION_E :
                        TIMEOUT_4 = TIMEOUT_4_EMERGENCY;
                    case FRAME_STATUS_INDICATION_N :
                        if (T3 != null && !T3.isCancelled()) {
                            T3.cancel(false);
                        }
                        cp = 0;
                        futureProving = false;
                        T4 = mtpTimer.schedule(t4Action, TIMEOUT_4, TimeUnit.MILLISECONDS);
                        state = MTP2_PROVING;
                        logger.info("Link( " + name + ") Proving...");
                    default :
                        queueFISU();
                }
                break;
            case MTP2_PROVING :
                switch (type) {
                    case FRAME_STATUS_INDICATION_O :
                        if (T4 != null && T4.isCancelled()) {
                            T4.cancel(false);
                        }
                        T3 = mtpTimer.schedule(t3Action, TIMEOUT_3, TimeUnit.MILLISECONDS);
                        state = MTP2_ALIGNED;
                    default :
                        queueFISU();
                }
                break;
            case MTP2_INSERVICE :
                state = MTP2_OUT_OF_SERVICE;
                logger.info("Link is out of service now");
                break;
            default :
                queueFISU();
        }
    }
    
    private void processFISU() {
        switch (state) {
            case MTP2_OUT_OF_SERVICE :
                break;
            case MTP2_PROVING :
                //state = 
                break;
            case MTP2_READY :
                state = MTP2_INSERVICE;
                logger.info("Link(" + name  + ") now in service");
                mtp3.inService();
                break;
            case MTP2_INSERVICE :
                break;
        }
    }
    
    private void processMSU(int len) {
        int sio = rxFrame[3];
        byte[] sif = new byte[len - 1];
        for (int i = 0; i < len -1; i++) {
            sif[i] = (byte)rxFrame[i+4];
        }
        mtp3.onMessage(sio, sif);
    }
    
    private void processFrame() throws IOException {
        int bsn = rxFrame[0] & 0x7F;
        int bib = rxFrame[0] >> 7;
        int fsn = rxFrame[1] & 0x7F;
        int fib = rxFrame[1] >> 7;
        
        if (logger.isTraceEnabled()) {
            logger.trace("Link " + name + " processing frame: " + dump(rxFrame, this.rxLen) + " fsn=" + fsn + " bsn=" + bsn);
        }
        int li = rxFrame[2] & 0x3f;
        
        if (li + 5 > rxLen) {
            return;
        }
        
        if (li == 0) {
            processFISU();
        } else if (li == 1 || li == 2) {
            processLssu(fsn, bsn);
        } else {
            processMSU(li);
        }
        
    }
    
    private void processRx(byte[] buff, int len) throws IOException {
        int i = 0;
        while (i < len) {
            while (rxState.bits <= 24 && i < len) {
                int b = buff[i++] & 0xff;
                hdlc.fasthdlc_rx_load_nocheck(rxState, b);
            }
            int res = hdlc.fasthdlc_rx_run(rxState);
            switch (res) {
                case FastHDLC.RETURN_COMPLETE_FLAG :
                    if (rxCRC == 0xF0B8) {
                        processFrame();
                    } else {
                        if (logger.isTraceEnabled()) {
                            logger.trace("CRC error on link '" + name + "'");
                        }
                    }
                    rxLen = 0;
                    rxCRC = 0xffff;
                    break;
                case FastHDLC.RETURN_DISCARD_FLAG :
                    rxLen = 0;
                    break;
                case FastHDLC.RETURN_EMPTY_FLAG :
                    rxLen = 0;
                    break;
                default :
                    if (rxLen > 279) {
                        rxLen = 0;
                    } else {
                        rxFrame[rxLen++] = res;
                        rxCRC = PPP_FCS(rxCRC, res & 0xff);
                    }
                    
            }
        }
    }
    
    private void processTx() throws IOException {
        for (int i = 0; i < RX_TX_BUFF_SIZE; i++) {
            if (txState.bits < 8) {
                //need more bits
                if (doCRC == 0 && txOffset < txLen) {
                    int data = txFrame[txOffset++] & 0xff;
                    hdlc.fasthdlc_tx_load(txState, data);
                    txCRC = PPP_FCS(txCRC, data);
                    if (txOffset == txLen) {
                        doCRC = 1;
                        txCRC ^= 0xffff;
                    }
                } else if (doCRC == 1) {
                    hdlc.fasthdlc_tx_load_nocheck(txState, (txCRC) & 0xff);
                    doCRC = 2; 
                } else if (doCRC == 2) {
                    hdlc.fasthdlc_tx_load_nocheck(txState, (txCRC >> 8) & 0xff);
                    doCRC = 0;
                } else {
                    //nextFrame();
                    if (txQueue.isEmpty()) {
                        queueNextFrame();
                    }
                    txFrame = txQueue.peak();
                    txLen = txFrame.length;
                    txOffset = 0;
                    txCRC = 0xffff;
                    hdlc.fasthdlc_tx_frame_nocheck(txState);
                }
            }
            
            txBuffer[i] = (byte)hdlc.fasthdlc_tx_run_nocheck(txState);
        }
        
    }
    
    public void run() {
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("link " + name + " <<<<<< reading ");
            }
            int len = layer1.read(rxBuffer);
            if (logger.isTraceEnabled()) {
                logger.trace("link " + name + " <<<<<< read " + dump(rxBuffer, RX_TX_BUFF_SIZE));
            }
            processRx(rxBuffer, len);
        } catch (IOException e) {
            logger.error("Error during reading data from layer 1. Caused by", e);
        }
        
        try {
            processTx();
            layer1.write(txBuffer);        
            if (logger.isTraceEnabled()) {
                logger.trace("link " + name + " >>>> writes "  + dump(txBuffer, RX_TX_BUFF_SIZE));
            }
        } catch (IOException e) {
            logger.error("Error during writting data from layer 1. Caused by", e);
        }
    }
    
    private void print(String label, byte[] buff, int len) {
        System.out.print(label + " ");
        for (int i = 0; i < len; i++) {
            String s = Integer.toHexString(buff[i] & 0xff);
            if (s.length() == 1) {
                s = "0" + s;
            }
            System.out.print(s + " ");
        }
        System.out.println();
    }
    
    private void print(String label, int[] buff, int len) {
        System.out.print(label + " ");
        for (int i = 0; i < len; i++) {
            String s = Integer.toHexString(buff[i] & 0xff);
            if (s.length() == 1) {
                s = "0" + s;
            }
            System.out.print(s + " ");
        }
        System.out.println();
    }

    private String dump(byte[] buff, int size) {
        String s = "";
        for (int i = 0; i < size; i++) {
            String ss = Integer.toHexString(buff[i] & 0xff);
            if (ss.length() == 1) {
                ss = "0" + ss;
            }
            s += " " + ss;
        }
        return s;
    }

    private String dump(int[] buff, int size) {
        String s = "";
        for (int i = 0; i < size; i++) {
            String ss = Integer.toHexString(buff[i] & 0xff);
            if (ss.length() == 1) {
                ss = "0" + ss;
            }
            s += " " + ss;
        }
        return s;
    }
    
    private class IPTransceiver implements Runnable {

        public void run() {
            while (started) {
                
            }
        }
        
    }
    
    private class MTPThreadFactoryImpl implements ThreadFactory {

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setPriority(Thread.MAX_PRIORITY);
            t.setName("MTP2");
            return t;
        }
        
    }
    
    private static class MTPTimerThreadFactoryImpl implements ThreadFactory {

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setPriority(Thread.MAX_PRIORITY);
            t.setName("MTP-Timer");
            return t;
        }
        
    }

    private class T1Action implements Runnable {
        public void run() {
            emergency = false;
            state = MTP2_OUT_OF_SERVICE;
            startInitialAlignment();
        }
    }
    
    private class T2Action implements Runnable {
        public void run() {
            emergency = false;
            state = MTP2_OUT_OF_SERVICE;
            logger.info("Timer T2 has been expired, Alignment not possible. " +
                    "Link " + name + " out of service");
        }
    }
    
    private class T3Action implements Runnable {
        public void run() {
            emergency = false;
            state = MTP2_OUT_OF_SERVICE;
            logger.info("Timer T3 has been expired, Alignment not possible. " +
                    "Link " + name + " out of service");
        }
    }

    private class T4Action implements Runnable {
        public void run() {
            emergency = false;
            state = MTP2_READY;
            logger.info("Proving is succesfful on link '" + name + "'");
        }
    }
    
}
