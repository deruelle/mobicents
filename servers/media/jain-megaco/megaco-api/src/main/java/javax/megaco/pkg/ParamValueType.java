package javax.megaco.pkg;

import java.io.Serializable;

public final class ParamValueType implements Serializable {
	public static final int M_ITEM_PARAM_VALUE_STRING = 1;
	public static final int M_ITEM_PARAM_VALUE_INTEGER = 2;
	public static final int M_ITEM_PARAM_VALUE_BOOLEAN = 3;
	public static final int M_ITEM_PARAM_VALUE_DOUBLE = 4;

	public static final ParamValueType ITEM_PARAM_VALUE_STRING = new ParamValueType(
			M_ITEM_PARAM_VALUE_STRING);
	public static final ParamValueType ITEM_PARAM_VALUE_INTEGER = new ParamValueType(
			M_ITEM_PARAM_VALUE_INTEGER);
	public static final ParamValueType ITEM_PARAM_VALUE_BOOLEAN = new ParamValueType(
			M_ITEM_PARAM_VALUE_BOOLEAN);
	public static final ParamValueType ITEM_PARAM_VALUE_DOUBLE = new ParamValueType(
			M_ITEM_PARAM_VALUE_DOUBLE);

	private int value_type;

	private ParamValueType(int value_type) {
		this.value_type = value_type;
	}

	public int getParamValueType() {
		return this.value_type;
	}

	public static final ParamValueType getObject(int value)
			throws IllegalArgumentException {
		ParamValueType p = null;
		switch (value) {
		case M_ITEM_PARAM_VALUE_STRING:
			p = ITEM_PARAM_VALUE_STRING;
			break;

		case M_ITEM_PARAM_VALUE_INTEGER:
			p = ITEM_PARAM_VALUE_INTEGER;
			break;

		case M_ITEM_PARAM_VALUE_BOOLEAN:
			p = ITEM_PARAM_VALUE_BOOLEAN;
			break;

		case M_ITEM_PARAM_VALUE_DOUBLE:
			p = ITEM_PARAM_VALUE_DOUBLE;
			break;

		default:
			throw new IllegalArgumentException(
					"No ParamValueType for passed value " + value);
		}
		return p;
	}

	private Object readResolve() {
		return this.getObject(this.value_type);
	}
}
