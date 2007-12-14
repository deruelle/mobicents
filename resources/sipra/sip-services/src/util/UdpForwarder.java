/*
 * UdpForwarder.java
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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for Proxy Servers to contact me.
 * 
 * @author M. Ranganathan ( NIST ) 
 *
 */
public interface UdpForwarder  extends Remote {
    
    
    public Ping handlePing( Ping ping ) throws RemoteException ;

}

