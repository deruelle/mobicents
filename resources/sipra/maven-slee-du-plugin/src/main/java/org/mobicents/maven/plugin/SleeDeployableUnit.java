/**
 * 
 */
package org.mobicents.maven.plugin;

import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author jean.deruelle@gmail.com
 *
 */
public interface SleeDeployableUnit {
	
    /**
     * Resolves the {@link Artifact} represented by the module with
     * the specified execution configuration.
     *
     * @param artifacts            the project's artifacts
     */
    public void resolveArtifact( Set artifacts)
        throws MojoFailureException;
}
