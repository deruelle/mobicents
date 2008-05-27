package org.mobicents.maven.plugin.utils;

import java.io.File;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;


/**
 * Contains ulitities for dealing with Maven projects.
 *
 * @author Chad Brandon
 */
public class ProjectUtils
{
    /**
     * Stores previously discovered projects.
     */
    private static final Map projectCache = new HashMap();

    /**
     * Gets a project for the given <code>pom</code>.
     *
     * @param pom the pom from which to build the project.
     * @return the built project.
     * @throws ProjectBuildingException
     */
    public static synchronized MavenProject getProject(
        final MavenProjectBuilder projectBuilder,
        final MavenSession session,
        final File pom,
        final Log logger)
        throws ProjectBuildingException
    {
        // - first attempt to get a project from the cache
        MavenProject project = (MavenProject)projectCache.get(pom);
        if (project == null)
        {
            // - next attempt to get the existing project from the session
            project = getProjectFromSession(
                    session,
                    pom);
            if (project == null)
            {
                // - if we didn't find it in the session, create it
                try
                {
                    project =
                        projectBuilder.build(
                            pom,
                            session.getLocalRepository(),
                            new DefaultProfileManager(session.getContainer()));
                    projectCache.put(
                        pom,
                        project);
                }
                catch (Exception ex)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Failed to build project from pom: " + pom, ex);
                    }
                }
            }
        }
        return project;
    }

    /**
     * The POM file name.
     */
    private static final String POM_FILE = "pom.xml";

    /**
     * Attempts to retrieve the Maven project for the given <code>pom</code>.
     *
     * @param pom the POM to find.
     * @return the maven project with the matching POM.
     */
    private static MavenProject getProjectFromSession(
        final MavenSession session,
        final File pom)
    {
        MavenProject foundProject = null;
        for (final Iterator projectIterator = session.getSortedProjects().iterator(); projectIterator.hasNext();)
        {
            final MavenProject project = (MavenProject)projectIterator.next();
            final File projectPom = new File(
                    project.getBasedir(),
                    POM_FILE);
            if (projectPom.equals(pom))
            {
                foundProject = project;
            }
        }
        return foundProject;
    }
}