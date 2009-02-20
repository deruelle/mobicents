package javax.megaco.pkg.CPToneDetPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgConsts;

public class CPToneDetPkg extends MegacoPkg {

	private int[] extendedPkgIds = new int[] { PkgConsts.TONE_DET_PACKAGE };

	/**
	 * This method gets the package ids of all the package which the package had
	 * directly or indirectly extended. Package ids are defined in
	 * {@link PkgConsts}.
	 * 
	 * @return Since this packge extends MEGACO Tone Detect package, this
	 *         shall return value {@link PkgConsts.TONE_DET_PACKAGE}.
	 */
	public int[] getExtendedPkgIds() {

		return extendedPkgIds;
	}

	/**
	 * This method return the package Id of the MEGACO package for which the object is created. For Call Progress Tone Detection Package constant value
	 * {@link PkgConsts.CALL_PROG_TONE_DET_PACKAGE} shall be returned.
	 * 
	 * @return Constant value {@link PkgConsts.CALL_PROG_TONE_DET_PACKAGE}
	 *         indicating DTMF Generator Package.
	 */
	public int getPkgId() {
		
		return PkgConsts.CALL_PROG_TONE_DET_PACKAGE;
	}
}
