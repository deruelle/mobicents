package javax.megaco.pkg.NetworkPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgPrptyItem;

/**
 * The MEGACO Maximum Jitter Buffer property class extends the PkgPrptyItem
 * class. This is a final class. This class defines Maximum Jitter Buffer
 * property of Network package. The methods shall define that this property item
 * belongs to the Network package.
 */
public final class NetworkJitPrpt extends PkgPrptyItem {

	/**
	 * Identifies Maximum jitter buffer property of the MEGACO Network Package.
	 * Its value shall be set equal to 0x0007.
	 */
	public static final int NETWORK_JIT_PRPT = 0x0007;

	protected int[] itemsDescriptorIds = null;

	/**
	 * Constructs a Jain MEGACO Object representing property Item of the MEGACO
	 * Package for property Maximum Jitter Buffer and Package as Network.
	 */
	public NetworkJitPrpt() {
		super();
		super.itemId = NETWORK_JIT_PRPT;
		super.propertyId = NETWORK_JIT_PRPT;
		super.packageId = new NetworkPkg();
		super.itemValueType = ParamValueType.M_INTEGER;
		itemsDescriptorIds = new int[] { DescriptorType.M_LOCAL_CONTROL_DESC };
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the Jitter
	 * statistics of Network Package.
	 * 
	 * @return It shall return {@link NetworkJitPrpt.NETWORK_JIT_PRPT}.
	 */
	public int getItemId() {
		return super.itemId;
	}

	/**
	 * The method can be used to get the type of the value as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or double value or boolean.
	 * 
	 * @return It returns {@link ParamValueType.M_INTEGER}
	 *         indicating that the parameter is a double.
	 */
	public int getItemValueType() {
		return itemValueType;
	}

	/**
	 * This method is used to get the statistics identifier from an Statistics
	 * Item object. The implementations of this method in this class returns the
	 * id of the Jitter statistics of Network Package.
	 * 
	 * @return It shall return {@link NetworkJitPrpt.NETWORK_JIT_PRPT}.
	 */
	public int getPropertyId() {
		return super.propertyId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the
	 * Jitter property is defined in the Network Package of MEGACO protocol,
	 * this method returns the value NETWORK_PACKAGE constant. This constant is
	 * defined in the PkgConsts class.
	 * 
	 * @return The package id {@link NetworkPkg.NETWORK_PACKAGE}.
	 */
	public MegacoPkg getItemsPkgId() {

		return super.packageId;
	}

	/**
	 * The method can be used to get the descriptor ids corresponding to the
	 * parameters to which the parameter can be set.
	 * 
	 * @return This parameter can be present in Event descriptor. It shall thus
	 *         return a value {@link DescriptorType.M_LOCAL_CONTROL_DESC} as a
	 *         part of integer vector.
	 */
	public int[] getItemsDescriptorIds() {
		return this.itemsDescriptorIds;
	}

}
