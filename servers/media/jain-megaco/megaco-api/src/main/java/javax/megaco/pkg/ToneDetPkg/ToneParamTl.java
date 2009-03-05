package javax.megaco.pkg.ToneDetPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgConsts;
import javax.megaco.pkg.PkgItemParam;

/**
 * The MEGACO parameter class for the Tonelist Parameter is associated with
 * Start Tone Detect, Long Tone Detect and End Tone Detect event of Network
 * Package. This class defines all the static information for this parameter.
 */
public class ToneParamTl extends PkgItemParam {

	/**
	 * Identifies Duration parameter of the MEGACO Tone Detect Package. Its
	 * value shall be set equal to 0x0001.
	 */
	public static final int TONE_DET_PARAM_TL = 0x0001;

	protected int[] paramsItemIds = null;

	/**
	 * Constructs a Tone package parameter class that specifies the parameter as
	 * tone list.
	 */
	public ToneParamTl() {
		super();
		super.paramId = TONE_DET_PARAM_TL;
		super.itemValueType = ParamValueType.M_STRING;
		super.paramsDescriptorIds = new int[] { DescriptorType.M_EVENT_DESC };
		this.paramsItemIds = new int[] { ToneStdEvent.TONE_DET_STD_EVENT, ToneEtdEvent.TONE_DET_ETD_EVENT, ToneLtdEvent.TONE_DET_LTD_EVENT };
	}

	/**
	 * The method can be used to get the parameter identifier as defined in the
	 * MEGACO packages. The implementation of this method in this class returns
	 * Id of Tone List parameter.
	 * 
	 * @return paramId - Returns param id as {@link TONE_DET_PARAM_TL}.
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
	 * @return The integer values corresponding to start tone detected, end tone
	 *         detected and long tone detected. Thus this shall return a vector
	 *         containing the elements {@link ToneStdEvent.TONE_DET_STD_EVENT},
	 *         {@link AnalogLineSOnEvt.TONE_DET_ETD_EVENT} and
	 *         {@link ToneLtdEvent.TONE_DET_LTD_EVENT}.
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
	 * @return This shall return {@link PkgConsts.TONE_DET_PACKAGE} as the
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
	 *         return a value {@link DescriptorType.M_EVENT_DESC} as a part of
	 *         integer vector.
	 */
	public int[] getParamsDescriptorIds() {
		return super.paramsDescriptorIds;
	}

}
