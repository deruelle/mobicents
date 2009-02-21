package javax.megaco.pkg.AnalogLineSPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgEventItem;

/**
 * The MEGACO Flash Hook event class extends the PkgEventItem class. This is a
 * final class. This class defines Flash Hook event of Analog Line Supervision
 * package. The methods shall define that this event item belongs to the Analog
 * Line Supervision package.
 */
public final class AnalogLineSFlashEvt extends PkgEventItem {
	/**
	 * Identifies Flash Hook event of the MEGACO Analog Line Supervision
	 * Package. Its value shall be set equal to 0x0006.
	 */
	public static final int ANALOG_LINE_FLASH_EVENT = 0x0006;

	/**
	 * Constructs a Jain MEGACO Object representing event Item of the MEGACO
	 * Package for event FlashHook and package as Analog Line Supervision.
	 */
	public AnalogLineSFlashEvt() {
		super();
		super.itemId = ANALOG_LINE_FLASH_EVENT;
		super.eventId = ANALOG_LINE_FLASH_EVENT;
		super.packageId = new AnalogLineSPkg();

	}

	/**
	 * This method is used to get the event identifier from an Event Item
	 * object. The implementations of this method in this class returns the id
	 * of the Flash Hook event of Analog Line Supervision Package.
	 * 
	 * @return It shall {@link ANALOG_LINE_FLASH_EVENT}.
	 */
	public int getEventId() {

		return super.itemId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the Flash
	 * Hook event of Analog Line Supervision Package.
	 * 
	 * @return It shall return {@link ANALOG_LINE_FLASH_EVENT}.
	 */
	public int getItemId() {

		return super.itemId;
	}

	/**
	 * This method gets the package to which the item belongs. Since the Flash
	 * Hook event is defined in the Analog Line Supervision Package of MEGACO
	 * protocol, this method returns the AnalogLineSPkg class object.
	 * 
	 * @return The package is {@link AnalogLineSPkg}.
	 */
	public MegacoPkg getItemsPkgId() {
		return super.packageId;
	}
}
