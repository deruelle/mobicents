package javax.megaco.pkg;

import java.io.Serializable;

/**
 * The MegacoPkg class is class defined as the base class for all the packages
 * in the MEGACO. This is an abstract class.
 * 
 * 
 */
public abstract class MegacoPkg implements Serializable {

	/**
	 * Constructs a Jain MEGACO Package Object. This is an abstract class and
	 * can be called only by the derived classes.
	 */
	public MegacoPkg() {

	}

	/**
	 * This method is an abstract method. This specifies the package id for
	 * which the MegacoPkg object is created. This shall return an integer value
	 * defined in PkgConsts.
	 * 
	 * @return packageId - An integer value for the package id corresponding to
	 *         the derived object.
	 */
	public abstract int getPkgId();

	/**
	 * This method gets the package ids of all the packages which the package
	 * directly or indirectly extends. If the package does not extend any
	 * package, then this returns a NULL refernece. Package ids are defined in
	 * PkgConsts.
	 * 
	 * @return packageId [] - The list of package ids which takes value from
	 *         PkgConsts.
	 */
	public abstract int[] getExtendedPkgIds();

	// FIXME: shoudl this be int[] ??
	@Override
	public java.lang.String toString() {
		return this.getPkgId() + " " + this.getExtendedPkgIds();
	}

	public String getPackageName() {
		// this method is referenced in extending classes but no javadoc
		// present... ech
		return getClass().getName().replace("Pkg", "");
	}

}
