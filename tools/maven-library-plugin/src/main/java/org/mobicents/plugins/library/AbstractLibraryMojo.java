package org.mobicents.plugins.library;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.mobicents.plugins.library.pojos.LibraryJar;
import org.mobicents.plugins.library.pojos.LibraryRef;

/**
 * Base class for creating a library jar.
 * 
 * @author <a href="brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class AbstractLibraryMojo extends AbstractMojo {

  private LibraryRef libraryId;

  private List<LibraryRef> libraryRefs = new ArrayList<LibraryRef>();
  private List<LibraryJar> libraryJars = new ArrayList<LibraryJar>();

  /**
   * Base directory.
   * 
   * @parameter expression="${basedir}"
   * @required
   * @readonly
   */
  private File basedir;

  // ///////////////////////////////////////////////
  // REACTOR
  // ///////////////////////////////////////////////

  /**
   * Project builder
   * 
   * @component
   */
  protected MavenProjectBuilder mavenProjectBuilder;

  /**
   * The local repository.
   * 
   * @parameter expression="${localRepository}"
   */
  protected ArtifactRepository localRepository;

  /**
   * Used to look up Artifacts in the remote repository.
   * 
   * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
   * @required
   * @readonly
   */
  protected org.apache.maven.artifact.factory.ArtifactFactory factory;


  /**
   * Used to look up Artifacts in the remote repository.
   * 
   * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
   * @required
   * @readonly
   */
  protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;

  /**
   * List of Remote Repositories used by the resolver
   * 
   * @parameter expression="${project.remoteArtifactRepositories}"
   * @readonly
   * @required
   */
  protected java.util.List remoteRepos;

  // ///////////////////////////////////////////////
  // RESOURCES
  // ///////////////////////////////////////////////
  /**
   * The list of resources we want to transfer.
   * 
   * @parameter expression="${project.resources}"
   * @required
   */
  private List resources;

  /**
   * The character encoding scheme to be applied.
   * 
   * @parameter
   */
  private String encoding;

  /**
   * The list of additional key-value pairs aside from that of the System, and that of the project, which would be
   * used for the filtering.
   * 
   * @parameter expression="${project.build.filters}"
   */
  private List filters;

  /**
   * @parameter expression="${component.org.apache.maven.artifact.installer.ArtifactInstaller}"
   * @required @ readonly
   */
  protected ArtifactInstaller installer;

  /**
   * Default artifact handler.
   * 
   * @parameter expression="${component.org.apache.maven.artifact.handler.ArtifactHandler}" @ readonly
   * @required
   */
  protected org.apache.maven.artifact.handler.ArtifactHandler artifactHandler;

  /**
   * The location of the deployment descriptor xml file to be used within the library jar.
   * 
   * @parameter expression="${basedir}/src/main/resources/META-INF/library-jar.xml"
   */
  private File libraryDD;

  // /////////////////////////////////////////////////////////////////////
  // JAR
  // /////////////////////////////////////////////////////////////////////

  /**
   * Directory containing the generated JAR.
   * 
   * @parameter expression="${project.build.outputDirectory}"
   * @required
   */
  private File outputDirectory;

  /**
   * Directory target.
   * 
   * @parameter expression="${project.build.output}"
   */
  private File targetDirectory;

  /**
   * Name of the generated JAR.
   * 
   * @parameter alias="jarName" expression="${jar.finalName}" default-value="${project.build.finalName}"
   * @required
   */
  private String finalName;

  /**
   * The Jar archiver.
   * 
   * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
   * @required
   */
  private JarArchiver jarArchiver;

  /**
   * The Maven project.
   * 
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * The archive configuration to use. See <a href="http://maven.apache.org/shared/maven-archiver/index.html">the
   * documentation for Maven Archiver</a>.
   * 
   * @parameter
   */
  private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

  /**
   * Path to the default MANIFEST file to use. It will be used if <code>useDefaultManifestFile</code> is set to
   * <code>true</code>.
   * 
   * @parameter expression="${project.build.outputDirectory}/META-INF/MANIFEST.MF"
   * @required
   * @readonly
   * @since 2.2
   */
  private File defaultManifestFile;

  /**
   * Set this to <code>true</code> to enable the use of the <code>defaultManifestFile</code>.
   * 
   * @parameter expression="${jar.useDefaultManifestFile}" default-value="false"
   * @since 2.2
   */
  private boolean useDefaultManifestFile;

  /**
   * @component
   */
  private MavenProjectHelper projectHelper;

  /**
   * Whether creating the archive should be forced.
   * 
   * @parameter expression="${jar.forceCreation}" default-value="false"
   */
  private boolean forceCreation;

  /**
   * Return the specific output directory to serve as the root for the archive.
   */
  // protected abstract File getClassesDirectory();

  protected final MavenProject getProject() {
    return project;
  }

  /**
   * Overload this to produce a jar with another classifier, for example a test-jar.
   */
  protected abstract String getClassifier();

  /**
   * Overload this to produce a test-jar, for example.
   */
  protected abstract String getType();

  protected static File getJarFile(File basedir, String finalName,
      String classifier) {
    if (classifier == null) {
      classifier = "";
    } else if (classifier.trim().length() > 0
        && !classifier.startsWith("-")) {
      classifier = "-" + classifier;
    }

    return new File(basedir, finalName + classifier + ".jar");
  }

  /**
   * Default Manifest location. Can point to a non existing file. Cannot return null.
   */
  protected File getDefaultManifestFile() {
    return defaultManifestFile;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.maven.plugin.AbstractMojo#execute()
   */
  public void execute() throws MojoExecutionException
  {
    if(getLog().isDebugEnabled())
    {
      getLog().debug("Executing Maven JAIN SLEE 1.1 Library Plugin");
    }
    
    // Initialize the plugin
    initialize();

    // Obtain the name#vendor#version from pom
    this.libraryId = getLibraryRef( project );

    if( this.libraryId == null )
    {
      throw new MojoExecutionException("Unable to get Library ID from pom, please verify.");
    }
    else
    {
      if(getLog().isDebugEnabled())
      {
        getLog().info( libraryId.getName() + "#" + libraryId.getVendor() + "#" + libraryId.getVersion() );
      }
    }

    for(Object dep : project.getDependencyArtifacts())
    {
      Artifact depArtifact = (Artifact) dep;

      String depGroupId = depArtifact.getGroupId();
      String depArtifactId = depArtifact.getArtifactId();
      String depVersion = depArtifact.getVersion();
      String depScope = depArtifact.getScope();

      if(depScope.equals("compile"))
      {
        this.libraryJars.add(new LibraryJar(depArtifact.getFile()));
      }
      else if(depScope.equals("runtime"))
      {
        MavenProject depPom = getDependency(depGroupId, depArtifactId, depVersion, "pom");
        LibraryRef depRef = getLibraryRef(depPom);
        if(depRef != null)
        {
          this.libraryRefs.add(depRef);
        }
      }

      getLog().info("I depend on: [" + depGroupId + "#" + depArtifactId + "#" + depVersion + "] @ " + depScope);

    }

    scanResources( new File("./src/main/resources/"), this.libraryJars );

    generateLibraryDeploymentDescritptor();

    copyJars();

    File libraryJar = createArchive();

    // verify is works
    String classifier = getClassifier();
    if (classifier != null)
    {
      projectHelper.attachArtifact(getProject(), getType(), classifier, libraryJar);
    }
    else
    {
      getProject().getArtifact().setFile(libraryJar);
    }

		// install in local repo
		try
		{
			Artifact duArtifact = new DefaultArtifact(project.getGroupId(), project.getArtifactId(), VersionRange.createFromVersion(project.getVersion()),
          Artifact.SCOPE_RUNTIME, "jar", getClassifier(), artifactHandler);

			duArtifact.setFile(libraryJar);

			installer.install(libraryJar, duArtifact, localRepository);

		}
		catch (ArtifactInstallationException e) {
			throw new MojoExecutionException("Cannot install library jar in local repository", e);
		}

  }

  /**
   * Initiaizes the plugin (directory init/creation)
   * 
   * @throws MojoExecutionException
   */
  private void initialize() throws MojoExecutionException
  {
    if (outputDirectory == null)
    {
      outputDirectory = new File(basedir.getAbsolutePath(), "target");
    } 
    if (!outputDirectory.exists())
    {
      outputDirectory.mkdirs();
    }    
  }

  /**
   * Generates the deployment descriptor for the library jar, based on the library-ref
   * element, library jars and jars present at resources folder.
   * 
   * @throws MojoExecutionException
   */
  private void generateLibraryDeploymentDescritptor() throws MojoExecutionException
  {
    File libraryDescriptorDir = new File(outputDirectory, "META-INF");

    if (!libraryDescriptorDir.exists())
    {
      libraryDescriptorDir.mkdirs();
    }

    File libraryDD = new File(libraryDescriptorDir.getAbsolutePath(), "library-jar.xml");

    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
    "<!DOCTYPE library-jar PUBLIC \r\n" +
    "\t\t\"-//Sun Microsystems, Inc.//DTD JAIN SLEE Library 1.1//EN\" \r\n" +
    "\t\t\"http://java.sun.com/dtd/slee-library-jar_1_1.dtd\">\r\n\r\n" +
    "<library-jar>\r\n\t<library>\r\n";

    xml += this.libraryId.toXmlEntry() + "\r\n";

    for(LibraryRef libraryRef : this.libraryRefs)
    {
      xml += libraryRef.toXmlEntryWithRef();
    }
    
    xml += "\r\n";
    
    for(LibraryJar libraryJar : this.libraryJars)
    {
      xml += libraryJar.toXmlEntry();
    }

    xml += "\t</library>\r\n</library-jar>\r\n";

    getLog().info("Generated Library descriptor: " + libraryDD.getAbsolutePath() + "\n" + xml + "\n");

    try
    {
      BufferedWriter out = new BufferedWriter(new FileWriter(libraryDD));
      out.write(xml);
      out.close();
    }
    catch ( IOException e )
    {
      getLog().error( "Failed to create deployment descriptor in " + libraryDD.getAbsolutePath(), e );
    }

    this.libraryDD = libraryDD;
  }

  /**
   * Gets the dependency identified by group, artifact id and version. If no packaging is defined, defaults to jar.
   * 
   * @param depGroupId
   * @param depArtifactId
   * @param depVersion
   * @param depPackaging
   */
  private MavenProject getDependency(String depGroupId, String depArtifactId,String depVersion, String depPackaging)
  {
    if(depPackaging == null || depPackaging.equals(""))
    {
      depPackaging = "jar";
    }

    try
    {
      Artifact pomArtifact = this.factory.createArtifact( depGroupId, depArtifactId, depVersion, "", depPackaging );             
      this.resolver.resolve( pomArtifact, remoteRepos, this.localRepository );
      MavenProject projectDependencyArtifactMavenProject = mavenProjectBuilder.buildFromRepository( pomArtifact, this.remoteRepos, this.localRepository, false );

      return projectDependencyArtifactMavenProject;
    }
    catch (Exception e) {
      getLog().error( "Failed to obtain dependency", e );
    }

    return null;
  }

  /**
   * Obtains the library-name, library-vendor, library-version into a LibraryRef element. 
   * 
   * @param mavenProject the maven project to obtain it from
   * @return
   */

  private LibraryRef getLibraryRef(MavenProject mavenProject)
  {
    String libraryName;
    String libraryVendor;
    String libraryVersion;

    String libraryDescription = null;

    for(Object pObject : mavenProject.getBuildPlugins())
    {
      Plugin plugin = (Plugin)pObject;
      if( plugin.getArtifactId().equals( "maven-library-plugin" ) )
      {
        Xpp3Dom configuration = (Xpp3Dom)plugin.getConfiguration();

        if(configuration != null)
        {
          if(configuration.getChildren("library-name").length > 0)
          {
            libraryName = configuration.getChildren("library-name")[0].getValue();
          }
          else
          {
            getLog().error( "Library Name missing in plugin configuration!" );
            return null;
          }

          if(configuration.getChildren("library-vendor").length > 0)
          {
            libraryVendor = configuration.getChildren("library-vendor")[0].getValue();
          }
          else
          {
            getLog().error( "Library Vendor missing in plugin configuration!" );
            return null;
          }

          if(configuration.getChildren("library-version").length > 0)
          {
            libraryVersion = configuration.getChildren("library-version")[0].getValue();
          }
          else
          {
            getLog().error( "Library Version missing in plugin configuration!" );
            return null;
          }

          if(configuration.getChildren("description").length > 0)
          {
            libraryDescription = configuration.getChildren("description")[0].getValue();
          }
        }
        else
        {
          getLog().error( "Configuration missing in plugin!" );
          return null;         
        }

        return new LibraryRef(libraryName, libraryVendor, libraryVersion, libraryDescription);    
      }
    }

    return null;
  }


  /**
   * Scans a folder in a recursive way to find jars to be included in the library.
   * 
   * @param file
   * @param jars
   */
  private void scanResources(File file, Collection<LibraryJar> jars)
  {
    if(getLog().isDebugEnabled())
    {
      getLog().debug( "Scanning " + file.getAbsolutePath() + " ..." );
    }

    final File[] children = file.listFiles();

    if (children != null)
    {
      for (File child : children)
      {
        if(!child.isDirectory() && child.getName().toLowerCase().endsWith(".jar"))
        {
          jars.add(new LibraryJar(child));
        }
        else if(child.isDirectory())
        {
          scanResources(child, jars);
        }
      }
    }
  }

  /**
   * Copies the jars from maven repository and/or src/main/resources folder to target folder.
   * 
   * @throws MojoExecutionException
   */
  private void copyJars() throws MojoExecutionException
  {
    try
    {
      File jarsFolder = new File(outputDirectory, "jars");

      if(!jarsFolder.exists())
      {
        jarsFolder.mkdir();
      }

      for(LibraryJar libJar : libraryJars)
      {
        FileUtils.copyFile( libJar.getFile(), new File(jarsFolder, libJar.getFile().getName()) );
      }
    }
    catch (Exception e) {
      throw new MojoExecutionException("Failed to copy jars to destination folder.", e);
    }
  }

  /**
   * Creates the library archive.
   * 
   * @return
   * @throws MojoExecutionException
   */
  public File createArchive() throws MojoExecutionException
  {
    try
    {
      if (targetDirectory == null)
      {
        targetDirectory = new File(basedir.getAbsolutePath(), "target");
      }
      if (!targetDirectory.exists())
      {
        targetDirectory.mkdir();
      }

      File jarFile = getJarFile(targetDirectory, finalName, getClassifier());

      MavenArchiver archiver = new MavenArchiver();
      archiver.setArchiver(jarArchiver);
      archiver.setOutputFile(jarFile);
      archive.setForced(forceCreation);

      // add output directory
      archiver.getArchiver().addDirectory(outputDirectory);
      // create archive
      archiver.createArchive(project, archive);

      return jarFile;
    }
    catch (Exception e) {
      throw new MojoExecutionException("Failed to create library jar.", e);
    }
  }

}
