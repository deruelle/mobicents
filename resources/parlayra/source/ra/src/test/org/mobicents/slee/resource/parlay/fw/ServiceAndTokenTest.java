package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

import org.csapi.IpInterface;
import org.csapi.IpService;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.TpCommonExceptions;
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
 * Class Description for ServiceAndTokenTest
 */
public class ServiceAndTokenTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        ipService = new IpService() {
            
            public void setCallback(IpInterface arg0)
                    throws TpCommonExceptions, P_INVALID_INTERFACE_TYPE {

            }

            public void setCallbackWithSessionID(IpInterface arg0, int arg1)
                    throws TpCommonExceptions, P_INVALID_SESSION_ID,
                    P_INVALID_INTERFACE_TYPE {

            }

            public void _release() {

            }

            public boolean _non_existent() {
                return false;
            }

            public int _hash(int maximum) {
                return 0;
            }

            public boolean _is_a(String repositoryIdentifier) {
                return false;
            }

            public DomainManager[] _get_domain_managers() {
                return null;
            }

            public Object _duplicate() {
                return null;
            }

            public Object _get_interface_def() {
                return null;
            }

            public boolean _is_equivalent(Object other) {
                return false;
            }

            public Policy _get_policy(int policy_type) {
                return null;
            }

            public Request _request(String operation) {
                return null;
            }

            public Object _set_policy_override(Policy[] policies,
                    SetOverrideType set_add) {
                return null;
            }

            public Request _create_request(Context ctx, String operation,
                    NVList arg_list, NamedValue result) {
                return null;
            }

            public Request _create_request(Context ctx, String operation,
                    NVList arg_list, NamedValue result, ExceptionList exclist,
                    ContextList ctxlist) {
                return null;
            }
        };
        
        serviceToken = "TOKEN";
        
        serviceAndToken = new ServiceAndToken(ipService, serviceToken);
         
    }
    
    IpService ipService;
    
    String serviceToken;
    
    ServiceAndToken serviceAndToken;

    public void testGetIpService() {
        
        assertEquals(ipService, serviceAndToken.getIpService());
    }

    public void testGetServiceToken() {
        
        assertEquals(serviceToken, serviceAndToken.getServiceToken());
    }
    
    public void testEquals() {
        assertEquals(true, serviceAndToken.equals(new ServiceAndToken(ipService, serviceToken)));
        
    }
    
    public void testToString() {
        assertTrue(serviceAndToken.toString() != null);
        
    }

}
