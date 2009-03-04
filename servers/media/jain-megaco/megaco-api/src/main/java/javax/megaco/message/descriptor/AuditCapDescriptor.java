package javax.megaco.message.descriptor;

import java.io.Serializable;

import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the audit
 * capability request descriptor. It specifies the tokens for which audit
 * capability is required.
 */
public class AuditCapDescriptor extends Descriptor implements Serializable {

	private boolean obsEventToken;
	private boolean eventsToken;
	private boolean statsToken;
	private boolean eventBuffToken;
	private boolean signalToken;
	private boolean mediaToken;
	private boolean modemTokenPresent;
	private boolean muxToken;

	/**
	 * Constructs a Audit Capability request descriptor. It specifies the tokens
	 * for which the audit capability is required.
	 */
	public AuditCapDescriptor() {
		super.descriptorId = DescriptorType.M_AUDIT_CAP_DESC;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type audit capability
	 * request descriptor. This method overrides the corresponding method of the
	 * base class Descriptor.
	 * 
	 * @return Returns an integer value that identifies this object of the type
	 *         of audit capability request descriptor. It returns the value
	 *         M_AUDIT_CAP_DESC of a audit capability descriptor.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
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
		return this.mediaToken ;
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
