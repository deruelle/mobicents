package javax.megaco.pkg.GenericPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgEventItem;

/**
 * The MEGACO Cause Event class extends the PkgEventItem class. This is a final
 * class. This class defines Cause Event of MEGACO Generic package. The methods
 * shall define that this event item belongs to the Generic package.
 */
public class GenCauseEvent extends PkgEventItem {

	/**
	 * Identifies Cause event of the MEGACO Generic Package.
	 */
	public static final int GEN_CAUSE_EVENT = 0x0001;

	/**
	 * 
	 * Constructs a Jain MEGACO object representing the Cause event of the
	 * Generic package.
	 */
	public GenCauseEvent() {
		super();
		super.packageId = new GenericPkg();
		super.itemId = GEN_CAUSE_EVENT;
		super.eventId = GEN_CAUSE_EVENT;

	}

	/**
	 * This method is used to get the event identifier from an Event Item
	 * object. The implementations of this method in this class returns the id
	 * of the Cause event of Generic Package.
	 * 
	 * @return It shall return {@link GEN_CAUSE_EVENT}.
	 */
	public final int getEventId() {

		return super.eventId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the Cause
	 * event of Generic Package
	 * 
	 * @return It shall return {@link GEN_CAUSE_EVENT}.
	 */
	public final int getItemId() {

		return super.itemId;
	}

	/**
	 * This method gets the package to which the item belongs. Since the Generic
	 * Cause event is defined in the Generic Package of MEGACO protocol, this
	 * method returns the GenericPkg class object.
	 * 
	 * @return The package is {@link GenericPkg}.
	 */
	public MegacoPkg getItemsPkgId() {
		return super.packageId;
	}

}
