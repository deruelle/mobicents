package javax.megaco;

/**
 * This Exception is thrown when an attempt is made to access a parameter of a
 * JAIN MEGACO primitive when the value of the primitive has not yet been set.
 * 

 */
public class ParameterNotSetException extends MegacoRuntimeException {

	public ParameterNotSetException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ParameterNotSetException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}



}
