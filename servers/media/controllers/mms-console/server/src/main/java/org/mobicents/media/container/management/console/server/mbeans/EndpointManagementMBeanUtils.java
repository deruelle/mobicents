package org.mobicents.media.container.management.console.server.mbeans;

import java.util.ArrayList;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.mobicents.media.container.management.console.client.ManagementConsoleException;
import org.mobicents.media.container.management.console.client.endpoint.ConnectionInfo;
import org.mobicents.media.container.management.console.client.endpoint.EndpointFullInfo;
import org.mobicents.media.container.management.console.client.endpoint.EndpointManagementService;
import org.mobicents.media.container.management.console.client.endpoint.EndpointShortInfo;
import org.mobicents.media.container.management.console.client.endpoint.EndpointType;
import org.mobicents.media.container.management.console.client.endpoint.NoSuchConnectionException;
import org.mobicents.media.container.management.console.client.endpoint.NoSuchEndpointException;

public class EndpointManagementMBeanUtils implements EndpointManagementService {

	protected MBeanServerConnection server = null;
	protected ObjectName ivrObjectName = null;
	protected ObjectName confObjectName = null;
	protected ObjectName annObjectName = null;
	protected ObjectName pktObjectName = null;
	protected ObjectName loopObjectName = null;

	public EndpointManagementMBeanUtils(MBeanServerConnection server, ObjectName ivrObjectName, ObjectName confObjectName, ObjectName annObjectName, ObjectName pktObjectName,
			ObjectName loopObjectName) {
		super();
		this.server = server;
		this.ivrObjectName = ivrObjectName;
		this.confObjectName = confObjectName;
		this.annObjectName = annObjectName;
		this.pktObjectName = pktObjectName;
		this.loopObjectName = loopObjectName;
	}

