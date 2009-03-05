package javax.megaco.pkg.ContinuityPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgConsts;
import javax.megaco.pkg.PkgItemParam;

/**
 * The MEGACO parameter class for the Result Parameter is associated with
 * Completion event of Continuity Package. This class defines all the static
 * information for this parameter.
 */
public class ContParamRes extends PkgItemParam {

	/**
	 * Identifies Result parameter of the MEGACO Continuity Package. Its value
	 * shall be set equal to 0x0008.
	 */
	public static final int CONT_PARAM_RES = 0x0008;

	/**
	 * Constructs a parameter class for Continuity package that specifies the
	 * parameter as Result.
	 */
	public ContParamRes() {
		super();
		super.paramId = CONT_PARAM_RES;
		super.itemValueType = ParamValueType.M_STRING;
		super.paramsDescriptorIds = new int[] { DescriptorType.M_EVENT_DESC };
		super.paramsItemIds = new int[] { ContComplEvt.CONT_COMPL_EVENT };
	}

	/**
	 * The method can be used to get the parameter identifier as defined in the
	 * MEGACO packages. The implementation of this method in this class returns
	 * Id of Result parameter.
	 * 
	 * @return paramId - Returns param id {@link CONT_PARAM_RES}.
	 */
	public int getParamId() {

		return super.paramId;
	}

	/**
	 * The method can be used to get the type of the parameter as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or double value or boolean.
	 * 
	 * @return It returns {@link STRING} indicating that the
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
	 *         return a value {@link EVENT_DESC} as a part of integer vector.
	 */
	public int[] getParamsDescriptorIds() {
		return super.paramsDescriptorIds;
	}

	/**
	 * The method can be used to get the item ids corresponding to the
	 * parameters to which the parameter can be set. This method specifies the
	 * valid item (event/signal) ids to which the parameter can belong to.
	 * 
	 * @return The integer value corresponding to Completion. Thus this shall
	 *         return {@link CONT_COMPL_EVENT}.
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
	 * @return This shall return {@link CONTINUITY_PACKAGE} as the integer
	 *         value. The integer values are defined in {@link PkgConsts}.
	 */
	public int getParamsPkgId() {
		return PkgConsts.CONTINUITY_PACKAGE;
	}

}
