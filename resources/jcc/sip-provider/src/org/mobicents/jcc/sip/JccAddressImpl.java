/*
 * File Name     : JccAddressImpl.java
 *
 * The Java Call Control API for SIP
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

package org.mobicents.jcc.sip;

import javax.csapi.cc.jcc.JccAddress;
import javax.csapi.cc.jcc.JccProvider;

import javax.sip.address.Address;
import javax.sip.address.URI;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class JccAddressImpl implements JccAddress {
    
    private JccProvider provider = null;
    protected Address address = null;
    
    /** Creates a new instance of JccAddressImpl */
    public JccAddressImpl(JccProvider provider, Address address) {
        this.provider = provider;
        this.address = address;
    }

    public String getName() {
        return address != null ? address.toString() : null;
    }

    public JccProvider getProvider() {
        return provider;
    }

    public int getType() {
        return JccAddress.SIP;
    }
    
    public String toString() {
        String s = address.toString().trim();
        if (s.startsWith("<")) {
            s = s.replaceAll("<", "");
            s = s.replaceAll(">", "");
        }
        return s;
    }
    

    public int hasCode() {
        return address.getURI().toString().hashCode();
    }
    
    protected String getURI() {
        return address.getURI().toString();
    }
    
    public boolean equals(Object o) {
    	if (o != null && o.getClass() == this.getClass()) {
			return ((JccAddressImpl)o).address.getURI().toString().equals(this.address.getURI().toString());
		}
		else {
			return false;
		}
    }
}
