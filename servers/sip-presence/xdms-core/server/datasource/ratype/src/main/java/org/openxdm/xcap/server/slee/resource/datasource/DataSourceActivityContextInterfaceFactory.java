package org.openxdm.xcap.server.slee.resource.datasource;

import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;

/**
 * @author Eduardo Martins
 * @version 1.0
 *
 */

public interface DataSourceActivityContextInterfaceFactory {

    public ActivityContextInterface getActivityContextInterface(
    		DocumentActivity activity) throws NullPointerException,
            UnrecognizedActivityException, FactoryException;
    
    public ActivityContextInterface getActivityContextInterface(
    		AppUsageActivity activity) throws NullPointerException,
            UnrecognizedActivityException, FactoryException;
    
}
