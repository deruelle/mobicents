package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.PkgItemStr;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the package
 * descriptor.
 */
public class PackageDescriptor extends Descriptor implements Serializable {

	private PackagesItem[] packagesItems;
	private PkgItemStr[] pkgItemStr;

	/**
	 * Constructs a Package Descriptor.
	 */
	public PackageDescriptor() {
		super.descriptorId = DescriptorType.M_PACKAGE_DESC;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type Package descriptor.
	 * This method overrides the corresponding method of the base class
	 * Descriptor.
	 * 
	 * @return Returns an integer value that identifies this object as the type
	 *         of package descriptor. It returns that it is Package Descriptor
	 *         i.e., M_PACKAGE_DESC.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns the packages Item in the package descriptor.
	 * 
	 * @return Returns vector value that identifies the package Item value. If
	 *         PackagesItem is not set, then this method would return NULL.
	 */
	public final PackagesItem[] getMegacoPkgItems() {
		return this.packagesItems;

	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns the vector of the object of the type packages Item.
	 * 
	 * @param pkgs_item
	 *            - Vector value that identifies the package Item value.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Packages Item
	 *             passed to this method is NULL.
	 */
	public final void setMegacoPkgItems(PackagesItem[] pkgs_item) throws IllegalArgumentException {

		if (pkgs_item == null) {
			throw new IllegalArgumentException("PackagesItem[] must not be null.");
		}

		if (pkgs_item.length == 0) {
			throw new IllegalArgumentException("PackagesItem[] must not be empty.");
		}

		this.packagesItems = pkgs_item;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns the packages Item in the package descriptor.
	 * 
	 * @return Returns vector value that identifies the package Item value. If
	 *         PkgItemStr is not set, then this method would return NULL.
	 */
	public final PkgItemStr[] getMegacoPkgItemsStr() {
		return this.pkgItemStr;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns the vector of the object of the type packages Item. This method
	 * will be used for the MEGACO packages which are not defined in the
	 * javax.megaco.pkg package.
	 * 
	 * @param pkgs_item
	 *            - Vector value that identifies the package Item value.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of Package Item
	 *             String passed to this method is NULL.
	 */
	public final void setMegacoPkgItemsStr(PkgItemStr[] pkgs_item) throws IllegalArgumentException {

		if (pkgs_item == null) {
			throw new IllegalArgumentException("PkgItemStr[] must not be null.");
		}

		if (pkgs_item.length == 0) {
			throw new IllegalArgumentException("PkgItemStr[] must not be empty.");
		}

		this.pkgItemStr = pkgs_item;
	}

}
