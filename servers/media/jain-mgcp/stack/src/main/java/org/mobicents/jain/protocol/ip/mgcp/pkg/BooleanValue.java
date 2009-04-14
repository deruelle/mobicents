package org.mobicents.jain.protocol.ip.mgcp.pkg;

public class BooleanValue extends Value {

	boolean flag = false;
	Parameter parameter = null;

	public BooleanValue(Parameter p, boolean flag) {
		this.parameter = p;
		this.flag = flag;
	}

	public boolean getFlag() {
		return this.flag;
	}

	public Parameter getParameter() {
		return this.parameter;
	}

	@Override
	public String toString() {
		String s = this.parameter + "=" + this.flag;
		return s;
	}
}
