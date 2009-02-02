package javax.megaco.message;

import java.io.Serializable;

import javax.megaco.InvalidArgumentException;

/**
 * This class defines the context information block that contains the context id
 * sent in the protocol and the context level parameters.
 * 
 */
public class ContextInfo implements Serializable {
	public static final int M_CTX_NULL = 0;
	public static final int M_CTX_CHOOSE = 0xFFFFFFFE;
	public static final int M_CTX_ALL = 0xFFFFFFFF;

	private int contextId;
	private ContextParam contextParam = null;

	public ContextInfo(int contextId)
			throws javax.megaco.InvalidArgumentException {
		if (contextId < 0) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"ContextID for ContextInfo cannot be less than zero");
			throw invalidArgumentException;
		}
		this.contextId = contextId;
	}

	public int getContextId() {
		return this.contextId;
	}

	public ContextParam getContextParam() {
		return this.contextParam;
	}

	public void setContextParam(ContextParam contextParam)
			throws javax.megaco.InvalidArgumentException {
		if (contextParam == null) {
			InvalidArgumentException InvalidArgumentException = new InvalidArgumentException(
					"ContextParam cannot be null for ContextInfo");
			throw InvalidArgumentException;
		}

		this.contextParam = contextParam;
	}
}
