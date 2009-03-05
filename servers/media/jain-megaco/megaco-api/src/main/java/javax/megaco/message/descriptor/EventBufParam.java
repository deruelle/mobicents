package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.ParameterNotSetException;
import javax.megaco.pkg.PkgEventItem;
import javax.megaco.pkg.PkgItemStr;

/**
 * The EventBufParam object is a class that shall be used to set the stream id,
 * event id, package id and parameter name and value for an event id. The class
 * optionally provides interface to specify the package name, item name and the
 * associated parameters in the string format. This is an independent class
 * derived from java.util.Object and shall not have any derived classes.
 */
public class EventBufParam implements Serializable {

	private PkgItemStr eventItemStr;
	private PkgEventItem eventItem;
	private Integer streamId;

	/**
	 * Constructs Event buffer parameter object with the PkgEventItem object.
	 * This is to be set within an event buffer descriptor.
	 * 
	 * @param eventItem
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid eventItem object reference is set.
	 */
	public EventBufParam(PkgEventItem eventItem) throws IllegalArgumentException {
		this.eventItem = eventItem;
	}

	/**
	 * 
	 * Constructs Event buffer parameter object with the PkgItemStr object. This
	 * method would be used if the package parameters are to be conveyed in the
	 * string format. This is to be set within an event buffer descriptor.
	 * 
	 * @param eventItemStr
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid eventItemStr object reference is set.
	 */
	public EventBufParam(PkgItemStr eventItemStr) throws IllegalArgumentException {
		this.eventItemStr = eventItemStr;
	}

	/**
	 * The method can be used to get the pkdgName as set in the event buffer
	 * descriptor. This method gives the package information, the attached event
	 * information and the parameter name and value for the event id.
	 * 
	 * @return The object reference for the megaco package, which has the object
	 *         reference for megaco package event and which in turn has the
	 *         reference for the parameter info. If the parameter has not been
	 *         set, then this method shall return NULL.
	 */
	public PkgEventItem getMegacoPkgEvtItem() {
		return this.eventItem;
	}

	/**
	 * The method can be used to get the pkdgName as set in the event buffer
	 * descriptor. This method gives the package information, the attached event
	 * information and the parameter name and value. As comapres to the
	 * getMegacoPkgEvtItem() method, this method returnes the package parameters
	 * in the string format.
	 * 
	 * @return The object reference for the megaco package item. This class
	 *         holds information about package name, item name and the
	 *         parameters in the string format. If the parameter has not been
	 *         set, then this method shall return NULL.
	 */
	public PkgItemStr getMegacoPkgItemStr() {
		return this.eventItemStr;
	}

	/**
	 * The method can be used to get stream id in the event buffer descriptor.
	 * 
	 * @return streamId - The integer value of the stream id shall be returned.
	 *         This shall be returned only if the streamId is present in the
	 *         event parameter of the event buffer descriptor.
	 * @throws javax.megaco.ParameterNotSetException
	 *             - Thrown if streamId has not been set. Thus this method
	 *             should be called only after verifying that the streamId is
	 *             set using isStreamIdPresen
	 */
	public int getStreamId() throws javax.megaco.ParameterNotSetException {
		if (!isStreamIdPresent()) {
			throw new ParameterNotSetException("StreamId must be present");
		}

		return this.streamId.intValue();
	}

	/**
	 * The method can be used to set stream id in the event buffer descriptor.
	 * This automatically sets the value of stream id present in the event
	 * parameter object to true.
	 * 
	 * @param streamId
	 *            - The integer value of the stream id shall be set.
	 * @throws IllegalArgumentException
	 *             - Thrown if streamId is set with an invalid value.
	 */
	public void setStreamId(int streamId) throws IllegalArgumentException {

		if (streamId <= 0) {
			throw new IllegalArgumentException("StreamId must be greater than zero.");
		}

		this.streamId = new Integer(streamId);
	}

	/**
	 * The method can be used to find if the stream id is present in the current
	 * object.
	 * 
	 * @return Returns true if the stream id is present. This needs to be
	 *         checked before getting the stream id from this object.
	 */
	public boolean isStreamIdPresent() {
		return this.streamId != null;
	}

	public java.lang.String toString()

	{
		return super.toString();
	}

}
