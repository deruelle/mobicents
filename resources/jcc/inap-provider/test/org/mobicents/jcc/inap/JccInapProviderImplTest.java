/*
 * JccInapProviderImplTest.java
 * JUnit based test
 *
 * Created on 28 Август 2007 г., 16:15
 */

package org.mobicents.jcc.inap;

import javax.csapi.cc.jcc.JccAddress;
import javax.csapi.cc.jcc.JccPeer;
import javax.csapi.cc.jcc.JccPeerFactory;
import junit.framework.*;
import javax.csapi.cc.jcc.JccCall;
import javax.csapi.cc.jcc.JccProvider;
import org.mobicents.jcc.inap.address.JccCalledPartyNumber;

/**
 *
 * @author mitrenko
 */
public class JccInapProviderImplTest extends TestCase {
    
    private JccPeer peer;
    private JccInapProviderImpl provider;

    private String conf = "<jcc-inap>; " +
            "sccp.provider=dummy;" +
            "sccp.conf=/sccp.properties;" +
            "incoming.initial.translation=/local-translation.properties;" +
            "incoming.final.translation=/msc-translation.properties;" +
            "outgoing.initial.translation=/local-bcd-translation.properties;" +
            "outgoing.final.translation=/msc-bcd-translation.properties";
    
    public JccInapProviderImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        try {
            peer = JccPeerFactory.getJccPeer("org.mobicents.jcc.inap.JccPeerImpl");
            provider = (JccInapProviderImpl)peer.getProvider(conf);
        } catch (ClassNotFoundException ex) {
            fail(ex.getMessage());
        }
    }

    protected void tearDown() throws Exception {
    }

    public void testCreateWithCallingNumber() {
        JccCall call = (JccCallImpl)((JccInapProviderImpl)provider).createCall(
                    new JccCalledPartyNumber((JccInapProviderImpl)provider, "123"));
        if (call == null) {
            fail("Call should not be null");
        }
    }
    
    public void testLookupCall() {
        JccAddress address = new JccCalledPartyNumber((JccInapProviderImpl)provider, "123");
        JccCall call = (JccCallImpl)((JccInapProviderImpl)provider).createCall(
                    address);
        assertEquals(call, provider.getCall(address));
    }
    
}
