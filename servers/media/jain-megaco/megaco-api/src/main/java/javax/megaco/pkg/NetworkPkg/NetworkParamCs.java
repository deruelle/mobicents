package javax.megaco.pkg.NetworkPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgConsts;
import javax.megaco.pkg.PkgItemParam;

/**
 * The MEGACO parameter class for the Cause Parameter is associated with Network
 * Failure event of Network Package. This class defines all the static
 * information for this parameter.
 */
public class NetworkParamCs extends PkgItemParam {

	/**
	 * Identifies Cause parameter of the MEGACO Network Package. Its value shall
	 * be set equal to 0x0001.
	 */
	public static final int NETWORK_PARAM_CS = 0x0001;

	/**
	 * Constructs a parameter class for Network package that specifies the
	 * parameter as Cause.
	 */
	public NetworkParamCs() {
		super();
		super.paramId = NETWORK_PARAM_CS;
		super.itemValueType = ParamValueType.M_STRING;
		super.paramsItemIds = new int[] { NetworkNetfailEvt.NETWORK_NETFAIL_EVENT };
		super.paramsDescriptorIds = new int[] { DescriptorType.M_OBSERVED_EVENT_DESC };
	}

	/**
	 * The method can be used to get the parameter identifier as defined in the
	 * MEGACO packages. The implementation of this method in this class returns
	 * Id of Cause parameter.
	 * 
	 * @return paramId - Returns param id {@link NETWORK_PARAM_CS}.
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
	 * The method can be used to get the descriptor ids corresponding to the
	 * parameters to which the parameter can be set.
	 * 
	 * @return This parameter can be present in Observed Event descriptor. It
	 *         shall thus return a value
	 *         {@link DescriptorType.M_OBSERVED_EVENT_DESC} as a part of integer
	 *         vector.
	 */
	public int[] getParamsDescriptorIds() {
		return super.paramsDescriptorIds;
	}

	/**
	 * The method can be used to get the item ids corresponding to the
	 * parameters to which the parameter can be set. This method specifies the
	 * valid item (event/signal) ids to which the parameter can belong to.The
	 * method can be used to get the item ids corresponding to the parameters to
	 * which the parameter can be set. This method specifies the valid item
	 * (event/signal) ids to which the parameter can belong to.
	 * 
	 * @return The integer value corresponding to Network Failure. Thus this
	 *         shall return {@link NetworkNetfailEvt.NETWORK_NETFAIL_EVENT}.
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
