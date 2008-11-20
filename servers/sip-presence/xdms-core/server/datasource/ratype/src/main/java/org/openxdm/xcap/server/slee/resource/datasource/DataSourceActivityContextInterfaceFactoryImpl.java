package org.openxdm.xcap.server.slee.resource.datasource;

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
 * @version 1.0
 *
 */

public class DataSourceActivityContextInterfaceFactoryImpl implements
		ResourceAdaptorActivityContextInterfaceFactory,
		DataSourceActivityContextInterfaceFactory {
	
    private final String jndiName = "java:slee/resources/datasourceacif";
    private String raEntityName;
    private SleeContainer sleeContainer;
    private ActivityContextFactory activityContextFactory;

    public DataSourceActivityContextInterfaceFactoryImpl(SleeContainer sleeContainer, String raEntityName) {
        this.sleeContainer = sleeContainer;
        this.activityContextFactory = sleeContainer.getActivityContextFactory();
        this.raEntityName = raEntityName;
    }
    
	public String getJndiName() {
		return jndiName;
	}

    public ActivityContextInterface getActivityContextInterface(DocumentActivity activity) throws NullPointerException,
	UnrecognizedActivityException, FactoryException {
    	return getACI(activity);
	}
    
    public ActivityContextInterface getActivityContextInterface(AppUsageActivity activity) throws NullPointerException,
	UnrecognizedActivityException, FactoryException {
		return getACI(activity);
	}
    
    private ActivityContextInterface getACI(ActivityObject activity) throws NullPointerException,
	UnrecognizedActivityException, FactoryException {
    	// if parameter is null throw exception
		if (activity == null) {
			throw new NullPointerException();
		}
				
		// check if activity exists
		ResourceAdaptorEntity raEntity;
		try {
			raEntity = sleeContainer.getResourceAdaptorEntity(raEntityName);
		} catch (Exception e) {
			throw new FactoryException("",e);
		}
		ResourceAdaptor ra  = raEntity.getResourceAdaptor();
				
		// if it doesn't exist throw exception
		ActivityHandle activityHandle = new ActivityHandle(activity.id);
		if (ra.getActivity(activityHandle) == null) {
			throw new UnrecognizedActivityException(activity);
		}
		else {
			return new ActivityContextInterfaceImpl(sleeContainer,
				this.activityContextFactory.getActivityContext(new SleeActivityHandle(raEntityName, activityHandle, sleeContainer)).getActivityContextId());
		}
    }

}

