package javax.megaco.pkg;

/**
 * The MEGACO statistics item class is an abstract class defined as the base
 * class for all the statistics items in the MEGACO Package. This class shall be
 * used for setting the statistics and their values. This extends the PkgItem
 * class. This is an abstract class and hence cannot be created as a separate
 * object.
 * 
 * 
 */
public abstract class PkgStatsItem extends PkgItem {

	private ParamRelation paramRelation;
	private int[] value;
	protected int statisticsId = -1;

	/**
	 * Constructs a Jain MEGACO Package Statistics Item Object. This is an
	 * abstract class and can be called only by the derived classes.
	 */
	public PkgStatsItem() {

	}

	/**
	 * The method can be used to get the statistics identifier. This method
	 * gives the statistics id of the package. This is an abstract method and is
	 * defined by the methods of the derived class which shall return an hard
	 * coded value for the statistics id.
	 * 
	 * @return statisticsId - The integer corresponding to the statistics id.
	 */
	public abstract int getStatisticsId();

	/**
	 * The method can be used to get the statistics identifier. This method
	 * gives the statistics id of the package. This is an abstract method and is
	 * defined by the methods of the derived class which shall return an hard
	 * coded value for the statistics id.
	 * 
	 * statisticsId - The integer corresponding to the statistics id.
	 */
	public abstract int getItemId();

	/**
	 * This method overrides the corresponding method in PkgItem. This shall
	 * return an hard coded value to indicate the item type is statistics.
	 * 
	 * itemType - An integer value for the item type corresponding to the
	 * statistics. This shall return M_ITEM_STATISTICS.
	 */
	public final int getItemType() {
		return PkgItemType.M_ITEM_STATISTICS;
	}

	/**
	 * The method can be used to get the type of the value as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or boolean or double value.
	 * 
	 * @return valueType - The integer corresponding to the value type. The
	 *         values shall be defined in ParamValueType.
	 */
	public abstract int getItemValueType();

	/**
	 * The method can be used to get the relation of the value as defined in the
	 * MEGACO packages. The relation operator can be one of equal, not equal,
	 * greater than or less than operator for single value. The MEGACO parameter
	 * is accompanied by a parameter value that can be single value or set of
	 * values or sublist of values or range of values. The relation operator can
	 * be equal when the value is set or sublist or range. This method specifies
	 * both the relation operator and also specifies whether the accompaning
	 * parameter value is single value or set of values or sublist of values or
	 * range of value. If the relation specifies set or range or sublist, it
	 * automatically assumes the relation to be "MEGACO_EQUAL".
	 * 
	 * @return paramRelation - The int corresponding to the value relation. The
	 *         values shall be defined in ParamRelation.
	 * @throws javax.megaco.ParameterNotSetException
	 *             Thrown if this parameter has not been set.
	 */
	public final int getItemsValueRelation()
			throws javax.megaco.ParameterNotSetException {
		// TODO
		return paramRelation.getParamRelation();
	}

	/**
	 * The method can be used to get the descriptor ids corresponding to the
	 * item. This shall be overriden by the derived class and would be returned
	 * a hardcoded value from the derived class.
	 * 
	 * @return descriptorId - The vector of integers corresponding to the
	 *         descriptor to which the item can be used. The values shall be
	 *         defined in DescriptorType.
	 */
	public abstract int[] getItemsDescriptorIds();

	public final java.lang.String[] getItemStringValue()
			throws javax.megaco.MethodInvocationException,
			javax.megaco.ParameterNotSetException {
		// TODO
		return null;
	}

	public final int[] getItemIntValue()
			throws javax.megaco.MethodInvocationException,
			javax.megaco.ParameterNotSetException {
		// TODO
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws javax.megaco.MethodInvocationException
	 * @throws javax.megaco.ParameterNotSetException
	 */
	public final boolean getItemBooleanValue()
			throws javax.megaco.MethodInvocationException,
			javax.megaco.ParameterNotSetException {
		// TODO
		return false;
	}

	public final double[] getItemDoubleValue()
			throws javax.megaco.MethodInvocationException,
			javax.megaco.ParameterNotSetException {
		// TODO
		return null;
	}

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
	 * automatically assumes the relation to be "MEGACO_EQUAL".
	 * 
	 * @param paramRelation
	 *            paramRelation - The integer corresponding to the value
	 *            relation. The values shall be defined in ParamRelation.
	 */
	public final void setItemsValueRelation(ParamRelation paramRelation) {
		this.paramRelation = paramRelation;
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
