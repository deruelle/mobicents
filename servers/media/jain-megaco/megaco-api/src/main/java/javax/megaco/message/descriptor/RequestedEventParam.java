package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.pkg.PkgEventItem;
import javax.megaco.pkg.PkgItemStr;

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

	private PkgEventItem eventItem = null;
	private PkgItemStr itemStr = null;
	private EmbedFirstEventParam embedFirstEventParam = null;
	private EventParam eventParam = null;

	/**
	 * 
	 * Constructs Requested event parameter object with the {@link PkgEventItem}
	 * object. This is used to set the first level event parameters.
	 * 
	 * @param eventItem
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid eventItem object reference is set.
	 */
	public RequestedEventParam(PkgEventItem eventItem) throws IllegalArgumentException {

		// FIXME: add error throw ? - what does invalind even item reference
		// mean ?

		this.eventItem = eventItem;
	}

	/**
	 * 
	 * Constructs Requested event parameter object with the PkgItemStr object.
	 * This method would be used if the package parameters are to be conveyed in
	 * the string format. This is used to set the first level event parameters.
	 * 
	 * @param eventItemStr
	 * @throws IllegalArgumentException
	 *             IllegalArgumentException - Thrown if an invalid eventItemStr
	 *             object reference is set.
	 */
	public RequestedEventParam(PkgItemStr eventItemStr) throws IllegalArgumentException {
		// FIXME: add error throw ?
		this.itemStr = eventItemStr;
	}

	/**
	 * The method can be used the to retrieve the embedded event information.
	 * 
	 * @return embedFirst - The object identifier corresponding to the embedded
	 *         first. This object interface may optionally be there. If
	 *         emberFirstEventParam is not set, then this method would return
	 *         NULL.
	 */
	public EmbedFirstEventParam getEmbedFirstEventParam() {
		return this.embedFirstEventParam;
	}

	/**
	 * The method can be used the to set the object reference to the embeded
	 * event which has reference to the its event parameters, request id, and
	 * its package object. This method validates whether the package name, event
	 * id and the parameter can be set for the embedded event descriptor. In
	 * case of an error, an exception is raised.
	 * 
	 * @param embedEvent
	 *            - The objectIdentifier corresponding to the embedded event
	 *            descriptor.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of embed first
	 *             event parameter passed to this method is NULL.
	 */
	public void setEmbedFirstEventParam(EmbedFirstEventParam embedEvent) throws IllegalArgumentException {

		if (embedEvent == null) {
			throw new IllegalArgumentException();
		}
		this.embedFirstEventParam = embedEvent;

	}

	/**
	 * The method can be used the to retrieve the parameters corresponding to
	 * whether the keep alive token is set, the digit map name or digit map
	 * value is set or the embeded signal is set or stream id is set.
	 * 
	 * @return EventParam - object identifier corresponding to the event
	 *         parameters corresponding to the non embedded event id. This
	 *         object interface may optionally be there. If the event parameter
	 *         is not set then this returns a NULL value.
	 */
	public EventParam getEventParam() {

		return this.eventParam;
	}

	/**
	 * The method can be used the to set the object reference to the event
	 * parameter which has reference to whether keepactive token is present,
	 * signal descriptor, digit map descriptor stream id. In case of an error,
	 * an exception is raised.
	 * 
	 * @param eventParam
	 *            - The objectIdentifier corresponding to the event paramater of
	 *            first level event.
	 * @throws IllegalArgumentException
	 *             - Thrown if an illegal event is set in the embedded event
	 *             descriptor.
	 */
	public void setEventParam(EventParam eventParam) throws IllegalArgumentException {

		// FIXME: add checks
		this.eventParam = eventParam;

	}

	/**
	 * The method can be used to get the package name in the Event descriptor.
	 * This method gives the package information, the attached event information
	 * and the parameter name and value for the event id.
	 * 
	 * @return package - The object reference for the Event Item. This has the
	 *         object reference of corresponding megaco package and has the
	 *         reference of the parameter info associated with it. If the
	 *         parameter has not been set, then this method shall return NULL.
	 */
	public PkgEventItem getMegacoPkgEventItem() {
		return this.eventItem;
	}

	/**
	 * The method can be used to get the pkdgName as set in the Event
	 * descriptor. This method gives the package information, the attached event
	 * information and the parameter name and value. Compared to the
	 * getMegacoPkgEventItem() method, this method returnes the package
	 * parameters in the string format.
	 * 
	 * @return The object reference for the megaco package item. This class
	 *         holds information about package name, item name and the
	 *         parameters in the string format. If the parameter has not been
	 *         set, then this method shall return NULL.
	 */
	public PkgItemStr getMegacoPkgItemStr() {
		return this.itemStr;
	}

}
