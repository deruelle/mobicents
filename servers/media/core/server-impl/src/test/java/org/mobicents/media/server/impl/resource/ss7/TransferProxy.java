/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.ss7;

/**
 *
 * @author kulikov
 */
public class TransferProxy {
    
    private LocalChannel channel1;
    private LocalChannel channel2;
    
    public TransferProxy() {
        channel1 = new LocalChannel();
        channel2 = new LocalChannel();
    }
    
    public synchronized LocalChannel getRxChannel(Mtp1 mtp1) {
        if (channel1.getDirection() == LocalChannel.TX && channel1.getMtp1() != mtp1) {
            return channel1;
        } else if (channel2.getDirection() == LocalChannel.TX && channel2.getMtp1() != mtp1) {
            return channel2;
        } else if (channel1.getDirection() != LocalChannel.RX && channel1.getMtp1() != mtp1) {
            channel1.setDirection(LocalChannel.RX);
            channel1.setMtp1(mtp1);
            return channel1;
        } else {
            channel2.setDirection(LocalChannel.RX);
            channel2.setMtp1(mtp1);
            return channel2;
        }
    }

    public synchronized LocalChannel getTxChannel(Mtp1 mtp1) {
        if (channel1.getDirection() == LocalChannel.RX && channel1.getMtp1() != mtp1) {
            return channel1;
        } else if (channel2.getDirection() == LocalChannel.RX && channel2.getMtp1() != mtp1) {
            return channel2;
        } else if (channel1.getDirection() != LocalChannel.TX && channel1.getMtp1() != mtp1) {
            channel1.setDirection(LocalChannel.TX);
            return channel1;
        } else {
            channel2.setDirection(LocalChannel.TX);
            channel2.setMtp1(mtp1);
            return channel2;
        }
    }
    
    
}
