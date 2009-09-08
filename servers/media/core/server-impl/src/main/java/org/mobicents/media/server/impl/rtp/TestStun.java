/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.rtp;

import net.java.stun4j.StunAddress;
import net.java.stun4j.client.NetworkConfigurationDiscoveryProcess;
import net.java.stun4j.client.StunDiscoveryReport;

/**
 *
 * @author kulikov
 */
public class TestStun {

    public static void main(String[] args) throws Exception {
        StunAddress localStunAddress = new StunAddress("192.168.1.2", 8000);
        StunAddress serverStunAddress = new StunAddress("stun.ekiga.net", 3478);

        NetworkConfigurationDiscoveryProcess addressDiscovery =
                new NetworkConfigurationDiscoveryProcess(
                localStunAddress, serverStunAddress);
        addressDiscovery.start();

        StunDiscoveryReport report = addressDiscovery.determineAddress();
        if (report.getPublicAddress() != null) {
            String publicAddressFromStun = report.getPublicAddress().getSocketAddress().getAddress().getHostAddress();
            System.out.println("Public address: " + publicAddressFromStun);
        // TODO set a timer to retry the binding and provide a
        // callback to update the global ip address and port
        } else {
            System.out.println("Stun discovery failed to find a valid public ip address, disabling stun !");
        }
        System.out.println("Stun report = " + report);
        addressDiscovery.shutDown();
    }
}
