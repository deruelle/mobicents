package org.mobicents.maven.plugin.eclipse;

import java.io.File;
import java.io.FileWriter;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;


/**
 * Writes .project files for Eclipse.
 *
 * @author Chad Brandon
 */
public class ProjectWriter
    extends EclipseWriter
{
    public ProjectWriter(
        final MavenProject project,
        final Log logger)
    {
        super(project, logger);
    }

    /**
     * Writes the .classpath file for Eclipse.
     */
    public void write(String projectName)
        throws Exception
    {
        final File projectFile = this.getFile(".project");
        final FileWriter fileWriter = new FileWriter(projectFile);
        final XMLWriter writer = new PrettyPrintXMLWriter(fileWriter,"UTF-8",null);
        writer.startElement("projectDescription");
        writer.startElement("name");
        writer.writeText(projectName);
        writer.endElement();
        writer.startElement("comment");
        writer.endElement();
        writer.startElement("projects");
        writer.endElement();
        writer.startElement("buildSpec");
        writer.startElement("buildCommand");
        writer.startElement("name");
        writer.writeText("org.eclipse.jdt.core.javabuilder");
        writer.endElement();
        writer.startElement("arguments");
        writer.endElement();
        writer.endElement();
        writer.endElement();
        writer.startElement("natures");
        writer.startElement("nature");
        writer.writeText("org.eclipse.jdt.core.javanature");
        writer.endElement();
        writer.endElement();
        writer.endElement();
        IOUtil.close(fileWriter);
        this.logger.info("Project file written --> '" + projectFile + "'");
    }
}