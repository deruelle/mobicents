package javax.megaco.pkg.ToneGenPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgConsts;
import javax.megaco.pkg.PkgItemParam;

/**
 * The MEGACO parameter class for the Tonelist Parameter is associated with Play
 * Tone signal of Tone Generator Package. This class defines all the static
 * information for this parameter.
 */
public class ToneGenParamTl extends PkgItemParam {

	/**
	 * Identifies Tone List parameter of the MEGACO Tone Generator Package. Its
	 * value shall be set equal to 0x0001.
	 */
	public static final int TONE_GEN_PARAM_IND = 0x0001;

	protected int[] paramsItemIds = null;

	/**
	 * Constructs a parameter class for Tone Generator package that specifies
	 * the parameter as tone list.
	 */
	public ToneGenParamTl() {
		super();
		super.paramId = TONE_GEN_PARAM_IND;
		super.itemValueType = ParamValueType.M_STRING;
		super.paramsDescriptorIds = new int[] { DescriptorType.M_SIGNAL_DESC };
		this.paramsItemIds = new int[] { ToneGenPlayToneSignal.TONE_GEN_PLAY_TONE_SIGNAL };
	}

	/**
	 * The method can be used to get the parameter identifier as defined in the
	 * MEGACO packages. The implementation of this method in this class returns
	 * Id of Tone List parameter.
	 * 
	 * @return paramId - Returns param id as {@link TONE_GEN_PARAM_IND}.
	 */
	public int getParamId() {

		return super.paramId;
	}

	/**
	 * The method can be used to get the type of the parameter as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or double value or boolean.
	 * 
	 * @return It returns {@link ParamValueType.M_STRING}
	 *         indicating that the parameter is a string.
	 */
	public int getParamValueType() {

		return super.itemValueType;
	}

	/**
	 * The method can be used to get the item ids corresponding to the
	 * parameters to which the parameter can be set. This method specifies the
	 * valid item (event/signal) ids to which the parameter can belong to.
	 * 
	 * @return The integer value corresponding to Play Tone. Thus this shall
	 *         return {@link ContRspSignal.TONE_GEN_PLAY_TONE_SIGNAL}.
	 */
	public int[] getParamsItemIds() {
		return this.paramsItemIds;
	}

	/**
	 * The method can be used to get the package id corresponding to the to
	 * which the parameter can be set. This method specifies the package for
	 * which the parameter is valid. Even though the parameter may be set for an
	 * item, but the parameter may not be valid for package to which the item
	 * belongs, but may be valid for a package which has extended this package.
	 * 
	 * @return This shall return {@link PkgConsts.TONE_GEN_PACKAGE} as the
	 *         integer value. The integer values are defined in
	 *         {@link PkgConsts}.
	 */
	public int getParamsPkgId() {
		return PkgConsts.TONE_GEN_PACKAGE;
	}

	/**
	 * The method can be used to get the descriptor ids corresponding to the
	 * parameters to which the parameter can be set.
	 * 
	 * @return This parameter can be present in Signal descriptor. It shall thus
	 *         return a value {@link DescriptorType.M_SIGNAL_DESC} as a part of
	 *         integer vector.
	 */
	public int[] getParamsDescriptorIds() {
		return super.paramsDescriptorIds;
	}

}
