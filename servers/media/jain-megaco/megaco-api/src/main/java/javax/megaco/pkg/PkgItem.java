package javax.megaco.pkg;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The MEGACO item class is defined as the base class for all the items in
 * MEGACO packages. This class shall be used for setting the events, signals,
 * statistics, properties, list of parameters names and their values.
 * 
 * 
 */
public class PkgItem implements Serializable {

	protected int itemId;
	protected int itemType;
	protected MegacoPkg packageId;
	protected MegacoPkg associatedPkgId = null;

	/**
	 * Constructs a Jain MEGACO Item Object. This class may be derived by the
	 * different MEGACO event, signal, property and statistics item classes. For
	 * item to represent ALL items, object of this class is created.
	 */
	public PkgItem() {

	}

	/**
	 * The method returns the value of the item id as defined in the MEGACO
	 * Package. This method may be overriden by the derived class. When called
	 * from within this class shall return a value 0xFFFF to indicate all
	 * values.
	 * 
	 * @return An integer value representing the item as defined in the megaco
	 *         package..
	 */
	public int getItemId() {
		return this.itemId;
	}

	/**
	 * This method may be overriden by the derived class. This shall return an
	 * integer value defined in PkgItemType.
	 * 
	 * @return An integer value for the item type corresponding to the derived
	 *         object. This shall return an integer value {@link javax.megaco.pkg.PkgItemType.M_ALL} when called
	 *         from this class.
	 */
	public int getItemType() {
		return this.itemType;
	}

	/**
	 * This method gets the object reference for the package id to which the
	 * item belongs.
	 * 
	 * @return The object reference of the package object to which the item is
	 *         associated. It shall return a NULL value when the method from
	 *         this class is called, indicating that there is no package
	 *         specified for item as "*".
	 */
	public MegacoPkg getItemsPkgId() {
		return this.packageId;
	}

	/**
	 * This method gets the object reference for the package id for which this
	 * item has been set in the megaco parameters. If the package that is to be
	 * associated is ALL, i.e. "*", then a package id need not be associated
	 * explicitly. If the package is not associated it would imply that the
	 * package id used for dynamic linking is "*". If the package associated is
	 * ALL, then the item too definitely needs to be ALL and hence in that case
	 * item object should be formed from the current class and not from any
	 * derived class. Though the item might belong to one package according to
	 * the protocol, but while sending it across, the item might be sent for the
	 * same package that the protocol specfies or a package that extends this
	 * package.
	 * 
	 * @return packageId - The object reference of the package object to which
	 *         the item is dynamically associated.
	 * @throws javax.megaco.ParameterNotSetException
	 *             ParameterNotSetException If the Package had not been
	 *             associated and the item type too denotes anything other than
	 *             M_ITEM_ALL.
	 */
	public final MegacoPkg getAssociatedPkgId() throws javax.megaco.ParameterNotSetException {
		// FIXME: Add exception checks
		//FIXME: add proper error  - IllegalState exception ?
		return this.associatedPkgId;
	}

	/**
	 * This method sets the object reference for the package id for which this
	 * item has been set in the megaco parameters. If the package that is to be
	 * associated is ALL, i.e. "*", then a package id need not be associated
	 * explicitly. If the package is not associated it would imply that the
	 * package id used for dynamic linking is "*". If the package associated is
	 * ALL, then the item too definitely needs to be ALL and hence in that case
	 * item object should be formed from the current class and not from any
	 * derived class. Though the item might belong to one package according to
	 * the protocol, but while sending it across, the item might be sent for the
	 * same package that the protocol specfies or a package that extends this
	 * package. This method may be overriden by the derived class.
	 * 
	 * @param packageId
	 *            packageId - The object reference of the package object to
	 *            which the item is dynamically associated.
	 * @throws IllegalArgumentException
	 *             If the package that is dynamically being linked cannot be set
	 *             due to the fact that the item does not belong to the package
	 *             or the package is not one of the ancestor package of the
	 *             package, to which the item belongs.
	 */
	public void setAssociatedPkgId(MegacoPkg packageId) throws IllegalArgumentException {
		// FIXME: addd exception checks
		this.associatedPkgId = packageId;
	}

	@Override
	public java.lang.String toString() {
		// TODO
		return this.getClass().getSimpleName() + " : ItemId = " + this.itemId + " : PackageId = " + this.packageId + " : Type = " + typeToString(this.itemType);
	}

	/**
	 * This is helper method.
	 * 
	 * @param type
	 * @return
	 */
	protected static String typeToString(int itemType) {

		switch (itemType) {
		case ParamValueType.M_BOOLEAN:
			return "boolean";
		case ParamValueType.M_DOUBLE:
			return "double";
		case ParamValueType.M_INTEGER:
			return "integer";
		case ParamValueType.M_STRING:
			return "String";
		default:
			return "Type not known: " + itemType;

		}

	}
}
