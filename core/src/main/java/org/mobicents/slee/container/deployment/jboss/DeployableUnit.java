package org.mobicents.slee.container.deployment.jboss;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.slee.ComponentID;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jboss.deployment.DeploymentInfo;
import org.jboss.logging.Logger;
import org.jboss.system.server.ServerConfig;
import org.jboss.system.server.ServerConfigLocator;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.container.component.ComponentIDImpl;
import org.mobicents.slee.container.component.DeployableUnitDescriptorImpl;
import org.mobicents.slee.runtime.transaction.SleeTransactionManager;

/**
 * This class represents a SLEE Deployable Unit, represented by a collection of
 * Deployable Components. Contains all the DU dependencies, install/uninstall
 * actions needed for the DU and post-install and pre-uninstall actions.
 * 
 * @author Alexandre Mendonça
 * @version 1.0
 */
public class DeployableUnit
{
  // The logger.
  private static Logger logger = Logger.getLogger( DeployableUnit.class );
  
  // The deployment info of this DU
  private DeploymentInfo duDeploymentInfo;
  
  // The deployment manager in charge of this DU
  private DeploymentManager deploymentManager;
  
  // A collection of the Deployable Components in this DU
  private Collection<DeployableComponent> components = new ArrayList<DeployableComponent>();
  
  // A collection of the IDs of the components in this DU.
  private Collection<String> componentIDs = new ArrayList<String>();
  
  // A collection of the IDs of the components that this DU depends on. 
  private Collection<String> dependencies = new ArrayList<String>();
  
  // The install actions needed to install/activate this DU components.
  private Collection<String[]> installActions = new ArrayList<String[]>();

  // The post-install actions needed to install/activate this DU components.
  private HashMap<String, Collection<String[]>> postInstallActions = new HashMap<String, Collection<String[]>>();

  // The pre-uninstall actions needed to deactivate/uninstall this DU components.
  private HashMap<String, Collection<String[]>> preUninstallActions = new HashMap<String, Collection<String[]>>();

  // The install actions needed to deactivate/uninstall this DU components.
  private Collection<String[]> uninstallActions = new ArrayList<String[]>();

  // A flag indicating wether this DU is installed
  private boolean isInstalled = false;
  
  /**
   * Constructor.
   * @param duDeploymentInfo this DU deployment info.
   * @param deploymentManager the DeploymentManager in charge of this DU.
   * @throws Exception 
   */
  public DeployableUnit( DeploymentInfo duDeploymentInfo, DeploymentManager deploymentManager ) throws Exception
  {
    this.duDeploymentInfo = duDeploymentInfo;
    
    this.deploymentManager = deploymentManager;
    
    // First action for the DU is always install.
    installActions.add( new String[] {"-install", duDeploymentInfo.url.toString()} );
    
    // Parse the deploy-config.xml to obtain post-install/pre-uninstall actions
    parseDeployConfig();
  }

  /**
   * Adder method for a Deployable Component.
   * @param dc the deployable component object.
   */
  public void addComponent( DeployableComponent dc )
  {
    if( logger.isDebugEnabled() )
      logger.debug( "Adding Component " + dc.getComponentKey() );
    
    // Add the component ..
    components.add( dc );
    
    // .. the key ..
    componentIDs.add( dc.getComponentKey() );
    
    // .. the dependencies ..
    dependencies.addAll( dc.getDependencies() );
    
    // .. the install actions to be taken ..
    installActions.addAll( dc.getInstallActions() );
    
    // .. post-install actions (if any) ..
    Collection<String[]> postInstallActionsStrings = postInstallActions.get( dc.getComponentKey() );
    
    if( postInstallActionsStrings != null && postInstallActions.size() > 0 )
    {
      installActions.addAll( postInstallActionsStrings );
    }
    else if( dc.getComponentType() == DeployableComponent.RA_COMPONENT )
    {
      String raID = dc.getComponentKey();
      
      logger.warn( "\r\n------------------------------------------------------------" +
          "\r\nNo RA Entity and Link config for " + raID + " found. Using default values!" +
          "\r\n------------------------------------------------------------" );

      String raName = raID.substring( raID.indexOf( '[' ) + 1, raID.indexOf( '#' ) );
      
      // Add the default Create and Activate RA Entity actions to the Install Actions
      installActions.add( new String[] { "-createRaEntity", raID, raName, null } );
      installActions.add( new String[] { "-activateRaEntity", raName } );
      
      // Create default link
      installActions.add( new String[] { "-createRaLink", raName, raName } );
      
      // Remove default link
      uninstallActions.add( new String[] { "-removeRaLink", raName } );
      
      // Add the default Deactivate and Remove RA Entity actions to the Uninstall Actions
      uninstallActions.add( new String[] { "-deactivateRaEntity", raName } );
      uninstallActions.add( new String[] { "-removeRaEntity", raName } );
    }
    
    // .. pre-uninstall actions (if any) ..
    Collection<String[]> preUninstallActionsStrings = preUninstallActions.get( dc.getComponentKey() );
    
    if( preUninstallActionsStrings != null )
      uninstallActions.addAll( preUninstallActionsStrings );
    
    // .. and finally the uninstall actions to the DU.
    uninstallActions.addAll( dc.getUninstallActions() );
  }
  
