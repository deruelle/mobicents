/*
 * Created on Mar 7, 2005
 * 
 * The Open SLEE Project
 * 
 * A SLEE for the People
 * 
 * The source code contained in this file is in in the public domain.          
 * It can be used in any project or product without prior permission, 	      
 * license or royalty payments. There is no claim of correctness and
 * NO WARRANTY OF ANY KIND provided with this code.
 */
package org.mobicents.slee.resource.sip;

import javax.slee.resource.ActivityHandle;

/**
 * 
 * TODO Class Description
 * 
 * @author F.Moggia
 */
public class SipActivityHandle implements ActivityHandle{
    String transactionId;
    
    public SipActivityHandle(String transactionId){
    	this.transactionId = transactionId;
    }
    
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == this.getClass()){
        	return this.toString().equals(obj.toString());
        } else return false;
    }
    
    public int hashCode() {
        return transactionId.hashCode();
    }

    public String toString() {
        return this.transactionId;
    }
}
