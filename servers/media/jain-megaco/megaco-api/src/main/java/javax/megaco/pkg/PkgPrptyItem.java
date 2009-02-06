package javax.megaco.pkg;

/**
 * The MEGACO property item class is an abstract class defined as the base class
 * for all the property items in the MEGACO Package. This class shall be used
 * for setting the property and their values. This extends the PkgItem class.
 * This is an abstract class and hence cannot be created as a separate object.
 * 
 * 
 */
public abstract class PkgPrptyItem extends PkgItem {

	private ParamRelation paramRelation;

	/**
	 * Constructs a Jain MEGACO Pacakge Property Item Object. This is an
	 * abstract class and can be called only by the derived classes.
	 */
	public PkgPrptyItem() {

	}

	/**
	 * The method can be used to get the property identifier. This method gives
	 * the property id of the package. This is an abstract method and is defined
	 * by the methods of the derived class which shall return an hard coded
	 * value for the property id.
	 * 
	 * @return propertyId - The integer corresponding to the property id.
	 */
	public abstract int getPropertyId();

	/**
	 * The method can be used to get the property identifier. This method gives
	 * the property id of the package. This is an abstract method and is defined
	 * by the methods of the derived class which shall return an hard coded
	 * value for the property id.
	 * 
	 * @return itemId - The integer corresponding to the property id.
	 */
	public abstract int getItemId();

	/**
	 * This method overrides the corresponding method in PkgItem. This shall
	 * return an hard coded value to indicate the item type is property.
	 * 
	 * @return itemType - An integer value for the item type corresponding to
	 *         the property. This shall return M_ITEM_PROPERTY.
	 */
	public final int getItemType() {
		return PkgItemType.M_ITEM_PROPERTY;
	}

	/**
	 * The method can be used to get the type of the value as defined in the
	 * MEGACO packages. These could be one of string or integer or boolean or
	 * double value.
	 * 
	 * @return valueType - The integer corresponding to the value type. The
	 *         values shall be defined in ParamValueType.
	 */
	public abstract int getItemValueType();

	/**
	 * The method can be used to set the relation of the value as defined in the
	 * MEGACO packages. The relation operator can be one of equal, not equal,
	 * greater than or less than operator for single value. The MEGACO parameter
	 * is accompanied by a parameter value that can be single value or set of
	 * values or sublist of values or range of values. The relation operator can
	 * be equal when the value is set or sublist or range. This method specifies
	 * both the relation operator and also specifies whether the accompaning
	 * parameter value is single value or set of values or sublist of values or
	 * range of value. If the relation specifies set or range or sublist, it
	 * automatically assumes the relation to be "MEGACO_EQUAL". The default
	 * value of the relation can be set in constructor of each class that
	 * derives this class.
	 * 
	 * @return paramRelation - The integer corresponding to the value relation.
	 *         The values shall be defined in ParamRelation.
	 * @throws javax.megaco.ParameterNotSetException
	 */
	public final int getItemsValueRelation()
			throws javax.megaco.ParameterNotSetException {
		return this.paramRelation.getParamRelation();
	}

	public final void setItemValue(java.lang.String[] value)
			throws javax.megaco.InvalidArgumentException {
		// TODO
	}

	public final void setItemValue(int[] value)
			throws javax.megaco.InvalidArgumentException {
		// TODO
	}

	public final void setItemValue(boolean value) {
		// TODO
	}

	public final void setItemValue(double[] value)
			throws javax.megaco.InvalidArgumentException {
		// TODO
	}

	public java.lang.String toString() {
		// TODO
		return this.toString();
	}

}
