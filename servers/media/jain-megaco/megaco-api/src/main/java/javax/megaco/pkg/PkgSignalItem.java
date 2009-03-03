package javax.megaco.pkg;

/**
 * The MEGACO signal item class is an abstract class defined as the base class
 * for all the signal items in the MEGACO Package. This class shall be used for
 * setting the signals, list of parameters names and their values. This extends
 * the PkgItem class. This is an abstract class and hence cannot be created as a
 * separate object.
 * 
 */
public abstract class PkgSignalItem extends PkgItem {
	public PkgSignalItem() {

	}

	protected int signalId = -1;
	protected MegacoPkg packageId;

	/**
	 * The method can be used to get the signal identifier. This method gives
	 * the signal id of the package. This is an abstract method and is defined
	 * by the methods of the derived class which shall return an hard coded
	 * value for the signal id.
	 * 
	 * @return signalId - The integer corresponding to the signal id.
	 */
	public abstract int getSignalId();

	/**
	 * The method can be used to get the signal identifier. This method gives
	 * the signal id of the package. This is an abstract method and is defined
	 * by the methods of the derived class which shall return an hard coded
	 * value for the signal id.
	 * 
	 * eventId - The integer corresponding to the event id.
	 */
	public abstract int getItemId();

	/**
	 * This method overrides the corresponding method in PkgItem. This shall
	 * return an hard coded value to indicate the item type is signal.
	 * 
	 * itemType - An integer value for the item type corresponding to the
	 * signal. This shall return M_ITEM_SIGNAL.
	 */
	public final int getItemType() {
		return PkgItemType.M_ITEM_SIGNAL;
	}

	public MegacoPkg getItemsPkgId() {
		return this.packageId;
	}

	/**
	 * This method overrides the corresponding method in PkgItem. This method
	 * would set a flag to indicate that the dynamic package has been associated
	 * and would not allow the application to overwrite the package association
	 * once the parameter has been set for the item. If the package name can be
	 * linked, then this method inturn calls the corresponding method of the
	 * PkgItem class.
	 * @param packageId - The object reference of the package object to which the item is dynamically associated.
	 */
	public void setAssociatedPkgId(MegacoPkg packageId) throws javax.megaco.InvalidArgumentException {
		// TODO
	}

	public final PkgItemParam[] getMegacoPkgItemParam() {
		// TODO
		return null;
	}

	public final void setMegacoPkgItemParam(PkgItemParam[] paramInfo) throws javax.megaco.InvalidArgumentException, javax.megaco.MethodInvocationException {
		// TODO
	}

	@Override
	public java.lang.String toString() {
		// TODO
		return this.toString();
	}
}
