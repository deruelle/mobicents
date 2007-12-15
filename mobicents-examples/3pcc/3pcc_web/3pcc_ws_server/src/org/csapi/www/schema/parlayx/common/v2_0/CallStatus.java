/**
 * CallStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.csapi.www.schema.parlayx.common.v2_0;

public class CallStatus implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected CallStatus(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _CallInitial = "CallInitial";
    public static final java.lang.String _CallConnected = "CallConnected";
    public static final java.lang.String _CallTerminated = "CallTerminated";
    public static final CallStatus CallInitial = new CallStatus(_CallInitial);
    public static final CallStatus CallConnected = new CallStatus(_CallConnected);
    public static final CallStatus CallTerminated = new CallStatus(_CallTerminated);
    public java.lang.String getValue() { return _value_;}
    public static CallStatus fromValue(java.lang.String value)
          throws java.lang.IllegalStateException {
        CallStatus enum = (CallStatus)
            _table_.get(value);
        if (enum==null) throw new java.lang.IllegalStateException();
        return enum;
    }
    public static CallStatus fromString(java.lang.String value)
          throws java.lang.IllegalStateException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
}
