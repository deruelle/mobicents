package javax.megaco.pkg.DTMFGenPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgSignalItem;

/**
 * The MEGACO DTMF character # signal class extends the PkgSignalItem class.
 * This is a final class. This class defines DTMF character # signal of MEGACO
 * DTMF package. The methods shall define that this signal item belongs to the
 * DTMF package.
 */
public final class DTMFGenDHashSignal extends PkgSignalItem {

	/**
	 * Identifies signal id DTMF character # of the MEGACO DTMF Generator
	 * Package. Its value shall be set equal to 0x0021.
	 */
	public static final int DTMF_GEN_DHASH_SIGNAL = 0x0021;

	private DTMFGenPkg itemsPkgId = new DTMFGenPkg();

	/**
	 * Constructs a MEGACO signal item with signal id as DTMF character #.
	 */
	public DTMFGenDHashSignal() {
		super();
		super.signalId = DTMF_GEN_DHASH_SIGNAL;
		super.itemId = DTMF_GEN_DHASH_SIGNAL;
	}

	/**
	 * This method is used to get the signal identifier from an Signal Item
	 * object. The implementations of this method in this class returns the id
	 * of the DTMF character # signal of DTMF Generator Package.
	 * 
	 * @return It shall return {@link DTMF_GEN_DHASH_SIGNAL}.
	 */
	public int getItemId() {

		return super.itemId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the DTMF
	 * character # signal of DTMF Generator Package.
	 * 
	 * @return It shall return {@link DTMF_GEN_DHASH_SIGNAL}.
	 */
	public int getSignalId() {
		return super.signalId;
	}

	@Override
	public MegacoPkg getItemsPkgId() {

		return itemsPkgId;
	}

}
