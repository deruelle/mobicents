package org.mobicents.media.container.management.console.server.mbeans;

import java.util.ArrayList;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.mobicents.media.container.management.console.client.rtp.RTPManagementMBeanDoesNotExistException;
import org.mobicents.media.container.management.console.client.rtp.RTPManagementOperationFailedException;
import org.mobicents.media.container.management.console.client.rtp.RTPManagementService;
import org.mobicents.media.container.management.console.client.rtp.RTPManagerInfo;
import org.mobicents.media.container.management.console.client.rtp.XFormat;

public class RTPManagementMBeanUtils implements RTPManagementService {

	protected MBeanServerConnection server = null;
	protected ObjectName rptMgmtRegexName = null;

	public RTPManagementMBeanUtils(ObjectName rptMgmtRegexName, MBeanServerConnection beanServerConnection) {
		super();
		this.rptMgmtRegexName = rptMgmtRegexName;
		this.server = beanServerConnection;
	}

	public XFormat[] getAudioFormats(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}

		
		try {
			// FIXME: move to JAXB: for now there are some problems on my
			// machine - JVM related propably
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			String raw = (String) this.server.invoke(on, "getAudioFormats", null, null);
			return XFormat.fromString(raw);

		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public String getBindAddress(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}

		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			return (String) this.server.invoke(on, "getBindAddress", null, null);
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public Integer getJitter(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			return (Integer) this.server.invoke(on, "getJitter", null, null);
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public String getJndiName(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			return (String) this.server.invoke(on, "getJndiName", null, null);
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public Integer getPacketizationPeriod(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			return (Integer) this.server.invoke(on, "getPacketizationPeriod", null, null);
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public Integer[] getPortRange(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			String pr = (String) this.server.invoke(on, "getPortRange", null, null);
			String[] sRange = pr.split("-");
			Integer[] range = new Integer[2];
			range[0] = Integer.valueOf(sRange[0]);
			range[1] = Integer.valueOf(sRange[1]);
			return range;
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public String getPublicAddressFromStun(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			return (String) this.server.invoke(on, "getPublicAddressFromStun", null, null);
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public String getStunServerAddress(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			return (String) this.server.invoke(on, "getStunServerAddress", null, null);
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public Integer getStunServerPort(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			return (Integer) this.server.invoke(on, "getStunServerPort", null, null);
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public XFormat[] getVideoFormats(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}

		try {
			// FIXME: move to JAXB: for now there are some problems on my
			// machine - JVM related propably
			ArrayList<XFormat> xformatList = new ArrayList<XFormat>();
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			String raw = (String) this.server.invoke(on, "getVideoFormats", null, null);
			return XFormat.fromString(raw);

		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public Boolean isUsePortMapping(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			return (Boolean) this.server.invoke(on, "isUsePortMapping", null, null);
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public Boolean isUseStun(String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {
			ObjectName on = new ObjectName(rtpMBeanObjectName);
			return (Boolean) this.server.invoke(on, "isUseStun", null, null);
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public String[] listRTPMBeans() throws RTPManagementOperationFailedException {
		// try {
		// Set<ObjectInstance> qResult =
		// this.server.queryMBeans(this.rptMgmtRegexName, null);

		// String[] result = new String[qResult.size()];
		// int c = 0;
		// for (ObjectInstance oi : qResult) {
		// result[c++] = oi.getObjectName().getCanonicalName();
		// }

		// return result;
		// } catch (Exception e) {

		// e.printStackTrace();
		// throw new
		// RTPManagementOperationFailedException(MMSManagementMBeanUtils
		// .doMessage(e));
		// }

		try {

			Set<ObjectInstance> set = server.queryMBeans(null, null);
			ArrayList<String> names = new ArrayList<String>();

			for (ObjectInstance oi : set) {
				if (oi.getObjectName().toString().contains("service=RTPManager")) {
					names.add(oi.getObjectName().toString());
				}

			}

			return names.toArray(new String[names.size()]);
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException(MMSManagementMBeanUtils.doMessage(e));
		}

	}

	private boolean isValidRTPMGMTMBean(String objectName) {
		String[] list;
		try {
			list = listRTPMBeans();
			for (String name : list) {
				if (name.compareTo(objectName) == 0)
					return true;
			}
			return false;
		} catch (RTPManagementOperationFailedException e) {

			return false;
		}

	}

	public void setAudioFormats(XFormat[] formats, String rtpMBeanObjectName) throws RTPManagementOperationFailedException, RTPManagementMBeanDoesNotExistException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			String arg = "";
			for(int i=0;i<formats.length;i++)
			{
				arg+=formats[i];
				if(i!=formats.length-1)
					arg+=";";
			}
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setAudioFormats", new Object[] { arg }, new String[] { "java.lang.String" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setBindAddress(String bindAddress, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			String arg = bindAddress;
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setBindAddress", new Object[] { arg }, new String[] { "java.lang.String" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setJitter(Integer jitter, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			Object arg = jitter;
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setJitter", new Object[] { arg }, new String[] { "java.lang.Integer" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setJndiName(String jndiName, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			Object arg = jndiName;
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setJndiName", new Object[] { arg }, new String[] { "java.lang.String" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setPacketizationPeriod(Integer period, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}

		try {

			Object arg = period;
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setPacketizationPeriod", new Object[] { arg }, new String[] { "java.lang.Integer" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public void setPortRange(Integer lowPort, Integer highPort, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			Object arg = "" + lowPort.intValue() + "-" + highPort.intValue();
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setPortRange", new Object[] { arg }, new String[] { "java.lang.String" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public void setPublicAddressFromStun(String publicAddressFromStun, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException,
			RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			Object arg = publicAddressFromStun;
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setPublicAddressFromStun", new Object[] { arg }, new String[] { "java.lang.String" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setStunServerAddress(String stunServerAddress, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			Object arg = stunServerAddress;
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setStunServerAddress", new Object[] { arg }, new String[] { "java.lang.String" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setStunServerPort(Integer stunServerPort, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			Object arg = stunServerPort;
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setStunServerPort", new Object[] { arg }, new String[] { "java.lang.Integer" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setUsePortMapping(Boolean usePortMapping, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setUsePortMapping", new Object[] { usePortMapping.booleanValue() }, new String[] { "boolean" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setUseStun(Boolean useStun, String rtpMBeanObjectName) throws RTPManagementMBeanDoesNotExistException, RTPManagementOperationFailedException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			//Object arg = useStun;
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setUseStun", new Object[] { useStun.booleanValue() }, new String[] { "boolean" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setVideoFormats(XFormat[] formats, String rtpMBeanObjectName) throws RTPManagementOperationFailedException, RTPManagementMBeanDoesNotExistException {
		if (!isValidRTPMGMTMBean(rtpMBeanObjectName)) {
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!");
		}
		try {

			String arg = "";
			for(int i=0;i<formats.length;i++)
			{
				arg+=formats[i];
				if(i!=formats.length-1)
					arg+=";";
			}
			ObjectName on = new ObjectName(rtpMBeanObjectName);

			this.server.invoke(on, "setVideoFormats", new Object[] { arg }, new String[] { "java.lang.String" });
		} catch (MalformedObjectNameException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new RTPManagementMBeanDoesNotExistException("RTP Manager MBean [" + rtpMBeanObjectName + "] does not exist!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new RTPManagementOperationFailedException("RTP Manager MBean [" + rtpMBeanObjectName + "] Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public RTPManagerInfo getManagerInfo(String rtpMBeanObjectName) throws RTPManagementOperationFailedException, RTPManagementMBeanDoesNotExistException {

		try{
		return new RTPManagerInfo(this.getAudioFormats(rtpMBeanObjectName), this.getBindAddress(rtpMBeanObjectName), getJitter(rtpMBeanObjectName),
				getJndiName(rtpMBeanObjectName), rtpMBeanObjectName, getPacketizationPeriod(rtpMBeanObjectName), getPortRange(rtpMBeanObjectName),
				getPublicAddressFromStun(rtpMBeanObjectName), getStunServerAddress(rtpMBeanObjectName), getStunServerPort(rtpMBeanObjectName), this
						.isUsePortMapping(rtpMBeanObjectName), this.isUseStun(rtpMBeanObjectName), this.getVideoFormats(rtpMBeanObjectName));
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new RTPManagementOperationFailedException(e);
		}
	}

	public static void main(String[] args) {

		String t = "<root><payload>" + "           <rtpmap>8</rtpmap>" + "           <format>H261/90000</format>" + "       </payload>" + "       <payload>"
				+ "           <rtpmap>0</rtpmap>" + "           <format>H261/90000</format>" + "       </payload></root>";
		// System.out.println("[0]"+t);
		// t=t.trim();
		// System.out.println("[1]"+t);
		// t=t.replace("<payload>", "");
		// System.out.println("[2]"+t);
		// String[] pairs=t.split("</payload>");
		// for(String pair:pairs)
		// {
		// pair=pair.replace("<rtpmap>", "");
		// pair=pair.replace("<format>", "");
		// }

		// try {
		// JAXBContext jc = JAXBContext.newInstance(
		// "org.mobicents.media.container.management.console.server.mbeans.xml"
		// );
		//	
		// Payload[] pay=(Payload[]) jc.createUnmarshaller().unmarshal(new
		// ByteArrayInputStream(t.getBytes()));
		// } catch (JAXBException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
