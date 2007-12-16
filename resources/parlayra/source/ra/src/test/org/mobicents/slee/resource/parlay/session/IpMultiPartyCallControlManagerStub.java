package org.mobicents.slee.resource.parlay.session;

import org.csapi.IpInterface;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddressRange;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallLoadControlMechanism;
import org.csapi.cc.TpCallNotificationRequest;
import org.csapi.cc.TpCallTreatment;
import org.csapi.cc.TpNotificationRequested;
import org.csapi.cc.TpNotificationRequestedSetEntry;
import org.csapi.cc.mpccs.IpAppMultiPartyCall;
import org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager;
import org.csapi.cc.mpccs.IpMultiPartyCallControlManager;
import org.csapi.cc.mpccs.TpMultiPartyCallIdentifier;
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
 * Class Description for IpMultiPartyCallControlManagerStub
 */
public class IpMultiPartyCallControlManagerStub implements
        IpMultiPartyCallControlManager {

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#createCall(org.csapi.cc.mpccs.IpAppMultiPartyCall)
     */
    public TpMultiPartyCallIdentifier createCall(IpAppMultiPartyCall arg0)
            throws TpCommonExceptions, P_INVALID_INTERFACE_TYPE {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#createNotification(org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager, org.csapi.cc.TpCallNotificationRequest)
     */
    public int createNotification(IpAppMultiPartyCallControlManager arg0,
            TpCallNotificationRequest arg1) throws TpCommonExceptions,
            P_INVALID_CRITERIA, P_INVALID_INTERFACE_TYPE, P_INVALID_EVENT_TYPE {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#destroyNotification(int)
     */
    public void destroyNotification(int arg0) throws TpCommonExceptions,
            P_INVALID_ASSIGNMENT_ID {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#changeNotification(int, org.csapi.cc.TpCallNotificationRequest)
     */
    public void changeNotification(int arg0, TpCallNotificationRequest arg1)
            throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID,
            P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#getNotification()
     */
    public TpNotificationRequested[] getNotification()
            throws TpCommonExceptions {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#setCallLoadControl(int, org.csapi.cc.TpCallLoadControlMechanism, org.csapi.cc.TpCallTreatment, org.csapi.TpAddressRange)
     */
    public int setCallLoadControl(int arg0, TpCallLoadControlMechanism arg1,
            TpCallTreatment arg2, TpAddressRange arg3)
            throws TpCommonExceptions, P_INVALID_ADDRESS,
            P_UNSUPPORTED_ADDRESS_PLAN {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#enableNotifications(org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager)
     */
    public int enableNotifications(IpAppMultiPartyCallControlManager arg0)
            throws TpCommonExceptions {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#disableNotifications()
     */
    public void disableNotifications() throws TpCommonExceptions {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#getNextNotification(boolean)
     */
    public TpNotificationRequestedSetEntry getNextNotification(boolean arg0)
            throws TpCommonExceptions {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
     */
    public void setCallback(IpInterface arg0) throws TpCommonExceptions,
            P_INVALID_INTERFACE_TYPE {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.csapi.IpServiceOperations#setCallbackWithSessionID(org.csapi.IpInterface, int)
     */
    public void setCallbackWithSessionID(IpInterface arg0, int arg1)
            throws TpCommonExceptions, P_INVALID_SESSION_ID,
            P_INVALID_INTERFACE_TYPE {
        //  Auto-generated method stub

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
    public Object _set_policy_override(Policy[] policies,
            SetOverrideType set_add) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
     */
    public Request _create_request(Context ctx, String operation,
            NVList arg_list, NamedValue result) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
     */
    public Request _create_request(Context ctx, String operation,
            NVList arg_list, NamedValue result, ExceptionList exclist,
            ContextList ctxlist) {
        //  Auto-generated method stub
        return null;
    }

}
