/**
 * ThirdPartyCallSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.csapi.www.schema.parlayx.common.v2_0;

public class ThirdPartyCallSoapBindingStub extends org.jboss.axis.client.Stub implements org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCallImpl {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.jboss.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.jboss.axis.description.OperationDesc[4];
        org.jboss.axis.description.OperationDesc oper;
        oper = new org.jboss.axis.description.OperationDesc();
        oper.setName("makeCall");
        oper.addParameter(new javax.xml.namespace.QName("", "callingParty"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"), org.jboss.axis.types.URI.class, org.jboss.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "calledParty"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"), org.jboss.axis.types.URI.class, org.jboss.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "charging"), new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ChargingInformation"), org.csapi.www.schema.parlayx.common.v2_0.ChargingInformation.class, org.jboss.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "makeCallResponse"));
        oper.setStyle(org.jboss.axis.enums.Style.RPC);
        oper.setUse(org.jboss.axis.enums.Use.ENCODED);
        oper.addFault(new org.jboss.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://localhost:8080/jboss-net/services/ThirdPartyCall", "fault"),
                      "org.csapi.www.schema.parlayx.common.v2_0.PolicyException",
                      new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "PolicyException"), 
                      true
                     ));
        oper.addFault(new org.jboss.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://localhost:8080/jboss-net/services/ThirdPartyCall", "fault"),
                      "org.csapi.www.schema.parlayx.common.v2_0.ServiceException",
                      new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "ServiceException"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.jboss.axis.description.OperationDesc();
        oper.setName("cancelCallRequest");
        oper.addParameter(new javax.xml.namespace.QName("", "guid"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.jboss.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.jboss.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.jboss.axis.enums.Style.RPC);
        oper.setUse(org.jboss.axis.enums.Use.ENCODED);
        oper.addFault(new org.jboss.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://localhost:8080/jboss-net/services/ThirdPartyCall", "fault"),
                      "org.csapi.www.schema.parlayx.common.v2_0.PolicyException",
                      new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "PolicyException"), 
                      true
                     ));
        oper.addFault(new org.jboss.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://localhost:8080/jboss-net/services/ThirdPartyCall", "fault"),
                      "org.csapi.www.schema.parlayx.common.v2_0.ServiceException",
                      new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "ServiceException"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.jboss.axis.description.OperationDesc();
        oper.setName("getCallInformation");
        oper.addParameter(new javax.xml.namespace.QName("", "guid"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.jboss.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "CallInformation"));
        oper.setReturnClass(org.csapi.www.schema.parlayx.common.v2_0.CallInformation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getCallInformationResponse"));
        oper.setStyle(org.jboss.axis.enums.Style.RPC);
        oper.setUse(org.jboss.axis.enums.Use.ENCODED);
        oper.addFault(new org.jboss.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://localhost:8080/jboss-net/services/ThirdPartyCall", "fault"),
                      "org.csapi.www.schema.parlayx.common.v2_0.PolicyException",
                      new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "PolicyException"), 
                      true
                     ));
        oper.addFault(new org.jboss.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://localhost:8080/jboss-net/services/ThirdPartyCall", "fault"),
                      "org.csapi.www.schema.parlayx.common.v2_0.ServiceException",
                      new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "ServiceException"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.jboss.axis.description.OperationDesc();
        oper.setName("endCall");
        oper.addParameter(new javax.xml.namespace.QName("", "guid"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.jboss.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.jboss.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.jboss.axis.enums.Style.RPC);
        oper.setUse(org.jboss.axis.enums.Use.ENCODED);
        oper.addFault(new org.jboss.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://localhost:8080/jboss-net/services/ThirdPartyCall", "fault"),
                      "org.csapi.www.schema.parlayx.common.v2_0.PolicyException",
                      new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "PolicyException"), 
                      true
                     ));
        oper.addFault(new org.jboss.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://localhost:8080/jboss-net/services/ThirdPartyCall", "fault"),
                      "org.csapi.www.schema.parlayx.common.v2_0.ServiceException",
                      new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "ServiceException"), 
                      true
                     ));
        _operations[3] = oper;

    }

    public ThirdPartyCallSoapBindingStub() throws org.jboss.axis.AxisFault {
         this(null);
    }

    public ThirdPartyCallSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.jboss.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ThirdPartyCallSoapBindingStub(javax.xml.rpc.Service service) throws org.jboss.axis.AxisFault {
        if (service == null) {
            super.service = new org.jboss.axis.client.Service();
        } else {
            super.service = service;
        }
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            java.lang.Class beansf = org.jboss.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.jboss.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.jboss.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.jboss.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.jboss.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.jboss.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.jboss.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.jboss.axis.encoding.ser.SimpleDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "CallStatus");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.CallStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "PolicyException");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.PolicyException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ChargingInformation");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.ChargingInformation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://v2_0.common.parlayx.schema.www.csapi.org", "ServiceException");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.ServiceException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "CallTerminationCause");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.CallTerminationCause.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "CallInformation");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.CallInformation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    private org.jboss.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.jboss.axis.client.Call _call =
                    (org.jboss.axis.client.Call) super.service.createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.jboss.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.jboss.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                        java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                        _call.registerTypeMapping(cls, qName, sf, df, false);
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable t) {
            throw new org.jboss.axis.AxisFault("Failure trying to get the Call object", t);
        }
    }

    public java.lang.String makeCall(org.jboss.axis.types.URI callingParty, org.jboss.axis.types.URI calledParty, org.csapi.www.schema.parlayx.common.v2_0.ChargingInformation charging) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException {
        if (super.cachedEndpoint == null) {
            throw new org.jboss.axis.NoEndPointException();
        }
        org.jboss.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.jboss.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://impl.server.callcontrol.sip.jayway.se", "makeCall"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {callingParty, calledParty, charging});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.jboss.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public void cancelCallRequest(java.lang.String guid) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException {
        if (super.cachedEndpoint == null) {
            throw new org.jboss.axis.NoEndPointException();
        }
        org.jboss.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.jboss.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://impl.server.callcontrol.sip.jayway.se", "cancelCallRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {guid});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public org.csapi.www.schema.parlayx.common.v2_0.CallInformation getCallInformation(java.lang.String guid) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException {
        if (super.cachedEndpoint == null) {
            throw new org.jboss.axis.NoEndPointException();
        }
        org.jboss.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.jboss.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://impl.server.callcontrol.sip.jayway.se", "getCallInformation"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {guid});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.csapi.www.schema.parlayx.common.v2_0.CallInformation) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.csapi.www.schema.parlayx.common.v2_0.CallInformation) org.jboss.axis.utils.JavaUtils.convert(_resp, org.csapi.www.schema.parlayx.common.v2_0.CallInformation.class);
            }
        }
    }

    public void endCall(java.lang.String guid) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException {
        if (super.cachedEndpoint == null) {
            throw new org.jboss.axis.NoEndPointException();
        }
        org.jboss.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.jboss.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://impl.server.callcontrol.sip.jayway.se", "endCall"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {guid});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

}
