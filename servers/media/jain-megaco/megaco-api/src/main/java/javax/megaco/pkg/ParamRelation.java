package javax.megaco.pkg;

import java.io.Serializable;

public class ParamRelation implements Serializable {

	private int relation_type;

	public static final int M_PARAM_REL_SET = 1;
	public static final int M_PARAM_REL_SUBLIST = 2;
	public static final int M_PARAM_REL_RANGE = 3;
	public static final int M_PARAM_REL_EQUAL = 4;
	public static final int M_PARAM_REL_NOT_EQUAL = 5;
	public static final int M_PARAM_REL_GREATER = 6;
	public static final int M_PARAM_REL_LESS = 7;

	public static final ParamRelation PARAM_REL_SET = new ParamRelation(M_PARAM_REL_SET);
	public static final ParamRelation PARAM_REL_SUBLIST = new ParamRelation(M_PARAM_REL_SUBLIST);
	public static final ParamRelation PARAM_REL_RANGE = new ParamRelation(M_PARAM_REL_RANGE);
	public static final ParamRelation PARAM_REL_EQUAL = new ParamRelation(M_PARAM_REL_EQUAL);
	public static final ParamRelation PARAM_REL_NOT_EQUAL = new ParamRelation(M_PARAM_REL_NOT_EQUAL);
	public static final ParamRelation PARAM_REL_GREATER = new ParamRelation(M_PARAM_REL_GREATER);
	public static final ParamRelation PARAM_REL_LESS = new ParamRelation(M_PARAM_REL_LESS);

	private ParamRelation(int relation_type) {
		this.relation_type = relation_type;
	}

	public int getParamRelation() {
		return this.relation_type;
	}

	public static final ParamRelation getObject(int value) throws IllegalArgumentException {
		ParamRelation p = null;
		switch (value) {
		case (M_PARAM_REL_SET):
			p = PARAM_REL_SET;
			break;

		case (M_PARAM_REL_SUBLIST):
			p = PARAM_REL_SUBLIST;
			break;

		case (M_PARAM_REL_RANGE):
			p = PARAM_REL_RANGE;
			break;

		case (M_PARAM_REL_EQUAL):
			p = PARAM_REL_EQUAL;
			break;

		case (M_PARAM_REL_NOT_EQUAL):
			p = PARAM_REL_NOT_EQUAL;
			break;

		case (M_PARAM_REL_GREATER):
			p = PARAM_REL_GREATER;
			break;

		case (M_PARAM_REL_LESS):
			p = PARAM_REL_LESS;
		default:
			throw new IllegalArgumentException("There is no ParamRelation for passed value = " + value);
		}
		return p;
	}

	private Object readResolve() {
		return this.getObject(this.relation_type);
	}

	@Override
	public String toString() {
		String p = null;
		switch (this.relation_type) {
		case (M_PARAM_REL_SET):
			p = "ParamRelation[SET]";
			break;

		case (M_PARAM_REL_SUBLIST):
			p = "ParamRelation[SUBLIST]";
			break;

		case (M_PARAM_REL_RANGE):
			p = "ParamRelation[RANGE]";
			break;

		case (M_PARAM_REL_EQUAL):
			p = "ParamRelation[EQUAL]";
			break;

		case (M_PARAM_REL_NOT_EQUAL):
			p = "ParamRelation[NOT_EQUAL]";
			break;

		case (M_PARAM_REL_GREATER):
			p = "ParamRelation[GREATER]";
			break;

		case (M_PARAM_REL_LESS):
			p = "ParamRelation[LESS]";
		default:
			p = "ParamRelation[" + this.relation_type + "]";
		}
		return p;
	}

}
