package javax.megaco.message.descriptor;


import javax.megaco.pkg.PkgEventItem;
import javax.megaco.pkg.PkgItemStr;

/**
 * The class constructs the embedded event descriptor within an event
 * descriptor.
 */
public class EmbedFirstEventParam {

	private int requestId = -1;
	private PkgEventItem eventItem = null;
	private PkgItemStr itemStr = null;

	private EventParam eventParam = null;
	private PkgEventItem pkgEventItem = null;

	/**
	 * 
	 * Constructs a Embedded Event Descriptor with specific request identifier.
	 * 
	 * @param requestId
	 *            - An integer value specifying the request identifier, which
	 *            uniquely identifies the event.
	 * @throws IllegalArgumentException
	 *             - Thrown if request id is set to an invalid value.
	 */
	public EmbedFirstEventParam(int requestId) throws IllegalArgumentException {
		// FIXME: what is invalid value?
		this.requestId = requestId;

	}

	/**
	 * 
	 * Constructs a Embedded Event Descriptor with specific request identifier
	 * and the PkgEventItem object. The PkgEventItem object contains the package
	 * parameters for the event to be detected.
	 * 
	 * @param requestId
	 *            - An integer value specifying the request identifier, which
	 *            uniquely identifies the event.
	 * @param eventItem
	 *            - An PkgEventItem object which identifies the event to be
	 *            detected and corresponding package parameters.
	 * @throws IllegalArgumentException
	 *             - Thrown if request id is set to an invalid value or the
	 *             reference of Package Event Item object is set to NULL.
	 */
	public EmbedFirstEventParam(int requestId, PkgEventItem eventItem) throws IllegalArgumentException {
		this(requestId);
		if (eventItem == null) {
			throw new IllegalArgumentException("PkgEventItem must nto be null");
		}
		this.eventItem = eventItem;
	}

	/**
	 * Constructs a Event Descriptor with specific request identifier and the
	 * PkgItemStr object. The PkgItemStr object contains the package parameters
	 * for the event to be detected in the string format. This constructor will
	 * be used for the MEGACO packages which are not part of the
	 * javax.megaco.pkg package.
	 * 
	 * @param requestId
	 *            - An integer value specifying the request identifier, which
	 *            uniquely identifies the event.
	 * @param eventItem
	 *            - An PkgItemStr object which identifies the event to be
	 *            detected and corresponding package parameters in the string
	 *            format.
	 * @throws IllegalArgumentException
	 *             - Thrown if request id is set to an invalid value or the
	 *             reference of Package Item String object is set to NULL.
	 */
	public EmbedFirstEventParam(int requestId, PkgItemStr eventItemStr) throws IllegalArgumentException {
		this(requestId);

		if (eventItemStr == null) {
			throw new IllegalArgumentException("PkgItemStr must nto be null");
		}
		this.itemStr = eventItemStr;
	}

	/**
	 * This method returns an integer value corresponding to the unique
	 * requested event.
	 * 
	 * @return Returns an integer value that identifies request identifier which
	 *         uniquely identifies the event descriptor.
	 */
	public int getRequestIdentifier() {
		return this.requestId;
	}

	/**
	 * The method can be used the to retrieve the parameters corresponding to
	 * whether the keep alive token is set, the digit map name or digit map
	 * value is set or the embeded signal is set or stream id is set.
	 * 
	 * @return EventParam - object identifier corresponding to the event
	 *         parameters corresponding to the non embedded event id. This
	 *         object interface may optionally be there. If the event parameter
	 *         is not set then this returns a null value.
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
	 * @param EventParam
	 *            The objectIdentifier corresponding to the event paramater of
	 *            first level event.
	 * @throws IllegalArgumentException
	 *             Thrown if an illegal event is set in the embedded event
	 *             descriptor.
	 */
	public void setEventParam(EventParam eventParam) throws IllegalArgumentException {

		// FIXME: add checks
		// if(eventItem == null)
		// throw new IllegalArgumentException("EventParam must not be null.");
		this.eventParam = eventParam;
	}

	/**
	 * The method can be used to get the package name in the Embedded Event
	 * descriptor. This method gives the package information, the attached event
	 * information and the parameter name and value for the event id.
	 * 
	 * @return The object reference for the Event Item. This has the object
	 *         reference of corresponding megaco package and has the reference
	 *         of the parameter info associated with it. If the parameter has
	 *         not been set, then this method shall return NULL.
	 */
	public PkgEventItem getMegacoPkgEventItem() {
		return this.pkgEventItem;
	}

	/**
	 * The method can be used to get the pkdgName as set in the Embedded Event
	 * descriptor. This method gives the package information, the attached event
	 * information and the parameter name and value. Compared to the
	 * getMegacoPkgEventItem( ) method, this method returnes the package
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

	/**
	 * The method can be used to set the package name in the Embedded Event
	 * descriptor. The Package Event Item object contains package information,
	 * the attached event information and the parameter name and value for the
	 * event id.
	 * 
	 * @param eventItem
	 *            - The object reference for the Event Item. This has the object
	 *            reference of corresponding megaco package and has the
	 *            reference of the parameter info associated with it.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Package Event
	 *             Item passed to this method is NULL.
	 */
	public void setMegacoPkgEventItem(PkgEventItem eventItem) throws IllegalArgumentException {
		if (eventItem == null)
			throw new IllegalArgumentException("PkgEventItem must not be null.");
	}

	/**
	 * The method can be used to set the pkdgName as set in the Embedded Event
	 * descriptor. The Package Item String contains package information, the
	 * attached event information and the parameter name and value. Compared to
	 * the setMegacoPkgEventItem( ) method, this method takes the package
	 * parameters in the string format.
	 * 
	 * @param eventItemStr
	 *            - The object reference for the megaco package item. This class
	 *            holds information about package name, item name and the
	 *            parameters in the string format.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of package item
	 *             string passed to this method is NULL.
	 */
	public void setMegacoPkgItemStr(PkgItemStr eventItemStr) throws IllegalArgumentException {
		if (eventItemStr == null)
			throw new IllegalArgumentException("PkgItemStr must not be null.");

		this.itemStr = eventItemStr;

	}

}
