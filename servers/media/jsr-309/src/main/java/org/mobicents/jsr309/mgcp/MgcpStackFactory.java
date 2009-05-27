package org.mobicents.jsr309.mgcp;

import jain.protocol.ip.mgcp.CreateProviderException;
import jain.protocol.ip.mgcp.DeleteProviderException;
import jain.protocol.ip.mgcp.JainMgcpStack;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpStackImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MgcpStackFactory {

	private static final Logger logger = Logger.getLogger(MgcpStackFactory.class);
	private static MgcpStackFactory mgcpStackFactory;

	/**
	 * Store the MGCP Stack with name provided in HashMap. This gives
	 * flexibility for different applications to instantiate different stacks.
	 * Hence in case if one goes down or mis behaves not all applications have
	 * to pay penalty
	 */
	private ConcurrentHashMap<String, MgcpWrapper> stringToMgcpWrapper = new ConcurrentHashMap<String, MgcpWrapper>();

	public static final String MGCP_STACK_NAME = "mgcp.stack.name";
	public static final String MGCP_STACK_IP = "mgcp.stack.ip";
	public static final String MGCP_STACK_PORT = "mgcp.stack.port";
	public static final String MGCP_PEER_IP = "mgcp.stack.peer.ip";
	public static final String MGCP_PEER_PORT = "mgcp.stack.peer.port";

	private MgcpStackFactory() {

	}

	public static MgcpStackFactory getInstance() {
		if (mgcpStackFactory == null) {
			mgcpStackFactory = new MgcpStackFactory();
		}
		return mgcpStackFactory;
	}

	public MgcpWrapper getMgcpStackProvider(Properties properties) {
		String stackName = "DEFAULT";
		if (properties != null) {
			stackName = properties.getProperty(MGCP_STACK_NAME, "DEFAULT");
		}
		MgcpWrapper mgcpWrapper = stringToMgcpWrapper.get(stackName);

		if (mgcpWrapper == null) {
			String ip = "127.0.0.1";
			String portString = "2727";
			String mgcpStackPeerIp = "127.0.0.1";
			int mgcpStackPeerPort = 2427;

			if (properties != null) {
				ip = properties.getProperty(MGCP_STACK_IP, "127.0.0.1");
				portString = properties.getProperty(MGCP_STACK_PORT, "2727");

				mgcpStackPeerIp = properties.getProperty(MgcpStackFactory.MGCP_PEER_IP, "127.0.0.1");
				mgcpStackPeerPort = Integer.parseInt(properties.getProperty(MgcpStackFactory.MGCP_PEER_PORT, "2427"));
			}

			InetAddress inetAddress;
			try {
				inetAddress = InetAddress.getByName(ip);

				int port = Integer.parseInt(portString);

				JainMgcpStackImpl stack = new JainMgcpStackImpl(inetAddress, port);
				JainMgcpStackProviderImpl jainMgcpStackProviderImpl = (JainMgcpStackProviderImpl) stack
						.createProvider();

				if (properties != null) {

				}

				NotifiedEntity notifiedEntity = new NotifiedEntity(inetAddress.getHostName(), inetAddress
						.getHostAddress(), stack.getPort());

				mgcpWrapper = new MgcpWrapper(jainMgcpStackProviderImpl, notifiedEntity, mgcpStackPeerPort, mgcpStackPeerIp);
				jainMgcpStackProviderImpl.addJainMgcpListener(mgcpWrapper);
				stringToMgcpWrapper.put(stackName, mgcpWrapper);

				if (logger.isDebugEnabled()) {
					logger.debug("Created new MgcpWrapper for MGCP_STACK_NAME = " + stackName);
				}

				return mgcpWrapper;
			} catch (UnknownHostException e) {
				logger.error(e);
			} catch (CreateProviderException e) {
				logger.error(e);
			} catch (TooManyListenersException e) {
				logger.error(e);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Found JainMgcpStackProvider for MGCP_STACK_NAME = " + stackName);
			}
			return mgcpWrapper;
		}

		return null;
	}

	public void clearMgcpStackProvider(Properties properties) {
		String stackName = "DEFAULT";
		if (properties != null) {
			stackName = properties.getProperty(MGCP_STACK_NAME, "DEFAULT");
		}
		MgcpWrapper mgcpWrapper = this.stringToMgcpWrapper.get(stackName);

		if (mgcpWrapper == null) {
			logger.warn("No JainMgcpStackProvider found for MGCP_STACK_NAME = " + stackName);
		} else {
			JainMgcpStackProviderImpl stackProvider = mgcpWrapper.getJainMgcpStackProvider();
			stackProvider.removeJainMgcpListener(mgcpWrapper);
			JainMgcpStack mgcpStack = stackProvider.getJainMgcpStack();
			try {
				mgcpStack.deleteProvider(mgcpWrapper.getJainMgcpStackProvider());
				this.stringToMgcpWrapper.remove(stackName);
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully deleted JainMgcpStackProvider for MGCP_STACK_NAME = " + stackName);
				}
			} catch (DeleteProviderException e) {
				logger.error(e);
			}
		}

	}
}
