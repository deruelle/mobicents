package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.ParameterNotSetException;

/**
 * The DigitMapValue object is a class that shall be used to set the digit map
 * within the digit map descriptor. The DigitMap value class contains
 * information about the dial plan. This may also contains special symbols like
 * wildcards and timer values to be used on application of the dial plan. This
 * is an independent class derived from java.util.Object and shall not have any
 * derived classes.
 */
public class DigitMapValue implements Serializable {

	private Integer timerT, timerL, timerS;
	private DigitMapString[] digitMapString = null;

	/**
	 * Constructs a Digit Map Value Object. This shall contain information about
	 * the digit string in the dial plan.
	 */
	public DigitMapValue() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method specifies whether the timer T value is present or not.
	 * 
	 * @return TRUE if the timer T value is present.
	 */
	public boolean isTimerTPresent() {
		return this.timerT != null;
	}

	/**
	 * The method gets the timer T value.This method should be invoked after
	 * verifying using method isTimerTPresent() that the timer T value has been
	 * set.
	 * 
	 * @return The integer value for the timer value if it is set.
	 * @throws javax.megaco.ParameterNotSetException
	 *             - if isTimerTPresent() returns FALSE.
	 */
	public int getTimerTValue() throws javax.megaco.ParameterNotSetException {
		if (!this.isTimerTPresent()) {
			throw new ParameterNotSetException("Timer value has not been set.");
		}
		return this.timerT.intValue();
	}

	/**
	 * The method sets the timer T value.
	 * 
	 * @param timerT
	 *            The integer value for the timer value. This automatically sets
	 *            the isTimerTPresent() to TRUE.
	 * @throws IllegalArgumentException
	 *             - if timer value is not correctly set.
	 */
	public void setTimerTValue(int timerT) throws IllegalArgumentException {
		if (timerT <= 0) {
			new IllegalArgumentException("Timer value must not be less or equal to zero");
		}
		this.timerT = timerT;
	}

	/**
	 * This method specifies whether the timer S value is present or not.
	 * 
	 * @return TRUE if the timer S value is present.
	 */
	public boolean isTimerSPresent() {
		return this.timerS != null;
	}

	/**
	 * The method gets the timer S value. This method should be invoked after
	 * verifying using method isTimerSPresent() that the timer S value has been
	 * set.
	 * 
	 * @return The integer value for the timer value if it is set.
	 * @throws javax.megaco.ParameterNotSetException
	 *             - if isTimerSPresent() returns FALSE.
	 */
	public int getTimerSValue() throws javax.megaco.ParameterNotSetException {
		if (!this.isTimerSPresent()) {
			throw new ParameterNotSetException("Timer value has not been set.");
		}
		return this.timerS.intValue();
	}

	/**
	 * The method sets the timer S value.
	 * 
	 * @param timerS
	 *            The integer value for the timer value. This automatically sets
	 *            the isTimerSPresent() to TRUE.
	 * @throws IllegalArgumentException
	 *             - if timer value is not correctly set.
	 */
	public void setTimerSValue(int timerS) throws IllegalArgumentException {
		if (timerS <= 0) {
			new IllegalArgumentException("Timer value must not be less or equal to zero");
		}
		this.timerS = timerS;
	}

	/**
	 * This method specifies whether the timer L value is present or not.
	 * 
	 * @return TRUE if the timer L value is present.
	 */
	public boolean isTimerLPresent() {
		return this.timerL != null;
	}

	/**
	 * The method gets the timer L value. This method should be invoked after
	 * verifying using method isTimerLPresent() that the timer L value has been
	 * set.
	 * 
	 * @return The integer value for the timer value if it is set.
	 * @throws javax.megaco.ParameterNotSetException
	 *             - if isTimerLPresent() returns FALSE.
	 */
	public int getTimerLValue() throws javax.megaco.ParameterNotSetException {
		if (!this.isTimerLPresent()) {
			throw new ParameterNotSetException("Timer value has not been set.");
		}
		return this.timerL.intValue();
	}

	/**
	 * The method sets the timer L value.
	 * 
	 * @param timerL
	 *            The integer value for the timer value. This automatically sets
	 *            the isTimerLPresent() to TRUE.
	 * @throws IllegalArgumentException
	 *             - if timer value is not correctly set.
	 */
	public void setTimerLValue(int timerL) throws IllegalArgumentException {
		if (timerL <= 0) {
			new IllegalArgumentException("Timer value must not be less or equal to zero");
		}
		this.timerL = timerL;
	}

	/**
	 * The method is used to get the vector of all dial plans in the digit map.
	 * 
	 * @return The vector of all digit strings in a dial plan. If the vector of
	 *         digit map strings is not set then this method will return NULL.
	 */
	public DigitMapString[] getDigitMapStrings() {
		return this.digitMapString;
	}

	/**
	 * The method sets the vector of the object identifier for the digit string.
	 * Each of the vector element specifies one digit string.
	 * 
	 * @param digitStrings
	 *            The vector of the object identifer of the digit strings.
	 * @throws IllegalArgumentException
	 *             - if the digit string is not set properly.
	 */
	public void setDigitMapStrings(DigitMapString[] digitStrings) throws IllegalArgumentException {
		// FIXME: what does mean - not correctly?
		this.digitMapString = digitStrings;
	}

	public java.lang.String toString() {
		return this.getClass().getSimpleName()+" : TimerL = "+this.timerL+" : TimerS = "+this.timerS+" : TimerT = "+this.timerT;
	}

}
