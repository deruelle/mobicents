/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl;

import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;
import org.mobicents.media.server.impl.common.ConnectionMode;
import org.mobicents.media.server.impl.common.ConnectionState;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.ivr.IVREndpointImpl;
import org.mobicents.media.server.impl.sdp.AVProfile;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class UA implements Runnable {

    private final static String UA_ENDPOINT = "";
    private final static String SERVER_ENDPOINT = "";
    
    private Endpoint uaEndpoint;
    private Endpoint serverEndpoint;
    
    private Connection uaConnection;
    private Connection serverConnection;
    
    private NotificationListener serverListener;
    private int id;
    
    public UA(NotificationListener listener, int id) {
        this.serverListener = listener;
        this.id = id;
    }
    
    public void run() {
        try {
            uaEndpoint = new IVREndpointImpl("test/client/" + id);
            uaEndpoint.addFormat(AVProfile.PCMA.getPayload(), AVProfile.PCMA);
            uaEndpoint.setJitter(60);
            uaEndpoint.setPacketizationPeriod(20);
            uaEndpoint.setBindAddress(InetAddress.getLocalHost());
            uaEndpoint.setPortRange("1024-65535");
            Properties dtmfConfig = new Properties();
            dtmfConfig.setProperty("detector.mode", "INBAND");
            
            uaEndpoint.setDefaultConfig(MediaResourceType.DTMF_DETECTOR, dtmfConfig);
            uaEndpoint.configure(MediaResourceType.DTMF_DETECTOR, dtmfConfig);
            
            uaConnection = uaEndpoint.createConnection(ConnectionMode.SEND_RECV);
            uaConnection.addListener(new UAConnectionListener(uaConnection));

            serverEndpoint = new IVREndpointImpl("test/server/" + id);
            serverEndpoint.addFormat(AVProfile.PCMA.getPayload(), AVProfile.PCMA);
            serverEndpoint.setJitter(60);
            serverEndpoint.setPacketizationPeriod(20);
            serverEndpoint.setBindAddress(InetAddress.getLocalHost());
            serverEndpoint.setPortRange("1024-65535");
            serverEndpoint.setDefaultConfig(MediaResourceType.DTMF_DETECTOR, dtmfConfig);
            
            serverConnection = serverEndpoint.createConnection(ConnectionMode.SEND_RECV);
            serverConnection.addListener(new ServerConnectionListener(serverListener));
            serverEndpoint.configure(MediaResourceType.SPECTRUM_ANALYSER, serverConnection, null);

            System.out.println(uaConnection.getLocalDescriptor());
            serverConnection.setRemoteDescriptor(uaConnection.getLocalDescriptor());
            System.out.println(serverConnection.getLocalDescriptor());
            uaConnection.setRemoteDescriptor(serverConnection.getLocalDescriptor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class UAConnectionListener implements ConnectionListener, NotificationListener {

    private Connection connection;
    
    public UAConnectionListener(Connection connection) {
        this.connection = connection;
    }
    
    public void onStateChange(Connection connection, ConnectionState oldState) {
        System.out.println("Client side: " + connection.getState());
        if (connection.getState() == ConnectionState.OPEN) {
            try {
            connection.getEndpoint().configure(MediaResourceType.SPECTRUM_ANALYSER, connection, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            URL url = getClass().getResource("/dtmf-0.wav");
            String[] params = new String[]{url.toExternalForm()};
            try {
                connection.getEndpoint().play(
                        EventID.TEST_SINE, params, connection.getId(), this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update(NotifyEvent event) {
        System.out.println("Client side: " + event.getID());
        if (event.getID() == EventID.COMPLETE) {
            try {
            connection.getEndpoint().configure(MediaResourceType.SPECTRUM_ANALYSER, connection, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            URL url = getClass().getResource("/dtmf-0.wav");
            String[] params = new String[]{url.toExternalForm()};
            try {
                connection.getEndpoint().play(
                        EventID.TEST_SINE, params, connection.getId(), this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class ServerConnectionListener implements ConnectionListener {
    
    private NotificationListener listener;
    
    public ServerConnectionListener(NotificationListener listener) {
        this.listener = listener;
    }
    
    public void onStateChange(Connection connection, ConnectionState oldState) {
        System.out.println("Server side: " + connection.getState());
        if (connection.getState() == ConnectionState.OPEN) {
            String[] params = new String[]{""};
            try {
                connection.getEndpoint().subscribe(
                        EventID.TEST_SPECTRA, connection.getId(), null, listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update(NotifyEvent event) {
        System.out.println(event);
    }
}
