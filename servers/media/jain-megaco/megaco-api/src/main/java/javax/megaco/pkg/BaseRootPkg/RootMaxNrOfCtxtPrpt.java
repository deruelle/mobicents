package javax.megaco.pkg.BaseRootPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgConsts;
import javax.megaco.pkg.PkgPrptyItem;

/**
 * The MEGACO MaxTermsPerCtxt property class extends the PkgPrptyItem class.
 * This is a final class. This class defines MaxTermsPerCtxt property of MEGACO
 * Root package. The methods shall define that this property item belongs to the
 * Root package.
 */
public final class RootMaxNrOfCtxtPrpt extends PkgPrptyItem {

	/**
	 * Identifies MaxTermsPerCtxt property of the MEGACO Base Root Package. Its
	 * value shall be set equal to 0x0002.
	 */
	public static final int ROOT_MAX_NR_OF_CTXT_PRPT = 0x0001;

	protected int[] itemsDescriptorIds = null;

	/**
	 * Constructs a Jain MEGACO Object representing property item of the MEGACO
	 * Package for property MaxTermsPerCtxt and Package as Base Root.
	 */
	public RootMaxNrOfCtxtPrpt() {
		super();
		super.itemId = ROOT_MAX_NR_OF_CTXT_PRPT;
		super.itemType = ParamValueType.M_DOUBLE;
		super.packageId = new BaseRootPkg();

		this.itemsDescriptorIds = new int[] { DescriptorType.M_TERMINATION_STATE_DESC };
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the
	 * Maximum Number of Context property of ROOT Package.
	 * 
	 * @return It shall return {@link ROOT_MAX_NR_OF_CTXT_PRPT}.
	 */
	public int getItemId() {

		return super.itemId;
	}

	/**
	 * The method can be used to get the type of the value as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or double value or boolean.
	 * 
	 * @return It returns {@link DOUBLE} indicating that the
	 *         parameter is a double.
	 */
	public int getItemValueType() {
		return super.itemType;
	}

	/**
	 * This method is used to get the property identifier from an Property Item
	 * object. The implementations of this method in this class returns the id
	 * of the Maximum Number of Context property of ROOT Package.
	 * 
	 * @return It shall return {@link ROOT_MAX_NR_OF_CTXT_PRPT}.
	 */
	public int getPropertyId() {// TODO Auto-generated method stub
		return ROOT_MAX_NR_OF_CTXT_PRPT;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the
	 * Maximum Number of Context property is defined in the Base ROOT Package of
	 * MEGACO protocol, this method returns the value {@link BASE_ROOT_PACKAGE}
	 * constant. This constant is defined in the {@link PkgConsts} class.
	 * 
	 * @return The package id {@link BASE_ROOT_PACKAGE}.
	 */
	public MegacoPkg getItemsPkgId() {

		return super.packageId;
	}

	/**
	 * The method can be used to get the descriptor ids corresponding to the
	 * parameters to which the parameter can be set.
	 * 
	 * @return This parameter can be present in Event descriptor. It shall thus
	 *         return a value {@link M_TERMINATION_STATE_DESC} as a part of
	 *         integer vector.
	 */
	public int[] getItemsDescriptorIds() {
		return this.itemsDescriptorIds;
	}

}
