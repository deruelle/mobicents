package org.openxdm.xcap.client.slee.resource;

import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.resource.ResourceAdaptor;

import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.ResourceAdaptorEntity;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

/**
 * @author Eduardo Martins
 * @author Neutel
 * @version 2.1
 *
 */

public class XCAPClientActivityContextInterfaceFactoryImpl implements
		ResourceAdaptorActivityContextInterfaceFactory,
		XCAPClientActivityContextInterfaceFactory {
	
    private final String jndiName = "java:slee/resources/xcapclientacif";
    private String raEntityName;
    private SleeContainer sleeContainer;
    private ActivityContextFactory activityContextFactory;

    public XCAPClientActivityContextInterfaceFactoryImpl(SleeContainer sleeContainer, String raEntityName) {
        this.sleeContainer = sleeContainer;
        this.activityContextFactory = sleeContainer.getActivityContextFactory();
        this.raEntityName = raEntityName;
    }
    
	public String getJndiName() {
		return jndiName;
	}

	public ActivityContextInterface getActivityContextInterface(XcapUriKeyActivity activity) throws NullPointerException,
	UnrecognizedActivityException, FactoryException {
		
		// if parameter is null throw exception
		if (activity == null) {
			throw new NullPointerException();
		}
		
		// create handle
		XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(activity.getXcapUriKey());
		
		// check if activity exists
		ResourceAdaptorEntity raEntity = sleeContainer.getResourceAdaptorEnitity(raEntityName);
		ResourceAdaptor ra  = raEntity.getResourceAdaptor();
				
		// if it doesn't exist throw exception
		if (ra.getActivity(handle) == null) {
			throw new UnrecognizedActivityException(activity);
		}
		else {
			return new ActivityContextInterfaceImpl(this.sleeContainer,
				this.activityContextFactory.getActivityContext(new SleeActivityHandle(raEntityName, handle, sleeContainer)).getActivityContextId());
		}
	}
	
	public ActivityContextInterface getActivityContextInterface(DocumentUriKeyActivity activity) throws NullPointerException,
	UnrecognizedActivityException, FactoryException {
		
		// if parameter is null throw exception
		if (activity == null) {
			throw new NullPointerException();
		}
		
		// create handle
		XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(activity.getDocumentUriKey());
		
		// check if activity exists
		ResourceAdaptorEntity raEntity = sleeContainer.getResourceAdaptorEnitity(raEntityName);
		ResourceAdaptor ra  = raEntity.getResourceAdaptor();
				
		// if it doesn't exist throw exception
		if (ra.getActivity(handle) == null) {
			throw new UnrecognizedActivityException(activity);
		}
		else {
			return new ActivityContextInterfaceImpl(this.sleeContainer,
				this.activityContextFactory.getActivityContext(new SleeActivityHandle(raEntityName, handle, sleeContainer)).getActivityContextId());
		}
	}
	
	public ActivityContextInterface getActivityContextInterface(ElementUriKeyActivity activity) throws NullPointerException,
	UnrecognizedActivityException, FactoryException {
		
		// if parameter is null throw exception
		if (activity == null) {
			throw new NullPointerException();
		}
		
		// create handle
		XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(activity.getElementUriKey());
		
		// check if activity exists
		ResourceAdaptorEntity raEntity = sleeContainer.getResourceAdaptorEnitity(raEntityName);
		ResourceAdaptor ra  = raEntity.getResourceAdaptor();
				
		// if it doesn't exist throw exception
		if (ra.getActivity(handle) == null) {
			throw new UnrecognizedActivityException(activity);
		}
		else {
			return new ActivityContextInterfaceImpl(this.sleeContainer,
				this.activityContextFactory.getActivityContext(new SleeActivityHandle(raEntityName, handle, sleeContainer)).getActivityContextId());
		}
	}

	public ActivityContextInterface getActivityContextInterface(AttributeUriKeyActivity activity) throws NullPointerException,
	UnrecognizedActivityException, FactoryException {
		
		// if parameter is null throw exception
		if (activity == null) {
			throw new NullPointerException();
		}
		
		// create handle
		XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(activity.getAttributeUriKey());
		
		// check if activity exists
		ResourceAdaptorEntity raEntity = sleeContainer.getResourceAdaptorEnitity(raEntityName);
		ResourceAdaptor ra  = raEntity.getResourceAdaptor();
				
		// if it doesn't exist throw exception
		if (ra.getActivity(handle) == null) {
			throw new UnrecognizedActivityException(activity);
		}
		else {
			return new ActivityContextInterfaceImpl(this.sleeContainer,
				this.activityContextFactory.getActivityContext(new SleeActivityHandle(raEntityName, handle, sleeContainer)).getActivityContextId());
		}
	}
	
	public ActivityContextInterface getActivityContextInterface(NamespaceBindingsUriKeyActivity activity) throws NullPointerException,
	UnrecognizedActivityException, FactoryException {
		
		// if parameter is null throw exception
		if (activity == null) {
			throw new NullPointerException();
		}
		
		// create handle
		XCAPResourceAdaptorActivityHandle handle = new XCAPResourceAdaptorActivityHandle(activity.getNamespaceBindingsUriKey());
		
		// check if activity exists
		ResourceAdaptorEntity raEntity = sleeContainer.getResourceAdaptorEnitity(raEntityName);
		ResourceAdaptor ra  = raEntity.getResourceAdaptor();
				
		// if it doesn't exist throw exception
		if (ra.getActivity(handle) == null) {
			throw new UnrecognizedActivityException(activity);
		}
		else {
			return new ActivityContextInterfaceImpl(this.sleeContainer,
				this.activityContextFactory.getActivityContext(new SleeActivityHandle(raEntityName, handle, sleeContainer)).getActivityContextId());
		}
	}
}
