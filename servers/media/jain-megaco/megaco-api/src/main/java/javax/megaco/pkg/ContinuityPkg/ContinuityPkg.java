package javax.megaco.pkg.ContinuityPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgConsts;

/**
 * 
 * The MEGACO Continuity Package inherits all methods of the Package, but
 * overrides the getPkgId and getPkgName to define packageid corresponding to
 * the Continuity Package. This class also overrides the getExtendedPkgIds to
 * define that there are no packages that this package extends.
 */
public class ContinuityPkg extends MegacoPkg {

	private int[] extendedPkgIds = null;

	/**
	 * Constructs a derived class of Continuity Package that extends the Package
	 */
	public ContinuityPkg() {
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
	 *This method return the package Id of the MEGACO package for which the
	 * object is created. For Continuity Package constant value
	 * {@link PkgConsts.CONTINUITY_PACKAGE} shall be returned.
	 * 
	 * @return Constant value {@link PkgConsts.CONTINUITY_PACKAGE} indicating
	 *         Continuity Package..
	 */
	public int getPkgId() {

		return PkgConsts.CALL_PROG_TONE_DET_PACKAGE;
	}
}
