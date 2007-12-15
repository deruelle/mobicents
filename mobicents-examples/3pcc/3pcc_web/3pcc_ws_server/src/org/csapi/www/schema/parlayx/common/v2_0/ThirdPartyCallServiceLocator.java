/**
 * ThirdPartyCallServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.csapi.www.schema.parlayx.common.v2_0;

public class ThirdPartyCallServiceLocator extends org.apache.axis.client.Service implements org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCallService {

    // Use to get a proxy class for ThirdPartyCall
    private final java.lang.String ThirdPartyCall_address = "http://localhost:8080/jboss-net/services/ThirdPartyCall";

    public java.lang.String getThirdPartyCallAddress() {
        return ThirdPartyCall_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ThirdPartyCallWSDDServiceName = "ThirdPartyCall";

    public java.lang.String getThirdPartyCallWSDDServiceName() {
        return ThirdPartyCallWSDDServiceName;
    }

    public void setThirdPartyCallWSDDServiceName(java.lang.String name) {
        ThirdPartyCallWSDDServiceName = name;
    }

    public org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCall getThirdPartyCall() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ThirdPartyCall_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getThirdPartyCall(endpoint);
    }

    public org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCall getThirdPartyCall(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCallBindingStub _stub = new org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCallBindingStub(portAddress, this);
            _stub.setPortName(getThirdPartyCallWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCall.class.isAssignableFrom(serviceEndpointInterface)) {
                org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCallBindingStub _stub = new org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCallBindingStub(new java.net.URL(ThirdPartyCall_address), this);
                _stub.setPortName(getThirdPartyCallWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("ThirdPartyCall".equals(inputPortName)) {
            return getThirdPartyCall();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.csapi.org/wsdl/parlayx/third_party_call/v2_0/service", "ThirdPartyCallService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("ThirdPartyCall"));
        }
        return ports.iterator();
    }

}
