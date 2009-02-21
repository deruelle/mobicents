package javax.megaco.pkg.ToneDetPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgEventItem;

/**
 * The MEGACO Long Tone Detect event class extends the PkgEventItem class. This
 * is a final class. This class defines Long Tone Detect event of MEGACO Tone
 * Detect package. The methods shall define that this event item belongs to the
 * Tone Detect package.
 */
public final class ToneLtdEvent extends PkgEventItem {

	/**
	 * Identifies Long tone detect event of the MEGACO Tone Detect Package. Its
	 * value shall be set equal to 0x0003.
	 */
	public static final int TONE_DET_LTD_EVENT = 0x0004;

	/**
	 * Constructs a Jain MEGACO Object representing Event Item of the MEGACO
	 * Package for Event Long Tone Detected and Package as ToneDet.
	 */
	public ToneLtdEvent() {
		super();
		super.itemId = TONE_DET_LTD_EVENT;
		super.eventId = TONE_DET_LTD_EVENT;
		super.packageId = new ToneDetPkg();

	}

	/**
	 * This method is used to get the event identifier from an Event Item
	 * object. The implementations of this method in this class returns the id
	 * of the Long Tone Detect event of Tone Detect Package.
	 * 
	 * @return It shall {@link TONE_DET_LTD_EVENT}.
	 */
	public int getEventId() {

		return super.itemId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the Long
	 * Tone Detect event of Tone Detect Package.
	 * 
	 * @return It shall return {@link TONE_DET_LTD_EVENT}.
	 */
	public int getItemId() {

		return super.itemId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the Long
	 * Tone Detect event is defined in the Tone Detect Package of MEGACO
	 * protocol, this method returns the ToneDetPkg class object.
	 * 
	 * @return The package is {@link ToneDetPkg}.
	 */
	public MegacoPkg getItemsPkgId() {
		return super.packageId;
	}

}
