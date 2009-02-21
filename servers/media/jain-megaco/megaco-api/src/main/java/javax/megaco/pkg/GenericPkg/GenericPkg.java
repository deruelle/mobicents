/**
 * Start time:17:09:08 2009-02-17<br>
 * Project: mobicents-jain-megaco-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package javax.megaco.pkg.GenericPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgConsts;

/**
 * The MEGACO Generic package inherits all methods of the Package, but overrides
 * the getPkgId and getPkgName to define packageid corresponding to the Generic
 * Package. This class also overrides the getExtendedPkgIds to define that there
 * are no packages that this package extends.
 */
public class GenericPkg extends MegacoPkg {

	/**
	 * Constructs a MEGACO Generic Package.
	 */
	public GenericPkg() {
		super();

	}

	/**
	 * This method gets the package ids of all the package which the package had
	 * directly or indirectly extended. Package ids are defined in PkgConsts.
	 * 
	 * @return Since this packge extends no other package, this shall return a
	 *         NULL value.
	 */
	public final int[] getExtendedPkgIds() {

		return null;
	}

	/**
	 * This method return the package Id of the MEGACO package for which the
	 * object is created. For Generic Package constant value GENERIC_PACKAGE
	 * shall be returned.
	 * 
	 * @return Constant value {@link PkgConsts.GENERIC_PACKAGE} indicating
	 *         Generic Package.
	 */
	public final int getPkgId() {

		return PkgConsts.GENERIC_PACKAGE;
	}

}
