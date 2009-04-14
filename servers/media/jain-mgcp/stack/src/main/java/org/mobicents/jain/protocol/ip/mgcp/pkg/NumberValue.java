package org.mobicents.jain.protocol.ip.mgcp.pkg;

public class NumberValue extends Value {
	private int number = -1;
	private Parameter parameter = null;

	public NumberValue(Parameter parameter, int number) {
		super();
		this.number = number;
		this.parameter = parameter;

	}

	public int getNumber() {
		return this.number;
	}

	public Parameter getParameter() {
		return this.parameter;
	}

	@Override
	public String toString() {
		String s = this.parameter + "=" + this.number;
		return s;
	}
}
