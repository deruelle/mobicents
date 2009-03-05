package javax.megaco.message.descriptor;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



/**
 * The TimeStamp object is a class that shall be used to set the time and date
 * at which the event was detected within the observed event descriptor. This is
 * an independent class derived from java.util.Object and shall not have any
 * derived classes.
 */
public class TimeStamp implements Serializable {

	private GregorianCalendar gc = new GregorianCalendar();

	/**
	 * Constructs a timestamp object that contains the date and time. This shall
	 * be used within the observed event to detect the time at which the event
	 * was detected. Time stamp should be valid GregorianCallendar date.
	 * 
	 * @param year
	 *            -
	 * @param month
	 *            - index of month, starting from 0 for January
	 * @param day
	 *            - day of month
	 * @param hour
	 *            - 24h format hour
	 * @param min
	 *            -
	 * @param sec
	 *            -
	 * @throws IllegalArgumentException
	 *             - Thrown if an illegal date or time parameter is set.
	 */
	public TimeStamp(int year, int month, int day, int hour, int min, int sec) throws IllegalArgumentException {
		// Lets use trick to validate.

		gc.setLenient(false);

		gc.set(gc.YEAR, year);
		gc.set(gc.MONTH, month);
		gc.set(gc.DAY_OF_MONTH, day);
		gc.set(gc.HOUR_OF_DAY, hour);
		gc.set(gc.MILLISECOND, min);
		gc.set(gc.SECOND, sec);

		// here we validate
		gc.getTime();

	}

	/**
	 * The method is used the to retrieve the year from the absolute date set.
	 * 
	 * @return year - The integer value of the year.
	 */
	public int getYear() {
		return this.gc.get(gc.YEAR);
	}

	/**
	 * The method can be used the to retrieve month set in the object.
	 * 
	 * @return month - Integer value of the month.
	 */
	public int getMonth() {
		return this.gc.get(gc.MONTH);
	}

	/**
	 * The method can be used the to retrieve day set in the object.
	 * 
	 * @return day - Integer value of the day.
	 */
	public int getDay() {
		return this.gc.get(gc.DAY_OF_MONTH);
	}

	/**
	 * The method is used the to retrieve the hour from the absolute time set.
	 * 
	 * @return hour - The integer value of the hour.
	 */
	public int getHour() {
		return this.gc.get(gc.HOUR_OF_DAY);
	}

	/**
	 * The method is used the to retrieve the minutes from the absolute time
	 * set.
	 * 
	 * @return minutes - The integer value of the minutes.
	 */
	public int getMinutes() {
		return this.gc.get(gc.MINUTE);
	}

	/**
	 * The method is used the to retrieve the secs from the absolute time set.
	 * 
	 * @return secs - The integer value of the secs.
	 */
	public int getSecs() {
		return this.gc.get(gc.SECOND);
	}

	public java.lang.String toString() {
		return this.getClass().getSimpleName() + "[" + gc.toString() + "]";
	}

}
