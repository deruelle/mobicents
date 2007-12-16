
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call;

import javax.slee.resource.ActivityHandle;
import javax.slee.resource.ResourceException;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddress;
import org.csapi.TpAoCInfo;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallChargePlan;
import org.csapi.cc.TpCallError;
import org.csapi.cc.gccs.IpCall;
import org.csapi.cc.gccs.TpCallAppInfo;
import org.csapi.cc.gccs.TpCallEndedReport;
import org.csapi.cc.gccs.TpCallFault;
import org.csapi.cc.gccs.TpCallInfoReport;
import org.csapi.cc.gccs.TpCallReleaseCause;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportRequest;
import org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call;

/**
 *
 **/
public class CallStub implements Call{

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#init()
     */
    public void init() {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#dispose()
     */
    public void dispose() {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#getIpCall()
     */
    public IpCall getIpCall() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#routeRes(int, org.csapi.cc.gccs.TpCallReport, int)
     */
    public void routeRes(int callSessionID, TpCallReport eventReport, int callLegSessionID) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#routeErr(int, org.csapi.cc.TpCallError, int)
     */
    public void routeErr(int callSessionID, TpCallError errorIndication, int callLegSessionID) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#getCallInfoRes(int, org.csapi.cc.gccs.TpCallInfoReport)
     */
    public void getCallInfoRes(int callSessionID, TpCallInfoReport callInfoReport) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#getCallInfoErr(int, org.csapi.cc.TpCallError)
     */
    public void getCallInfoErr(int callSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#superviseCallRes(int, int, int)
     */
    public void superviseCallRes(int callSessionID, int report, int usedTime) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#superviseCallErr(int, org.csapi.cc.TpCallError)
     */
    public void superviseCallErr(int callSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#callFaultDetected(int, org.csapi.cc.gccs.TpCallFault)
     */
    public void callFaultDetected(int callSessionID, TpCallFault fault) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#getMoreDialledDigitsRes(int, java.lang.String)
     */
    public void getMoreDialledDigitsRes(int callSessionID, String digits) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#getMoreDialledDigitsErr(int, org.csapi.cc.TpCallError)
     */
    public void getMoreDialledDigitsErr(int callSessionID, TpCallError errorIndication) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#callEnded(int, org.csapi.cc.gccs.TpCallEndedReport)
     */
    public void callEnded(int callSessionID, TpCallEndedReport report) {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#getTpCallIdentifier()
     */
    public TpCallIdentifier getTpCallIdentifier() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call#getActivityHandle()
     */
    public ActivityHandle getActivityHandle() {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection#routeReq(org.csapi.cc.gccs.TpCallReportRequest[], org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.cc.gccs.TpCallAppInfo[])
     */
    public int routeReq(TpCallReportRequest[] responseRequested, TpAddress targetAddress, TpAddress originatingAddress, TpAddress originalDestinationAddress, TpAddress redirectingAddress, TpCallAppInfo[] appInfo) throws TpCommonExceptions, P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_NETWORK_STATE, P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE, ResourceException {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection#release(org.csapi.cc.gccs.TpCallReleaseCause)
     */
    public void release(TpCallReleaseCause cause) throws TpCommonExceptions, P_INVALID_NETWORK_STATE, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection#deassignCall()
     */
    public void deassignCall() throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection#getCallInfoReq(int)
     */
    public void getCallInfoReq(int callInfoRequested) throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection#setCallChargePlan(org.csapi.cc.TpCallChargePlan)
     */
    public void setCallChargePlan(TpCallChargePlan callChargePlan) throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection#setAdviceOfCharge(org.csapi.TpAoCInfo, int)
     */
    public void setAdviceOfCharge(TpAoCInfo aOCInfo, int tariffSwitch) throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection#getMoreDialledDigitsReq(int)
     */
    public void getMoreDialledDigitsReq(int length) throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection#superviseCallReq(int, int)
     */
    public void superviseCallReq(int time, int treatment) throws TpCommonExceptions, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection#continueProcessing()
     */
    public void continueProcessing() throws TpCommonExceptions, P_INVALID_NETWORK_STATE, ResourceException {
        //  Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.csapi.jr.slee.IpServiceConnection#closeConnection()
     */
    public void closeConnection() throws ResourceException {
        //  Auto-generated method stub
        
    }

    public org.csapi.cc.gccs.TpCallIdentifier getParlayTpCallIdentifier() {
        
        return GccsTestData.TP_CALL_IDENTIFIER;
    }

}
