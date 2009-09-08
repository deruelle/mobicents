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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author kulikov
 */
public class Mtp3 {
    
    private final static int T1 = 12;
    private final static int T2 = 90;
    
    private final static int LINK_MANAGEMENT = 0;
    private final static int LINK_TESTING = 1;
    private final static int SERVICE_SCCP = 3;
    private final static int SERVICE_ISUP= 5;
    
    private Mtp2 layer2;
    private ScheduledFuture t1;
    private ScheduledFuture t2;
    
    private int destinationPointCode;
    private int originationPointCode;
    private int signalingLinkSelection;
    
    private final static byte[] PATTERN = new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x0F};
    
    private T1Action t1Action = new T1Action();
    private T2Action t2Action = new T2Action();
    
    private String name;
    
    private Logger logger = Logger.getLogger(Mtp3.class);
    
    public Mtp3(String name) {
        this.name = name;
        layer2 = new Mtp2(name);
        layer2.setLayer3(this);
    }
    
    public void setOPC(int opc) {
        this.originationPointCode = opc;
    }
    
    public void setDPC(int dpc) {
        this.destinationPointCode = dpc;
    }
    
    public void setSLS(int sls) {
        this.signalingLinkSelection = sls;
    }
    
    public void start() throws IOException {
        layer2.start();
    }
    
    public void stop() {
        if (t2 != null && !t2.isCancelled()) {
            t2.cancel(false);
        }
        t2.cancel(false);
        layer2.stop();
    }
    
    /**
     * 
     * @param sio service information octet.
     * @param msg service information field;
     */
    public void onMessage(int sio, byte[] sif) {
        int ni = sio >>> 6;
        int piority = (sio >>> 4 & 0x03);
        int si = sio & 0x0f;
        
        int dpc = (sif[0] & 0xff | ((sif[1] & 0x3f) << 8));    
        int opc = ((sif[1] & 0x03) >> 6) | ((sif[2] & 0xff) << 2) | ((sif[3] & 0x0f) << 10);
        int sls = (sif[3] & 0xf0) >>> 4;
        
        if (logger.isDebugEnabled()) {
            logger.debug("Received MSSU [si=" + si + ", dpc=" + dpc + ", opc=" + opc + ", sls=" + sls + "]");
        }
        switch (si) {
            case LINK_MANAGEMENT :                
                break;
            case LINK_TESTING :
                int h0 = sif[4] & 0x0f;
                int h1 = (sif[4] & 0xf0) >>> 4;
                
                int len = (sif[5] & 0xf0) >>> 4;
                
                if (h0 == 1 && h1 == 1) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("SLTM received");
                    }
                    //receive SLTM from remote end
                    //create response
                    byte[] slta = new byte[len + 6];
                    slta[0] = (byte)sio;
                    
                    writeRoutingLabel(slta);
                    slta[5] = 0x021;
                    System.arraycopy(sif, 5, slta, 6, len);
                    layer2.queue(slta);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Responding with SLTA");
                    }
                } else if (h0 == 1 && h1 == 2) {
                    //receive SLTA from remote end
                    if (logger.isDebugEnabled()) {
                        logger.debug("Link(" + name + ") SLTA received");
                    }
                    //stop Q707 timer T1
                    if (t1 != null && !t1.isCancelled()) {
                        t1.cancel(false);
                    }
                    //check contidions for acceptance
                    if (opc != this.destinationPointCode) {
                        return;
                    }
                    
                    if (dpc != this.originationPointCode) {
                        return;
                    }
                    
                    if (!checkPattern(sif, PATTERN)) {
                        return;
                    }
                    logger.info("Notify layer 4");
                    //notify level 4
                } else {
                    logger.warn("Unexpected message type");
                }
                break;
            case SERVICE_SCCP :
                break;
            case SERVICE_ISUP :
                break;
        }
    }
    
    public void send(byte[] msg) {
        
    }
    
    public void failed() {
        
    }
    
    public void inService() {
        t2 = Mtp2.mtpTimer.scheduleAtFixedRate(new LinkTest(), 0, T2, TimeUnit.SECONDS);
    }
    
    /**
     * Assigns layer 1 implementation.
     * 
     * @param layer1 the implementation of MTP1 layer.
     */
    public void setLayer1(Mtp1 layer1) {
        layer2.setLayer1(layer1);
    }
    
    private void sendSLTM() {
        byte[] sltm = new byte[7 + PATTERN.length];
        sltm[0] = 1;
        writeRoutingLabel(sltm);
        sltm[5] = 0x11;
        sltm[6] = (byte)(PATTERN.length << 4);
        System.arraycopy(PATTERN, 0, sltm, 7, PATTERN.length);
        layer2.queue(sltm);
        Mtp2.mtpTimer.schedule(t1Action, T1, TimeUnit.SECONDS);
        if (logger.isDebugEnabled()) {
            logger.debug("Link ("  + name  + ") Queue SLTM");
        }
    }
    
    private void writeRoutingLabel(byte[] sif) {
        sif[1] = (byte) destinationPointCode;        
        sif[2] = (byte)(((destinationPointCode >> 8) & 0x3f) |((destinationPointCode & 0x03) << 6));
        sif[3] = (byte)(originationPointCode >> 2);
        sif[4] = (byte)(((originationPointCode >> 10) & 0x0f)| (signalingLinkSelection << 4));
    }
    
    private boolean checkPattern(byte[] sif, byte[] pattern) {
        if (sif.length - 5 != pattern.length) {
            return false;
        }
        for (int i = 0; i < pattern.length; i++) {
            if (sif[i + 5] != pattern[i]) {
                return false;
            }
        }
        return true;
    }
    
    private class LinkTest implements Runnable {
        public void run() {
            sendSLTM();
        }
    }
    
    private class T1Action implements Runnable {
        public void run() {
            
        }
    }
    
    private class T2Action implements Runnable {
        public void run() {
            
        }
    }

}
