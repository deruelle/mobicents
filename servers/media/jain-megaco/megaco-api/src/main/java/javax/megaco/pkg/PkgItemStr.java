package javax.megaco.pkg;

import java.io.Serializable;

import javax.megaco.ExceptionInfoCode;

/**
 * The MEGACO Package Item String class is used to define the Package, Items and
 * Parameters in the string format. If the implementations uses a MEGACO package
 * which is not defined in the javax.megaco.pkg package, then the values of
 * Package, Item and Parameter, are exchanged using this class. This class has
 * been provided to take care of the extensibility within the protocol using
 * protocol packages. The descriptor classes have necessary method to pass
 * reference to the object of the PkgItemStr class in addition to the existing
 * interface for PkgEventItem or PkgSignalItem etc.
 * 
 * Note: Since there is no configuration of the package parameters passed in
 * this class, the parameters passed are not validated.
 */
public class PkgItemStr implements Serializable {

	private String pkgName;
	private String itemName;
	private String parameter;
	private PkgItemType itemType;

	/**
	 * Constructs a MEGACO Package Item class to specify the package parameters
	 * in the string format. This class would be used to specify the package
	 * parameters for the package which is not defined in the javax.megaco.pkg
	 * package.
	 * 
	 * 
	 */
	public PkgItemStr() {

	}

	/**
	 * This method is used to set the package name. The package name specified
	 * must be in the syntax defined by the MEGACO protocol.Since the pkgName
	 * specified is not configured in the class, therefore its value cannot be
	 * validated.
	 * 
	 * @param pkgName
	 *            The string corresponding to the package name.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Package name
	 *             string passed to this method is NULL.
	 */
	public final void setPkgName(java.lang.String pkgName) throws IllegalArgumentException {
		if (pkgName == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("Package name cannot be null for PkgItemStr");
			// invalidArgumentException.setInfoCode(ExceptionInfoCode.MISSING_PKG_NAME);
			throw invalidArgumentException;
		}
		this.pkgName = pkgName;
	}

	/**
	 * This method is used to get the package name. The package name returned is
	 * in the string format.
	 * 
	 * @return String corresponding to the package name. In case no value for
	 *         package name is present, then this method shall return NULL.
	 */
	public final java.lang.String getAssociatedPkgName() {
		return this.pkgName;
	}

	/**
	 * This method is used to set the item name. The item name specified must be
	 * in the syntax defined by the MEGACO protocol.Since the item name
	 * specified is not configured in the class, therefore its value cannot be
	 * validated.
	 * 
	 * @param itemName
	 *            The string corresponding to the item name.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Item name string
	 *             passed to this method is NULL.
	 */
	public final void setItemName(java.lang.String itemName) throws IllegalArgumentException {
		if (itemName == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("Item name cannot be null for PkgItemStr");
			// invalidArgumentException.setInfoCode(ExceptionInfoCode.MISSING_ITEM_NAME);
			throw invalidArgumentException;
		}
		this.itemName = itemName;
	}

	/**
	 * This method is used to get the item name. The item name returned is in
	 * the string format.
	 * 
	 * @return String corresponding to the item name. In case no value for item
	 *         name is present, then this method shall return NULL.
	 */
	public final java.lang.String getItemName() {
		return this.itemName;
	}

	/**
	 * This method is used to set the package parameter. The parameter specified
	 * here has parameter name followed by the relational operator and then
	 * followed by the parameter values. The parameter specified must be in the
	 * syntax defined by the MEGACO protocol.Since the parameter is not
	 * configured in the class, therefore its value cannot be validated.
	 * 
	 * @param parameter
	 *            The string corresponding to the package parameters.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Package Item
	 *             object passed to this method is NULL.
	 */
	public final void setParameter(java.lang.String parameter) throws IllegalArgumentException {
		if (itemName == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("Parameter cannot be null for PkgItemStr");
			// invalidArgumentException.setInfoCode(ExceptionInfoCode.MISSING_PKG_PARAM);
			throw invalidArgumentException;
		}
		this.parameter = parameter;
	}

	/**
	 * This method is used to get the package parameter. The parameter returnes
	 * here has parameter name followed by the relational operator and then
	 * followed by the parameter values.
	 * 
	 * @return String corresponding to the package parameter. In case no value
	 *         for parameter is specified, then this methos shall return NULL.
	 */
	public final java.lang.String getParameter() {
		return this.parameter;
	}

	/**
	 * This method is used to set the Item type for which the parameters are
	 * specified. The item_type is set to
	 * {@link javax.megaco.pkg.PkgItemType.M_ALL} if the item_name is '*'. The
	 * item_type is set to {@link javax.megaco.pkg.PkgItemType.M_EVENT}, if the
	 * descriptor for which the parameters are specified is one of Event
	 * Descriptor, Observed Event Descriptor or Event Buffer Descriptor. The
	 * item_type is set to {@link javax.megaco.pkg.PkgItemType.M_STATISTICS}, if
	 * the descriptor for which the parameters are specified is Statistics
	 * Descriptor. The item_type is set to
	 * {@link javax.megaco.pkg.PkgItemType.M_SIGNAL}, if the descriptor for
	 * which the parameters are specified is Signal Descriptor. The item_type is
	 * set to {@link javax.megaco.pkg.PkgItemType.M_PROPERTY}, if the descriptor
	 * for which the parameters are specified is Media Descriptor.
	 * 
	 * @param itemType
	 *            The object reference of the class PkgItemType which identifies
	 *            the item type to which the parameter belongs.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Package Item
	 *             object passed to this method is NULL.
	 */
	public final void setItemType(PkgItemType itemType) throws IllegalArgumentException {
		if (itemType == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("Package Item Type cannot be null for PkgItemStr");
			// invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_PKG_ITEM_TYPE);
			throw invalidArgumentException;
		}
		this.itemType = itemType;
	}

	@Override
	public java.lang.String toString() {
		return this.pkgName + " " + this.parameter + " " + this.itemName + " " + this.itemType;
	}
}
