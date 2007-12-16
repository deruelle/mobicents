package org.mobicents.slee.resource.parlay.session;

import java.applet.Applet;
import java.util.Properties;

import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Request;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.OutputStream;

/**
 *
 * Class Description for ORBStub
 */
public class ORBStub extends ORB {

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#poll_next_response()
     */
    public boolean poll_next_response() {
        //  Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#list_initial_services()
     */
    public String[] list_initial_services() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_any()
     */
    public Any create_any() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#get_default_context()
     */
    public Context get_default_context() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_context_list()
     */
    public ContextList create_context_list() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_environment()
     */
    public Environment create_environment() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_exception_list()
     */
    public ExceptionList create_exception_list() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_list(int)
     */
    public NVList create_list(int count) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#get_next_response()
     */
    public Request get_next_response() throws WrongTransaction {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#send_multiple_requests_deferred(org.omg.CORBA.Request[])
     */
    public void send_multiple_requests_deferred(Request[] req) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#send_multiple_requests_oneway(org.omg.CORBA.Request[])
     */
    public void send_multiple_requests_oneway(Request[] req) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_string_tc(int)
     */
    public TypeCode create_string_tc(int bound) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_wstring_tc(int)
     */
    public TypeCode create_wstring_tc(int bound) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_recursive_sequence_tc(int, int)
     */
    public TypeCode create_recursive_sequence_tc(int bound, int offset) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_output_stream()
     */
    public OutputStream create_output_stream() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#object_to_string(org.omg.CORBA.Object)
     */
    public String object_to_string(Object obj) {
        //  Auto-generated method stub
        return "";
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#set_parameters(java.applet.Applet, java.util.Properties)
     */
    protected void set_parameters(Applet app, Properties props) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#set_parameters(java.lang.String[], java.util.Properties)
     */
    protected void set_parameters(String[] args, Properties props) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#resolve_initial_references(java.lang.String)
     */
    public Object resolve_initial_references(String object_name)
            throws InvalidName {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#string_to_object(java.lang.String)
     */
    public Object string_to_object(String str) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#get_primitive_tc(org.omg.CORBA.TCKind)
     */
    public TypeCode get_primitive_tc(TCKind tcKind) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_array_tc(int, org.omg.CORBA.TypeCode)
     */
    public TypeCode create_array_tc(int length, TypeCode element_type) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_sequence_tc(int, org.omg.CORBA.TypeCode)
     */
    public TypeCode create_sequence_tc(int bound, TypeCode element_type) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_named_value(java.lang.String, org.omg.CORBA.Any, int)
     */
    public NamedValue create_named_value(String s, Any any, int flags) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_interface_tc(java.lang.String, java.lang.String)
     */
    public TypeCode create_interface_tc(String id, String name) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_enum_tc(java.lang.String, java.lang.String, java.lang.String[])
     */
    public TypeCode create_enum_tc(String id, String name, String[] members) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_exception_tc(java.lang.String, java.lang.String, org.omg.CORBA.StructMember[])
     */
    public TypeCode create_exception_tc(String id, String name,
            StructMember[] members) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_struct_tc(java.lang.String, java.lang.String, org.omg.CORBA.StructMember[])
     */
    public TypeCode create_struct_tc(String id, String name,
            StructMember[] members) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_alias_tc(java.lang.String, java.lang.String, org.omg.CORBA.TypeCode)
     */
    public TypeCode create_alias_tc(String id, String name,
            TypeCode original_type) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.omg.CORBA.ORB#create_union_tc(java.lang.String, java.lang.String, org.omg.CORBA.TypeCode, org.omg.CORBA.UnionMember[])
     */
    public TypeCode create_union_tc(String id, String name,
            TypeCode discriminator_type, UnionMember[] members) {
        //  Auto-generated method stub
        return null;
    }

}
