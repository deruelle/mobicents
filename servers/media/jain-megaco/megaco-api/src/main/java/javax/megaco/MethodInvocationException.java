package javax.megaco;

/**
 * This Exception is thrown when an attempt is made to set an invalid parameter
 * in an object
 * 
 * @deprecated - THis is wrong practice. It should be indicated by runtime
 *             exception
 */
public class MethodInvocationException extends MegacoException {

	public MethodInvocationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MethodInvocationException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
