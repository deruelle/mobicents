package javax.megaco.message.descriptor;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;

/**
 * 
 * The class extends JAIN MEGACO Descriptor. This class describes the audit
 * capability response descriptor. It specifies the tokens and descriptors for
 * audit capability response.
 */
public class AuditCapRespDescriptor extends Descriptor implements Serializable {

	private boolean obsEventToken;
	private boolean eventsToken;
	private boolean statsToken;
	private boolean eventBuffToken;
	private boolean signalToken;
	private boolean mediaToken;
	private boolean modemTokenPresent;
	private boolean muxToken;
	private Descriptor[] descriptors;
	/**
	 * 1. Media Descriptor <br>
	 * 2. Modem Descriptor <br>
	 * 3. Mux Descriptor <br>
	 * 4. Events Descriptor <br>
	 * 5. Signal Descriptor <br>
	 * 6. Observed Events Descriptor <br>
	 * 7. Event Buffer Descriptor <br>
	 * 8. Statistics Descriptor <br>
	 * 9. Error Descriptor
	 **/
	private static final Set<Integer> allowedDescritpors;
	static {

		Set<Integer> tmps = new HashSet<Integer>();
		tmps.add(DescriptorType.M_MEDIA_DESC);
		tmps.add(DescriptorType.M_MODEM_DESC);
		tmps.add(DescriptorType.M_MUX_DESC);
		tmps.add(DescriptorType.M_EVENT_DESC);
		tmps.add(DescriptorType.M_SIGNAL_DESC);
		tmps.add(DescriptorType.M_OBSERVED_EVENT_DESC);
		tmps.add(DescriptorType.M_EVENT_BUF_DESC);
		tmps.add(DescriptorType.M_STATISTICS_DESC);
		tmps.add(DescriptorType.M_ERROR_DESC);
		
		allowedDescritpors = Collections.unmodifiableSet(tmps);
	}

	/**
	 * Constructs a Audit Capability response Descriptor. It specifies the
	 * tokens for which the audit capability is required.
	 */
	public AuditCapRespDescriptor() {
		super.descriptorId = DescriptorType.M_AUDIT_CAP_REPLY_DESC;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type audit capability
	 * response descriptor. This method overrides the corresponding method of
	 * the base class Descriptor.
	 * 
	 * @return Returns an integer value that identifies this object of the type
	 *         of audit capability response descriptor. It returns that it is
	 *         audit capability response Descriptor i.e.,
	 *         M_AUDIT_CAP_REPLY_DESC.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
	}

	/**
	 * Gets the Descriptor information for all the descriptor in this audit
	 * response parameter.
	 * 
	 * @return The vector of the reference to the object identifier of type
	 *         descriptor information.
	 */
	public Descriptor[] getDescriptor() {
		return this.descriptors;
	}

	/**
	 * Sets the vector of Descriptor Information for this audit response
	 * parameter. Only valid descriptors are <br>
	 * <br>
	 * 1. Media Descriptor <br>
	 * 2. Modem Descriptor <br>
	 * 3. Mux Descriptor <br>
	 * 4. Events Descriptor <br>
	 * 5. Signal Descriptor <br>
	 * 6. Observed Events Descriptor <br>
	 * 7. Event Buffer Descriptor <br>
	 * 8. Statistics Descriptor <br>
	 * 9. Error Descriptor
	 * 
	 * @param descriptor
	 *            The vector of reference to the object identifier of type
	 *            descriptor information.
	 * @throws IllegalArgumentException
	 *             if the descriptor passed to this method is invalid.
	 */
	public void setDescriptor(Descriptor[] descriptors) throws IllegalArgumentException {
		if(descriptors == null)
		{
			throw new IllegalArgumentException("Descriptor[] must not be null.");
		}
		
		if(descriptors.length == 0)
		{
			throw new IllegalArgumentException("Descriptor[] must not be empty.");
		}
		int count =0;
		for(Descriptor d: descriptors)
		{
			if(d == null)
			{
				throw new IllegalArgumentException("Descriptor["+count+"] is null!");
			}
			if(!allowedDescritpors.contains(d.getDescriptorId()))
			{
				throw new IllegalArgumentException("Descriptor["+count+"] is is of wrong type, its not allowed: "+d.toString());
			}
			
			count++;
		}
		this.descriptors = descriptors;
		
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * indicates if the mux token is present or not.
	 * 
	 * @return Returns TRUE if the Mux token is present.
	 */
	public final boolean isMuxTokenPresent() {
		return this.muxToken;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the mux token is present.
	 */
	public final void setMuxToken() {
		this.muxToken = true;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * indicates if the Modem token is present or not.
	 * 
	 * @return Returns TRUE if the Modem token is present.
	 */
	public final boolean isModemTokenPresent() {
		return this.modemTokenPresent;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the Modem token is present.
	 */
	public final void setModemToken() {
		this.modemTokenPresent = true;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * indicates if the Media token is present or not.
	 * 
	 * @return Returns TRUE if the Media token is present.
	 */
	public final boolean isMediaTokenPresent() {
		return this.mediaToken;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the Media token is present.
	 */
	public final void setMediaToken() {
		this.mediaToken = true;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * indicates if the Signal token is present or not.
	 * 
	 * @return Returns TRUE if the Signal token is present.
	 */
	public final boolean isSignalTokenPresent() {
		return this.signalToken;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the Signal token is present.
	 */
	public final void setSignalToken() {
		this.signalToken = true;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * indicates if the Event Buffer token is present or not.
	 * 
	 * @return Returns TRUE if the Event Buffer token is present.
	 */
	public final boolean isEventBuffTokenPresent() {
		return this.eventBuffToken;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the Event Buffer token is present.
	 */
	public final void setEventBuffToken() {
		this.eventBuffToken = true;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * indicates if the Statistics token is present or not.
	 * 
	 * @return Returns TRUE if the Statistics token is present.
	 */
	public final boolean isStatsTokenPresent() {
		return this.statsToken;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the Statistics token is present.
	 */
	public final void setStatsToken() {
		this.statsToken = true;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * indicates if the Events token is present or not.
	 * 
	 * @return Returns TRUE if the Events token is present.
	 */
	public final boolean isEventsTokenPresent() {

		return this.eventsToken;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the Events token is present.
	 */
	public final void setEventsToken() {
		this.eventsToken = true;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * indicates if the Observed Event token is present or not.
	 * 
	 * @return Returns TRUE if the Observed Event token is present.
	 */
	public final boolean isObsEventTokenPresent() {
		return this.obsEventToken;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the Observed Event token is present.
	 */
	public final void setObsEventToken() {
		this.obsEventToken = true;
	}

}
