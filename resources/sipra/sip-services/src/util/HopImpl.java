/*
 * HopImpl.java
 * 
 * Created on Jul 21, 2005
 * 
 * Created by: M. Ranganathan
 *
 * The Mobicents Open SLEE project
 * 
 * A SLEE for the people!
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

package util;

import javax.sip.address.Hop;

/**
 *
 */
public class HopImpl implements Hop {
    private String host;
    private int port;
    
    public HopImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    /* (non-Javadoc)
     * @see javax.sip.address.Hop#getHost()
     */
    public String getHost() {
       
        return host;
    }

    /* (non-Javadoc)
     * @see javax.sip.address.Hop#getPort()
     */
    public int getPort() {
      
        return port;
    }

    /* (non-Javadoc)
     * @see javax.sip.address.Hop#getTransport()
     */
    public String getTransport() {
        // TODO Auto-generated method stub
        return "udp";
    }

}

