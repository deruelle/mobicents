/*
 * JccCallImplTest.java
 * JUnit based test
 *
 * Created on 28 Август 2007 г., 16:03
 */

package org.mobicents.jcc.inap;

import junit.framework.*;
import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.csapi.cc.jcc.*;
import org.mobicents.jcc.inap.address.JccCalledPartyNumber;
import org.mobicents.jcc.inap.address.JccCallingPartyNumber;
import org.mobicents.ss7.sccp.SccpAddress;
import org.apache.log4j.Logger;

/**
 *
 * @author mitrenko
 */
public class JccCallImplTest extends TestCase {
    private JccPeer peer;
    private JccProvider provider;
    private JccCallImpl call;
    
    private String conf = "<jcc-inap>; " +
            "sccp.provider=dummy;" +
            "sccp.conf=/sccp.properties;" +
            "incoming.initial.translation=/local-translation.properties;" +
            "incoming.final.translation=/msc-translation.properties;" +
            "outgoing.initial.translation=/local-bcd-translation.properties;" +
            "outgoing.final.translation=/msc-bcd-translation.properties";
    
    public JccCallImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        try {
            peer = JccPeerFactory.getJccPeer("org.mobicents.jcc.inap.JccPeerImpl");
            provider = peer.getProvider(conf);
            call = (JccCallImpl)((JccInapProviderImpl)provider).createCall(
                    new JccCalledPartyNumber((JccInapProviderImpl)provider, "123"));
        } catch (ClassNotFoundException ex) {
            fail(ex.getMessage());
        }
    }

    protected void tearDown() throws Exception {
        provider.shutdown();
    }

    
    public void testRelease() {
        assertEquals(1, ((JccInapProviderImpl) provider).calls.size());
        call.clear();
        assertEquals(0, ((JccInapProviderImpl) provider).calls.size());
    }
    
    public void testAppend() {
        ConnectionID ID = new ConnectionID(1, null, null);
        JccConnection conn = new TerminatingConnection(ID, call, 
                new JccCalledPartyNumber((JccInapProviderImpl) provider, "123"),
                new JccCalledPartyNumber((JccInapProviderImpl) provider, "321"));
        call.append(conn);
        assertEquals(1, call.connections.size());
    }
    
    public void testRemove() {
        ConnectionID ID = new ConnectionID(1, null, null);
        JccConnection conn = new TerminatingConnection(ID, call, 
                new JccCalledPartyNumber((JccInapProviderImpl) provider, "123"),
                new JccCalledPartyNumber((JccInapProviderImpl) provider, "321"));
        call.append(conn);
        assertEquals(1, call.connections.size());
        call.remove(conn);
        assertEquals(0, call.connections.size());
    }
}
