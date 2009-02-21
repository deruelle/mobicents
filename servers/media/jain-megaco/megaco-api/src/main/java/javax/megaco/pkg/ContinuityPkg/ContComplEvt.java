package javax.megaco.pkg.ContinuityPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgEventItem;

/**
 * The MEGACO Completion event class extends the PkgEventItem class. This is a
 * final class. This class defines Completion event of MEGACO Continuity
 * package. The methods shall define that this event item belongs to the
 * Continuity package.
 */
public final class ContComplEvt extends PkgEventItem {

	/**
	 * Identifies Completion event of the MEGACO Continuity Package. Its value
	 * shall be set equal to 0x0005.
	 */
	public static final int CONT_COMPL_EVENT = 0x0005;

	private ContinuityPkg itemsPackageId = new ContinuityPkg();

	/**
	 * Constructs a Jain MEGACO Object representing event item of the MEGACO
	 * Package for Event Completion and Package as Continuity.
	 */
	public ContComplEvt() {
		super();
		super.itemId = CONT_COMPL_EVENT;
		super.eventId = CONT_COMPL_EVENT;
	}

	/**
	 * This method is used to get the event identifier from an Event Item
	 * object. The implementations of this method in this class returns the id
	 * of the Completion event of Continuity Package.
	 * 
	 * @return It shall return {@link CONT_COMPL_EVENT}.
	 */
	public int getEventId() {

		return super.eventId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the
	 * Completion event of Continuity Package.
	 * 
	 * @return It shall return {@link CONT_COMPL_EVENT}.
	 */
	public int getItemId() {

		return super.itemId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the
	 * Completion event is defined in the Continuity Package of MEGACO protocol,
	 * this method returns the ContinuityPkg class object.
	 * 
	 * @return The package is ContinuityPkg .
	 */
	public MegacoPkg getItemsPackageId() {
		return itemsPackageId;
	}

}
