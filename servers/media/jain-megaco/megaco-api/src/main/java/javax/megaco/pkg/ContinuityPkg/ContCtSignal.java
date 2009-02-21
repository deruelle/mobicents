package javax.megaco.pkg.ContinuityPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgSignalItem;

/**
 * The MEGACO Continuity Test signal class extends the PkgSignalItem class. This
 * is a final class. This class defines Continuity Test signal of MEGACO
 * Continuity package. The methods shall define that this signal item belongs to
 * the Continuity package.
 */
public final class ContCtSignal extends PkgSignalItem {

	/**
	 * Identifies Continuity Test signal of the MEGACO Continuity Package. Its
	 * value shall be set equal to 0x0003.
	 */
	public static final int CONT_CT_SIGNAL = 0x0003;

	protected int signalId = CONT_CT_SIGNAL;

	/**
	 * Constructs a Jain MEGACO Object representing signal item of the MEGACO
	 * Package for signal Continuity Test and Package as Continuity.
	 */
	public ContCtSignal() {
		super();
		super.itemId = CONT_CT_SIGNAL;
		super.packageId = new ContinuityPkg();
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the
	 * Continuity Test signal of Continuity Package.
	 */
	public int getItemId() {

		return CONT_CT_SIGNAL;
	}

	/**
	 * This method is used to get the signal identifier from an Signal Item
	 * object. The implementations of this method in this class returns the id
	 * of the Continuity Test signal of Continuity Package.
	 * 
	 * @return It shall return {@link CONT_CT_SIGNAL}.
	 */
	public int getSignalId() {
		return signalId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the
	 * Respond signal is defined in the Continuity Package of MEGACO protocol,
	 * this method returns the value ContinuityPkg class object.
	 * 
	 * @return The package is {@link ContinuityPkg}
	 */
	public MegacoPkg getItemsPkgId() {
		return super.packageId;
	}

}
