package javax.megaco.pkg.GenericPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgConsts;

/**
 * The MEGACO parameter class for the Termination Method Parameter is associated
 * with Signal Completion event of Generic Package. This class defines all the
 * static information for this parameter.
 */
public class GenParamTermMeth extends javax.megaco.pkg.PkgItemParam {

	/**
	 * Identifies Termination method parameter of the MEGACO Generic Package.
	 * Its value shall be set equal to 0x0002.
	 */
	public static final int GEN_PARAM_TERM_METH = 0x0002;

	/**
	 * Constructs a parameter class for Generic package that specifies the
	 * parameter as Termination Method.
	 */
	public GenParamTermMeth() {
		super();
		super.paramId = GEN_PARAM_TERM_METH;
		super.itemValueType = ParamValueType.M_STRING;
		super.paramsDescriptorIds = new int[] { DescriptorType.M_OBSERVED_EVENT_DESC };
		super.paramsItemIds = new int[] { GenSigComplEvent.GEN_SIG_COMPL_EVENT };

	}

	/**
	 * The method can be used to get the parameter identifier as defined in the
	 * MEGACO packages. The implementation of this method in this class returns
	 * Id of Termination Method parameter.
	 * 
	 * @return paramId - Returns param id as {@link GEN_PARAM_TERM_METH}.
	 */
	public int getParamId() {

		return super.paramId;
	}

	/**
	 * The method can be used to get the type of the parameter as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or double value or boolean.
	 * 
	 * @return It returns {@link M_STRING} indicating that the
	 *         parameter is a string.
	 */
	public int getParamValueType() {

		return super.itemValueType;
	}

	/**
	 * The method can be used to get the descriptor ids corresponding to the
	 * parameters to which the parameter can be set.
	 * 
	 * @return This parameter can be present in Event descriptor. It shall thus
	 *         return a value {@link M_OBSERVED_EVENT_DESC} as a part of integer
	 *         vector.
	 */
	public int[] getParamsDescriptorIds() {
		return super.paramsDescriptorIds;
	}

	/**
	 * The method can be used to get the item ids corresponding to the
	 * parameters to which the parameter can be set. This method specifies the
	 * valid item (event/signal) ids to which the parameter can belong to.
	 * 
	 * @return The integer value corresponding to Signal Completion Event. Thus
	 *         this shall return {@link GEN_SIG_COMPL_EVENT}.
	 */
	public int[] getParamsItemIds() {
		return super.paramsItemIds;
	}

	/**
	 * The method can be used to get the package id corresponding to the to
	 * which the parameter can be set. This method specifies the package for
	 * which the parameter is valid. Even though the parameter may be set for an
	 * item, but the parameter may not be valid for package to which the item
	 * belongs, but may be valid for a package which has extended this package.
	 * 
	 * @return This shall return @ GENERIC_PACKAGE} as the integer value. The
	 *         integer values are defined in @ PkgConsts} .
	 */
	public int getParamsPkgId() {
		return PkgConsts.GENERIC_PACKAGE;
	}

}
