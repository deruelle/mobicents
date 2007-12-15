/**
 * ThirdPartyCallImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.csapi.www.schema.parlayx.common.v2_0;

public interface ThirdPartyCallImpl extends java.rmi.Remote {
    public java.lang.String makeCall(org.jboss.axis.types.URI callingParty, org.jboss.axis.types.URI calledParty, org.csapi.www.schema.parlayx.common.v2_0.ChargingInformation charging) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException;
    public void cancelCallRequest(java.lang.String guid) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException;
    public org.csapi.www.schema.parlayx.common.v2_0.CallInformation getCallInformation(java.lang.String guid) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException;
    public void endCall(java.lang.String guid) throws java.rmi.RemoteException, org.csapi.www.schema.parlayx.common.v2_0.PolicyException, org.csapi.www.schema.parlayx.common.v2_0.ServiceException;
}
