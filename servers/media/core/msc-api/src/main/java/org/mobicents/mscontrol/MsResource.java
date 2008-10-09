/*
 * MsResource.java
 *
 * The Simple Media API RA
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

package org.mobicents.mscontrol;

import java.io.Serializable;

/**
 * @deprecated
 * @author Oleg Kulikov
 */
public interface MsResource extends Serializable {
	/**
	 * Returns the ID of this MsResource
	 * 
	 * @return unique identifier of MsResource
	 */
	public String getID();

	/**
	 * Add's the instance of class implementing MsResourceListener. update
	 * method of this class will be called when ever underlying resource is
	 * updated
	 * 
	 * @param listener
	 */
	public void addResourceListener(MsResourceListener listener);

	/**
	 * Removes's the instance of class implementing MsResourceListener. Call
	 * this method if the class is no more interested in events fired by resource.
	 * 
	 * @param listener
	 */
	public void removeResourceListener(MsResourceListener listener);
	
	public void setResourceStateIdle();

	/**
	 * Release's the underlying resource
	 */
	public void release();
}
