/**
 * Start time:00:45:05 2009-02-04<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.container.component;

import java.util.HashSet;
import java.util.Set;

import javax.slee.ComponentID;
import javax.slee.EventTypeID;
import javax.slee.management.ComponentDescriptor;
import javax.slee.management.DependencyException;
import javax.slee.management.DeploymentException;
import javax.slee.management.LibraryID;
import javax.slee.resource.ResourceAdaptorTypeDescriptor;
import javax.slee.resource.ResourceAdaptorTypeID;

import org.mobicents.slee.container.component.deployment.jaxb.descriptors.ResourceAdaptorTypeDescriptorImpl;
import org.mobicents.slee.container.component.deployment.jaxb.descriptors.common.references.MEventTypeRef;
import org.mobicents.slee.container.component.deployment.jaxb.descriptors.common.references.MLibraryRef;
import org.mobicents.slee.container.component.deployment.jaxb.descriptors.ratype.MActivityType;

/**
 * Start time:00:45:05 2009-02-04<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ResourceAdaptorTypeComponent extends SleeComponent {

	/**
	 * the ratype descriptor
	 */
	private final ResourceAdaptorTypeDescriptorImpl descriptor;
	
	/**
	 * the aci factory interface
	 */
	private Class activityContextInterfaceFactoryInterface;
	
	/**
	 * the aci factory concrete class, generated by SLEE
	 */
	private Class activityContextInterfaceFactoryConcreteClass;
	  /**
	   * Resource adaptor SBB interface
	   */
	private Class resourceAdaptorSBBInterface = null;
	
	/**
	 * the JAIN SLEE specs descriptor
	 */
	private ResourceAdaptorTypeDescriptor specsDescriptor = null;
	
	/**
	 * the instance of the aci factory for this ra type
	 */
	private Object activityContextInterfaceFactory;
	
	/**
	 * 
	 * @param descriptor
	 */
	public ResourceAdaptorTypeComponent(
			ResourceAdaptorTypeDescriptorImpl descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * Retrieves the ratype descriptor
	 * @return
	 */
	public ResourceAdaptorTypeDescriptorImpl getDescriptor() {
		return descriptor;
	}

	/**
	 * Retrieves the ratype id
	 * @return
	 */
	public ResourceAdaptorTypeID getResourceAdaptorTypeID() {
		return descriptor.getResourceAdaptorTypeID();
	}

	/**
	 * Retrieves the aci factory concrete class, generated by SLEE
	 * @return
	 */
	public Class getActivityContextInterfaceFactoryConcreteClass() {
		return activityContextInterfaceFactoryConcreteClass;
	}
	
	/**
	 * Retrieves the aci factory interface
	 * @return
	 */
	public Class getActivityContextInterfaceFactoryInterface() {
		return activityContextInterfaceFactoryInterface;
	}
	
	/**
	 * Sets the aci factory concrete class, generated by SLEE
	 * @param activityContextInterfaceFactoryConcreteClass
	 */
	public void setActivityContextInterfaceFactoryConcreteClass(
			Class activityContextInterfaceFactoryConcreteClass) {
		this.activityContextInterfaceFactoryConcreteClass = activityContextInterfaceFactoryConcreteClass;
	}
	
	/**
	 * Sets the aci factory interface
	 * @param activityContextInterfaceFactoryInterface
	 */
	public void setActivityContextInterfaceFactoryInterface(
			Class activityContextInterfaceFactoryInterface) {
		this.activityContextInterfaceFactoryInterface = activityContextInterfaceFactoryInterface;
	}
	
	@Override
	boolean addToDeployableUnit() {
		return getDeployableUnit().getResourceAdaptorTypeComponents().put(getResourceAdaptorTypeID(), this) == null;
	}
	
	@Override
	public Set<ComponentID> getDependenciesSet() {
		return descriptor.getDependenciesSet();
	}
	
	@Override
	public boolean isSlee11() {
		return descriptor.isSlee11();
	}
	
	@Override
	public ComponentID getComponentID() {
		return getResourceAdaptorTypeID();
	}
	
	@Override
	public boolean validate() throws DependencyException, DeploymentException {
		// FIXME use validator when available
		return true;
	}

	public Class getResourceAdaptorSBBInterface() {
		return resourceAdaptorSBBInterface;
	}

	public void setResourceAdaptorSBBInterface(Class resourceAdaptorSBBInterface) {
		this.resourceAdaptorSBBInterface = resourceAdaptorSBBInterface;
	}
	
	/**
	 * Retrieves the instance of the aci factory for this ra type
	 * @return
	 */
	public Object getActivityContextInterfaceFactory() {
		return activityContextInterfaceFactory;
	}
	
	/**
	 * Sets the instance of the aci factory for this ra type
	 * @param activityContextInterfaceFactory
	 */
	public void setActivityContextInterfaceFactory(
			Object activityContextInterfaceFactory) {
		this.activityContextInterfaceFactory = activityContextInterfaceFactory;
	}
	
	/**
	 *  Retrieves the JAIN SLEE specs descriptor
	 * @return
	 */
	public ResourceAdaptorTypeDescriptor getSpecsDescriptor() {
		if (specsDescriptor == null) {
			Set<LibraryID> libraryIDSet = new HashSet<LibraryID>();
			for (MLibraryRef mLibraryRef : getDescriptor().getLibraryRefs()) {
				libraryIDSet.add(mLibraryRef.getComponentID());
			}
			LibraryID[] libraryIDs = libraryIDSet.toArray(new LibraryID[libraryIDSet.size()]);
			
			Set<String> activityTypeSet = new HashSet<String>();
			for (MActivityType mActivityType : getDescriptor().getActivityTypes()) {
				activityTypeSet.add(mActivityType.getActivityTypeName());
			}
			String[] activityTypes = activityTypeSet.toArray(new String[activityTypeSet.size()]);
			
			Set<EventTypeID> eventTypeSet = new HashSet<EventTypeID>();
			for (MEventTypeRef mEventTypeRef : getDescriptor().getEventTypeRefs()) {
				eventTypeSet.add(mEventTypeRef.getComponentID());
			}
			EventTypeID[] eventTypes = eventTypeSet.toArray(new EventTypeID[eventTypeSet.size()]);
			String raInterface = getDescriptor().getResourceAdaptorInterface() == null ? null : getDescriptor().getResourceAdaptorInterface().getResourceAdaptorInterfaceName();
			specsDescriptor = new ResourceAdaptorTypeDescriptor(getResourceAdaptorTypeID(),getDeployableUnit().getDeployableUnitID(),getDeploymentUnitSource(),libraryIDs,activityTypes,raInterface,eventTypes);
		}
		return specsDescriptor;
	}
	
	@Override
	public ComponentDescriptor getComponentDescriptor() {
		return getSpecsDescriptor();
	}
}
