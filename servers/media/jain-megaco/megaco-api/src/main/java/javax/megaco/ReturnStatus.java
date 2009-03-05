package javax.megaco;

public class ReturnStatus {

	public static final int M_SUCCESS = 1;
	public static final int M_FAILURE = 2;

	public static final ReturnStatus SUCCESS = new ReturnStatus(M_SUCCESS);

	public static final ReturnStatus FAILURE = new ReturnStatus(M_FAILURE);

	private int return_status;

	private ReturnStatus(int return_status) {
		this.return_status = return_status;

	}

	public int getReturnStatus() {
		return this.return_status;
	}

	public static final ReturnStatus getObject(int value) throws IllegalArgumentException {
		ReturnStatus r = null;
		switch (value) {
		case M_SUCCESS:
			r = SUCCESS;
			break;

		case M_FAILURE:
			r = FAILURE;
			break;
		default:
			throw new IllegalArgumentException("No ReturnStatus for value " + value);

		}

		return r;
	}

	private Object readResolve() {

		return this.getObject(this.return_status);

	}

	@Override
	public String toString() {
		String r = null;
		switch (this.return_status) {
		case M_SUCCESS:
			r = "ReturnStatus[SUCCESS]";
			break;

		case M_FAILURE:
			r = "ReturnStatus[FAILURE]";
			break;
		default:
			r = "ReturnStatus[" + this.return_status + "]";

		}

		return r;
	}

}