  /**
   * Method for checking if DU is self-sufficient.
   * @return true if the DU has no external dependencies.
   */
  public boolean isSelfSufficient()
  {
    // All dependencies in the DU components?
    return componentIDs.containsAll( dependencies );
  }
  
  /**
   * Method for obtaining the external dependencies for this DU, if any.
   * @return a Collection of external dependencies identifiers.
   */
  public Collection<String> getExternalDependencies()
  {
    // Take all dependencies...
    Collection<String> externalDependencies = dependencies;
    
    // Remove those which are contained in this DU
    externalDependencies.removeAll( componentIDs );
    
    // Return what's left.
    return externalDependencies;
  }
  
  /**
   * Method for checking if the DU has all the dependencies needed to be deployed.
   * @param showMissing param to set whether to show or not missing dependencies.
   * @return true if all the dependencies are satisfied.
   */
  public boolean hasDependenciesSatisfied( boolean showMissing )
  {
    // First of all check if it is self-sufficient
    if( isSelfSufficient() )
      return true;
    
    // If not self-sufficient, get the remaining dependencies
    Collection<String> externalDependencies = getExternalDependencies();
    
    // Remove those that are already installed...
    externalDependencies.removeAll( deploymentManager.getDeployedComponents() );
    
    // Some reamining?
    if( externalDependencies.size() > 0 )
    {
      if( showMissing )
      {
        // List them to the user...
        String missingDepList = "";
        
        for( String missingDep : externalDependencies )
          missingDepList += "\r\n +-- " + missingDep;
        
        logger.info( "Missing dependencies for " + this.duDeploymentInfo.shortName + ":" + missingDepList );
      }
      
      // Return dependencies not satified.
      return false;
    }
    
    // OK, dependencies satisfied!
    return true;
  }
  
  /**
   * Method for checking if this DU contains any component that is already deployed.
   * @return true if there's a component that is already deployed.
   */
  public boolean hasDuplicates()
  {
    // For each component in the DU ..
    for( String componentId : componentIDs )
    {
      // Check if it is already deployed
      if( deploymentManager.getDeployedComponents().contains( componentId ) )
        return true;
    }
    
    // If we got here, there's no dups.
    return false;
  }
  
  /**
   * Method for doing all the checking to make sure it is ready to be installed.
   * @param showMissing param to set whether to show or not missing dependencies.
   * @return true if all the pre-reqs are met.
   */
  public boolean isReadyToInstall( boolean showMissing )
  {
    // Check if the deps are satisfied and there are no dups.
    return hasDependenciesSatisfied( showMissing ) && !hasDuplicates();
  }

  /**
   * Getter for the Install Actions.
   * @return a Collection of actions.
   */
  public Collection<String[]> getInstallActions()
  {
    return installActions;
  }

  /**
   * Getter for the Uninstall Actions.
   * @return a Collection of actions.
   */
  public Collection<String[]> getUninstallActions()
  {
    // To make sure uninstall is the last action...
    Collection<String[]> uActions = uninstallActions;
    
    // We add it just when we return them.
    uActions.add( new String[] {"-uninstall", duDeploymentInfo.url.toString()} );
    
    return uActions;
  }

