package org.mobicents.jsr309.mgcp;

import jain.protocol.ip.mgcp.CreateProviderException;
import jain.protocol.ip.mgcp.JainMgcpProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.mobicents.mgcp.stack.JainMgcpStackImpl;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 * 
 * @author amit bhayani
 *
 */
public class MgcpStackFactory {
	private static MgcpStackFactory mgcpStackFactory;

	/**
	 * Store the MGCP Stack with name provided in HashMap. This gives
	 * flexibility for different applications to instantiate different stacks.
	 * Hence in case if one goes down or mis behaves not all applications have
	 * to pay penalty
	 */
	private ConcurrentHashMap<String, JainMgcpStackProviderImpl> stringToMgcpProvider = new ConcurrentHashMap<String, JainMgcpStackProviderImpl>();

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

	public JainMgcpStackProviderImpl getMgcpStackProvider(Properties properties) {

		String stackName = properties.getProperty(MGCP_STACK_NAME, "DEFAULT");
		JainMgcpStackProviderImpl jainMgcpStackProviderImpl = stringToMgcpProvider.get(stackName);

		if (jainMgcpStackProviderImpl == null) {
			String ip = properties.getProperty(MGCP_STACK_IP, "127.0.0.1");
			String portString = properties.getProperty(MGCP_STACK_PORT, "2727");

			InetAddress inetAddress;
			try {
				inetAddress = InetAddress.getByName(ip);

				int port = Integer.parseInt(portString);

				JainMgcpStackImpl stack = new JainMgcpStackImpl(inetAddress,
						port);
				jainMgcpStackProviderImpl = (JainMgcpStackProviderImpl)stack.createProvider();
				stringToMgcpProvider.put(stackName, jainMgcpStackProviderImpl);
				return jainMgcpStackProviderImpl;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CreateProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return jainMgcpStackProviderImpl;
		}

		return null;
	}

}
