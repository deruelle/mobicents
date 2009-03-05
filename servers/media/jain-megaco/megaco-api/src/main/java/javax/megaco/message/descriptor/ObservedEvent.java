package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.MethodInvocationException;
import javax.megaco.pkg.PkgEventItem;
import javax.megaco.pkg.PkgItemStr;

/**
 * The ObservedEvent object is a class that shall be used to set the observed
 * events and the stream id in which it was observed along with optionally the
 * time at which it was detected. The class optionally provides interface to
 * specify the package name, item name and the associated parameters in the
 * string format. This is an independent class derived from java.util.Object and
 * shall not have any derived classes.
 */
public class ObservedEvent implements Serializable {

	private PkgEventItem eventItem;
	private PkgItemStr eventItemStr;
	private TimeStamp timeStamp;
	private Integer streamId;

	/**
	 * Constructs Observed Event parameter Object. This is to be set within an
	 * observed event descriptor.
	 * 
	 * @param eventItem
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid event id is set.
	 */
	public ObservedEvent(PkgEventItem eventItem) throws IllegalArgumentException {

		// FIXME: add error check on invalid event?
		if (eventItem == null) {
			throw new IllegalArgumentException("PkgEventItem must not be null");

		}

		this.eventItem = eventItem;
	}

	/**
	 * 
	 * Constructs Observed Event parameter object with the PkgItemStr object.
	 * This method would be used if the package parameters are to be conveyed in
	 * the string format. This is to be set within an event buffer descriptor.
	 * 
	 * @param eventItemStr
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid eventItemStr object reference is set.
	 */
	public ObservedEvent(PkgItemStr eventItemStr) throws IllegalArgumentException {
		if (eventItemStr == null) {
			throw new IllegalArgumentException("PkgItemStr must not be null");

		}

		this.eventItemStr = eventItemStr;
	}

	/**
	 * The method can be used to get the pkdgName as set in the observed event
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
	 * The method can be used to get the pkdgName as set in the observed event
	 * descriptor. This method gives the package information, the attached event
	 * information and the parameter name and value. As compares to the
	 * getMegacoPkgEvtItem() method, this method returns the package parameters
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
	 * The method can be used to get stream id in the event descriptor.
	 * 
	 * @return streamId - The integer value of the stream id shall be returned.
	 *         This shall be returned only if the streamId is present in the
	 *         event parameter of the event descriptor.
	 * @throws IllegalStateException
	 *             - Thrown if streamId has not been set. Thus this method
	 *             should be called only after verifying that the streamId is
	 *             set using isStreamIdPresent
	 */
	public int getStreamId() throws IllegalStateException {
		if (!isStreamIdPresent()) {
			throw new IllegalStateException("StreamId must be present");
		}
		return this.streamId.intValue();
	}

	/**
	 * The method can be used to set stream id in the event descriptor. This
	 * automatically sets the value of stream id present in the event parameter
	 * object to true.
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

	/**
	 * The method is used to retrieve the time stamp at which the event was
	 * detected.
	 * 
	 * @return timeStamp - The object reference for the timestamp. It returns a
	 *         NULL value when not set.
	 */
	public TimeStamp getTimeStamp() {
		return this.timeStamp;
	}

	/**
	 * The method is used to set the time stamp at which the event was detected.
	 * 
	 * @param timeStamp
	 *            - The object reference for the timestamp.
	 * @throws IllegalArgumentException
	 *             - Thrown if timestamp is set with an invalid value.
	 */
	public void setTimeStamp(TimeStamp timeStamp) throws IllegalArgumentException {

		// FIXME: add checks for invalid value?
		if (timeStamp == null) {
			throw new IllegalArgumentException("TimeStamp must not be null");
		}

		this.timeStamp = timeStamp;
	}

	public java.lang.String toString() {
		return super.toString();
	}

}