  /**
   * Getter for this DU components.
   * @return a Collection of component identifiers.
   */
  public Collection<String> getComponents()
  {
    return componentIDs;
  }

  /**
   * Method for doing all the checking to make sure it is ready to be uninstalled.
   * @return true if all the pre-reqs are met.
   * @throws Exception
   */
  public boolean isReadyToUninstall() throws Exception
  {
    // Check DU for readiness ..
    if( isInstalled && !hasReferringDU() )
    {
      // Check each DU for it's readiness also
      for( DeployableComponent dc : components )
      {
        if( !dc.isUndeployable() )
          return false;
      }
    }
    else
    {
      return false;
    }
    
    // It's good to go.
    return true;
  }
  
  /**
   * Method for checking if this DU components are referred by any others.
   * @return true if there are other DUs installed referring this.
   * @throws Exception
   */
  private boolean hasReferringDU() throws Exception
  {
    // Get transaction manager
    SleeTransactionManager tm = SleeContainer.getTransactionManager();

    try
    {
      // Get SleeContainer instance from JNDI
      SleeContainer sC = SleeContainer.lookupFromJndi();

      // Beging transaction (needed)
      tm.begin();

      // Get this DU Descriptor
      DeployableUnitDescriptorImpl dudesc = (DeployableUnitDescriptorImpl) sC.getDeploymentManager().getDeployableUnitIDtoDescriptorMap().get( sC.getDeployableUnitIDFromUrl( duDeploymentInfo.url.toString() ) );

      // Get the Components IDs from the descriptor
      ComponentID[] cid = dudesc.getComponents();
      
      // For each component...
      for( int i = 0; i < cid.length; i++ )
      {
        // Get the DUs related to the component
        Set hset = (Set) sC.getDeploymentManager().getComponentIDToDeployableUnitIDMap().get( cid[i] );
        
        // If we get null, there's none.
        if( hset == null )
        {
          return false;
        }
        else if( hset.size() != 1 )
        {
          // See if there's another DU that refers directly to me.
          return dudesc.hasInstalledComponent( cid[i] );
        }
        else
        {
          // I'm the only one that references this component.
          ComponentID[] referringComponents = sC.getReferringComponents( cid[i] );
          
          // See if there's any other component that refers to me that belongs to another DU.
          if( referringComponents != null )
          {
            for( int k = 0; k < referringComponents.length; k++ )
            {
              if( !( (Set) sC.getDeploymentManager().getComponentIDToDeployableUnitIDMap().get( referringComponents[k] ) ).contains( dudesc.getDeployableUnit() ) )
              {
                return true;
              }
            }
          }
        }
      }
      
      // If we got here
      return false;
    }
    catch ( Exception ex )
    {
      tm.setRollbackOnly();
      throw ex;
    }
    finally
    {
      tm.commit();
    }
  }

  /**
   * Getter for the DeploymentInfo object.
   * @return the DeploymentInfo object.
   */
  public DeploymentInfo getDeploymentInfo()
  {
    return duDeploymentInfo;
  }

  /**
   * Setter for the isInstalled flag.
   * @return a boolean indicating if the DU is already installed.
   */
  public boolean isInstalled()
  {
    return isInstalled;
  }
  
  /**
   * Setter for the isInstalled flag.
   * @param isInstalled the isInstalled flag indicating that the DU is already installed.
   */
  public void setInstalled( boolean isInstalled )
  {
    this.isInstalled = isInstalled;
  }
  
