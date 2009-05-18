/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.bootstrap;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;

/**
 * 
 * @author kulikov
 * @author amit bhayani
 */
public class MainDeployer {

	private Kernel kernel;
	private BasicXMLDeployer kernelDeployer;

	private int scanPeriod;
	private int initialDelay;

	private FileFilter fileFilter;

	private String path;
	private HashMap<URL, Long> deployments = new HashMap();
	private ScheduledExecutorService executor = null;
	private ScheduledFuture activeScan;
	private Logger logger = Logger.getLogger(MainDeployer.class);

	public MainDeployer() {
		executor = Executors.newSingleThreadScheduledExecutor(new ScannerThreadFactory());
	}

	public int getScanPeriod() {
		return scanPeriod;
	}

	public void setScanPeriod(int scanPeriod) {
		this.scanPeriod = scanPeriod;
	}

	public int getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(int delay) {
		this.initialDelay = delay;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public void start(Kernel kernel, BasicXMLDeployer kernelDeployer) {
		this.kernel = kernel;
		this.kernelDeployer = kernelDeployer;

		activeScan = executor.scheduleAtFixedRate(new HDScanner(), initialDelay, scanPeriod, TimeUnit.MILLISECONDS);
		logger.info("Successfuly started");
	}

	public void stop() {
		if (activeScan != null) {
			activeScan.cancel(true);
		}
		logger.info("Stopped");
	}

	private void deploy(URL url) throws Throwable {
		kernelDeployer.deploy(url);
		kernelDeployer.validate();
	}

	private void undeploy(URL url) {
		kernelDeployer.undeploy(url);
	}

	private void redeploy(URL url) throws Throwable {
		undeploy(url);
		deploy(url);
	}

	private String getFilePath(File file) {
		return System.getProperty("mms.home.dir") + "/deploy/" + file.getName();
	}

	private Collection<URL> getNew(File[] files) {
		ArrayList<URL> list = new ArrayList();
		for (File f : files) {
			try {
				if (this.fileFilter.accept(f)) {
					URL url = f.toURI().toURL();
					if (!deployments.containsKey(url)) {
						deployments.put(url, f.lastModified());
						list.add(url);
					}
				}
			} catch (MalformedURLException e) {
			}
		}

		return list;
	}

	private Collection<URL> getRemoved(File[] files) {
		List<URL> removed = new ArrayList();
		Set<URL> names = deployments.keySet();

		for (URL url : names) {
			boolean found = false;
			for (int i = 0; i < files.length; i++) {
				try {
					if (url.equals(files[i].toURI().toURL())) {
						found = true;
						break;
					}
				} catch (MalformedURLException e) {
				}
			}

			if (!found) {
				removed.add(url);
			}
		}

		for (URL url : removed) {
			deployments.remove(url);
		}

		return removed;
	}

	private Collection<URL> getUpdates(File[] files) {
		ArrayList<URL> list = new ArrayList();
		for (File f : files) {
			try {
				if (this.fileFilter.accept(f)) {
					URL name = f.toURI().toURL();
					if (deployments.containsKey(name)) {
						long lastModified = (Long) deployments.get(name);
						if (lastModified < f.lastModified()) {
							deployments.put(name, f.lastModified());
							list.add(name);
						}
					}
				}
			} catch (MalformedURLException e) {
			}
		}
		return list;
	}

	private class HDScanner implements Runnable {

		public void run() {
			File dir = new File(path);
			File[] files = dir.listFiles();

			// deploying new
			Collection<URL> list = getNew(files);
			if (!list.isEmpty()) {
				for (URL fileName : list) {
					logger.info("Deploying " + fileName);
					try {
						deploy(fileName);
						logger.info("Deployed " + fileName);
					} catch (Throwable t) {
						logger.error("Could not deploy " + fileName, t);
					}
				}
			}

			// undeploying
			list = getRemoved(files);
			if (!list.isEmpty()) {
				for (URL fileName : list) {
					logger.info("Undeploying " + fileName);
					try {
						undeploy(fileName);
						logger.info("Udeployed " + fileName);
					} catch (Throwable t) {
						logger.error("Could not undeploy " + fileName, t);
					}
				}
			}

			// redeploying
			list = getUpdates(files);
			if (!list.isEmpty()) {
				for (URL fileName : list) {
					logger.info("Redeploying " + fileName);
					try {
						redeploy(fileName);
						logger.info("Redeployed " + fileName);
					} catch (Throwable t) {
						logger.error("Could not redeploy " + fileName, t);
					}
				}
			}
		}
	}

	private class ScannerThreadFactory implements ThreadFactory {

		public Thread newThread(Runnable r) {
			return new Thread(r, "MMSDeployerScanner");
		}
	}

}
