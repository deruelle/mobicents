package javax.megaco.pkg.NetworkPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgConsts;

/**
 * 
 * The MEGACO Network Package inherits all methods of the Package, but overrides
 * the getPkgId and getPkgName to define packageid corresponding to the Network
 * Package. This class also overrides the getExtendedPkgIds to define that there
 * are no packages that this package extends.
 */
public class NetworkPkg extends MegacoPkg {

	private int[] extendedPkgIds = null;

	/**
	 * Constructs a derived class of Network Package that extends the Package
	 */
	public NetworkPkg() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method gets the package ids of all the package which the package had
	 * directly or indirectly extended. Package ids are defined in
	 * {@link PkgConsts}.
	 * 
	 * @return Since this packge extends no other package, this shall return a
	 *         NULL value..
	 */
	public int[] getExtendedPkgIds() {

		return extendedPkgIds;
	}

	/**
	 * This method return the package Id of the MEGACO package for which the
	 * object is created. For Network Package constant value
	 * {@link PkgConsts.NETWORK_PACKAGE} shall be returned.
	 * 
	 * @return Constant value {@link PkgConsts.NETWORK_PACKAGE} indicating
	 *         Continuity Package..
	 */
	public int getPkgId() {

		return PkgConsts.NETWORK_PACKAGE;
	}
}
