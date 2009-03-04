package javax.megaco.message.descriptor;

import java.io.Serializable;

import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the audit
 * value request descriptor. It specifies the tokens for which audit value is
 * required.
 */
public class AuditValDescriptor extends Descriptor implements Serializable {

	private boolean obsEventToken;
	private boolean eventsToken;
	private boolean statsToken;
	private boolean eventBuffToken;
	private boolean signalToken;
	private boolean mediaToken;
	private boolean modemTokenPresent;
	private boolean muxToken;
	private boolean digitMapToken;
	private boolean packagesToken;
	
	
	/**
	 * Constructs a Audit Value request Descriptor. It specifies the tokens for
	 * which the audit value is required.
	 */
	public AuditValDescriptor() {
		super.descriptorId = DescriptorType.M_AUDIT_VAL_DESC;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type audit value request
	 * descriptor. This method overrides the corresponding method of the base
	 * class Descriptor.
	 * 
	 * @return Returns an integer value that identifies this object of the type
	 *         of audit value request descriptor. It returns the value
	 *         M_AUDIT_VAL_DESC of a audit value descriptor.
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
	 * indicates if the digit map token is present or not.
	 * 
	 * @returnReturns TRUE if the digit map token is present.
	 */
	public final boolean isDigitMapTokenPresent() {
		return this.digitMapToken;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the digit map token is present.
	 */
	public final void setDigitMapToken() {
		this.digitMapToken = true;
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

	/**
	 * This method cannot be overridden by the derived class. This method
	 * indicates if the Packages token is present or not.
	 * 
	 * @return Returns TRUE if the Packages token is present.
	 */
	public final boolean isPackagesTokenPresent() {
		return this.packagesToken;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets a
	 * flag to indicate that the Packages token is present.
	 */
	public final void setPackagesToken() {
		this.packagesToken = true;
	}

}
