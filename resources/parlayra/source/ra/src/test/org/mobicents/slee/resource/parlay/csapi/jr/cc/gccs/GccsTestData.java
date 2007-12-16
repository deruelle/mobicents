
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs;

import org.csapi.TpAddress;
import org.csapi.TpAddressPlan;
import org.csapi.TpAddressPresentation;
import org.csapi.TpAddressRange;
import org.csapi.TpAddressScreening;
import org.csapi.TpAoCInfo;
import org.csapi.TpAoCOrder;
import org.csapi.cc.TpCallAdditionalErrorInfo;
import org.csapi.cc.TpCallChargePlan;
import org.csapi.cc.TpCallError;
import org.csapi.cc.TpCallErrorType;
import org.csapi.cc.TpCallLoadControlMechanism;
import org.csapi.cc.TpCallMonitorMode;
import org.csapi.cc.gccs.TpCallAdditionalReportCriteria;
import org.csapi.cc.gccs.TpCallAdditionalReportInfo;
import org.csapi.cc.gccs.TpCallAppInfo;
import org.csapi.cc.gccs.TpCallEndedReport;
import org.csapi.cc.gccs.TpCallEventCriteria;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;
import org.csapi.cc.gccs.TpCallEventInfo;
import org.csapi.cc.gccs.TpCallFault;
import org.csapi.cc.gccs.TpCallIdentifier;
import org.csapi.cc.gccs.TpCallInfoReport;
import org.csapi.cc.gccs.TpCallNotificationType;
import org.csapi.cc.gccs.TpCallReleaseCause;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportRequest;
import org.csapi.cc.gccs.TpCallReportType;
import org.csapi.cc.gccs.TpCallTreatment;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.gccs.CallAbortedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallEndedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallEventNotifyEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallFaultDetectedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallNotificationContinuedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallNotificationInterruptedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallOverloadCeasedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallOverloadEncounteredEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetCallInfoErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetCallInfoResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetMoreDialledDigitsErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetMoreDialledDigitsResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.RouteErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.RouteResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.SuperviseCallErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.SuperviseCallResEvent;
import org.mobicents.csapi.jr.slee.fw.TerminateAccessEvent;
import org.mobicents.csapi.jr.slee.fw.TerminateServiceAgreementEvent;
import org.mobicents.slee.resource.parlay.csapi.jr.ParlayServiceActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.CallStub;

/**
 *
 **/
public class GccsTestData {
    


    public static final TpServiceIdentifier TP_SERVICE_IDENTIFIER = new TpServiceIdentifier(1);
    
    public static final int SESSIONID = 10001;
    public static final int CALL_LEG_SESSIONID = 20001;
    public static final int CALL_REF_ID = 1;
    public static final int CALL_INFO_REQUESTED = 1;

    public static final int ASSIGNMENT_ID = 101;
    
    public static final int REPORT = 1;
    
    public static final int USED_TIME = 100;
    
    public static final int DURATION = 10000;
    
    public static final String DIGITS = "0";
    
    
    public static final org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier SLEE_TP_CALL_IDENTIFIER = new org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier(0, SESSIONID);
    
    
    public static final String CALL_EVENT_TIME = "13:42:01";  
    public static final Call CALL = new CallStub();

;
    public static final TpAddress TP_ADDRESS = new TpAddress(
            TpAddressPlan.P_ADDRESS_PLAN_E164, "44123", "",
            TpAddressPresentation.P_ADDRESS_PRESENTATION_UNDEFINED,
            TpAddressScreening.P_ADDRESS_SCREENING_UNDEFINED, "");
    public static final TpCallAdditionalReportInfo ADDITIONAL_REPORT_INFO = new TpCallAdditionalReportInfo();

    public static final TpCallAdditionalReportCriteria ADDITIONAL_REPORT_CRITERIA = new TpCallAdditionalReportCriteria();
    public static final TpCallReportRequest CALL_REPORT_REQUEST = new TpCallReportRequest(TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT, TpCallReportType.P_CALL_REPORT_ALERTING, ADDITIONAL_REPORT_CRITERIA);


    public static final TpCallReportRequest[] CALL_REPORT_REQUEST_ARRAY = {CALL_REPORT_REQUEST};
    public static final TpCallReport EVENTREPORT = new TpCallReport(TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT, CALL_EVENT_TIME, TpCallReportType.P_CALL_REPORT_ALERTING, ADDITIONAL_REPORT_INFO);

    public static final TpAddress TARGET_ADDRESS = TP_ADDRESS;

  

    public static final TpCallChargePlan TP_CALL_CHARGE_PLAN = new TpCallChargePlan();

    public static final TpAoCOrder TP_AOC_ORDER = new TpAoCOrder();
    
    public static final TpAoCInfo TP_AOC_INFO = new TpAoCInfo(TP_AOC_ORDER, "GBP");

    public static final TpCallEventCriteriaResult[] TP_CALL_EVENT_CRITERIA_RESULT_SET = new TpCallEventCriteriaResult[0];
    
