package javax.megaco.pkg.RTPPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgConsts;

/**
 * The MEGACO RTP Package inherits all methods of the Package, but overrides the
 * getPkgId and getPkgName to define packageid corresponding to the RTP Package.
 * This class also overrides the getExtendedPkgIds to define that this package
 * extends MEGACO Network Package.
 */
public class RTPPkg extends MegacoPkg {

	private int[] extendedPkgIds = new int[]{PkgConsts.NETWORK_PACKAGE};

	/**
	 * Constructs a MEGACO RTP Package.
	 */
	public RTPPkg() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method gets the package ids of all the package which the package had
	 * directly or indirectly extended. Package ids are defined in
	 * {@link PkgConsts}.
	 * 
	 * @return Since this packge extends MEGACO Network package, this shall
	 *         return {@link PkgConsts.RTP_PACKAGE}.
	 */
	public int[] getExtendedPkgIds() {

		return extendedPkgIds;
	}

	/**
	 * This method return the package Id of the MEGACO package for which the
	 * object is created. For RTP Package constant value
	 * {@link PkgConsts.RTP_PACKAGE} shall be returned.
	 * 
	 * @return Constant value {@link PkgConsts.RTP_PACKAGE} indicating
	 *         Continuity Package..
	 */
	public int getPkgId() {

		return PkgConsts.RTP_PACKAGE;
	}
}