	public EndpointShortInfo[] getEndpointsShortInfo(EndpointType type) throws ManagementConsoleException {

		ObjectName beanName = null;
		// Class equality does not work.
		if (type.toString().equals(EndpointType.ANNOUNCEMENT.toString())) {
			beanName = annObjectName;
		} else if (type.toString().equals(EndpointType.CONF.toString())) {
			beanName = confObjectName;
		} else if (type.toString().equals(EndpointType.IVR.toString())) {
			beanName = ivrObjectName;
		} else if (type.toString().equals(EndpointType.PCKT_RELAY.toString())) {
			beanName = pktObjectName;
		} else if (type.toString().equals(EndpointType.LOOPBACK.toString())) {
			beanName = loopObjectName;

		} else {
			return new EndpointShortInfo[0];
		}

		
		ArrayList<EndpointShortInfo> infos = new ArrayList<EndpointShortInfo>();
		String[] names;
		try {
			names = (String[]) this.server.invoke(beanName, "getEndpointNames", null, null);

			for (int i = 0; i < names.length; i++) {
				String name = names[i];
	
				try {

					Object value = null;
					String[] paramTypes = new String[] { "java.lang.String" };
					Object[] paramValues = new Object[] { name };

					EndpointShortInfo inf = new EndpointShortInfo();

					inf.setName(name);
					value = this.server.invoke(beanName, "getConnectionsCount", paramValues, paramTypes);
					inf.setConnections(((Integer) value).intValue());
					value = this.server.invoke(beanName, "getCreationTime", paramValues, paramTypes);
					inf.setCreationTime(((Long) value).longValue());
					value = this.server.invoke(beanName, "getGatherPerformanceFlag", paramValues, paramTypes);
					inf.setGetherPerformance(((Boolean) value).booleanValue());
					inf.setType(type);

					value = this.server.invoke(beanName, "getNumberOfBytes", paramValues, paramTypes);
					inf.setNumberOfBytes(((Long) value).longValue());
					value = this.server.invoke(beanName, "getPacketsCount", paramValues, paramTypes);
					inf.setPackets(((Long) value).longValue());
					infos.add(inf);

				} catch (NullPointerException e) {

					e.printStackTrace();
					throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
				} catch (InstanceNotFoundException e) {

					e.printStackTrace();
					throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
				} catch (MBeanException e) {

					e.printStackTrace();
					throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
				} catch (ReflectionException e) {

					e.printStackTrace();
					throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
				} catch (Exception e) {

					e.printStackTrace();
					// throw new
					// RTPManagementOperationFailedException("RTP Manager MBean ["
					// +
					// rtpMBeanObjectName + "] Failed due:\n" +
					// MMSManagementMBeanUtils.doMessage(e));
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist or failed to fetch endpoint names!!!!\n"
					+ MMSManagementMBeanUtils.doMessage(e));
		}
		return infos.toArray(new EndpointShortInfo[infos.size()]);
	}

	public ConnectionInfo getConnectionInfo(String endpointName, EndpointType type, String connectionId) throws ManagementConsoleException, NoSuchEndpointException,
			NoSuchConnectionException {

		ObjectName beanName = null;
		ConnectionInfo info = new ConnectionInfo();

		// Class equality does not work.
		if (type.toString().equals(EndpointType.ANNOUNCEMENT.toString())) {
			beanName = annObjectName;
		} else if (type.toString().equals(EndpointType.CONF.toString())) {
			beanName = confObjectName;
		} else if (type.toString().equals(EndpointType.IVR.toString())) {
			beanName = ivrObjectName;
		} else if (type.toString().equals(EndpointType.PCKT_RELAY.toString())) {
			beanName = pktObjectName;
		} else if (type.toString().equals(EndpointType.LOOPBACK.toString())) {
			beanName = loopObjectName;

		} else {
			throw new NoSuchEndpointException("Wrong endpoint type");
		}

		try {

			Object value = null;
			String[] paramTypes = new String[] { "java.lang.String", "java.lang.String" };
			Object[] paramValues = new Object[] { endpointName, connectionId };
			info.setConnecitonId(connectionId);
			value = this.server.invoke(beanName, "getConnectionCreationTime", paramValues, paramTypes);
			info.setCreationTime(((Long) value).longValue());

			value = this.server.invoke(beanName, "getConnectionState", paramValues, paramTypes);
			info.setState((String) value);
			if (!info.getState().toLowerCase().equals("closed")) {
				value = this.server.invoke(beanName, "getConnectionLocalSDP", paramValues, paramTypes);
				info.setLocalSdp((String) value);
				value = this.server.invoke(beanName, "getConnectionRemoteSDP", paramValues, paramTypes);
				info.setRemoteSdp((String) value);
			} else {
				info.setLocalSdp("X");
				info.setRemoteSdp("X");
			}
			value = this.server.invoke(beanName, "getOtherEnd", paramValues, paramTypes);
			info.setOtherEnd((String) value);
			value = this.server.invoke(beanName, "getConnectionMode", paramValues, paramTypes);
			info.setMode((String) value);

			value = this.server.invoke(beanName, "getNumberOfPackets", paramValues, paramTypes);
			info.setNumberOfPackets(((Long) value).longValue());
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

		return info;
	}

	public EndpointFullInfo getEndpointInfo(String endpointName, EndpointType type) throws ManagementConsoleException, NoSuchEndpointException {

		EndpointFullInfo info = new EndpointFullInfo();
		ObjectName beanName = null;
		// Class equality does not work.
		if (type.toString().equals(EndpointType.ANNOUNCEMENT.toString())) {
			beanName = annObjectName;
		} else if (type.toString().equals(EndpointType.CONF.toString())) {
			beanName = confObjectName;
		} else if (type.toString().equals(EndpointType.IVR.toString())) {
			beanName = ivrObjectName;
		} else if (type.toString().equals(EndpointType.PCKT_RELAY.toString())) {
			beanName = pktObjectName;
		} else if (type.toString().equals(EndpointType.LOOPBACK.toString())) {
			beanName = loopObjectName;

		} else {
			return null;
		}

		
		//ArrayList<EndpointShortInfo> infos = new ArrayList<EndpointShortInfo>();
		String[] names;
		try {
			

					// EndpointLocalManagement _managed=managedEndpoints[i];
					Object value = null;
					String[] paramTypes = new String[] { "java.lang.String" };
					Object[] paramValues = new Object[] { endpointName };

			

					info.setName(endpointName);
					value = this.server.invoke(beanName, "getConnectionsCount", paramValues, paramTypes);
					info.setConnections(((Integer) value).intValue());
					value = this.server.invoke(beanName, "getCreationTime", paramValues, paramTypes);
					info.setCreationTime(((Long) value).longValue());
					value = this.server.invoke(beanName, "getGatherPerformanceFlag", paramValues, paramTypes);
					info.setGetherPerformance(((Boolean) value).booleanValue());
					info.setType(type);

					value = this.server.invoke(beanName, "getNumberOfBytes", paramValues, paramTypes);
					info.setNumberOfBytes(((Long) value).longValue());
					value = this.server.invoke(beanName, "getPacketsCount", paramValues, paramTypes);
					info.setPackets(((Long) value).longValue());
					String[] connectionIds = (String[]) this.server.invoke(beanName, "getConnectionIds", paramValues, paramTypes);
					ArrayList<ConnectionInfo> connectionInfos = new ArrayList<ConnectionInfo>();

					for (String connectionId : connectionIds) {
						try {
							connectionInfos.add(this.getConnectionInfo(endpointName, type, connectionId));
						} catch (Exception e) {

						}
					}
					info.setConnectionsInfo(connectionInfos.toArray(new ConnectionInfo[connectionInfos.size()]));
					value = this.server.invoke(beanName, "getRTPFacotryJNDIName", paramValues, paramTypes);
					info.setRtpFactoryJNDIName((String) value);
					// infos.add(inf);
				
				} catch (NullPointerException e) {

					e.printStackTrace();
					throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
				} catch (InstanceNotFoundException e) {

					e.printStackTrace();
					throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
				} catch (MBeanException e) {

					e.printStackTrace();
					throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
				} catch (ReflectionException e) {

					e.printStackTrace();
					throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
				} catch (Exception e) {

					e.printStackTrace();
					throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
				}
			
		
		return info;

	}

	public EndpointShortInfo[] getEndpointsShortInfo() throws ManagementConsoleException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGatherPerformanceData(String endpointName, EndpointType type, boolean value) throws ManagementConsoleException, NoSuchEndpointException {

		ObjectName beanName = null;
		try {

			EndpointFullInfo info = new EndpointFullInfo();

			// Class equality does not work.
			if (type.toString().equals(EndpointType.ANNOUNCEMENT.toString())) {
				beanName = annObjectName;
			} else if (type.toString().equals(EndpointType.CONF.toString())) {
				beanName = confObjectName;
			} else if (type.toString().equals(EndpointType.IVR.toString())) {
				beanName = ivrObjectName;
			} else if (type.toString().equals(EndpointType.PCKT_RELAY.toString())) {
				beanName = pktObjectName;
			} else if (type.toString().equals(EndpointType.LOOPBACK.toString())) {
				beanName = loopObjectName;

			} else {
				throw new NoSuchEndpointException("Wrong endpoint type: " + type);
			}

			String[] paramTypes = new String[] { "java.lang.String", "boolean" };
			Object[] paramValues = new Object[] { endpointName, value };
			this.server.invoke(beanName, "setGatherPerformanceData", paramValues, paramTypes);
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void setRTPFactoryJNDIName(String endpointName, EndpointType type, String jndiName) throws ManagementConsoleException, NoSuchEndpointException {

		ObjectName beanName = null;
		try {

			EndpointFullInfo info = new EndpointFullInfo();
			// Class equality does not work.
			if (type.toString().equals(EndpointType.ANNOUNCEMENT.toString())) {
				beanName = annObjectName;
			} else if (type.toString().equals(EndpointType.CONF.toString())) {
				beanName = confObjectName;
			} else if (type.toString().equals(EndpointType.IVR.toString())) {
				beanName = ivrObjectName;
			} else if (type.toString().equals(EndpointType.PCKT_RELAY.toString())) {
				beanName = pktObjectName;
			} else if (type.toString().equals(EndpointType.LOOPBACK.toString())) {
				beanName = loopObjectName;

			} else {
				throw new NoSuchEndpointException("Wrong endpoint type: " + type);
			}

			String[] paramTypes = new String[] { "java.lang.String", "java.lang.String" };
			Object[] paramValues = new Object[] { endpointName, jndiName };
			this.server.invoke(beanName, "setRTPFacotryJNDIName", paramValues, paramTypes);
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public void destroyConnection(String name, EndpointType type, String connectionId) throws ManagementConsoleException, NoSuchEndpointException, NoSuchConnectionException {

		ObjectName beanName = null;
		try {

			// Class equality does not work.
			if (type.toString().equals(EndpointType.ANNOUNCEMENT.toString())) {
				beanName = annObjectName;
			} else if (type.toString().equals(EndpointType.CONF.toString())) {
				beanName = confObjectName;
			} else if (type.toString().equals(EndpointType.IVR.toString())) {
				beanName = ivrObjectName;
			} else if (type.toString().equals(EndpointType.PCKT_RELAY.toString())) {
				beanName = pktObjectName;
			} else {
				throw new NoSuchEndpointException("Wrong endpoint type: " + type);
			}

			String[] paramTypes = new String[] { "java.lang.String", "java.lang.String" };
			Object[] paramValues = new Object[] { name, connectionId };
			this.server.invoke(beanName, "destroyConnection", paramValues, paramTypes);
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
	}

	public void destroyEndpoint(String name, EndpointType type) throws ManagementConsoleException, NoSuchEndpointException {
		ObjectName beanName = null;
		try {


			// Class equality does not work.
			if (type.toString().equals(EndpointType.ANNOUNCEMENT.toString())) {
				beanName = annObjectName;
			} else if (type.toString().equals(EndpointType.CONF.toString())) {
				beanName = confObjectName;
			} else if (type.toString().equals(EndpointType.IVR.toString())) {
				beanName = ivrObjectName;
			} else if (type.toString().equals(EndpointType.PCKT_RELAY.toString())) {
				beanName = pktObjectName;
			} else if (type.toString().equals(EndpointType.LOOPBACK.toString())) {
				beanName = loopObjectName;

			} else {
				throw new NoSuchEndpointException("Wrong endpoint type: " + type);
			}

			String[] paramTypes = new String[] { "java.lang.String" };
			Object[] paramValues = new Object[] { name };
			this.server.invoke(beanName, "destroyEndpoint", paramValues, paramTypes);
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}

	}

	public EndpointFullInfo getEndpointTrunkInfo(EndpointType type) throws ManagementConsoleException, NoSuchEndpointException, NoSuchConnectionException {
		
		ObjectName beanName = null;
		try {


			// Class equality does not work.
			if (type.toString().equals(EndpointType.ANNOUNCEMENT.toString())) {
				beanName = annObjectName;
			} else if (type.toString().equals(EndpointType.CONF.toString())) {
				beanName = confObjectName;
			} else if (type.toString().equals(EndpointType.IVR.toString())) {
				beanName = ivrObjectName;
			} else if (type.toString().equals(EndpointType.PCKT_RELAY.toString())) {
				beanName = pktObjectName;
			} else if (type.toString().equals(EndpointType.LOOPBACK.toString())) {
				beanName = loopObjectName;

			} else {
				throw new NoSuchEndpointException("Wrong endpoint type: " + type);
			}

			String[] paramTypes = new String[] {  };
			Object[] paramValues = new Object[] {  };
			Object value=null;
			value=this.server.invoke(beanName, "getTrunkName", paramValues, paramTypes);
			String trunkName=(String) value;
			EndpointFullInfo trunkInfo=getEndpointInfo(trunkName, type);
			trunkInfo.setChildrenInfo(this.getEndpointsShortInfo(type));
			
			return trunkInfo;
		} catch (NullPointerException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (InstanceNotFoundException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (MBeanException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "] does not exist!!!!\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (ReflectionException e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		} catch (Exception e) {

			e.printStackTrace();
			throw new ManagementConsoleException("Endpoint Manager MBean [" + beanName + "]  Failed due:\n" + MMSManagementMBeanUtils.doMessage(e));
		}
		
		
	}

}
