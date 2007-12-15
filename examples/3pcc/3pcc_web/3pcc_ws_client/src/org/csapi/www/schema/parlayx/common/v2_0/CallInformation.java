/**
 * CallInformation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.csapi.www.schema.parlayx.common.v2_0;

public class CallInformation  implements java.io.Serializable {
    private org.csapi.www.schema.parlayx.common.v2_0.CallStatus callStatus;
    private java.util.Calendar startTime;
    private int duration;
    private org.csapi.www.schema.parlayx.common.v2_0.CallTerminationCause terminationCause;

    public CallInformation() {
    }

    public org.csapi.www.schema.parlayx.common.v2_0.CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(org.csapi.www.schema.parlayx.common.v2_0.CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public java.util.Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(java.util.Calendar startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public org.csapi.www.schema.parlayx.common.v2_0.CallTerminationCause getTerminationCause() {
        return terminationCause;
    }

    public void setTerminationCause(org.csapi.www.schema.parlayx.common.v2_0.CallTerminationCause terminationCause) {
        this.terminationCause = terminationCause;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CallInformation)) return false;
        CallInformation other = (CallInformation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.callStatus==null && other.getCallStatus()==null) || 
             (this.callStatus!=null &&
              this.callStatus.equals(other.getCallStatus()))) &&
            ((this.startTime==null && other.getStartTime()==null) || 
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime()))) &&
            this.duration == other.getDuration() &&
            ((this.terminationCause==null && other.getTerminationCause()==null) || 
             (this.terminationCause!=null &&
              this.terminationCause.equals(other.getTerminationCause())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCallStatus() != null) {
            _hashCode += getCallStatus().hashCode();
        }
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        _hashCode += getDuration();
        if (getTerminationCause() != null) {
            _hashCode += getTerminationCause().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.jboss.axis.description.TypeDesc typeDesc =
        new org.jboss.axis.description.TypeDesc(CallInformation.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "CallInformation"));
        org.jboss.axis.description.ElementDesc elemField = new org.jboss.axis.description.ElementDesc();
        elemField.setFieldName("callStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "callStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "CallStatus"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.jboss.axis.description.ElementDesc();
        elemField.setFieldName("startTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.jboss.axis.description.ElementDesc();
        elemField.setFieldName("duration");
        elemField.setXmlName(new javax.xml.namespace.QName("", "duration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.jboss.axis.description.ElementDesc();
        elemField.setFieldName("terminationCause");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminationCause"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "CallTerminationCause"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.jboss.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.jboss.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.jboss.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.jboss.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.jboss.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
