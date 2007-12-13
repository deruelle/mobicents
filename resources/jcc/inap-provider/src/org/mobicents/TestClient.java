/*
 * The Java Call Control API for CAMEL 2
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

package org.mobicents;

import javax.csapi.cc.jcc.InvalidArgumentException;
import javax.csapi.cc.jcc.InvalidPartyException;
import javax.csapi.cc.jcc.InvalidStateException;
import javax.csapi.cc.jcc.JccCallEvent;
import javax.csapi.cc.jcc.JccConnection;
import javax.csapi.cc.jcc.JccConnectionEvent;
import javax.csapi.cc.jcc.JccConnectionListener;
import javax.csapi.cc.jcc.JccEvent;
import javax.csapi.cc.jcc.JccPeer;
import javax.csapi.cc.jcc.JccPeerFactory;
import javax.csapi.cc.jcc.JccProvider;
import javax.csapi.cc.jcc.MethodNotSupportedException;
import javax.csapi.cc.jcc.PrivilegeViolationException;
import javax.csapi.cc.jcc.ResourceUnavailableException;

/**
 *
 * @author Oleg Kulikov
 */
public class TestClient implements JccConnectionListener {
    
    private String conf = "<jcc-inap>; " +
            "sccp.provider=M3UA;" +
            "sccp.conf=/sccp.properties;" +
            "originating.initial.translation=/local-bcd-translation.properties;" +
            "originating.final.translation=/msc-bcd-translation.properties;" +
            "terminating.initial.translation=/local-bcd-translation.properties;" +
            "terminating.final.translation=/msc-translation.properties";
    
    private JccPeer peer;
    private JccProvider provider;
    
    /** Creates a new instance of TestClient */
    public TestClient() throws Exception {
        peer = JccPeerFactory.getJccPeer("org.itech.jcc.inap.JccPeerImpl");
        provider = peer.getProvider(conf);
        provider.addConnectionListener(this, null);
    }
    
    public static void main(String args[]) throws Exception {
        new TestClient();
    }
    
    public void connectionAuthorizeCallAttempt(JccConnectionEvent event) {
        JccConnection connection = event.getConnection();
        System.out.println("Connection authorize call attempt: " + connection + ",isBlocked=" + connection.isBlocked());
        try {
            connection.continueProcessing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void connectionAddressCollect(JccConnectionEvent event) {
    }
    
    public void connectionAddressAnalyze(JccConnectionEvent event) {
        System.out.println("Connection address analyze");
        JccConnection connection = event.getConnection();
        try {
            connection.continueProcessing();
        } catch (PrivilegeViolationException ex) {
            ex.printStackTrace();
        } catch (ResourceUnavailableException ex) {
            ex.printStackTrace();
        } catch (InvalidStateException ex) {
            ex.printStackTrace();
        }
/*        try {
            connection.selectRoute("9044171395");
        } catch (InvalidStateException ex) {
            ex.printStackTrace();
        } catch (PrivilegeViolationException ex) {
            ex.printStackTrace();
        } catch (MethodNotSupportedException ex) {
            ex.printStackTrace();
        } catch (ResourceUnavailableException ex) {
            ex.printStackTrace();
        } catch (InvalidPartyException ex) {
            ex.printStackTrace();
        }
 */
    }
    
    public void connectionCallDelivery(JccConnectionEvent event) {
    }
    
    public void connectionMidCall(JccConnectionEvent event) {
    }
    
    public void connectionCreated(JccConnectionEvent event) {
        System.out.println("Connection created");
    }
    
    public void connectionAlerting(JccConnectionEvent event) {
    }
    
    public void connectionConnected(JccConnectionEvent event) {
    }
    
    public void connectionFailed(JccConnectionEvent event) {
        System.out.println("Connection failed: cause=" + event.getCause());
    }
    
    public void connectionDisconnected(JccConnectionEvent event) {
        System.out.println("Connection disconnected: cause=" + event.getCause());
    }
    
    public void callSuperviseStart(JccCallEvent event) {
    }
    
    public void callSuperviseEnd(JccCallEvent event) {
    }
    
    public void callActive(JccCallEvent event) {
    }
    
    public void callInvalid(JccCallEvent event) {
    }
    
    public void callEventTransmissionEnded(JccCallEvent event) {
    }
    
    public void callCreated(JccCallEvent event) {
    }
    
}

