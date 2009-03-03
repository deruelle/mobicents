package javax.megaco.pkg.RTPPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgConsts;
import javax.megaco.pkg.PkgStatsItem;

/**
 * 
 * The MEGACO Jitter statistics class extends the PkgStatsItem class. This is a
 * final class. This class defines Jitter statistics of MEGACO RTP package. The
 * methods shall define that this statistics item belongs to the RTP package.
 */
public final class RTPJitStats extends PkgStatsItem {

	/**
	 * Identifies Jitter statistics of the MEGACO RTP Package. Its value shall
	 * be set equal to 0x0007.
	 */
	public static final int RTP_JIT_STATS = 0x0007;

	/**
	 * Constructs a Jain MEGACO Object representing statistics item of the
	 * MEGACO Package for statistics Jitter and Package as RTP.
	 */
	public RTPJitStats() {
		super();
		super.statisticsId = RTP_JIT_STATS;
		super.itemId = RTP_JIT_STATS;
		super.packageId = new RTPPkg();
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the Jitter
	 * statistics of RTP Package.
	 * 
	 * @return It shall return {@link RTP_JIT_STATS}.
	 */
	public int getItemId() {
		return super.itemId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the
	 * Jitter statistics is defined in the RTP Package of MEGACO protocol, this
	 * method returns the value {@link PkgConsts.RTP_PACKAGE} constant. This
	 * constant is defined in the PkgConsts class.
	 * 
	 * 
	 * 
	 * @return The package id RTP_PACKAGE.
	 */
	public MegacoPkg getItemsPkgId() {
		return super.packageId;
	}

	/**
	 * This method is used to get the statistics identifier from an Statistics
	 * Item object. The implementations of this method in this class returns the
	 * id of the Jitter statistics of RTP Package.
	 * 
	 * @return It shall return {@link RTP_JIT_STATS}.
	 */
	public int getStatisticsId() {
		return super.statisticsId;
	}

	// FIXME; ??
	@Override
	public int getItemValueType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getItemsDescriptorIds() {
		// TODO Auto-generated method stub
		return null;
	}

}
