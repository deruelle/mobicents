/**
 * 
 */
package org.mobicents.maven.plugin;

import java.util.Iterator;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author jean.deruelle@gmail.com
 * 
 */
public abstract class AbstractSleeDeployableUnit implements SleeDeployableUnit {

	// Those are set by the configuration
	private String groupId;

	private String artifactId;

	protected String bundleDir;	

	/**
	 * Empty constructor to be used when the deployable unit is built based on the
	 * configuration.
	 */
	public AbstractSleeDeployableUnit() {
	}

	/**
	 * Method checking if this deployable unit is defined in the pom in the set of artifacts in parameter
	 * @param artifacts the dependencies of the projects
	 * @throws MojoFailureException if the deplyable unit is not a dependency
	 */
	public void resolveArtifact(Set artifacts) throws MojoFailureException {
		
			if (groupId == null || artifactId == null) {
				throw new MojoFailureException("Could not resolve artifact["
						+ groupId + ":" + artifactId + ":" + getType() + "]");
			}

			Iterator i = artifacts.iterator();
			while (i.hasNext()) {
				Artifact a = (Artifact) i.next();
				if (a.getGroupId().equals(groupId)
						&& a.getArtifactId().equals(artifactId))
				{					
					return;
				}
			}

			// Artifact has not been found
			throw new MojoFailureException("Artifact[" + groupId + ":"
					+ artifactId + ":" + getType() + "] "
					+ "is not a dependency of the project.");
		
	}

	/**
	 * Returns the type associated to the deployable unit.
	 * 
	 * @return the artifact's type of the deployable unit
	 */
	protected abstract String getType();	

	/**
	 * Returns the artifact's groupId.
	 * 
	 * @return the group Id
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Returns the artifact's Id.
	 * 
	 * @return the artifact Id
	 */
	public String getArtifactId() {
		return artifactId;
	}

	/**
	 * Returns the bundle directory. If null, the deployable unit is bundled in the root
	 * of the JAR.
	 * 
	 * @return the custom bundle directory
	 */
	public String getBundleDir() {
		return bundleDir;
	}	

	/**
	 * @inheritDoc
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getType()).append(":").append(groupId).append(":").append(
				artifactId);
		
		return sb.toString();
	}

}
