/*
 * SmppTransactionHandler.java
 *
 * Created on 6 Декабрь 2006 г., 13:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.smpp.ra;

import javax.slee.resource.ActivityHandle;
import net.java.slee.resource.smpp.Transaction;

/**
 *
 * @author Oleg Kulikov
 */
public class SmppTransactionHandle implements ActivityHandle {
    
    private Transaction tx;
    
    /** Creates a new instance of SmppTransactionHandler */
    public SmppTransactionHandle(Transaction tx) {
        this.tx = tx;
    }

    
    public boolean equals(Object obj) {
        if (obj instanceof SmppTransactionHandle) {
            return tx.getId() ==((SmppTransactionHandle) obj).tx.getId();
        } 
        else return false;
    }
       
    public int hashCode() {
        return new Integer(tx.getId()).hashCode();
    }       
    
}
