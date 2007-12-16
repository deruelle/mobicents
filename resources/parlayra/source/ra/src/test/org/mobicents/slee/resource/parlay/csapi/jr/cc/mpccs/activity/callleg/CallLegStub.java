package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg;

import javax.slee.resource.ActivityHandle;
import javax.slee.resource.ResourceException;

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
import org.csapi.cc.TpCallError;
import org.csapi.cc.TpCallEventInfo;
import org.csapi.cc.TpCallEventRequest;
import org.csapi.cc.TpCallLegConnectionProperties;
import org.csapi.cc.TpCallLegInfoReport;
import org.csapi.cc.TpReleaseCause;
import org.csapi.cc.mpccs.IpCallLeg;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;

/**
 *
 * Class Description for CallLegStub
 */
public class CallLegStub implements CallLeg {

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#getTpCallLegIdentifier()
     */
    public TpCallLegIdentifier getTpCallLegIdentifier() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#getIpCallLeg()
     */
    public IpCallLeg getIpCallLeg() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#getCallLegSessionID()
     */
    public int getCallLegSessionID() {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#getMpccsSession()
     */
    public MultiPartyCallControlManager getMpccsSession() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#init()
     */
    public void init() {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#dispose()
     */
    public void dispose() {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#attachMediaErr(int, org.csapi.cc.TpCallError)
     */
    public void attachMediaErr(int callLegSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#attachMediaRes(int)
     */
    public void attachMediaRes(int callLegSessionID) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#callLegEnded(int, org.csapi.cc.TpReleaseCause)
     */
    public void callLegEnded(int callLegSessionID, TpReleaseCause releaseCause) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#routeErr(int, org.csapi.cc.TpCallError)
     */
    public void routeErr(int callLegSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#superviseErr(int, org.csapi.cc.TpCallError)
     */
    public void superviseErr(int callLegSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#superviseRes(int, int, int)
     */
    public void superviseRes(int callLegSessionID, int report, int duration) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#detachMediaErr(int, org.csapi.cc.TpCallError)
     */
    public void detachMediaErr(int callLegSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#detachMediaRes(int)
     */
    public void detachMediaRes(int callLegSessionID) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#eventReportErr(int, org.csapi.cc.TpCallError)
     */
    public void eventReportErr(int callLegSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#eventReportRes(int, org.csapi.cc.TpCallEventInfo)
     */
    public void eventReportRes(int callLegSessionID,
            TpCallEventInfo callEventInfo) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#getInfoErr(int, org.csapi.cc.TpCallError)
     */
    public void getInfoErr(int callLegSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg#getInfoRes(int, org.csapi.cc.TpCallLegInfoReport)
     */
    public void getInfoRes(int callLegSessionID,
            TpCallLegInfoReport callLegInfoReport) {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#routeReq(org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.cc.TpCallAppInfo[], org.csapi.cc.TpCallLegConnectionProperties)
     */
    public void routeReq(TpAddress targetAddress, TpAddress originatingAddress,
            TpCallAppInfo[] appInfo,
            TpCallLegConnectionProperties connectionProperties)
            throws TpCommonExceptions, P_INVALID_NETWORK_STATE,
            P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN, ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#eventReportReq(org.csapi.cc.TpCallEventRequest[])
     */
    public void eventReportReq(TpCallEventRequest[] eventsRequested)
            throws TpCommonExceptions,
            P_INVALID_EVENT_TYPE, P_INVALID_CRITERIA, ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#release(org.csapi.cc.TpReleaseCause)
     */
    public void release(TpReleaseCause cause) throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#getInfoReq(int)
     */
    public void getInfoReq(int callLegInfoRequested) throws TpCommonExceptions,
            ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#getCall()
     */
    public TpMultiPartyCallIdentifier getCall() throws TpCommonExceptions,
            ResourceException {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#attachMediaReq()
     */
    public void attachMediaReq() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#detachMediaReq()
     */
    public void detachMediaReq() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#getCurrentDestinationAddress()
     */
    public TpAddress getCurrentDestinationAddress() throws TpCommonExceptions,
            ResourceException {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#continueProcessing()
     */
    public void continueProcessing() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#setChargePlan(org.csapi.cc.TpCallChargePlan)
     */
    public void setChargePlan(TpCallChargePlan callChargePlan)
            throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#setAdviceOfCharge(org.csapi.TpAoCInfo, int)
     */
    public void setAdviceOfCharge(TpAoCInfo aOCInfo, int tariffSwitch)
            throws TpCommonExceptions, P_INVALID_CURRENCY, P_INVALID_AMOUNT,
            ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#superviseReq(int, int)
     */
    public void superviseReq(int time, int treatment)
            throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection#deassign()
     */
    public void deassign() throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.IpServiceConnection#close()
     */
    public void closeConnection() throws ResourceException {
        //  Auto-generated method stub

    }

    public org.csapi.cc.mpccs.TpCallLegIdentifier getParlayTpCallLegIdentifier() {
 
        return MpccsTestData.TP_CALL_LEG_IDENTIFIER;
    }

    public ActivityHandle getActivityHandle() {
        // TODO Auto-generated method stub
        return null;
    }

}
