package org.mobicents.maven.plugin;

/**
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public class DeployableUnit extends AbstractSleeDeployableUnit {
	
	
	protected String getType() {
		// TODO Replace with constants
		return "jar";
	}

}
