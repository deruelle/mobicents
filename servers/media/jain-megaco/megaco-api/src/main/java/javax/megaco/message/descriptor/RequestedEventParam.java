package javax.megaco.message.descriptor;

import java.io.Serializable;

import javax.megaco.pkg.PkgEventItem;

/**
 * The RequestedEventParam object is a class that shall be used to set the event
 * params within the event descriptor. The class optionally provides interface
 * to specify the package name, item name and the associated parameters in the
 * string format. This is an independent class derived from java.util.Object and
 * shall not have any derived classes.
 * 
 * 
 */
public class RequestedEventParam implements Serializable {

	public RequestedEventParam(PkgEventItem eventItem)
			throws javax.megaco.InvalidArgumentException {

	}
}
