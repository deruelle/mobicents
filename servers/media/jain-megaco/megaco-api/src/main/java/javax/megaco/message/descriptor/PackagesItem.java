package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.pkg.MegacoPkg;

/**
 * This class describes the Packages Items. This class defines the parameters
 * for the packages descriptor..
 */
public class PackagesItem implements Serializable {

	private MegacoPkg megacoPkg;
	private short pkgVersion;

	/**
	 * Constructs a Packages Item object. This specifies the package name and
	 * its version.
	 * 
	 * @param pkg
	 *            - The Megaco Package object reference.
	 * @param version
	 *            - The version of the Megaco Package.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Megaco Package
	 *             passed to this method is NULL or the version passed is less
	 *             than 0.
	 */
	public PackagesItem(MegacoPkg pkg, short version) throws IllegalArgumentException {

		if (pkg == null) {
			throw new IllegalArgumentException("MegacoPkg must not be null");
		}

		if (version < 0) {
			throw new IllegalArgumentException("Version must not be less than zerp");
		}

		this.megacoPkg = pkg;
		this.pkgVersion = version;

	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns the Megaco package in the package descriptor. The value would be
	 * one of the packages set in MegacoPkg.
	 * 
	 * @return Returns an value that identifies the package identity. The values
	 *         are defined in MegacoPkg.
	 */
	public final MegacoPkg getMegacoPkg() {
		return this.megacoPkg;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns the version of the package supported.
	 * 
	 * @return Returns an integer value of the package version supported.
	 */
	public final short getPkgVersion() {
		return this.pkgVersion;
	}

}
