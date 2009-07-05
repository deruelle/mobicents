package org.openxdm.xcap.client.test;

import java.io.IOException;
import java.util.Hashtable;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;
import org.junit.After;
import org.junit.Before;
import org.mobicents.slee.enabler.userprofile.jpa.jmx.UserProfileControlManagementMBean;
import org.openxdm.xcap.client.XCAPClient;
import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;

public abstract class AbstractXDMJunitTest {

	protected XCAPClient client = null;
	protected AppUsage appUsage = new ResourceListsAppUsage(null);
	protected String user = "sip:eduardo@openxdm.org";
	protected String documentName = "index";
	protected String password = "password";
	
	private ObjectName userProfileMBeanObjectName;
	private RMIAdaptor rmiAdaptor;
	
	
	private void initRmiAdaptor() throws NamingException, MalformedObjectNameException, NullPointerException {
		// Set Some JNDI Properties
		Hashtable env = new Hashtable();
		env.put(Context.PROVIDER_URL, "jnp://"+ServerConfiguration.SERVER_HOST+":1099");
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jnp.interfaces.NamingContextFactory");
		env.put(Context.URL_PKG_PREFIXES, "org.jnp.interfaces");

		InitialContext ctx = new InitialContext(env);
		rmiAdaptor = (RMIAdaptor) ctx.lookup("jmx/rmi/RMIAdaptor");
		userProfileMBeanObjectName = new ObjectName(UserProfileControlManagementMBean.MBEAN_NAME);
	}
	
	private void createUser(String user, String password) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		String sigs[] = { String.class.getName(), String.class.getName() };
		Object[] args = { user, password };
		rmiAdaptor.invoke(userProfileMBeanObjectName, "addUser", args, sigs);	
	}
	
	private void removeUser(String user) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		String sigs[] = { String.class.getName()};
		Object[] args = { user};
		rmiAdaptor.invoke(userProfileMBeanObjectName, "removeUser", args, sigs);	
	}
	
	@Before
	public void runBefore() throws IOException, InterruptedException, MalformedObjectNameException, NullPointerException, NamingException, InstanceNotFoundException, MBeanException, ReflectionException {
		client = ServerConfiguration.getXCAPClientInstance();	
		initRmiAdaptor();
		createUser(user, password);
		client.setAuthenticationCredentials(user, password);
		client.setDoAuthentication(true);
	}
	
	@After
	public void runAfter() throws IOException, InstanceNotFoundException, MBeanException, ReflectionException {
		if (client != null) {
			client.shutdown();
			client = null;
		}		
		removeUser(user);
		appUsage = null;
		user = null;
		documentName = null;
	}
	
}
