/**
 * ServiceException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.csapi.www.schema.parlayx.common.v2_0;

public class ServiceException  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private java.lang.String messageId;
    private java.lang.String text;
    private java.lang.String[] variables;

    public ServiceException() {
    }

    public ServiceException(
           java.lang.String messageId,
           java.lang.String text,
           java.lang.String[] variables) {
        this.messageId = messageId;
        this.text = text;
        this.variables = variables;
    }

    public java.lang.String getMessageId() {
        return messageId;
    }

    public void setMessageId(java.lang.String messageId) {
        this.messageId = messageId;
    }

    public java.lang.String getText() {
        return text;
    }

    public void setText(java.lang.String text) {
        this.text = text;
    }

    public java.lang.String[] getVariables() {
        return variables;
    }

    public void setVariables(java.lang.String[] variables) {
        this.variables = variables;
    }

    public java.lang.String getVariables(int i) {
        return variables[i];
    }

    public void setVariables(int i, java.lang.String value) {
        this.variables[i] = value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServiceException)) return false;
        ServiceException other = (ServiceException) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.messageId==null && other.getMessageId()==null) || 
             (this.messageId!=null &&
              this.messageId.equals(other.getMessageId()))) &&
            ((this.text==null && other.getText()==null) || 
             (this.text!=null &&
              this.text.equals(other.getText()))) &&
            ((this.variables==null && other.getVariables()==null) || 
             (this.variables!=null &&
              java.util.Arrays.equals(this.variables, other.getVariables())));
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
        if (getMessageId() != null) {
            _hashCode += getMessageId().hashCode();
        }
        if (getText() != null) {
            _hashCode += getText().hashCode();
        }
        if (getVariables() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVariables());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVariables(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ServiceException.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messageId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "messageId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("text");
        elemField.setXmlName(new javax.xml.namespace.QName("", "text"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("variables");
        elemField.setXmlName(new javax.xml.namespace.QName("", "variables"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }


    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, this);
    }
}
