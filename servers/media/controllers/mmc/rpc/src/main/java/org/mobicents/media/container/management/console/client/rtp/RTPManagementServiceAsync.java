package org.mobicents.media.container.management.console.client.rtp;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RTPManagementServiceAsync {
	/**
	 * Gets the list of registered audio formats.
	 * 
	 * @return the string in the following format: <payload> <!-- payload number
	 *         --> <rtpmap>8</rtpmap> <!-- format description -->
	 *         <format>ALAW,8000, 8, 1</format> </payload> <payload>
	 *         <rtpmap>0</rtpmap> <format>ULAW,8000, 8, 1</format> </payload>
	 * @throws RTPManagementOperationFailedException 
	 * 
	 */
	public void getAudioFormats(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Gets the IP address to which trunk is bound. All endpoints of the trunk
	 * use this address for RTP connection.
	 * 
	 * @return the IP address string to which this trunk is bound.
	 * @throws RTPManagementOperationFailedException 
	 */
	public void getBindAddress(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Gets the size of the jitter buffer in milliseconds.
	 * 
	 * Jitter buffer is used at the receiving ends of a VoIP connection. A
	 * jitter buffer stores received, time-jittered VoIP packets, that arrive
	 * within its time window. It then plays stored packets out, in sequence,
	 * and at a constant rate for subsequent decoding. A jitter buffer is
	 * typically filled half-way before playing out packets to allow early, or
	 * late, packet-arrival jitter compensation.
	 * 
	 * Choosing a large jitter buffer reduces packet dropping from jitter but
	 * increases VoIP path delay
	 * 
	 * @return the size of the buffer in milliseconds.
	 * @throws RTPManagementOperationFailedException 
	 */
	public void getJitter(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Gets the JNDI name to which RTP Factory is boind.
	 * 
	 * @return the JNDI name.
	 * @throws RTPManagementOperationFailedException 
	 */
	public void getJndiName(String rtpMBeanObjectName,AsyncCallback callback);

	public void getManagerInfo(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Gets packetization period.
	 * 
	 * The packetization period is the period over which encoded voice bits are
	 * collected for encapsulation in packets.
	 * 
	 * Higher period will reduce VoIP overhead allowing higher channel
	 * utilization
	 * 
	 * @return packetion period for media in milliseconds.
	 * @throws RTPManagementOperationFailedException 
	 */
	public void getPacketizationPeriod(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Gets the range of the available port used for RTP connections.
	 * 
	 * @return the range of available port in the follwing format: low-high
	 * @throws RTPManagementOperationFailedException 
	 */
	public void getPortRange(String rtpMBeanObjectName,AsyncCallback callback);

	public void getPublicAddressFromStun(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Gets the STUN server address.
	 * 
	 * @return the address of the server as string.
	 * @throws RTPManagementOperationFailedException 
	 */
	public void getStunServerAddress(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Gets the port of the STUN server.
	 * 
	 * @return the port number.
	 * @throws RTPManagementOperationFailedException 
	 */
	public void getStunServerPort(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Gets the list of registered video formats.
	 * 
	 * @return the string in the following format: <payload> <!-- payload number
	 *         --> <rtpmap>8</rtpmap> <!-- format description -->
	 *         <format>H261/90000</format> </payload> <payload>
	 *         <rtpmap>0</rtpmap> <format>H261/90000</format> </payload>
	 * @throws RTPManagementOperationFailedException 
	 * 
	 */
	public void getVideoFormats(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Shows does NAT uses port mapping.
	 * 
	 * @return true if NAT uses port mapping.
	 * @throws RTPManagementOperationFailedException 
	 */
	public void isUsePortMapping(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Shows does STUN enabled.
	 * 
	 * @return true if STUN is used.
	 * @throws RTPManagementOperationFailedException 
	 */
	public void isUseStun(String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Return array containing list of string representation of Object Names of
	 * RTP MBeans
	 * 
	 * @return
	 * @throws RTPManagementOperationFailedException 
	 */
	public void listRTPMBeans(AsyncCallback callback);

	/**
	 * Sets the list of registered audio formats.
	 * 
	 * @param formats
	 *            the string in the following format: <payload> <!-- payload
	 *            number --> <rtpmap>8</rtpmap> <!-- format description -->
	 *            <format>ALAW,8000, 8, 1</format> </payload> <payload>
	 *            <rtpmap>0</rtpmap> <format>ULAW,8000, 8, 1</format> </payload>
	 * 
	 */
	public void setAudioFormats(XFormat[] formats, String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Modify the bind address. All endpoints of the trunk use this address for
	 * RTP connection.
	 * 
	 * @param the
	 *            IP address string.
	 */
	public void setBindAddress(String bindAddress, String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Modify size of the jitter buffer.
	 * 
	 * Jitter buffer is used at the receiving ends of a VoIP connection. A
	 * jitter buffer stores received, time-jittered VoIP packets, that arrive
	 * within its time window. It then plays stored packets out, in sequence,
	 * and at a constant rate for subsequent decoding. A jitter buffer is
	 * typically filled half-way before playing out packets to allow early, or
	 * late, packet-arrival jitter compensation.
	 * 
	 * Choosing a large jitter buffer reduces packet dropping from jitter but
	 * increases VoIP path delay
	 * 
	 * @param jitter
	 *            the new buffer's size in milliseconds
	 */
	public void setJitter(Integer jitter, String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Sets the JNDI name to which RTF Factory should be bound.
	 * 
	 * @param jndiName
	 *            the JNDI name to which trunk object will be bound.
	 */
	public void setJndiName(String jndiName, String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Modify packetization period.
	 * 
	 * The packetization period is the period over which encoded voice bits are
	 * collected for encapsulation in packets.
	 * 
	 * Higher period will reduce VoIP overhead allowing higher channel
	 * utilization
	 * 
	 * @param period
	 *            the value of the packetization period in milliseconds.
	 */
	public void setPacketizationPeriod(Integer period, String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Sets the range of the available port used for RTP connections.
	 * 
	 * @param portRange
	 *            string indicating range in the follwing format: low-high
	 */
	public void setPortRange(Integer lowPort, Integer highPort, String rtpMBeanObjectName,AsyncCallback callback);

	public void setPublicAddressFromStun(String publicAddressFromStun, String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Modify STUN server address.
	 * 
	 * @param stunServerAddress
	 *            the valid address of the server
	 */
	public void setStunServerAddress(String stunServerAddress, String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Configures port of the STUN server.
	 * 
	 * @param stunServerPort
	 *            port number.
	 */
	public void setStunServerPort(Integer stunServerPort, String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Configures NAT traversal procedure.
	 * 
	 * @param usePortMapping
	 *            true if NAT uses port mapping.
	 */
	public void setUsePortMapping(Boolean usePortMapping, String rtpMBeanObjectName,AsyncCallback callback);

	/**
	 * Enables/disables STUN usage.
	 * 
	 * @param useStun
	 *            true to use STUN or false otherwise.
	 */
	public void setUseStun(Boolean useStun, String rtpMBeanObjectName,AsyncCallback callback);
	
	/**
	 * Sets the list of registered audio formats.
	 * 
	 * @param formats
	 *            the string in the following format: <payload> <!-- payload
	 *            number --> <rtpmap>8</rtpmap> <!-- format description -->
	 *            <format>H261/90000</format> </payload> <payload>
	 *            <rtpmap>0</rtpmap> <format>H261/90000</format> </payload>
	 * 
	 */
	public void setVideoFormats(XFormat[] formats, String rtpMBeanObjectName,AsyncCallback callback);
}
