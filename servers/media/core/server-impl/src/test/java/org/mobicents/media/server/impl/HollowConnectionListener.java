package org.mobicents.media.server.impl;

import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;

public class HollowConnectionListener implements ConnectionListener {

    private RtpConnectionImplTest test1;
    private LocalConnectionImplTest test2;
    
    public HollowConnectionListener(RtpConnectionImplTest test1) {
        this.test1 = test1;
    }

    public HollowConnectionListener(LocalConnectionImplTest test2) {
        this.test2 = test2;
    }
    
    public void onStateChange(Connection connection, ConnectionState oldState) {
        // TODO Auto-generated method stub
    }

    public void onModeChange(Connection connection, ConnectionMode oldMode) {
        if (test1 != null) {
            test1.modeChanged = true;
        }
        if (test2 != null) {
            test2.modeChanged = true;
        }
        
    }
}
