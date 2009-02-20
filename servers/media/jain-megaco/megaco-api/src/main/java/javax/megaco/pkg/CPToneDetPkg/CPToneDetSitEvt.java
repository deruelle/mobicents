package javax.megaco.pkg.CPToneDetPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgEventItem;

/**
 * The MEGACO Special Information Tone event class extends the PkgEventItem
 * class. This is a final class. This class defines Special Information Tone
 * event of MEGACO Call Progress Tone Detect package. The methods shall define
 * that this event item belongs to the Call Progress Tone Detect package.
 */
public final class CPToneDetSitEvt extends PkgEventItem {

	/**
	 * Identifies Special Information tone event of the MEGACO Call Progress
	 * Tone Detect Package. Its value shall be set equal to 0x0034.
	 */
	public static final int CP_TONE_DET_SIT_EVENT = 0x0034;

	private CPToneDetPkg itemsPackageId = new CPToneDetPkg();

	/**
	 * Constructs a Jain MEGACO Object representing event Item of the MEGACO
	 * Package for event Special Information Tone and Package as Call Progress
	 * Tone Detect.
	 */
	public CPToneDetSitEvt() {
		super();
		super.itemId = CP_TONE_DET_SIT_EVENT;
		super.eventId = CP_TONE_DET_SIT_EVENT;
	}

	/**
	 * This method is used to get the event identifier from an Event Item
	 * object. The implementations of this method in this class returns the id
	 * of the Special Information Tone event of Call Progress Tone Detect
	 * Package.
	 * 
	 * @return It shall return {@link CP_TONE_DET_SIT_EVENT}.
	 */
	public int getEventId() {

		return super.eventId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the
	 * Special Information Tone event of Call Progress Tone Detect Package.
	 * 
	 * @return It shall return {@link CP_TONE_DET_SIT_EVENT}.
	 */
	public int getItemId() {

		return super.itemId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the
	 * Special Information Tone event is defined in the Call Progress Tone
	 * Detect Package of MEGACO protocol, this method returns the CPToneDetPkg
	 * class object.
	 * 
	 * @return
	 */
	public MegacoPkg getItemsPackageId() {
		return itemsPackageId;
	}

}
