package javax.megaco.pkg.GenericPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgEventItem;

/**
 * The MEGACO Signal Completion Event class extends the PkgEventItem class. This
 * is a final class. This class defines Signal Completion Event of MEGACO
 * Generic package. The methods shall define that this event item belongs to the
 * Generic package.
 * 
 */
public class GenSigComplEvent extends PkgEventItem {

	/**
	 * Identifies Signal completion event of the MEGACO Generic Package. Its
	 * value shall be set equal to 0x0002.
	 */
	public static final int GEN_SIG_COMPL_EVENT = 0x0002;

	/**
	 * Constructs a Jain MEGACO Object representing Event Item of the MEGACO
	 * Package for Signal Completion and Package as Generic.
	 */
	public GenSigComplEvent() {
		super();
		super.packageId = new GenericPkg();
		super.itemId = GEN_SIG_COMPL_EVENT;
		super.eventId = GEN_SIG_COMPL_EVENT;
	}

	/**
	 * This method is used to get the event identifier from an Event Item
	 * object. The implementations of this method in this class returns the id
	 * of the Signal Completion event of Generic Package.
	 * 
	 * @return It shall return {@link GEN_SIG_COMPL_EVENT}.
	 */
	public final int getEventId() {

		return super.eventId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the Signal
	 * Completion event of Generic Package.
	 * 
	 * @return It shall return {@link GEN_SIG_COMPL_EVENT}.
	 */
	public final int getItemId() {

		return super.itemId;
	}

	/**
	 * This method gets the package to which the item belongs. Since the Signal
	 * Completion event is defined in the Generic Package of MEGACO protocol,
	 * this method returns the GenericPkg class object.
	 * 
	 * @return The package is {@link GenericPkg}.
	 */
	public MegacoPkg getItemsPkgId() {
		return super.packageId;
	}

}
