package javax.megaco.pkg.DTMFDetPkg;

import javax.megaco.pkg.PkgConsts;

/**
 * The MEGACO DTMF Detect Package inherits all methods of the Package, but
 * overrides the getPkgId and getPkgName to define packageid corresponding to
 * the DTMF Detect Package. This class also overrides the getExtendedPkgIds to
 * define that this package extends MEGACO Tone Detect package.
 */
public class DTMFDetPkg extends javax.megaco.pkg.MegacoPkg {

	private int[] extendedPkgIds = new int[] { PkgConsts.TONE_DET_PACKAGE };

	/**
	 * This method gets the package ids of all the package which the package had
	 * directly or indirectly extended. Package ids are defined in
	 * {@link PkgConsts}.
	 * 
	 * @return Since this packge extends MEGACO Tone Detect package, this shall
	 *         return value {@link PkgConsts.TONE_DET_PACKAGE}.
	 */
	public int[] getExtendedPkgIds() {

		return extendedPkgIds;
	}

	/**
	 * This method return the package Id of the MEGACO package for which the
	 * object is created. For DTMF Detection Package constant value
	 * {@link PkgConsts.DTMF_DET_PACKAGE} shall be returned.
	 * 
	 * @return Constant value {@link PkgConsts.DTMF_DET_PACKAGE} indicating DTMF
	 *         Detection Package.
	 */
	public int getPkgId() {

		return PkgConsts.DTMF_DET_PACKAGE;
	}

}
