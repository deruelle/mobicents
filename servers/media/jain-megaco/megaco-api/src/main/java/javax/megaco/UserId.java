package javax.megaco;

import java.io.Serializable;

public class UserId implements Serializable {

	private String localEntId = null;
	private String[] remoteEntId = null;
	private char listenerInstance;
	private boolean isListenerInstancePresent = false;

	/**
	 * Constructs a UserId with the specified local entity id, list of remote
	 * entity ids and listener Instance. Using this, constructor will
	 * automatically set the listener Instance to false.
	 * 
	 * @param localEntId
	 *            the Local Entity Identity. If the local entity is MG, then
	 *            this gives the MG identity else shall give MGC identity. The
	 *            localEndId specified is used for encoding the message header
	 *            Id used for the megaco messages emanating from the stack to
	 *            the peer. The format of the string specified must be in
	 *            accordance with the syntax defined for "mId" by the protocol.
	 * @param remoteEntId
	 *            the list of remote entity ids of the User Id. If there are
	 *            number of remote entities, then all the remote entities are
	 *            specified in the user Id and any event received from any of
	 *            these remote entities are delivered to the same user Id. The
	 *            list of remote End Ids specified here are one to one in
	 *            correspondence with the remote entity address in the
	 *            CreateAssocReq
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Local Entity Id
	 *             or Remote Entity Id passed to this method is NULL.
	 */

	public UserId(java.lang.String localEntId, java.lang.String[] remoteEntId)
			throws IllegalArgumentException {
		if (remoteEntId == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException(
					"RemoteEntityIds cannot be null for UserId");
//			invalidArgumentException
//					.setInfoCode(ExceptionInfoCode.INV_REMOTE_ADDR);
			throw invalidArgumentException;
		}

		if (localEntId == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException(
					"LocalEntityId cannot be null for UserId");
			throw invalidArgumentException;
		}

		this.localEntId = localEntId;
		this.remoteEntId = remoteEntId;
		this.isListenerInstancePresent = false;
	}

	public UserId(java.lang.String localEntId, java.lang.String[] remoteEntId,
			char listenerInstance) throws IllegalArgumentException {
		if (remoteEntId == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException(
					"RemoteEntityIds cannot be null for UserId");
//			invalidArgumentException
//					.setInfoCode(ExceptionInfoCode.INV_REMOTE_ADDR);
			throw invalidArgumentException;
		}

		if (localEntId == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException(
					"LocalEntityId cannot be null for UserId");
			throw invalidArgumentException;
		}

		this.localEntId = localEntId;
		this.remoteEntId = remoteEntId;
		this.listenerInstance = listenerInstance;
		this.isListenerInstancePresent = true;

	}

	public boolean isListenerInstancePresent() {
		return this.isListenerInstancePresent;
	}

	public void setLocalEntId(java.lang.String localEntId)
			throws IllegalArgumentException {

		if (localEntId == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException(
					"LocalEntityId cannot be null for UserId");
			throw invalidArgumentException;
		}

		this.localEntId = localEntId;

	}

	public void setRemoteEntId(java.lang.String[] remoteEntId)
			throws IllegalArgumentException {
		if (remoteEntId == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException(
					"RemoteEntityIds cannot be null for UserId");
//			invalidArgumentException
//					.setInfoCode(ExceptionInfoCode.INV_REMOTE_ADDR);
			throw invalidArgumentException;
		}
		this.remoteEntId = remoteEntId;
	}

	public void setListenerInstance(char listenerInstance) {
		this.listenerInstance = listenerInstance;
	}

	public java.lang.String getLocalEntId() {
		return this.localEntId;
	}

	public java.lang.String[] getRemoteEntId() {
		return this.remoteEntId;
	}

	public char getListenerInstance()
			throws javax.megaco.ParameterNotSetException {
		
		//FIXME: ???
		if (!this.isListenerInstancePresent()) {
			ParameterNotSetException parameterNotSetException = new ParameterNotSetException(
					"ListenerInstance not yet set for UserId");
			throw parameterNotSetException;
		}
		return this.listenerInstance;
	}

	public java.lang.String toString() {
		String s = "LocalEntId = " + this.getLocalEntId() + " RemoteEntId = "
				+ this.getRemoteEntId() + " isListenerInstancePresent = "
				+ this.isListenerInstancePresent();
		if (this.isListenerInstancePresent()) {
			try {
				s = s + " ListenerInstance = " + this.getListenerInstance();
			} catch (ParameterNotSetException e) {			
			}
		}
		return s;
	}
}
