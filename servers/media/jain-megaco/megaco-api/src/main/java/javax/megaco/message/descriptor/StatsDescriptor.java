package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.PkgItemStr;
import javax.megaco.pkg.PkgStatsItem;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the statistics
 * descriptor. It stores the statistics values against a statistics identity.
 */
public class StatsDescriptor extends Descriptor implements Serializable {
	private PkgStatsItem[] pkgStatsItem;
	private PkgItemStr[] pkgItemStr;

	/**
	 * Constructs a Statistics Descriptor. This contains the statistics id and
	 * its associated value.
	 * 
	 * @param statsItem
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid statistics id is set.
	 */
	public StatsDescriptor(PkgStatsItem[] statsItem) throws IllegalArgumentException {
		super.descriptorId = DescriptorType.M_STATISTICS_DESC;

		if (statsItem == null) {
			throw new IllegalArgumentException("PkgStatsItem[] must not be null.");
		}

		if (statsItem.length == 0) {
			throw new IllegalArgumentException("PkgStatsItem[] must not be empty.");
		}

		this.pkgStatsItem = statsItem;
	}

	/**
	 * Constructs Statistics descriptor object with the PkgItemStr object. This
	 * method would be used if the package parameters are to be conveyed in the
	 * string format.
	 * 
	 * @param statsItemStr
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid statsItemStr object reference is set.
	 */
	public StatsDescriptor(PkgItemStr[] statsItemStr) throws IllegalArgumentException {
		super.descriptorId = DescriptorType.M_STATISTICS_DESC;

		if (statsItemStr == null) {
			throw new IllegalArgumentException("PkgItemStr[] must not be null.");
		}

		if (statsItemStr.length == 0) {
			throw new IllegalArgumentException("PkgItemStr[] must not be empty.");
		}

		this.pkgItemStr = statsItemStr;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type Statistics descriptor.
	 * This method overrides the corresponding method of the base class
	 * Descriptor.
	 * 
	 * @return Returns an integer value that identifies this object as the type
	 *         of Statistics descriptor. It returns that it is Statistics
	 *         Descriptor i.e., M_STATISTICS_DESC.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
	}

	/**
	 * The method can be used to get the statistics id and its associated value.
	 * 
	 * @return stats - The object reference for the megaco statistics identity
	 *         and the associated value.
	 */
	public PkgStatsItem[] getMegacoPkgStatsItem() {
		return this.pkgStatsItem;
	}

	/**
	 * The method can be used to get the pkdgName as set in the statistics
	 * descriptor. This method gives the package information, the attached event
	 * information and the parameter name and value. As comapres to the
	 * getMegacoPkgStatsItem() method, this method returnes the package
	 * parameters in the string format.
	 * 
	 * @return The object reference for the megaco package item. This class
	 *         holds information about package name, item name and the
	 *         parameters in the string format. If the parameter has not been
	 *         set, then this method shall return NULL.
	 */
	public PkgItemStr[] getMegacoPkgItemStr() {
		return this.pkgItemStr;

	}

}
