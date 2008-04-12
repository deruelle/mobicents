package org.openxdm.xcap.client.slee.resource;

import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;

/**
 * @author Eduardo Martins
 * @version 1.0
 *
 */

public interface XCAPClientActivityContextInterfaceFactory {

    public ActivityContextInterface getActivityContextInterface(
            XcapUriKeyActivity activity) throws NullPointerException,
            UnrecognizedActivityException, FactoryException;
	
    public ActivityContextInterface getActivityContextInterface(
            DocumentUriKeyActivity activity) throws NullPointerException,
            UnrecognizedActivityException, FactoryException;
    
    public ActivityContextInterface getActivityContextInterface(
            ElementUriKeyActivity activity) throws NullPointerException,
            UnrecognizedActivityException, FactoryException;
    
    public ActivityContextInterface getActivityContextInterface(
            AttributeUriKeyActivity activity) throws NullPointerException,
            UnrecognizedActivityException, FactoryException;
    
    public ActivityContextInterface getActivityContextInterface(
            NamespaceBindingsUriKeyActivity activity) throws NullPointerException,
            UnrecognizedActivityException, FactoryException;
    
}
