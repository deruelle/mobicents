package javax.megaco.pkg.NetworkPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgConsts;
import javax.megaco.pkg.PkgItemParam;

/**
 * The MEGACO parameter class for the Threshold Parameter is associated with
 * Quality Alert event of Network Package. This class defines all the static
 * information for this parameter.
 */
public class NetworkParamTh extends PkgItemParam {

	//FIXME: value is the same as for CS param, setting it for 2
	/**
	 * Identifies Threshold parameter of the MEGACO Network Package. Its value
	 * shall be set equal to 0x0002.
	 */
	public static final int NETWORK_PARAM_TH = 0x0002;

	/**
	 * Constructs a parameter class for Network package that specifies the
	 * parameter as Threshold.
	 */
	public NetworkParamTh() {
		super();
		super.paramId = NETWORK_PARAM_TH;
		super.itemValueType = ParamValueType.M_INTEGER;
		super.paramsItemIds = new int[] { NetworkQualertEvt.NETWORK_QLTY_ALERT_EVENT };
		super.paramsDescriptorIds = new int[] { DescriptorType.M_OBSERVED_EVENT_DESC, DescriptorType.M_EVENT_DESC };
	}

	/**
	 * The method can be used to get the parameter identifier as defined in the
	 * MEGACO packages. The implementation of this method in this class returns
	 * Id of Threshold parameter.
	 * 
	 * @return paramId - Returns param id {@link NETWORK_PARAM_TH}.
	 */
	public int getParamId() {
		return super.paramId;
	}

	/**
	 * The method can be used to get the type of the parameter as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or double value or boolean.
	 * 
	 * @return It returns {@link ParamValueType.M_INTEGER}
	 *         indicating that the parameter is a integer.
	 */
	public int getParamValueType() {
		return super.itemValueType;
	}

	/**
	 * The method can be used to get the descriptor ids corresponding to the
	 * parameters to which the parameter can be set.
	 * 
	 * @return This parameter can be present in both Event descriptor and
	 *         Observed Event Descriptor. It shall thus return values
	 *         {@link DescriptorType.M_EVENT_DESC},
	 *         {@link DescriptorType.M_OBSERVED_EVENT_DESC} as a part of integer
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
	 * @return The integer value corresponding to Network Failure. Thus this
	 *         shall return {@link NetworkQualertEvt.NETWORK_QLTY_ALERT_EVENT}.
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
	 * @return This shall return {@link PkgConsts.NETWORK_PACKAGE} as the
	 *         integer value. The integer values are defined in
	 *         {@link PkgConsts}.
	 */
	public int getParamsPkgId() {
		return PkgConsts.NETWORK_PACKAGE;
	}

}
