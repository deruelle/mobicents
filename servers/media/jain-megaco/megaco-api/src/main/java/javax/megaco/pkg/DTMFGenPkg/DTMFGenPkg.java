package javax.megaco.pkg.DTMFGenPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgConsts;
import javax.megaco.pkg.ToneGenPkg.ToneGenPkg;

/**
 * The MEGACO DTMF Generator Package inherits all methods of the Package, but
 * overrides the getPkgId and getPkgName to define packageid corresponding to
 * the DTMF Generator Package. This class also overrides the getExtendedPkgIds
 * to define that this package extends MEGACO Tonegen package.
 * 
 */
public class DTMFGenPkg extends MegacoPkg {

	private int[] extendedPkgIds = new int[] { PkgConsts.TONE_GEN_PACKAGE };

	/**
	 * This method gets the package ids of all the package which the package had
	 * directly or indirectly extended. Package ids are defined in
	 * {@link PkgConsts}.
	 * 
	 * @return Since this packge extends MEGACO Tone Generator package, this
	 *         shall return value {@link PkgConsts.TONE_GEN_PACKAGE}.
	 */
	public int[] getExtendedPkgIds() {

		return extendedPkgIds;
	}

	/**
	 * This method return the package Id of the MEGACO package for which the
	 * object is created. For DTMF Generator Package constant value
	 * {@link PkgConsts.BASIC_DTMF_GEN_PACKAGE} shall be returned.
	 * 
	 * @return Constant value {@link PkgConsts.BASIC_DTMF_GEN_PACKAGE}
	 *         indicating DTMF Generator Package.
	 */
	public int getPkgId() {
		// FIXME: jdoc says: DTMF_GEN_PACKAGE
		return PkgConsts.BASIC_DTMF_GEN_PACKAGE;
	}

}
