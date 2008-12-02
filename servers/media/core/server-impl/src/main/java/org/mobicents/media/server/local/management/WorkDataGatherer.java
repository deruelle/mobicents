/**
 * Start time:15:37:17 2008-11-27<br>
 * Project: mobicents-media-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.media.server.local.management;

/**
 * Start time:15:37:17 2008-11-27<br>
 * Project: mobicents-media-server-core<br>
 * This interface defines methods that are invoked by sink/source - or any other
 * component to update information about work done during operations, for
 * instance see http://tools.ietf.org/html/rfc3435#section-3.2.2
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public interface WorkDataGatherer {

	public void octetsReceived(int count);

	public void octetsSent(int count);

	public void packetsSent(int count);

	public void packetsReceived(int count);

	public void packetsLost(int count);
	
	public void interArrivalJitter(int value);
	
	public boolean isGatherStats();
}