  /**
   * Parser for the deployment config xml.
   * @throws Exception
   */
  private void parseDeployConfig() throws Exception
  {
    // Create a JarFile object
    JarFile componentJarFile = new JarFile( duDeploymentInfo.url.toString().replaceAll( "file:", "" ) );
    
    // Get the JarEntry for the deploy-config.xml
    JarEntry deployInfoXML = componentJarFile.getJarEntry( "META-INF/deploy-config.xml" );
      
    // If it exists, set an Input Stream on it 
    InputStream is = deployInfoXML != null ? componentJarFile.getInputStream( deployInfoXML ) : null;

    if( is != null )
    {
      /**
       * <deploy-config>
       *   <ra-entity resource-adaptor-id="HttpClientResourceAdaptor#org.mobicents#1.0" entity-name="HttpClientRA">
       *     <properties url="...">
       *       <property name="user" value="admin" />
       *       <property name="password" value="admin" />
       *     </properties>
       *     <ra-link name="HTTPClientRA" />
       *   </ra-entity>
       *   
       *   <ra-entity>
       *     <...>
       *   </ra-entity>
       * <deploy-config>
       **/
      
      // Read the file into a Document
      Document doc = new SAXReader().read( is );
      
      // By now we only care about <ra-entitu> nodes
      List<Element> raEntities = doc.getRootElement().selectNodes( "ra-entity" );
      
      // The RA identifier
      String raId = null;
      
      // The collection of Post-Install Actions
      Collection<String[]> cPostInstallActions = new ArrayList<String[]>();
      
      // The collection of Pre-Uninstall Actions
      Collection<String[]> cPreUninstallActions = new ArrayList<String[]>();
      
      // Iterate through each ra-entity node
      for(Element raEntity : raEntities)
      {
        // Get the component ID
        raId = ComponentIDImpl.RESOURCE_ADAPTOR_ID + "[" + raEntity.attributeValue( "resource-adaptor-id" ) + "]";
        
        // The RA Entity Name
        String entityName = raEntity.attributeValue( "entity-name" );
        
        // Select the properties node
        Node propsNode = raEntity.selectSingleNode( "properties" );
        
        // The URL for storing properties file
        String propsURL = null;
          
        // Do we have any properties at all?
        if( propsNode != null )
        {
          Properties props = new Properties();
          
          String propsFilename;
          
          // Do we have a properties file to load?
          if( ( propsFilename = ((Element)propsNode).attributeValue( "file" ) ) != null && !propsFilename.equals( "" ) )
          {
            // Get the entry from the jar
            JarEntry propsFile = componentJarFile.getJarEntry( "META-INF/" + propsFilename );
            
            // Load it.
            props.load( componentJarFile.getInputStream( propsFile ) );
          }
          
          // Select the property elements
          List<Element> propsList = propsNode.selectNodes( "property" );
          
          // For each element, add it to the Properties object
          for( Element property : propsList )
          {
            // If the property already exists, it will be overwritten.
            props.put( property.attributeValue( "name" ), property.attributeValue( "value" ) );
          }
          
          // Locate server configurations
          ServerConfig serverConfig = ServerConfigLocator.locate();
          
          // Get the server temp directory
          String serverTempDir = serverConfig.getServerTempDeployDir().toURL().toString().replaceFirst( "file:", "" );
          
          // Generate a property file name
          propsURL = serverTempDir + entityName + "_" + System.currentTimeMillis() + ".properties";
          
          // Store all the properties in a temporary file
          props.store( new FileOutputStream( propsURL ), null );
        }
        
        // Add the Create and Activate RA Entity actions to the Post-Install Actions
        cPostInstallActions.add( new String[] { "-createRaEntity", raId, entityName, propsURL != null ? "file:" + propsURL : null } );
        cPostInstallActions.add( new String[] { "-activateRaEntity", entityName } );
        
        // Each RA might have zero or more links.. get them
        List<Element> links = raEntity.selectNodes( "ra-link" );
        
        for( Element link : links )
        {
          String linkName = link.attributeValue( "name" );
          
          cPostInstallActions.add( new String[] { "-createRaLink", entityName, linkName } );
          
          cPreUninstallActions.add( new String[] { "-removeRaLink", linkName } );
        }
        
        // Add the Deactivate and Remove RA Entity actions to the Pre-Uninstall Actions
        cPreUninstallActions.add( new String[] { "-deactivateRaEntity", entityName } );
        cPreUninstallActions.add( new String[] { "-removeRaEntity", entityName } );
        
      }
      
      // Finally add the actions to the respective hashmap.
      if( raId != null )
      {
        postInstallActions.put( raId, cPostInstallActions );
        preUninstallActions.put( raId, cPreUninstallActions );
      }
    }
  }
}