package org.mobicents.maven.plugin.utils;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Stores projects ids.
 *
 * @author Chad Brandon
 */
public class Projects
{
    private Collection projects = new ArrayList();

    /**
     * The shared instance of this class.
     */
    private static Projects instance;

    /**
     * Retrieves the shared instance of this class.
     *
     * @return the shared instance.
     */
    public static Projects instance()
    {
        if (instance == null)
        {
            instance = new Projects();
        }
        return instance;
    }

    /**
     * Adds the project id to the store.
     *
     * @param projectId the project id.
     */
    public void add(final String projectId)
    {
        this.projects.add(projectId);
    }

    /**
     * Indicates whether or not the project is present.
     *
     * @param projectId the identifier of the project.
     * @return true/false
     */
    public synchronized boolean isPresent(final String projectId)
    {
        return projects.contains(projectId);
    }

    /**
     * Clears out any existing projects.
     */
    public void clear()
    {
        this.projects.clear();
        instance = null;
    }
}