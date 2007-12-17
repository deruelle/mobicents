/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.examples.profiles;

import javax.slee.Address;

public interface ControllerProfileCMP {

  	// 'userAddress' CMP field setter
	public abstract void setUserAddress(Address value);
	// 'userAddress' CMP field getter
	public abstract Address getUserAddress();

	// 'blockedAddresses' CMP field setter
	public abstract void setBlockedAddresses(Address[] value);
	// 'blockedAddresses' CMP field getter
	public abstract Address[] getBlockedAddresses();

	// 'backupAddress' CMP field setter
	public abstract void setBackupAddress(Address value);
	// 'backupAddress' CMP field getter
	public abstract Address getBackupAddress();

	// 'voicemailState' CMP field setter
	public abstract void setVoicemailState(boolean value);
	// 'voicemailState' CMP field getter
	public abstract boolean getVoicemailState();
	
}
