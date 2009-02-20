package javax.megaco.pkg.AnalogLineSPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgConsts;
import javax.megaco.pkg.ToneGenPkg.ToneGenPkg;

/**
 * The MEGACO Analog Line Supervision package inherits all methods of the
 * Package, but overrides the getPkgId and getPkgName to define packageid
 * corresponding to the Analog Line Supervision Package. This class also
 * overrides the getExtendedPkgIds to define that there are no packages that
 * this package extends.
 * 
 */
public class AnalogLineSPkg extends MegacoPkg {

	/**
	 * This method gets the package ids of all the package which the package had
	 * directly or indirectly extended. Package ids are defined in
	 * {@link PkgConsts}.
	 * 
	 * @return Since this packge extends no other package, this shall return a
	 *         NULL value.
	 */
	public int[] getExtendedPkgIds() {

		return null;
	}

	/**
	 * This method return the package Id of the MEGACO package for which the
	 * object is created. For Tone Detection Package constant value
	 * {@link ANALOG_LINE_PACKAGE} shall be returned.
	 * 
	 * @return Constant value {@link ANALOG_LINE_PACKAGE} indicating Analog Line
	 *         Supervision Package.
	 */
	public int getPkgId() {

		return PkgConsts.ANALOG_LINE_PACKAGE;
	}

}
