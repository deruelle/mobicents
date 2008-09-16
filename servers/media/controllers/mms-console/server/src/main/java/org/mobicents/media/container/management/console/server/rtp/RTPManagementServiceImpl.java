package org.mobicents.media.container.management.console.server.rtp;

import org.mobicents.media.container.management.console.client.rtp.RTPManagementMBeanDoesNotExistException;
import org.mobicents.media.container.management.console.client.rtp.RTPManagementOperationFailedException;
import org.mobicents.media.container.management.console.client.rtp.RTPManagementService;
import org.mobicents.media.container.management.console.client.rtp.RTPManagerInfo;
import org.mobicents.media.container.management.console.client.rtp.XFormat;
import org.mobicents.media.container.management.console.server.ManagementConsole;
import org.mobicents.media.container.management.console.server.mbeans.RTPManagementMBeanUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RTPManagementServiceImpl extends RemoteServiceServlet implements RTPManagementService{

protected final static ManagementConsole console=ManagementConsole.getInstance();
protected RTPManagementMBeanUtils utils=null;

public RTPManagementServiceImpl() {
	super();
	this.utils=console.getMMSConnection().getMMSManagementMBeanUtils().getRtpManagementMBeanUtils();
}

public XFormat[] getAudioFormats(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getAudioFormats(rtpMBeanObjectName);
}

public String getBindAddress(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getBindAddress(rtpMBeanObjectName);
}

public Integer getJitter(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getJitter(rtpMBeanObjectName);
}

public String getJndiName(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getJndiName(rtpMBeanObjectName);
}

public RTPManagerInfo getManagerInfo(String rtpMBeanObjectName) throws RTPManagementOperationFailedException, RTPManagementMBeanDoesNotExistException {

	return utils.getManagerInfo(rtpMBeanObjectName);
}

public Integer getPacketizationPeriod(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getPacketizationPeriod(rtpMBeanObjectName);
}

public Integer[] getPortRange(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getPortRange(rtpMBeanObjectName);
}

public String getPublicAddressFromStun(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getPublicAddressFromStun(rtpMBeanObjectName);
}

public String getStunServerAddress(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getStunServerAddress(rtpMBeanObjectName);
}

public Integer getStunServerPort(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getStunServerPort(rtpMBeanObjectName);
}

public XFormat[] getVideoFormats(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.getVideoFormats(rtpMBeanObjectName);
}

public Boolean isUsePortMapping(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.isUsePortMapping(rtpMBeanObjectName);
}

public Boolean isUseStun(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	return utils.isUseStun(rtpMBeanObjectName);
}

public String[] listRTPMBeans() throws RTPManagementOperationFailedException {
	return utils.listRTPMBeans();
}

public void setAudioFormats(XFormat[] formats, String rtpMBeanObjectName) throws RTPManagementOperationFailedException, RTPManagementMBeanDoesNotExistException {
	utils.setAudioFormats(formats, rtpMBeanObjectName);
}

public void setBindAddress(String bindAddress, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setBindAddress(bindAddress, rtpMBeanObjectName);
}

public void setJitter(Integer jitter, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setJitter(jitter, rtpMBeanObjectName);
}

public void setJndiName(String jndiName, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setJndiName(jndiName, rtpMBeanObjectName);
}

public void setPacketizationPeriod(Integer period, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setPacketizationPeriod(period, rtpMBeanObjectName);
}

public void setPortRange(Integer lowPort, Integer highPort, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setPortRange(lowPort, highPort, rtpMBeanObjectName);
}

public void setPublicAddressFromStun(String publicAddressFromStun, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setPublicAddressFromStun(publicAddressFromStun, rtpMBeanObjectName);
}

public void setStunServerAddress(String stunServerAddress, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setStunServerAddress(stunServerAddress, rtpMBeanObjectName);
}

public void setStunServerPort(Integer stunServerPort, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setStunServerPort(stunServerPort, rtpMBeanObjectName);
}

public void setUsePortMapping(Boolean usePortMapping, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setUsePortMapping(usePortMapping, rtpMBeanObjectName);
}

public void setUseStun(Boolean useStun, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
	utils.setUseStun(useStun, rtpMBeanObjectName);
}

public void setVideoFormats(XFormat[] formats, String rtpMBeanObjectName) throws RTPManagementOperationFailedException, RTPManagementMBeanDoesNotExistException {
	utils.setVideoFormats(formats, rtpMBeanObjectName);
}
 
}
