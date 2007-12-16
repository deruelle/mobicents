package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall;

import javax.slee.resource.ActivityHandle;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_AMOUNT;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_CURRENCY;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddress;
import org.csapi.TpAoCInfo;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallAppInfo;
import org.csapi.cc.TpCallChargePlan;
import org.csapi.cc.TpCallEndedReport;
import org.csapi.cc.TpCallError;
import org.csapi.cc.TpCallEventRequest;
import org.csapi.cc.TpCallInfoReport;
import org.csapi.cc.TpReleaseCause;
import org.csapi.cc.mpccs.IpAppCallLeg;
import org.csapi.cc.mpccs.IpMultiPartyCall;
import org.csapi.cc.mpccs.TpCallLegIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;

/**
 *
 * Class Description for MiltiPartyCallStub
 */
public class MultiPartyCallStub implements MultiPartyCall {

    private CallLeg callleg;

    public MultiPartyCallStub() {
        super();
        // set nothing
    }

    public MultiPartyCallStub(CallLeg callleg) {
        this.callleg = callleg;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#getTpMultiPartyCallIdentifier()
     */
    public TpMultiPartyCallIdentifier getTpMultiPartyCallIdentifier() {
        //  Auto-generated method stub        
        return MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#getIpMultiPartyCall()
     */
    public IpMultiPartyCall getIpMultiPartyCall() {
        //  Auto-generated method stub
        return MpccsTestData.IP_MULTI_PARTY_CALL;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#getCallSessionID()
     */
    public int getCallSessionID() {
        //  Auto-generated method stub
        return MpccsTestData.CALL_SESSION_ID;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#getMpccsSession()
     */
    public MultiPartyCallControlManager getMpccsSession() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#getIpAppCallLeg()
     */
    public IpAppCallLeg getIpAppCallLeg() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#getCallLeg(int)
     */
    public CallLeg getCallLeg(int callLegSessionID) {
        //  Auto-generated method stub
        return callleg;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#addCallLeg(int, org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg)
     */
    public void addCallLeg(int callLegSessionID, CallLeg callLeg) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#removeCallLeg(int)
     */
    public CallLeg removeCallLeg(int callLegSessionID) {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#init()
     */
    public void init() {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#dispose()
     */
    public void dispose() {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#callEnded(int, org.csapi.cc.TpCallEndedReport)
     */
    public void callEnded(int callSessionID, TpCallEndedReport callEndedReport) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#createAndRouteCallLegErr(int, org.csapi.cc.mpccs.TpCallLegIdentifier, org.csapi.cc.TpCallError)
     */
    public void createAndRouteCallLegErr(int callSessionID,
            TpCallLegIdentifier callLegIdentifier, TpCallError callError) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#getInfoErr(int, org.csapi.cc.TpCallError)
     */
    public void getInfoErr(int callSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#getInfoRes(int, org.csapi.cc.TpCallInfoReport)
     */
    public void getInfoRes(int callSessionID, TpCallInfoReport callInfoReport) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#superviseErr(int, org.csapi.cc.TpCallError)
     */
    public void superviseErr(int callSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#superviseRes(int, int, int)
     */
    public void superviseRes(int callSessionID, int report, int duration) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#getCallLegs()
     */
    public org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier[] getCallLegs()
            throws TpCommonExceptions, javax.slee.resource.ResourceException {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#createCallLeg()
     */
    public org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier createCallLeg()
            throws TpCommonExceptions, javax.slee.resource.ResourceException {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#createAndRouteCallLegReq(org.csapi.cc.TpCallEventRequest[], org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.cc.TpCallAppInfo[])
     */
    public org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier createAndRouteCallLegReq(
            TpCallEventRequest[] eventsRequested, TpAddress targetAddress,
            TpAddress originatingAddress, TpCallAppInfo[] appInfo)
            throws TpCommonExceptions, P_INVALID_ADDRESS,
            P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_NETWORK_STATE,
            P_INVALID_EVENT_TYPE, P_INVALID_CRITERIA,
            javax.slee.resource.ResourceException {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#release(org.csapi.cc.TpReleaseCause)
     */
    public void release(TpReleaseCause cause) throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, javax.slee.resource.ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#deassignCall()
     */
    public void deassignCall() throws TpCommonExceptions,
            javax.slee.resource.ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#getInfoReq(int)
     */
    public void getInfoReq(int callInfoRequested) throws TpCommonExceptions,
            javax.slee.resource.ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#setChargePlan(org.csapi.cc.TpCallChargePlan)
     */
    public void setChargePlan(TpCallChargePlan callChargePlan)
            throws TpCommonExceptions, javax.slee.resource.ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#setAdviceOfCharge(org.csapi.TpAoCInfo, int)
     */
    public void setAdviceOfCharge(TpAoCInfo aOCInfo, int tariffSwitch)
            throws TpCommonExceptions, P_INVALID_CURRENCY, P_INVALID_AMOUNT,
            javax.slee.resource.ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#superviseReq(int, int)
     */
    public void superviseReq(int time, int treatment)
            throws TpCommonExceptions, javax.slee.resource.ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.IpServiceConnection#close()
     */
    public void closeConnection() throws javax.slee.resource.ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall#getActivityHandle()
     */
    public ActivityHandle getActivityHandle() {
        // Auto-generated method stub
        return MpccsTestData.callActivityHandle;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallConnection#getIpCallLegConnection(org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier)
     */
    public IpCallLegConnection getIpCallLegConnection(org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier callLegIdentifier) throws javax.slee.resource.ResourceException {
        //  Auto-generated method stub
        return null;
    }

    public org.csapi.cc.mpccs.TpMultiPartyCallIdentifier getParlayTpMultiPartyCallIdentifier() {        
        return MpccsTestData.TP_CALL_IDENTIFIER;
    }

}
