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

import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.NotificationListener;

/**
 * The concept of events and signals is central to the Media Server.  
 * A Call Controller may ask to be notified about certain events occurring in an 
 * endpoint  (e.g., off-hook events) or may also request certain signals to be 
 * applied to an endpoint (e.g., dial-tone).
 * 
 * Signals are divided into different types depending on their behavior:
 * <ul>
 * <li>On/off (OO):  Once applied, these signals last until they are turned off.  
 * This can only happen as the result of a reboot/restart or a new signal 
 * request where the signal is explicitly turned off.  Signals of type OO are 
 * defined to be idempotent, thus multiple requests to turn a given OO signal on 
 * (or off) are perfectly valid.  An On/Off signal could be a visual 
 * message-waiting indicator (VMWI).  Once turned on, it MUST NOT be turned off 
 * until explicitly instructed to by the Call Agent, or as a result of an 
 * endpoint restart, i.e., these signals will not turn off as a result of the 
 * detection of a requested event. </li> 
 * <li>Time-out (TO):  Once applied, these signals last until they are either 
 * cancelled (by the occurrence of an event or by explicit releasing of signal 
 * generator), or a signal-specific period of time has elapsed.  A TO signal 
 * that times out will generate an "operation complete" event.  If an event 
 * occurs prior to the 180 seconds, the signal will, by default, be stopped 
 * (the "Keep signals active" action - will override this behavior).  If the 
 * signal is not stopped, the signal will time out, stop and generate an 
 * "operation complete" event, about which the server controller may or may not 
 * have requested to be notified.  A TO signal that fails after being started, 
 * but before having generated an "operation complete" event will generate an 
 * "operation failure" event which will include the name of the signal that 
 * failed.  Deletion of a connection with an active TO signal will result in 
 * such a failure. </li>
 * <li>Brief (BR):  The duration of these signals is normally so short that they 
 * stop on their own.  If a signal stopping event occurs, or a new signal 
 * requests is applied, a currently active BR signal will not stop.  
 * However, any pending BR signals not yet applied will be cancelled 
 * (a BR signal becomes pending if a signal request includes a BR signal, and 
 * there is already an active BR signal). As an example, a brief tone could be a 
 * DTMF digit. If the DTMF digit "1" is currently being played, and a signal 
 * stopping event occurs, the "1" would play to completion.  
 * If a request to play DTMF digit "2" arrives before DTMF digit "1" finishes 
 * playing, DTMF digit "2" would become pending.</li>
 * </ul>
 *
 *
 * @author Oleg Kulikov
 */
public interface Signal extends MediaSource {
    /**
     * Gets the unique identifier of the signal.
     * 
     * @return unique identifier of the signal
     */
    public String getID();
    
    /**
     * Gets the options of this signal
     * 
     * @return the options.
     */
    public Options getOptions();
    
    /**
     * Modify options of this signal
     * 
     * @param options the options
     */
    public void setOptions(Options options);
    
    public void addListener(NotificationListener listener);
    public void removeListener(NotificationListener listener);
}
