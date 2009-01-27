package javax.megaco;

public class MegacoException extends Exception {
	private int assocHandle;
	private ExceptionInfoCode exceptionInfoCode = null;

	public MegacoException() {
		super();
	}

	public MegacoException(java.lang.String msg) {
		super(msg);
	}

	public void setAssocHandle(int assocHandle) {
		this.assocHandle = assocHandle;

	}

	public int getAssocHandle() {
		return this.assocHandle;
	}

	public void setInfoCode(ExceptionInfoCode ecode) {
		this.exceptionInfoCode = ecode;
	}

	public int getInfoCode() {
		return this.exceptionInfoCode.getExceptionInfoCode();
	}
}
