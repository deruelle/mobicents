package org.mobicents.maven.plugin.eclipse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;


/**
 * An 'OR' artifact filter.  That means if one of the artifact filters applies
 * include returns true.
 *
 * @author Chad Brandon
 */
public class OrArtifactFilter
    implements ArtifactFilter
{
    private final List filters = new ArrayList();

    /**
     * @see org.apache.maven.artifact.resolver.filter.ArtifactFilter#include(org.apache.maven.artifact.Artifact)
     */
    public boolean include(final Artifact artifact)
    {
        boolean include = false;
        for (final Iterator iterator = this.filters.iterator(); iterator.hasNext();)
        {
            ArtifactFilter filter = (ArtifactFilter)iterator.next();
            if (filter.include(artifact))
            {
                include = true;
                break;
            }
        }
        return include;
    }

    /**
     * Adds the artifact filter to be applied.
     *
     * @param artifactFilter
     */
    public void add(final ArtifactFilter artifactFilter)
    {
        this.filters.add(artifactFilter);
    }
}