package javax.megaco.message.descriptor;

import java.io.Serializable;

import javax.megaco.ParameterNotSetException;

/**
 * The SignalParam is a class that shall be used to set the signal params within
 * the signal descriptor. This is an independent class derived from
 * java.util.Object and shall not have any derived classes.
 */
public class SignalParam implements Serializable {

	private Short listId = null;
	private SignalParamType signalParamType = null;
	private SignalRequest[] signalRequest = null;

	/**
	 * Constructs a Jain Megaco Signal Parameter Object with the parameters
	 * consisting of SignalRequest. This method implicitly sets the signal param
	 * type to {@link SignalParamType.M_REQUEST}.
	 * 
	 * @param signalRequest
	 *            - This contains a the object reference to the class signal
	 *            request.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Signal request
	 *             passed to this method is NULL.
	 */
	public SignalParam(SignalRequest signalRequest) throws IllegalArgumentException {

		if (signalRequest == null) {
			throw new IllegalArgumentException("SignalRequest must not be null");
		}

		this.signalRequest = new SignalRequest[] { signalRequest };
		this.signalParamType = SignalParamType.REQUEST;

	}

	/**
	 * Constructs a Jain Megaco Signal Parameter Object with the parameters
	 * consisting of SignalList and the List Id. This method implicitly sets the
	 * signal param type to {@link SignalParamType.M_LIST}.
	 * 
	 * @param listId
	 *            - Identifies the list Id of the signal list.
	 * @param signalRequest
	 *            - This contains a vector of object reference to the class
	 *            signal request.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Signal request
	 *             passed to this method is NULL or the value of list Id is less
	 *             than 0.
	 */
	public SignalParam(short listId, SignalRequest[] signalRequest) throws IllegalArgumentException {

		if (signalRequest == null) {
			throw new IllegalArgumentException("SignalRequest must not be null");
		}
		// FIXME???
		if (signalRequest.length == 0) {
			throw new IllegalArgumentException("SignalRequest list length must be greater than zero");
		}

		this.signalRequest = signalRequest;
		this.signalParamType = SignalParamType.LIST;
		this.listId = new Short(listId);

	}

	/**
	 * The method can be used to get the type of the signal param that specifies
	 * whether the signal param is signal list or signal request in the signal
	 * descriptor. This value would be set in the constructor of this class.
	 * 
	 * @return The integer value specifying the type of signal param. If this
	 *         value is not set, then this method would return value 0.
	 */
	public int getSignalParamType() {
		if (this.signalParamType == null)
			return 0;

		return this.signalParamType.getsignalParamType();
	}

	// public SignalParamType getSignalParamType() {
	// return this.signalParamType;
	// }

	/**
	 * The method is used to get the list id which identifies the signal list of
	 * signal descriptor.
	 * 
	 * @return The list id for the signal list.
	 * @throws javax.megaco.ParameterNotSetException
	 *             - Thrown if signal param type specifies the type to be signal
	 *             list, though this parameter has not been set.
	 * @throws IllegalStateException
	 *             - Thrown if signal param type specifies the type to be signal
	 *             request.
	 */
	public short getSignalParamListId() throws javax.megaco.ParameterNotSetException, IllegalStateException {
		if (this.signalParamType.getsignalParamType() == signalParamType.M_REQUEST) {
			throw new IllegalStateException("SIgnalParam is not a list type.");
		}

		if (this.listId == null) {
			throw new ParameterNotSetException("ListId must be set.");
		}

		return this.listId.shortValue();
	}

	/**
	 * The method gives the vector of object identifier of the signal request.
	 * The signal param type field of this class indicates if the signal
	 * parameter is signal request or signal list. If the signal param type is
	 * M_SIGNAL_PARAM_LIST, then this indicates that the signal request returned
	 * is part of the signal list.
	 * 
	 * @return The vector of object identifier for signal requests.
	 */
	public SignalRequest[] getSignalRequest() {

		return this.signalRequest;
	}

	// public String toString()
	// return super.toString();
	// }
}
