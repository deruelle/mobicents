package org.mobicents.maven.plugin.eclipse;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.mobicents.maven.plugin.utils.PathNormalizer;

/**
 * Provides Eclipse configuration file writing
 * capabilities.
 * 
 * @author Chad Brandon
 */
public abstract class EclipseWriter
{
    protected Log logger;
    
    protected MavenProject project;

    public EclipseWriter(final MavenProject project, final Log logger)
    {
        this.project = project;
        this.logger = logger;
    }
    
    /**
     * Gets the project relative file given the <code>name</code> of the file.
     * 
     * @param name the name of the file.
     * @return the actual file instance.
     */
    protected File getFile(final String name)
    {
        final String rootDirectory = PathNormalizer.normalizePath(this.project.getBasedir().toString());
        final File file = new File(rootDirectory, name);
        return file;
    }

}
