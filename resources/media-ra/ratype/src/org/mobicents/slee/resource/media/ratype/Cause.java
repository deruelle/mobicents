/*
 * Cause.java
 *
 * Created on 29 Май 2007 г., 12:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.ratype;

import java.io.Serializable;

/**
 *
 * @author Oleg Kulikov
 */
public interface Cause extends Serializable {
    public final static int NORMAL = 0;
    public final static int INTERNAL_SERVER_ERROR = 1;
    public final static int INVALID_OR_MISSING_REMOTE_DESCRIPTOR = 2;
    
    public final static int NO_DATASOURCE = 3;
    public final static int NO_PROCESSOR = 5;
    public final static int NO_FORMAT = 6;
    public final static int IO_ERROR = 4;
    public final static int NO_CONNECTIONS = 8;
    public final static int TIMEOUT = 9;
    public final static int DATA_LOST = 10;
    public final static int RESOURCE_UNAVAILABLE = 11;
    public final static int CONNECTION_ERROR = 12;
        
    public final static int DTMF_0 = 100;
    public final static int DTMF_1 = 101;
    public final static int DTMF_2 = 102;
    public final static int DTMF_3 = 103;
    public final static int DTMF_4 = 104;
    public final static int DTMF_5 = 105;
    public final static int DTMF_6 = 106;
    public final static int DTMF_7 = 107;
    public final static int DTMF_8 = 108;
    public final static int DTMF_9 = 109;
    public final static int DTMF_A = 110;
    public final static int DTMF_B = 111;
    
}
