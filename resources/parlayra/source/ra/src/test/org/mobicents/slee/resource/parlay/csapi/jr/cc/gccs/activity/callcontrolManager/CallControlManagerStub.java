
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolManager;

import javax.slee.resource.ResourceException;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddressRange;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallLoadControlMechanism;
import org.csapi.cc.gccs.IpAppCall;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallTreatment;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager;

/**
 *
 **/
public class CallControlManagerStub implements CallControlManager {

    private Call call;

    /**
     * @param call always return this call activity when asked
     */
    public CallControlManagerStub(Call call) {
        this.call = call;
    }

    public CallControlManagerStub() {
        
    }
    
    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#getCall(int)
     */
    public Call getCall(int callSessionID) {         
        return call;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#removeCall(int)
     */
    public Call removeCall(int callSessionID) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#addCall(int, org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call)
     */
    public void addCall(int callSessionID, Call call) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#getIpAppCall()
     */
    public IpAppCall getIpAppCall() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#callAborted(int)
     */
    public void callAborted(int callReference) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#callEventNotify(org.csapi.cc.gccs.TpCallIdentifier, org.csapi.cc.gccs.TpCallEventInfo, int)
     */
    public void callEventNotify(org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier callReference, TpCallEventInfo eventInfo, int assignmentID) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#callNotificationInterrupted()
     */
    public void callNotificationInterrupted() {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#callNotificationContinued()
     */
    public void callNotificationContinued() {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#callOverloadEncountered(int)
     */
    public void callOverloadEncountered(int assignmentID) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#callOverloadCeased(int)
     */
    public void callOverloadCeased(int assignmentID) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#getTpServiceIdentifier()
     */
    public TpServiceIdentifier getTpServiceIdentifier() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.session.ServiceSession#getType()
     */
    public int getType() {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.session.ServiceSession#init()
     */
    public void init() throws ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.session.ServiceSession#destroy()
     */
    public void destroy() {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallControlManagerConnection#getIpCallConnection(org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier)
     */
    public IpCallConnection getIpCallConnection(org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier callIdentifier) throws ResourceException {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallControlManagerConnection#createCall()
     */
    public org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier createCall() throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallControlManagerConnection#enableCallNotification(org.csapi.cc.gccs.TpCallEventCriteria)
     */
    public int enableCallNotification(TpCallEventCriteria eventCriteria) throws TpCommonExceptions, P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE, ResourceException {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallControlManagerConnection#disableCallNotification(int)
     */
    public void disableCallNotification(int assignmentID) throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallControlManagerConnection#setCallLoadControl(int, org.csapi.cc.TpCallLoadControlMechanism, org.csapi.cc.gccs.TpCallTreatment, org.csapi.TpAddressRange)
     */
    public int setCallLoadControl(int duration, TpCallLoadControlMechanism mechanism, TpCallTreatment treatment, TpAddressRange addressRange) throws TpCommonExceptions, P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN, ResourceException {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallControlManagerConnection#changeCallNotification(int, org.csapi.cc.gccs.TpCallEventCriteria)
     */
    public void changeCallNotification(int assignmentID, TpCallEventCriteria eventCriteria) throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID, P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallControlManagerConnection#getCriteria()
     */
    public TpCallEventCriteriaResult[] getCriteria() throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.IpServiceConnection#closeConnection()
     */
    public void closeConnection() throws ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager#createCall(org.csapi.cc.gccs.TpCallIdentifier)
     */
    public Call createCall(TpCallIdentifier callReference) {
        // TODO Auto-generated method stub
        return null;
    }

}
