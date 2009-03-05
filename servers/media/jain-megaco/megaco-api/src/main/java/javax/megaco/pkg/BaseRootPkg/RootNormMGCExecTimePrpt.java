package javax.megaco.pkg.BaseRootPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgPrptyItem;

/**
 * The MEGACO NormMGCExecTime property class extends the PkgPrptyItem class.
 * This is a final class. This class defines NormMGCExecTime property of MEGACO
 * Root package. The methods shall define that this property item belongs to the
 * Root package.
 */
public final class RootNormMGCExecTimePrpt extends PkgPrptyItem {

	/**
	 *Identifies NormMGCExecTime property of the MEGACO Base Root Package. Its
	 * value shall be set equal to 0x0004.
	 */
	public static final int ROOT_NORM_MGC_EXEC_TIME_PRPT = 0x0004;

	protected int[] itemsDescriptorIds = null;

	/**
	 * Constructs a Jain MEGACO Object representing property item of the MEGACO
	 * Package for property NormMGCExecTime and Package as Base Root.
	 */
	public RootNormMGCExecTimePrpt() {
		super();
		super.propertyId = ROOT_NORM_MGC_EXEC_TIME_PRPT;
		super.itemId = ROOT_NORM_MGC_EXEC_TIME_PRPT;
		super.packageId = new BaseRootPkg();
		super.itemValueType = ParamValueType.M_INTEGER;

		this.itemsDescriptorIds = new int[] { DescriptorType.M_TERMINATION_STATE_DESC };
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the Normal
	 * MGC Execution Time property of ROOT Package.
	 * 
	 * @return It shall return {@link ROOT_NORM_MGC_EXEC_TIME_PRPT}
	 */
	public int getItemId() {
		return super.itemId;
	}

	/**
	 * The method can be used to get the type of the value as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or double value or boolean.
	 * 
	 * @return It returns {@link ParamValueType.INTEGER}
	 *         indicating that the parameter is a double.
	 */
	public int getItemValueType() {
		return super.itemValueType;
	}

	/**
	 * This method is used to get the property identifier from an Property Item
	 * object. The implementations of this method in this class returns the id
	 * of the Normal MGC Execution Time property of ROOT Package.
	 * 
	 * @return It shall return {@link ROOT_NORM_MGC_EXEC_TIME_PRPT}
	 */
	public int getPropertyId() {

		return super.propertyId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the
	 * Normal MGC Execution Time property is defined in the Base ROOT Package of
	 * MEGACO protocol, this method returns the value {@link BASE_ROOT_PACKAGE}
	 * constant. This constant is defined in the PkgConsts class.
	 */
	public MegacoPkg getItemsPkgId() {
		return this.packageId;
	}

	/**
	 * The method can be used to get the descriptor ids corresponding to the
	 * parameters to which the parameter can be set.
	 * 
	 * @return This parameter can be present in Event descriptor. It shall thus
	 *         return a value DescriptorType.M_TERMINATION_STATE_DESC as a part
	 *         of integer vector.
	 */
	public int[] getDescriptorIds() {
		return this.itemsDescriptorIds;

	}
}
