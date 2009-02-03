package org.mobicents.media.server.standalone;

import java.net.URL;

import org.apache.log4j.Logger;
import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;

/**
 * 
 * @author amit bhayani
 * 
 */
public class EmbeddedBootstrap extends BasicBootstrap {

	public static final Logger log = Logger
			.getLogger(EmbeddedBootstrap.class);

	protected BasicXMLDeployer deployer;

	public EmbeddedBootstrap() throws Exception {
		super();
	}

	public void bootstrap() throws Throwable {
		super.bootstrap();
		deployer = new BasicXMLDeployer(getKernel());
		Runtime.getRuntime().addShutdownHook(new Shutdown());
	}

	public void deploy(URL url) {
		try {
			// Workaround the fact that the BasicXMLDeployer does not handle
			// redeployment correctly
			if (deployer.getDeploymentNames().contains(url.toString())) {
				log.warn("Service is already deployed.");
				return;
			}
			deployer.deploy(url);
		} catch (Throwable t) {
			log.warn("Error during deployment: " + url, t);
		}
	}

	public void undeploy(URL url) {
		if (!deployer.getDeploymentNames().contains(url.toString())) {
			log.warn("Service is already undeployed.");
			return;
		}
		try {
			deployer.undeploy(url);
		} catch (Throwable t) {
			log.warn("Error during undeployment: " + url, t);
		}
	}

	protected class Shutdown extends Thread {
		public void run() {
			log.info("Shutting down");
			deployer.shutdown();
		}
	}
}