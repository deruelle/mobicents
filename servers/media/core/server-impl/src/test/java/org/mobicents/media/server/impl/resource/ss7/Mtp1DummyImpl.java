/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.ss7;

import java.io.IOException;

/**
 *
 * @author kulikov
 */
public class Mtp1DummyImpl implements Mtp1 {

    private TransferProxy proxy;
    
    private LocalChannel rxChannel;
    private LocalChannel txChannel;
    
    public Mtp1DummyImpl(TransferProxy proxy) {
        this.proxy = proxy;
        rxChannel = proxy.getRxChannel(this);
        txChannel = proxy.getTxChannel(this);
    }
    
    public int read(byte[] buffer) throws IOException {
        return rxChannel.read(buffer);
    }

    public void write(byte[] buffer) throws IOException {
        txChannel.push(buffer);
    }

    public void open() {
    }

    public void close() {
    }

}
