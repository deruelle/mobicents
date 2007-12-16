package org.mobicents.slee.resource.parlay.fw;

import org.csapi.TpCommonExceptions;
import org.csapi.fw.P_ACCESS_DENIED;
import org.csapi.fw.P_ILLEGAL_SERVICE_TYPE;
import org.csapi.fw.P_INVALID_PROPERTY;
import org.csapi.fw.P_UNKNOWN_SERVICE_TYPE;
import org.csapi.fw.TpService;
import org.csapi.fw.TpServiceDescription;
import org.csapi.fw.TpServiceProperty;
import org.csapi.fw.TpServiceTypeDescription;
import org.csapi.fw.fw_application.discovery.IpServiceDiscovery;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;

/**
 *
 **/
public class IpServiceDiscoveryStub implements IpServiceDiscovery {
    
    TpService service = new TpService("ui123", new TpServiceDescription());
    
    TpService[] services = {service};

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_application.discovery.IpServiceDiscoveryOperations#listServiceTypes()
     */
    public String[] listServiceTypes() throws TpCommonExceptions, P_ACCESS_DENIED {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_application.discovery.IpServiceDiscoveryOperations#describeServiceType(java.lang.String)
     */
    public TpServiceTypeDescription describeServiceType(String name) throws TpCommonExceptions, P_ILLEGAL_SERVICE_TYPE, P_ACCESS_DENIED, P_UNKNOWN_SERVICE_TYPE {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_application.discovery.IpServiceDiscoveryOperations#discoverService(java.lang.String, org.csapi.fw.TpServiceProperty[], int)
     */
    public TpService[] discoverService(String serviceTypeName, TpServiceProperty[] desiredPropertyList, int max) throws TpCommonExceptions, P_ILLEGAL_SERVICE_TYPE, P_ACCESS_DENIED, P_INVALID_PROPERTY, P_UNKNOWN_SERVICE_TYPE {
        return services;
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_application.discovery.IpServiceDiscoveryOperations#listSubscribedServices()
     */
    public TpService[] listSubscribedServices() throws TpCommonExceptions, P_ACCESS_DENIED {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_release()
     */
    public void _release() {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_non_existent()
     */
    public boolean _non_existent() {
        //  Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_hash(int)
     */
    public int _hash(int maximum) {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_is_a(java.lang.String)
     */
    public boolean _is_a(String repositoryIdentifier) {
        //  Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_get_domain_managers()
     */
    public DomainManager[] _get_domain_managers() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_duplicate()
     */
    public Object _duplicate() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_get_interface_def()
     */
    public Object _get_interface_def() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
     */
    public boolean _is_equivalent(Object other) {
        //  Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_get_policy(int)
     */
    public Policy _get_policy(int policy_type) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_request(java.lang.String)
     */
    public Request _request(String operation) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[], org.omg.CORBA.SetOverrideType)
     */
    public Object _set_policy_override(Policy[] policies, SetOverrideType set_add) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
     */
    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
     */
    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result, ExceptionList exclist, ContextList ctxlist) {
        //  Auto-generated method stub
        return null;
    }

}
