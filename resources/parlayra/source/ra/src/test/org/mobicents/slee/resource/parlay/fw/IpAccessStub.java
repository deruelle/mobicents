
package org.mobicents.slee.resource.parlay.fw;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.csapi.IpInterface;
import org.csapi.P_INVALID_INTERFACE_NAME;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.TpCommonExceptions;
import org.csapi.fw.P_ACCESS_DENIED;
import org.csapi.fw.P_INVALID_PROPERTY;
import org.csapi.fw.P_INVALID_SIGNATURE;
import org.csapi.fw.P_NO_ACCEPTABLE_SIGNING_ALGORITHM;
import org.csapi.fw.TpProperty;
import org.csapi.fw.fw_access.trust_and_security.IpAccess;
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
public class IpAccessStub implements IpAccess {
    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory.getLog(IpAccessStub.class);

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_access.trust_and_security.IpAccessOperations#obtainInterface(java.lang.String)
     */
    public IpInterface obtainInterface(String interfaceName) throws TpCommonExceptions, P_ACCESS_DENIED, P_INVALID_INTERFACE_NAME {
        
            logger.debug("obtainInterface(String) - start");

        
            logger.debug("obtainInterface(String) - end");
        
        return new IpServiceDiscoveryStub();
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_access.trust_and_security.IpAccessOperations#obtainInterfaceWithCallback(java.lang.String, org.csapi.IpInterface)
     */
    public IpInterface obtainInterfaceWithCallback(String interfaceName, IpInterface clientInterface) throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, P_ACCESS_DENIED, P_INVALID_INTERFACE_NAME {
        
        logger.debug("obtainInterfaceWithCallback(String, IpInterface) - start");

        
            logger.debug("obtainInterfaceWithCallback(String, IpInterface) - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_access.trust_and_security.IpAccessOperations#endAccess(org.csapi.fw.TpProperty[])
     */
    public void endAccess(TpProperty[] endAccessProperties) throws TpCommonExceptions, P_ACCESS_DENIED, P_INVALID_PROPERTY {
        
            logger.debug("endAccess(TpProperty[]) - start");

            logger.debug("endAccess(TpProperty[]) - end");
        
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_access.trust_and_security.IpAccessOperations#listInterfaces()
     */
    public String[] listInterfaces() throws TpCommonExceptions, P_ACCESS_DENIED {
        
            logger.debug("listInterfaces() - start");

            logger.debug("listInterfaces() - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_access.trust_and_security.IpAccessOperations#releaseInterface(java.lang.String)
     */
    public void releaseInterface(String interfaceName) throws TpCommonExceptions, P_ACCESS_DENIED, P_INVALID_INTERFACE_NAME {
        
            logger.debug("releaseInterface(String) - start");

            logger.debug("releaseInterface(String) - end");
        
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_access.trust_and_security.IpAccessOperations#selectSigningAlgorithm(java.lang.String)
     */
    public String selectSigningAlgorithm(String signingAlgorithmCaps) throws TpCommonExceptions, P_ACCESS_DENIED, P_NO_ACCEPTABLE_SIGNING_ALGORITHM {
        
            logger.debug("selectSigningAlgorithm(String) - start");
        

        
            logger.debug("selectSigningAlgorithm(String) - end");
        
        return "NULL";
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_access.trust_and_security.IpAccessOperations#terminateAccess(java.lang.String, byte[])
     */
    public void terminateAccess(String terminationText, byte[] digitalSignature) throws TpCommonExceptions, P_INVALID_SIGNATURE {
        
            logger.debug("terminateAccess(String, byte[]) - start");

            logger.debug("terminateAccess(String, byte[]) - end");
        
    }

    /* (non-Javadoc)
     * @see org.csapi.fw.fw_access.trust_and_security.IpAccessOperations#relinquishInterface(java.lang.String, java.lang.String, byte[])
     */
    public void relinquishInterface(String interfaceName, String terminationText, byte[] digitalSignature) throws TpCommonExceptions, P_INVALID_SIGNATURE, P_INVALID_INTERFACE_NAME {
        
            logger.debug("relinquishInterface(String, String, byte[]) - start");

            logger.debug("relinquishInterface(String, String, byte[]) - end");
        
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_release()
     */
    public void _release() {
        
            logger.debug("_release() - start");

            logger.debug("_release() - end");
        
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_non_existent()
     */
    public boolean _non_existent() {
        
            logger.debug("_non_existent() - start");
        
            logger.debug("_non_existent() - end");
        
        return false;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_hash(int)
     */
    public int _hash(int maximum) {
        
            logger.debug("_hash(int) - start");

            logger.debug("_hash(int) - end");
        
        return 0;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_is_a(java.lang.String)
     */
    public boolean _is_a(String repositoryIdentifier) {
        
            logger.debug("_is_a(String) - start");

            logger.debug("_is_a(String) - end");
        
        return false;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_get_domain_managers()
     */
    public DomainManager[] _get_domain_managers() {
        
            logger.debug("_get_domain_managers() - start");

            logger.debug("_get_domain_managers() - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_duplicate()
     */
    public Object _duplicate() {
        
            logger.debug("_duplicate() - start");

            logger.debug("_duplicate() - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_get_interface_def()
     */
    public Object _get_interface_def() {
        
            logger.debug("_get_interface_def() - start");

            logger.debug("_get_interface_def() - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
     */
    public boolean _is_equivalent(Object other) {
        
            logger.debug("_is_equivalent(Object) - start");
        
            logger.debug("_is_equivalent(Object) - end");
        
        return false;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_get_policy(int)
     */
    public Policy _get_policy(int policy_type) {
        
            logger.debug("_get_policy(int) - start");
        

            logger.debug("_get_policy(int) - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_request(java.lang.String)
     */
    public Request _request(String operation) {
        
            logger.debug("_request(String) - start");

            logger.debug("_request(String) - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[], org.omg.CORBA.SetOverrideType)
     */
    public Object _set_policy_override(Policy[] policies, SetOverrideType set_add) {
        
        logger.debug("_set_policy_override(Policy[], SetOverrideType) - start");

            logger.debug("_set_policy_override(Policy[], SetOverrideType) - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
     */
    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result) {
        
        logger.debug("_create_request(Context, String, NVList, NamedValue) - start");

            logger.debug("_create_request(Context, String, NVList, NamedValue) - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
     */
    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result, ExceptionList exclist, ContextList ctxlist) {
        
        logger.debug("_create_request(Context, String, NVList, NamedValue, ExceptionList, ContextList) - start");

        
        logger.debug("_create_request(Context, String, NVList, NamedValue, ExceptionList, ContextList) - end");
        
        return null;
    }

}
