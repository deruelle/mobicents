/**
 * ThirdPartyCallBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.csapi.www.schema.parlayx.common.v2_0;

public class ThirdPartyCallBindingStub extends org.apache.axis.client.Stub implements org.csapi.www.schema.parlayx.common.v2_0.ThirdPartyCall {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[4];
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("makeCall");
        oper.addParameter(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "callingParty"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"), org.apache.axis.types.URI.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "calledParty"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"), org.apache.axis.types.URI.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "charging"), new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ChargingInformation"), org.csapi.www.schema.parlayx.common.v2_0.ChargingInformation.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "result"));
        oper.setStyle(org.apache.axis.enum.Style.WRAPPED);
        oper.setUse(org.apache.axis.enum.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "PolicyException"),
                      "org.csapi.www.schema.parlayx.common.v2_0.PolicyException",
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "PolicyException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException"),
                      "org.csapi.www.schema.parlayx.common.v2_0.ServiceException",
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCallInformation");
        oper.addParameter(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "callIdentifier"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0", "CallInformation"));
        oper.setReturnClass(org.csapi.www.schema.parlayx.common.v2_0.CallInformation.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "result"));
        oper.setStyle(org.apache.axis.enum.Style.WRAPPED);
        oper.setUse(org.apache.axis.enum.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "PolicyException"),
                      "org.csapi.www.schema.parlayx.common.v2_0.PolicyException",
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "PolicyException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException"),
                      "org.csapi.www.schema.parlayx.common.v2_0.ServiceException",
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("endCall");
        oper.addParameter(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "callIdentifier"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.enum.Style.WRAPPED);
        oper.setUse(org.apache.axis.enum.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "PolicyException"),
                      "org.csapi.www.schema.parlayx.common.v2_0.PolicyException",
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "PolicyException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException"),
                      "org.csapi.www.schema.parlayx.common.v2_0.ServiceException",
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("cancelCallRequest");
        oper.addParameter(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "callIdentifier"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.enum.Style.WRAPPED);
        oper.setUse(org.apache.axis.enum.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "PolicyException"),
                      "org.csapi.www.schema.parlayx.common.v2_0.PolicyException",
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "PolicyException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException"),
                      "org.csapi.www.schema.parlayx.common.v2_0.ServiceException",
                      new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException"), 
                      true
                     ));
        _operations[3] = oper;

    }

    public ThirdPartyCallBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ThirdPartyCallBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ThirdPartyCallBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0", "CallTerminationCause");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.CallTerminationCause.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "ServiceException");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.ServiceException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0", "CallStatus");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.CallStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0", "CallInformation");
            cachedSerQNames.add(qName);
            cls = org.csapi.www.schema.parlayx.common.v2_0.CallInformation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_0", "PolicyException");
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

    }

    private org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call =
                    (org.apache.axis.client.Call) super.service.createCall();
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
                    _call.setEncodingStyle(null);
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
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", t);
        }
    }

    public java.lang.String makeCall(org.apache.axis.types.URI callingParty, org.apache.axis.types.URI calledParty, org.csapi.www.schema.parlayx.common.v2_0.ChargingInformation charging) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "makeCall"));

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
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public org.csapi.www.schema.parlayx.common.v2_0.CallInformation getCallInformation(java.lang.String callIdentifier) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "getCallInformation"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {callIdentifier});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.csapi.www.schema.parlayx.common.v2_0.CallInformation) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.csapi.www.schema.parlayx.common.v2_0.CallInformation) org.apache.axis.utils.JavaUtils.convert(_resp, org.csapi.www.schema.parlayx.common.v2_0.CallInformation.class);
            }
        }
    }

    public void endCall(java.lang.String callIdentifier) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "endCall"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {callIdentifier});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void cancelCallRequest(java.lang.String callIdentifier) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/third_party_call/v2_0/local", "cancelCallRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {callIdentifier});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

}