    public static final TpCallAppInfo[] TP_CALL_APP_INFO_ARRAY = new TpCallAppInfo[] {};
    
    public static final TpCallReleaseCause TP_CALL_RELEASE_CAUSE = new TpCallReleaseCause(1,1);

    public static final TpCallEventCriteria TP_CALL_EVENT_CRITERIA = new TpCallEventCriteria();

    public static final TpCallLoadControlMechanism TP_CALL_LOAD_CONTROL_MECHANIM = new TpCallLoadControlMechanism();
    
    public static final TpCallTreatment TP_CALL_TREATMENT = new TpCallTreatment();
    
    public static final TpAddressRange TP_ADDRESS_RANGE = new TpAddressRange();
    
    public static final TpCallAdditionalErrorInfo TP_CALL_ADDITIONAL_ERROR_INFO = new TpCallAdditionalErrorInfo();

    public static final TpCallError TP_CALL_ERROR = new TpCallError(CALL_EVENT_TIME, TpCallErrorType.P_CALL_ERROR_UNDEFINED, TP_CALL_ADDITIONAL_ERROR_INFO);

    public static final GetMoreDialledDigitsErrEvent getMoreDialledDigitsErrEvent = new GetMoreDialledDigitsErrEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, TP_CALL_ERROR);

    public static final RouteErrEvent routeErrEvent = new RouteErrEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, TP_CALL_ERROR, CALL_LEG_SESSIONID);

    public static final GetMoreDialledDigitsResEvent getMoreDialledDigitsResEvent = new GetMoreDialledDigitsResEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, DIGITS);

    public static final RouteResEvent routeResEvent = new RouteResEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, EVENTREPORT ,CALL_LEG_SESSIONID);
    
    public static final TpCallInfoReport TP_CALL_INFO_REPORT = new TpCallInfoReport();
    
    public static final TpCallFault TP_CALL_FAULT = TpCallFault.P_CALL_FAULT_UNDEFINED;

    public static final SuperviseCallErrEvent superviseCallErrEvent = new SuperviseCallErrEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, TP_CALL_ERROR);
    
    public static final TpCallEndedReport TP_CALL_ENDED_REPORT = new TpCallEndedReport();

    public static final SuperviseCallResEvent superviseCallResEvent = new SuperviseCallResEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, REPORT, USED_TIME);

    public static final TerminateAccessEvent terminateAccessEvent = new TerminateAccessEvent("Termination Text", "signingAlgorithm", new byte[0]);
    
    public static final TpCallNotificationType callNotificationType = TpCallNotificationType.P_ORIGINATING;
    
    public static final TpCallIdentifier TP_CALL_IDENTIFIER = new TpCallIdentifier();

    public static final TerminateServiceAgreementEvent terminateServiceAgreementEvent = new TerminateServiceAgreementEvent("service Token", "termination text");

    public static final CallOverloadEncounteredEvent callOverloadEncountered = new CallOverloadEncounteredEvent(TP_SERVICE_IDENTIFIER, ASSIGNMENT_ID);

    public static final TpCallEventInfo TP_CALL_EVENT_INFO = new TpCallEventInfo(TP_ADDRESS, TP_ADDRESS, TP_ADDRESS, TP_ADDRESS, TP_CALL_APP_INFO_ARRAY, 1, TpCallNotificationType.P_ORIGINATING, TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT);
    
    public static final CallAbortedEvent callAbortedEvent = new CallAbortedEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER);
    
    public static final TpCallActivityHandle TP_CALL_ACTIVITY_HANDLE = new TpCallActivityHandle(SLEE_TP_CALL_IDENTIFIER);
    
    public static final ParlayServiceActivityHandle PARLAY_ACTIVITY_HANDLER = new ParlayServiceActivityHandle(TP_SERVICE_IDENTIFIER);
    
    public static final CallEndedEvent callEndedEvent = new CallEndedEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, TP_CALL_ENDED_REPORT);
    
    public static final CallFaultDetectedEvent callFaultDetectedEvent = new CallFaultDetectedEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, TP_CALL_FAULT);

    public static final CallNotificationInterruptedEvent callNotificationInterruptedEvent = new CallNotificationInterruptedEvent(TP_SERVICE_IDENTIFIER);

    public static final CallNotificationContinuedEvent callNotificationContinuedEvent = new CallNotificationContinuedEvent(TP_SERVICE_IDENTIFIER);
    
    public static final CallOverloadCeasedEvent callOverloadCeasedEvent = new CallOverloadCeasedEvent(TP_SERVICE_IDENTIFIER, ASSIGNMENT_ID);
    
    public static final GetCallInfoErrEvent callInfoErrEvent = new GetCallInfoErrEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, TP_CALL_ERROR);
    
    public static final GetCallInfoResEvent callInfoResEvent = new GetCallInfoResEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, TP_CALL_INFO_REPORT);
    
    public static final CallEventNotifyEvent callEventNotifyEvent = new CallEventNotifyEvent(TP_SERVICE_IDENTIFIER, SLEE_TP_CALL_IDENTIFIER, TP_CALL_EVENT_INFO, ASSIGNMENT_ID);
}
