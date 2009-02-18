package javax.megaco.pkg;

import javax.megaco.ExceptionInfoCode;
import javax.megaco.InvalidArgumentException;

/**
 * The MEGACO package item parameter class is an abstract and shall be used for
 * setting the parameter name and value attached to an event or a signal. The
 * derived class for this would specify the hard coded value for their identity,
 * name, type and other parameters, but the value for the parameter would be set
 * within this base class.
 * 
 * 
 */
public abstract class PkgItemParam implements java.io.Serializable {

	private ParamRelation paramRelation;

	protected int paramId = -1;
	protected int paramValueType = -1;
	protected int[] paramsDescriptorIds = null;
	protected int[] paramsItemIds = null;

	/**
	 * Constructs a Jain MEGACO package item parameter Object. This is an
	 * abstract class and can be called only by the derived classes.
	 */
	public PkgItemParam() {

	}

	/**
	 * The method can be used to get the parameter identifier as defined in the
	 * MEGACO packages. A hardcoded value is returned by the derived class.
	 * 
	 * @return paramId - The integer corresponding to the parameter id.
	 */
	public abstract int getParamId();

	/**
	 * The method can be used to get the type of the parameter as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or double value or boolean.
	 * 
	 * @return paramType - The integer corresponding to the parameter type. The
	 *         values shall be defined in ParamValueType.
	 */
	public abstract int getParamValueType();

	public final int getParamsValueRelation() throws javax.megaco.ParameterNotSetException {
		return this.paramRelation.getParamRelation();
	}

	public final void setParamsValueRelation(ParamRelation paramRelation) throws javax.megaco.InvalidArgumentException {
		if (paramRelation == null) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException("ParamRelation cannot be null from PkgItemParam");
			invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_PARAM_RELATION);
			throw invalidArgumentException;
		}
		this.paramRelation = paramRelation;
	}

	public final void setParamsValue(java.lang.String[] value) throws javax.megaco.MethodInvocationException, javax.megaco.InvalidArgumentException {
		// TODO
	}

	public final void setParamsValue(int[] value) throws javax.megaco.MethodInvocationException, javax.megaco.InvalidArgumentException {
		// TODO
	}

	public final void setParamsValue(boolean value) throws javax.megaco.MethodInvocationException, javax.megaco.InvalidArgumentException {
		// TODO
	}

	public final void setParamsValue(double[] value) throws javax.megaco.MethodInvocationException, javax.megaco.InvalidArgumentException {
		// TODO
	}

	@Override
	public java.lang.String toString() {
		// TODO
		return this.toString();
	}
}
