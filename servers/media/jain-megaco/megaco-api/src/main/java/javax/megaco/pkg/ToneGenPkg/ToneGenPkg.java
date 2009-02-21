package javax.megaco.pkg.ToneGenPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgConsts;

/**
 * The MEGACO Tone Generator Package inherits all methods of the Package, but
 * overrides the getPkgId and getPkgName to define packageid corresponding to
 * the Tone Generator Package. This class also overrides the getExtendedPkgIds
 * to define that there are no packages that this package extends.
 */
public class ToneGenPkg extends MegacoPkg {

	/**
	 * This method gets the package ids of all the package which the package had
	 * directly or indirectly extended. Package ids are defined in
	 * {@link PkgConsts}.
	 * 
	 * @return Since this packge extends no other package, this shall return a
	 *         NULL value.
	 */
	public final int[] getExtendedPkgIds() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method return the package Id of the MEGACO package for which the
	 * object is created. For Tone Generator Package constant value
	 * {@link TONE_GEN_PACKAGE} shall be returned.
	 * 
	 * @return Constant value {@link TONE_GEN_PACKAGE} indicating Tone Generator
	 *         Package.
	 */
	public int getPkgId() {
		return PkgConsts.TONE_GEN_PACKAGE;
	}

}
