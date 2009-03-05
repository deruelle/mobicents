package javax.megaco.pkg;

import java.io.Serializable;

public final class ParamValueType implements Serializable {
	public static final int M_STRING = 1;
	public static final int M_INTEGER = 2;
	public static final int M_BOOLEAN = 3;
	public static final int M_DOUBLE = 4;

	public static final ParamValueType STRING = new ParamValueType(M_STRING);
	public static final ParamValueType INTEGER = new ParamValueType(M_INTEGER);
	public static final ParamValueType BOOLEAN = new ParamValueType(M_BOOLEAN);
	public static final ParamValueType DOUBLE = new ParamValueType(M_DOUBLE);

	private int value_type;

	private ParamValueType(int value_type) {
		this.value_type = value_type;
	}

	public int getParamValueType() {
		return this.value_type;
	}

	public static final ParamValueType getObject(int value) throws IllegalArgumentException {
		ParamValueType p = null;
		switch (value) {
		case M_STRING:
			p = STRING;
			break;

		case M_INTEGER:
			p = INTEGER;
			break;

		case M_BOOLEAN:
			p = BOOLEAN;
			break;

		case M_DOUBLE:
			p = DOUBLE;
			break;

		default:
			throw new IllegalArgumentException("No ParamValueType for passed value " + value);
		}
		return p;
	}

	private Object readResolve() {
		return this.getObject(this.value_type);
	}

	@Override
	public String toString() {
		String p = null;
		switch (this.value_type) {
		case M_STRING:
			p = "ParamValueType[STRING]";
			break;

		case M_INTEGER:
			p = "ParamValueType[INTEGER]";
			break;

		case M_BOOLEAN:
			p = "ParamValueType[BOOLEAN]";
			break;

		case M_DOUBLE:
			p = "ParamValueType[DOUBLE]";
			break;

		default:
			p = "ParamValueType[" + this.value_type + "]";
		}
		return p;
	}

}
