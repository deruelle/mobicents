/*
 * Mobicents Media Gateway
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

package org.mobicents.media.server.impl;

import java.io.Serializable;
import org.mobicents.media.server.spi.events.EventPackage;

/**
 * 
 * @author Oleg Kulikov
 */
public class EventPackageFactory implements Serializable {

	public static EventPackage load(String packageName) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		String className = packageName.replace("media", "media.server.impl.events") + ".PackageImpl";
		ClassLoader classLoader = EventPackageFactory.class.getClassLoader();
		Class instance = classLoader.loadClass(className);

		return (EventPackage) instance.newInstance();
	}
}
