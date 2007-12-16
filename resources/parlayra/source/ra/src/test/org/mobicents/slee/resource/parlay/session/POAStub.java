package org.mobicents.slee.resource.parlay.session;

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
import org.omg.PortableServer.AdapterActivator;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 *
 * Class Description for POAStub
 */
public class POAStub implements POA {

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#id()
     */
    public byte[] id() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#destroy(boolean, boolean)
     */
    public void destroy(boolean arg0, boolean arg1) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#deactivate_object(byte[])
     */
    public void deactivate_object(byte[] arg0) throws ObjectNotActive,
            WrongPolicy {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#the_name()
     */
    public String the_name() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#reference_to_id(org.omg.CORBA.Object)
     */
    public byte[] reference_to_id(Object arg0) throws WrongAdapter, WrongPolicy {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#id_to_reference(byte[])
     */
    public Object id_to_reference(byte[] arg0) throws ObjectNotActive,
            WrongPolicy {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#the_activator()
     */
    public AdapterActivator the_activator() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#the_activator(org.omg.PortableServer.AdapterActivator)
     */
    public void the_activator(AdapterActivator arg0) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#the_parent()
     */
    public POA the_parent() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#the_children()
     */
    public POA[] the_children() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#the_POAManager()
     */
    public POAManager the_POAManager() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#get_servant()
     */
    public Servant get_servant() throws NoServant, WrongPolicy {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#set_servant(org.omg.PortableServer.Servant)
     */
    public void set_servant(Servant arg0) throws WrongPolicy {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#activate_object(org.omg.PortableServer.Servant)
     */
    public byte[] activate_object(Servant arg0) throws ServantAlreadyActive,
            WrongPolicy {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#servant_to_id(org.omg.PortableServer.Servant)
     */
    public byte[] servant_to_id(Servant arg0) throws ServantNotActive,
            WrongPolicy {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#id_to_servant(byte[])
     */
    public Servant id_to_servant(byte[] arg0) throws ObjectNotActive,
            WrongPolicy {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#activate_object_with_id(byte[], org.omg.PortableServer.Servant)
     */
    public void activate_object_with_id(byte[] arg0, Servant arg1)
            throws ServantAlreadyActive, ObjectAlreadyActive, WrongPolicy {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#get_servant_manager()
     */
    public ServantManager get_servant_manager() throws WrongPolicy {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#set_servant_manager(org.omg.PortableServer.ServantManager)
     */
    public void set_servant_manager(ServantManager arg0) throws WrongPolicy {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_reference(java.lang.String)
     */
    public Object create_reference(String arg0) throws WrongPolicy {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_reference_with_id(byte[], java.lang.String)
     */
    public Object create_reference_with_id(byte[] arg0, String arg1) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#servant_to_reference(org.omg.PortableServer.Servant)
     */
    public Object servant_to_reference(Servant arg0) throws ServantNotActive,
            WrongPolicy {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue)
     */
    public IdAssignmentPolicy create_id_assignment_policy(
            IdAssignmentPolicyValue arg0) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue)
     */
    public IdUniquenessPolicy create_id_uniqueness_policy(
            IdUniquenessPolicyValue arg0) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue)
     */
    public ImplicitActivationPolicy create_implicit_activation_policy(
            ImplicitActivationPolicyValue arg0) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue)
     */
    public LifespanPolicy create_lifespan_policy(LifespanPolicyValue arg0) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#find_POA(java.lang.String, boolean)
     */
    public POA find_POA(String arg0, boolean arg1) throws AdapterNonExistent {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue)
     */
    public RequestProcessingPolicy create_request_processing_policy(
            RequestProcessingPolicyValue arg0) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#reference_to_servant(org.omg.CORBA.Object)
     */
    public Servant reference_to_servant(Object arg0) throws ObjectNotActive,
            WrongPolicy, WrongAdapter {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue)
     */
    public ServantRetentionPolicy create_servant_retention_policy(
            ServantRetentionPolicyValue arg0) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_thread_policy(org.omg.PortableServer.ThreadPolicyValue)
     */
    public ThreadPolicy create_thread_policy(ThreadPolicyValue arg0) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.PortableServer.POAOperations#create_POA(java.lang.String, org.omg.PortableServer.POAManager, org.omg.CORBA.Policy[])
     */
    public POA create_POA(String arg0, POAManager arg1, Policy[] arg2)
            throws AdapterAlreadyExists, InvalidPolicy {
        //  Auto-generated method stub
        return new POAStub();
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
    public int _hash(int arg0) {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_is_a(java.lang.String)
     */
    public boolean _is_a(String arg0) {
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
    public boolean _is_equivalent(Object arg0) {
        //  Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_get_policy(int)
     */
    public Policy _get_policy(int arg0) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_request(java.lang.String)
     */
    public Request _request(String arg0) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[], org.omg.CORBA.SetOverrideType)
     */
    public Object _set_policy_override(Policy[] arg0, SetOverrideType arg1) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
     */
    public Request _create_request(Context arg0, String arg1, NVList arg2,
            NamedValue arg3) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
     */
    public Request _create_request(Context arg0, String arg1, NVList arg2,
            NamedValue arg3, ExceptionList arg4, ContextList arg5) {
        //  Auto-generated method stub
        return null;
    }

}
