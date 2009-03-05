package javax.megaco.pkg;

import java.io.Serializable;

public class ParamRelation implements Serializable {

	private int relation_type;

	public static final int M_SET = 1;
	public static final int M_SUBLIST = 2;
	public static final int M_RANGE = 3;
	public static final int M_EQUAL = 4;
	public static final int M_NOT_EQUAL = 5;
	public static final int M_GREATER = 6;
	public static final int M_LESS = 7;

	public static final ParamRelation SET = new ParamRelation(M_SET);
	public static final ParamRelation SUBLIST = new ParamRelation(M_SUBLIST);
	public static final ParamRelation RANGE = new ParamRelation(M_RANGE);
	public static final ParamRelation EQUAL = new ParamRelation(M_EQUAL);
	public static final ParamRelation NOT_EQUAL = new ParamRelation(M_NOT_EQUAL);
	public static final ParamRelation GREATER = new ParamRelation(M_GREATER);
	public static final ParamRelation LESS = new ParamRelation(M_LESS);

	private ParamRelation(int relation_type) {
		this.relation_type = relation_type;
	}

	public int getParamRelation() {
		return this.relation_type;
	}

	public static final ParamRelation getObject(int value) throws IllegalArgumentException {
		ParamRelation p = null;
		switch (value) {
		case (M_SET):
			p = SET;
			break;

		case (M_SUBLIST):
			p = SUBLIST;
			break;

		case (M_RANGE):
			p = RANGE;
			break;

		case (M_EQUAL):
			p = EQUAL;
			break;

		case (M_NOT_EQUAL):
			p = NOT_EQUAL;
			break;

		case (M_GREATER):
			p = GREATER;
			break;

		case (M_LESS):
			p = LESS;
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
		case (M_SET):
			p = "ParamRelation[SET]";
			break;

		case (M_SUBLIST):
			p = "ParamRelation[SUBLIST]";
			break;

		case (M_RANGE):
			p = "ParamRelation[RANGE]";
			break;

		case (M_EQUAL):
			p = "ParamRelation[EQUAL]";
			break;

		case (M_NOT_EQUAL):
			p = "ParamRelation[NOT_EQUAL]";
			break;

		case (M_GREATER):
			p = "ParamRelation[GREATER]";
			break;

		case (M_LESS):
			p = "ParamRelation[LESS]";
		default:
			p = "ParamRelation[" + this.relation_type + "]";
		}
		return p;
	}

}
