package javax.megaco.pkg;

/**
 * The MEGACO event item class is an abstract class defined as the base class
 * for all the event items in the MEGACO Package. This class shall be used for
 * setting the events, list of parameters names and their values. This extends
 * the PkgItem class. This is an abstract class and hence cannot be created as a
 * separate object.
 * 
 * 
 */
public abstract class PkgEventItem extends PkgItem {

	protected int eventId = -1;

	protected PkgItemParam[] paramInfo;

	/**
	 * Constructs a Jain MEGACO Package Event Item Object. This is an abstract
	 * class and can be called only by the derived classes.
	 */
	public PkgEventItem() {

	}

	/**
	 * The method can be used to get the event identifier. This method gives the
	 * event id of the event item. This is an abstract method and is defined by
	 * the methods of the derived class which shall return an hard coded value
	 * for the event id.
	 * 
	 * @return eventId - The integer corresponding to the event id.
	 */
	public abstract int getEventId();

	/**
	 * The method can be used to get the event identifier. This method gives the
	 * item id of the event item. This is an abstract method and is defined by
	 * the methods of the derived class which shall return an hard coded value
	 * for the event id.
	 * 
	 * eventId - The integer corresponding to the event id.
	 */
	public abstract int getItemId();

	/**
	 * This method overrides the corresponding method in PkgItem. This shall
	 * return an hard coded value to indicate the item type is event.
	 * 
	 * itemType - An integer value for the item type corresponding to the event.
	 * This shall return {@link javax.megaco.pkg.PkgItemType.M_EVENT}
	 */
	public final int getItemType() {
		return PkgItemType.M_EVENT;
	}

	/**
	 * This method overrides the corresponding method in PkgItem. This method
	 * would set a flag to indicate that the dynamic package has been associated
	 * and would not allow the application to overwrite the package association
	 * once the parameter has been set for the item. If the package name can be
	 * linked, then this method inturn calls the corresponding method of the
	 * PkgItem class.
	 * 
	 * packageId - The object reference of the package object to which the item
	 * is dynamically associated.
	 * 
	 * IllegalArgumentException This exception will be raised in the following
	 * cases 1. If the method is called to modify the associated package after
	 * the parameter has been set. 2. If the package that is dynamically being
	 * linked cannot be set due to the fact that the item does not belong to the
	 * package or the package is not one of the ancestor package of the package,
	 * to which the item belongs. 3. If the reference of package Id passed to
	 * this method is NULL.
	 */
	public void setAssociatedPkgId(MegacoPkg packageId)
			throws IllegalArgumentException {
		// TODO
	}

	/**
	 * The method can be used to get the vector object identifier that specifies
	 * the attached Parameter name and the corresponding values for the event.
	 * 
	 * @return paramInfo - The vector of objectIdentifier corresponding to the
	 *         param information set for the package. If the parameter is not
	 *         set then this shall return a NULL value.
	 */
	public final PkgItemParam[] getMegacoPkgItemParam() {
		return this.paramInfo;
	}

	/**
	 * The method can be used to set the Parameter information. This method sets
	 * the attached parameters for the event. This method should be called only
	 * after a package has been associated with the item. Once the parameter has
	 * been set, it shall not allow the associated item to be overwritten. This
	 * method shall verify the following:
	 * 
	 * <br>
	 * 1. The parameter can be set for the relevant item. <br>
	 * 2. The parameter too belongs to the same package for which the item
	 * belongs. <br>
	 * 3. If the parameter does not belong to the same package, then does it
	 * belong to a package that extends the package to which the item belongs. <br>
	 * 4. If the parameter and the item belong to different packages, but if the
	 * package to which the parameter belongs extends the package to which the
	 * item belongs, then it should validate that the current association of the
	 * item is to the package to which the parameter belongs or should be such
	 * that it extends the package to which the parameter belongs.
	 * 
	 * @param paramInfo - The vector of objectIdentifier corresponding to the parameter information.
	 * @throws IllegalArgumentException If the parameter cannot be linked to the current event.
	 * @throws IllegalStateException If the item has not been associated with a package.
	 */
	public final void setMegacoPkgItemParam(PkgItemParam[] paramInfo) throws IllegalArgumentException, IllegalStateException {
		// FIXME: add checks for exceptions
		this.paramInfo = paramInfo;
	}

	@Override
	public java.lang.String toString() {
		// TODO
		return super.toString() +" : EventId = "+eventId;
	}
}
