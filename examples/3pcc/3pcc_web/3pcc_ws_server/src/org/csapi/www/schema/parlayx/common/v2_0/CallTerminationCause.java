/**
 * CallTerminationCause.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.csapi.www.schema.parlayx.common.v2_0;

public class CallTerminationCause implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected CallTerminationCause(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _CallingPartyNoAnswer = "CallingPartyNoAnswer";
    public static final java.lang.String _CalledPartyNoAnswer = "CalledPartyNoAnswer";
    public static final java.lang.String _CallingPartyBusy = "CallingPartyBusy";
    public static final java.lang.String _CalledPartyBusy = "CalledPartyBusy";
    public static final java.lang.String _CallingPartyNotReachable = "CallingPartyNotReachable";
    public static final java.lang.String _CalledPartyNotReachable = "CalledPartyNotReachable";
    public static final java.lang.String _CallHangUp = "CallHangUp";
    public static final java.lang.String _CallAborted = "CallAborted";
    public static final CallTerminationCause CallingPartyNoAnswer = new CallTerminationCause(_CallingPartyNoAnswer);
    public static final CallTerminationCause CalledPartyNoAnswer = new CallTerminationCause(_CalledPartyNoAnswer);
    public static final CallTerminationCause CallingPartyBusy = new CallTerminationCause(_CallingPartyBusy);
    public static final CallTerminationCause CalledPartyBusy = new CallTerminationCause(_CalledPartyBusy);
    public static final CallTerminationCause CallingPartyNotReachable = new CallTerminationCause(_CallingPartyNotReachable);
    public static final CallTerminationCause CalledPartyNotReachable = new CallTerminationCause(_CalledPartyNotReachable);
    public static final CallTerminationCause CallHangUp = new CallTerminationCause(_CallHangUp);
    public static final CallTerminationCause CallAborted = new CallTerminationCause(_CallAborted);
    public java.lang.String getValue() { return _value_;}
    public static CallTerminationCause fromValue(java.lang.String value)
          throws java.lang.IllegalStateException {
        CallTerminationCause enum = (CallTerminationCause)
            _table_.get(value);
        if (enum==null) throw new java.lang.IllegalStateException();
        return enum;
    }
    public static CallTerminationCause fromString(java.lang.String value)
          throws java.lang.IllegalStateException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
}
