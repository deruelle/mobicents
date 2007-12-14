/*
 * Ping.java
 * 
 * Created on Jul 14, 2005
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

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *The proxy sends me periodic pings.
 *Each ping looks like this.
 *if the ping does not arrive every 10 seconds, then I remove the 
 *proxy from the list of live proxies.
 *
 */
public class Ping implements Serializable {
   
        private transient InetAddress inetAddress;
        
        private long lastPing;

        private String host;

        private int port;
        
        public long getLastPing() {
            return this.lastPing;
        }
        
        public String getHost() {
            return this.host;
        }
        
        public int getPort() {
            return this.port;
        }
        
        public InetAddress getInetAddress()throws UnknownHostException  {
            if ( this.inetAddress == null ) {
                this.inetAddress = InetAddress.getByName(host);
            }  
            return this.inetAddress;
        }
        
        public void setPingTime()  {
            this.lastPing = System.currentTimeMillis();
           
        }
        
        public Ping( String host, int port) {
            this.host  = host;
            this.port = port;
            this.lastPing = System.currentTimeMillis();
        }
        
        
    
}

