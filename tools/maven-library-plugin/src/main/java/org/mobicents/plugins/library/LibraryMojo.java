package org.mobicents.plugins.library;

import java.io.File;

/**
 * Build a Library jar from the current project.
 *
 * @author <a href="brainslog@gmail.com"> Alexandre Mendonca </a>
 * @version $Id$
 * 
 * @goal create
 * @phase package
 * @requiresProject
 * @requiresDependencyResolution runtime
 * @aggregator
 */
public class LibraryMojo extends AbstractLibraryMojo
{
    /**
     * Directory containing the classes.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;

    /**
     * Classifier to add to the artifact generated. If given, the artifact will be an attachment instead.
     *
     * @parameter
     */
    private String classifier;

    protected String getClassifier()
    {
        return classifier;
    }

    /**
     * @return type of the generated artifact
     */
    protected String getType()
    {
        return "jar";
    }

    /**
     * Return the main classes directory, so it's used as the root of the jar.
     */
    protected File getClassesDirectory()
    {
        return classesDirectory;
    }
}
