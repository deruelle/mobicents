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

package org.mobicents.media.server.spi.events;

/**
 * Represents the event package.
 * 
 * Events and signals are grouped in packages, within which they share
 * the same name space which we will refer to as event names. 
 * 
 * @author Oleg Kulikov
 */
public interface EventPackage {
    public Signal getSignal(String signalID, Options options);
    public EventDetector getDetector(String signalID, Options options);
}
