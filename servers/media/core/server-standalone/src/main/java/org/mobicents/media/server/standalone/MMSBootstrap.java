package org.mobicents.media.server.standalone;

import java.net.URL;

import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.dependency.KernelController;
import org.jboss.kernel.spi.registry.KernelBus;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MMSBootstrap {

	private boolean useBus = false;
	private URL url;

	private EmbeddedBootstrap bootstrap;
	private Kernel kernel;
	private KernelController controller;
	private KernelBus bus;

	public static void main(String[] args) throws Exception {

		MMSBootstrap client = new MMSBootstrap();

	}

	public MMSBootstrap() throws Exception {
		this.useBus = useBus;

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		url = cl.getResource("jboss-beans.xml");

		// Start JBoss Microcontainer
		bootstrap = new EmbeddedBootstrap();
		bootstrap.run();

		kernel = bootstrap.getKernel();
		controller = kernel.getController();
		bus = kernel.getBus();

		bootstrap.deploy(url);

	}

	void deploy() {
		bootstrap.deploy(url);

	}

	void undeploy() {
		bootstrap.undeploy(url);
	}

}
