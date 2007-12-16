package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager;

import javax.slee.resource.ResourceException;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddressRange;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallLoadControlMechanism;
import org.csapi.cc.TpCallNotificationInfo;
import org.csapi.cc.TpCallNotificationRequest;
import org.csapi.cc.TpCallTreatment;
import org.csapi.cc.TpNotificationRequested;
import org.csapi.cc.TpNotificationRequestedSetEntry;
import org.csapi.cc.mpccs.IpAppMultiPartyCall;
import org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager;
import org.csapi.cc.mpccs.IpMultiPartyCallControlManager;
import org.csapi.cc.mpccs.TpCallLegIdentifier;
import org.csapi.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.IpAppMultiPartyCallControlManagerImpl;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.IpAppMultiPartyCallImpl;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLegStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCallStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;
import org.omg.PortableServer.POA;

/**
 *
 * Class Description for MultiPartyCallControlManagerStub
 */
public class MultiPartyCallControlManagerStub implements
        MultiPartyCallControlManager {
    
    private MultiPartyCall multiPartyCall;

    /**
     * @param multiPartyCall a dummy call activity that will get returned
     */
    public MultiPartyCallControlManagerStub(MultiPartyCall multiPartyCall){
        this.multiPartyCall = multiPartyCall;
    }
    
    public MultiPartyCallControlManagerStub(){
        // set nothing 
    }
    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#getIpAppMultiPartyCall()
     */
    public IpAppMultiPartyCall getIpAppMultiPartyCall() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#getIpAppMultiPartyCallControlManager()
     */
    public IpAppMultiPartyCallControlManager getIpAppMultiPartyCallControlManager() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#getIpAppMultiPartyCallControlManagerImpl()
     */
    public IpAppMultiPartyCallControlManagerImpl getIpAppMultiPartyCallControlManagerImpl() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#getIpAppMultiPartyCallImpl()
     */
    public IpAppMultiPartyCallImpl getIpAppMultiPartyCallImpl() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#getIpMultiPartyCallControlManager()
     */
    public IpMultiPartyCallControlManager getIpMultiPartyCallControlManager() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#getIpAppCallLegPOA()
     */
    public POA getIpAppCallLegPOA() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#getMultiPartyCall(int)
     */
    public MultiPartyCall getMultiPartyCall(int callSessionID) {
        // alway return the same call activity 
       return  multiPartyCall;
       
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#removeMultiPartyCall(int)
     */
    public MultiPartyCall removeMultiPartyCall(int callSessionID) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#addMultiPartyCall(int, org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall)
     */
    public void addMultiPartyCall(int callSessionID,
            MultiPartyCall multiPartyCall) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#createCall(org.csapi.cc.mpccs.TpMultiPartyCallIdentifier)
     */
    public MultiPartyCall createCall(TpMultiPartyCallIdentifier callIdentifier) {
        return new MultiPartyCallStub();
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#createCallLeg(org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall, org.csapi.cc.mpccs.TpCallLegIdentifier)
     */
    public CallLeg createCallLeg(MultiPartyCall call,
            TpCallLegIdentifier identifier) {
        return new CallLegStub();
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#callAborted(int)
     */
    public void callAborted(int callID) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#callOverloadCeased(int)
     */
    public void callOverloadCeased(int assignmentID) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#callOverloadEncountered(int)
     */
    public void callOverloadEncountered(int assignmentID) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#managerInterrupted()
     */
    public void managerInterrupted() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#managerResumed()
     */
    public void managerResumed() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager#reportNotification(org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier, org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier[], org.csapi.cc.TpCallNotificationInfo, int)
     */
    public void reportNotification(
            org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier callIdentifier,
            org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier[] callLegIdentifier,
            TpCallNotificationInfo callNotificationInfo, int assignmentID) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.session.ServiceSession#getType()
     */
    public int getType() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.session.ServiceSession#getTpServiceIdentifier()
     */
    public TpServiceIdentifier getTpServiceIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.session.ServiceSession#init()
     */
    public void init() throws ResourceException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.session.ServiceSession#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#getIpMultiPartyCallConnection(org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier)
     */
    public IpMultiPartyCallConnection getIpMultiPartyCallConnection(
            org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier multiPartyCallIdentifier)
            throws ResourceException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#createCall()
     */
    public org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier createCall()
            throws TpCommonExceptions, ResourceException {
        return MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#createNotification(org.csapi.cc.TpCallNotificationRequest)
     */
    public int createNotification(TpCallNotificationRequest notificationRequest)
            throws TpCommonExceptions, P_INVALID_CRITERIA,
            P_INVALID_EVENT_TYPE, ResourceException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#destroyNotification(int)
     */
    public void destroyNotification(int assignmentID)
            throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID,
            ResourceException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#changeNotification(int, org.csapi.cc.TpCallNotificationRequest)
     */
    public void changeNotification(int assignmentID,
            TpCallNotificationRequest notificationRequest)
            throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID,
            P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE, ResourceException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#getNotification()
     */
    public TpNotificationRequested[] getNotification()
            throws TpCommonExceptions, ResourceException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#setCallLoadControl(int, org.csapi.cc.TpCallLoadControlMechanism, org.csapi.cc.TpCallTreatment, org.csapi.TpAddressRange)
     */
    public int setCallLoadControl(int duration,
            TpCallLoadControlMechanism mechanism, TpCallTreatment treatment,
            TpAddressRange addressRange) throws TpCommonExceptions,
            P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN, ResourceException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#enableNotifications()
     */
    public int enableNotifications() throws TpCommonExceptions,
            ResourceException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#disableNotifications()
     */
    public void disableNotifications() throws TpCommonExceptions,
            ResourceException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection#getNextNotification(boolean)
     */
    public TpNotificationRequestedSetEntry getNextNotification(boolean reset)
            throws TpCommonExceptions, ResourceException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.IpServiceConnection#closeConnection()
     */
    public void closeConnection() throws ResourceException {
        // TODO Auto-generated method stub

    }

}
