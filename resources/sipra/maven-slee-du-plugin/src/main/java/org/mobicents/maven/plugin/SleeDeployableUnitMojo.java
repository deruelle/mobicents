package org.mobicents.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Core Plugin Implementation of the maven-slee-du-plugin.
 * 
 * This class allows one to use this plugin ot bundle JAIN-SLEE applications
 * in deployable units through maven in an easy and consistent manner.
 * 
 * Write environment information for the current build to file.
 * 
 * @goal slee-du
 * @phase package
 */
public class SleeDeployableUnitMojo extends AbstractMojo {
	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * The Jar archiver.
	 * 
	 * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
	 * @required
	 */
	private JarArchiver jarArchiver;

	/**
	 * The maven archive configuration to use.
	 * 
	 * @parameter
	 */
	private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();
	
	/**
	 * The name of the generated deployable unit.
	 * 
	 * @parameter expression="${project.build.finalName}"
	 * @optional
	 */
	private String targetName;

	/**
	 * The directory where the deployable unit is built.
	 * 
	 * @parameter expression="${project.build.directory}/${project.build.finalName}"
	 * @optional
	 */
	private File sleeDuDirectory;
	/**
	 * The directory where the deployable unit is built.
	 * 
	 * @parameter expression="${project.build.directory}/dist"
	 * @optional
	 */
	private File outputDirectory;
	/**
     * The directory where to put the third party libs.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}/library"
     * @optionnal
     */
    private File libraryDirectory;
    /**
     * The directory containing generated classes and resources.
     * Mandatory to have the deployable-unit.xml file when packaging
     * 
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private File classesDirectory;
    /**
     * The location of the deployable-unit.xml file.  If it is present in the META-INF
     * directory in src/main/resources with that name then it will automatically be 
     * included.  Otherwise this parameter must be set.
     * Will be auto-generated in the future.
     *
     * @parameter 
     */
    private File sleeDeployableUnitFile;

	/**
	 * The deployable Units configuration. This maps to the set of <deployableUnits> tags 
	 * in the configuration of the plugin located in the pom.xml
	 * 
	 * @parameter
	 */
	private DeployableUnit[] deployableUnits;

	public void execute() throws MojoExecutionException, MojoFailureException {		
		getLog().info("Packaging Slee Deployable Unit...");
		
		//Checking if the deployable units are set as dependencies
		//this make sure that they are present when the jar packaging will be performed
		for (int i = 0; i < deployableUnits.length; i++) {			
			deployableUnits[i].resolveArtifact(project.getArtifacts());
		}
		File sleeDuFile = new File(outputDirectory, targetName + ".jar");
		if(sleeDuFile.exists()) {
			sleeDuFile.delete();
		}
		//Assembing the slee deployable unit jar file
		try {
			performPackaging(sleeDuFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException(
					"Error assembling slee deployable unit", e);
		}

		getLog().info("Slee Deployable Unit Packaged.");
	}

	/**
	 * Generates the jar.
	 * 
	 * @param targetFile the target du jar file
	 * @throws IOException
	 * @throws ArchiverException
	 * @throws ManifestException
	 * @throws DependencyResolutionRequiredException
	 */
	private void performPackaging(File targetFile) throws IOException,
			ArchiverException, ManifestException,
			DependencyResolutionRequiredException, MojoExecutionException {
		
		//preparing for the packaging 
		buildExplodedSleeDu();		
		// generate deployable unit file
		getLog().info(
				"Generating slee deployable unit "
						+ targetFile.getAbsolutePath());
		jarArchiver.addDirectory(sleeDuDirectory);		
		MavenArchiver archiver = new MavenArchiver();
		archiver.setArchiver(jarArchiver);
		archiver.setOutputFile(targetFile);		
		
		// create archive		
		archiver.createArchive(project, archive);		
		project.getArtifact().setFile(targetFile);
	}

	/**
	 * Creates the directory structure that will be in the jar file
	 * and copy the necessary files 
	 * @throws MojoExecutionException
	 */
	private void buildExplodedSleeDu() throws MojoExecutionException {
		getLog().info( "Exploding slee deployable unit..." );        
        
        sleeDuDirectory.mkdirs();
        libraryDirectory.mkdirs();
        try
        {
            getLog().info( "Assembling slee deployable unit " + project.getArtifactId() + " in " + sleeDuDirectory );

            if ( classesDirectory.exists() && ( !classesDirectory.equals( sleeDuDirectory ) ) )
            {
                FileUtils.copyDirectoryStructure( classesDirectory, sleeDuDirectory );
            }
            
            File sleeDeployableUnitFileTarget = new File( sleeDuDirectory, "META-INF" );
            sleeDeployableUnitFileTarget = new File( sleeDeployableUnitFileTarget, "deployable-unit.xml" );
            if ( ! sleeDeployableUnitFileTarget.exists() )
            {
                if ( ! sleeDeployableUnitFileTarget.getParentFile().exists() )
                {
                    sleeDeployableUnitFileTarget.getParentFile().mkdirs();
                }
                
                if ( sleeDeployableUnitFile == null || ! sleeDeployableUnitFile.exists() )
                {
                    throw new MojoExecutionException( "Could not find the deployable-unit.xml file." );
                }
                else 
                {
                    FileUtils.copyFile( sleeDeployableUnitFile, sleeDeployableUnitFileTarget );
                }
            }
            
            Set artifacts = project.getArtifacts();            
            getLog().info( "");
            getLog().info( "    Including artifacts: ");
            getLog().info( "    -------------------");
            for (int i = 0; i < deployableUnits.length; i++) {			    			    		
            	for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
            	{
            		Artifact artifact = (Artifact) iter.next();
            		if ( artifact.getGroupId().equals( deployableUnits[i].getGroupId()) && artifact.getArtifactId().equals( deployableUnits[i].getArtifactId() ) ) {
            			getLog().info(deployableUnits[i].toString());
            			String bundleDir=deployableUnits[i].getBundleDir();
            			if(bundleDir!=null && bundleDir.length()>0) {
            				new File(sleeDuDirectory,bundleDir).mkdirs();
            				FileUtils.copyFileToDirectory( artifact.getFile(), new File(sleeDuDirectory,bundleDir));
            			}            			
            			else {
            				FileUtils.copyFileToDirectory( artifact.getFile(), sleeDuDirectory);
            			}
            		}                
            	}
            }
                        
            getLog().info( "" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Could not explode slee deployable unit...", e );
        }
	}
}
