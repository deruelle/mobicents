package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import org.csapi.TpAddress;
import org.csapi.TpAddressPlan;
import org.csapi.TpAddressPresentation;
import org.csapi.TpAddressRange;
import org.csapi.TpAddressScreening;
import org.csapi.ui.TpUICollectCriteria;
import org.csapi.ui.TpUIError;
import org.csapi.ui.TpUIEventCriteria;
import org.csapi.ui.TpUIEventCriteriaResult;
import org.csapi.ui.TpUIEventInfo;
import org.csapi.ui.TpUIEventInfoDataType;
import org.csapi.ui.TpUIEventNotificationInfo;
import org.csapi.ui.TpUIFault;
import org.csapi.ui.TpUIIdentifier;
import org.csapi.ui.TpUIInfo;
import org.csapi.ui.TpUIMessageCriteria;
import org.csapi.ui.TpUIVariableInfo;
import org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.csapi.jr.slee.ui.AbortActionErrEvent;
import org.mobicents.csapi.jr.slee.ui.AbortActionResEvent;
import org.mobicents.csapi.jr.slee.ui.CallUITarget;
import org.mobicents.csapi.jr.slee.ui.DeleteMessageErrEvent;
import org.mobicents.csapi.jr.slee.ui.DeleteMessageResEvent;
import org.mobicents.csapi.jr.slee.ui.RecordMessageErrEvent;
import org.mobicents.csapi.jr.slee.ui.RecordMessageResEvent;
import org.mobicents.csapi.jr.slee.ui.ReportEventNotificationEvent;
import org.mobicents.csapi.jr.slee.ui.ReportNotificationEvent;
import org.mobicents.csapi.jr.slee.ui.SendInfoAndCollectErrEvent;
import org.mobicents.csapi.jr.slee.ui.SendInfoAndCollectResEvent;
import org.mobicents.csapi.jr.slee.ui.SendInfoErrEvent;
import org.mobicents.csapi.jr.slee.ui.SendInfoResEvent;
import org.mobicents.csapi.jr.slee.ui.TpUITargetObject;
import org.mobicents.csapi.jr.slee.ui.UserInteractionAbortedEvent;
import org.mobicents.csapi.jr.slee.ui.UserInteractionFaultDetectedEvent;
import org.mobicents.csapi.jr.slee.ui.UserInteractionNotificationContinuedEvent;
import org.mobicents.csapi.jr.slee.ui.UserInteractionNotificationInterruptedEvent;
import org.mobicents.slee.resource.parlay.csapi.jr.ParlayServiceActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.CallStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolManager.CallControlManagerStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLegStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCallStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManagerStub;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGeneric;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGenericStub;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICall;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallStub;

/**
 * 
 */
public class UiTestData {

    public static final org.mobicents.csapi.jr.slee.TpServiceIdentifier SLEE_TP_SERVICE_IDENTIFIER = new org.mobicents.csapi.jr.slee.TpServiceIdentifier(
            6789);

    public static final ParlayServiceActivityHandle SERVICE_ACTIVITY_HANDLE = new ParlayServiceActivityHandle(
            SLEE_TP_SERVICE_IDENTIFIER);

    public static final int UI_ID = 3;

    public static final int UI_SESSION_ID = 44;

    public static final org.mobicents.csapi.jr.slee.ui.TpUIIdentifier SLEE_TP_UI_IDENTIFIER = new org.mobicents.csapi.jr.slee.ui.TpUIIdentifier(
            UI_ID, UI_SESSION_ID);
 
    public static final TpUIActivityHandle TP_UI_ACTIVITY_HANDLE = new TpUIActivityHandle(
            SLEE_TP_UI_IDENTIFIER);

    public static final int UI_CALL_ID = 345;
    public static final int UI_CALL_SESSION_ID = 4467;
    public static final org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier SLEE_TP_UICALL_IDENTIFIER = new org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier(
            UI_CALL_ID, UI_CALL_SESSION_ID);

    public static final TpUICallActivityHandle TP_UICALL_ACTIVITY_HANDLE = new TpUICallActivityHandle(
            SLEE_TP_UICALL_IDENTIFIER);
 
    public static final int SESSIONID = 10001;

    public static final int CALL_REF_ID = 1;

    public static final int CALL_INFO_REQUESTED = 1;

    public static final int ASSIGNMENT_ID = 101;

    public static final int REPORT = 1;

    public static final int USED_TIME = 100;

    public static final int DURATION = 10000;

    public static final String CALL_EVENT_TIME = "13:42:01";
 

    public static final TpAddress TP_ADDRESS = new TpAddress(
            TpAddressPlan.P_ADDRESS_PLAN_E164, "44123", "",
            TpAddressPresentation.P_ADDRESS_PRESENTATION_UNDEFINED,
            TpAddressScreening.P_ADDRESS_SCREENING_UNDEFINED, "");

    private static final String DATA_STRING = "my data string";

    private static final TpUIEventInfoDataType DATA_TYPE_INDICATION = TpUIEventInfoDataType.P_UI_EVENT_DATA_TYPE_TEXT;

    // A SMS Delivery receipt
    private static final String SERVICE_CODE_DEL_RCPT = "01";

    public static final TpUIEventInfo TP_UI_EVENT_INFO = new TpUIEventInfo(
            TP_ADDRESS, TP_ADDRESS, SERVICE_CODE_DEL_RCPT, DATA_TYPE_INDICATION,
            DATA_STRING);

   public static final TpUIIdentifier TP_UI_IDENTIFIER = new TpUIIdentifier(null, 567);

 

    private static final byte[] UI_EVENT_DATA = new byte[] { 1, 2, 3 };

    public static final TpUIEventNotificationInfo TP_UI_EVENT_NOTIFICATION_INFO = new TpUIEventNotificationInfo(
            TP_ADDRESS, TP_ADDRESS, SERVICE_CODE_DEL_RCPT, DATA_TYPE_INDICATION,
            UI_EVENT_DATA);

    public static final org.csapi.ui.TpUIReport TP_UI_REPORT = org.csapi.ui.TpUIReport.P_UI_REPORT_INFO_COLLECTED;

    public static final TpUIError TP_UI_ERROR = TpUIError.P_UI_ERROR_ILLEGAL_INFO;

    public static final TpUIFault TP_UI_FAULT = TpUIFault.P_UI_FAULT_UNDEFINED;

    public static final String COLLECTED_INFO = "my collected info";

    public static final int MESSAGE_ID = 999;

    ;

    // Mgr events
    public static final UserInteractionAbortedEvent userInteractionAbortedEvent = new UserInteractionAbortedEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UI_IDENTIFIER);

    public static final UserInteractionNotificationInterruptedEvent userInteractionNotificationInterruptedEvent = new UserInteractionNotificationInterruptedEvent(
            SLEE_TP_SERVICE_IDENTIFIER);

    public static final UserInteractionNotificationContinuedEvent userInteractionNotificationContinuedEvent = new UserInteractionNotificationContinuedEvent(
            SLEE_TP_SERVICE_IDENTIFIER);

    // @SuppressWarnings({"deprecated"})
    public static final ReportNotificationEvent reportNotificationEvent = new ReportNotificationEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UI_IDENTIFIER,
            TP_UI_EVENT_INFO, ASSIGNMENT_ID);

    public static final ReportEventNotificationEvent reportEventNotificationEvent = new ReportEventNotificationEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UI_IDENTIFIER,
            TP_UI_EVENT_NOTIFICATION_INFO, ASSIGNMENT_ID);

    // UiGeneric events

    public static final SendInfoResEvent sendInfoResEvent = new SendInfoResEvent(
            SLEE_TP_SERVICE_IDENTIFIER, null, SLEE_TP_UI_IDENTIFIER,
            ASSIGNMENT_ID, TP_UI_REPORT);

    public static final SendInfoErrEvent sendInfoErrEvent = new SendInfoErrEvent(
            SLEE_TP_SERVICE_IDENTIFIER, null, SLEE_TP_UI_IDENTIFIER,
            ASSIGNMENT_ID, TP_UI_ERROR);

    public static final SendInfoAndCollectResEvent sendInfoAndCollectResEvent = new SendInfoAndCollectResEvent(
            SLEE_TP_SERVICE_IDENTIFIER, null, SLEE_TP_UI_IDENTIFIER,
            ASSIGNMENT_ID, TP_UI_REPORT, COLLECTED_INFO);

    public static final SendInfoAndCollectErrEvent sendInfoAndCollectErrEvent = new SendInfoAndCollectErrEvent(
            SLEE_TP_SERVICE_IDENTIFIER, null, SLEE_TP_UI_IDENTIFIER,
            ASSIGNMENT_ID, TP_UI_ERROR);

    public static final UserInteractionFaultDetectedEvent userInteractionFaultDetectedEvent = new UserInteractionFaultDetectedEvent(
            SLEE_TP_SERVICE_IDENTIFIER, null, SLEE_TP_UI_IDENTIFIER,
            TP_UI_FAULT);

    // UiCall events

    public static final SendInfoResEvent sendInfoResEventForUiCall = new SendInfoResEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER, null,
            ASSIGNMENT_ID, TP_UI_REPORT);

    public static final SendInfoErrEvent sendInfoErrEventForUiCall = new SendInfoErrEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER, null,
            ASSIGNMENT_ID, TP_UI_ERROR);

    public static final SendInfoAndCollectResEvent sendInfoAndCollectResEventForUiCall = new SendInfoAndCollectResEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER, null,
            ASSIGNMENT_ID, TP_UI_REPORT, COLLECTED_INFO);

    public static final SendInfoAndCollectErrEvent sendInfoAndCollectErrEventForUiCall = new SendInfoAndCollectErrEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER, null,
            ASSIGNMENT_ID, TP_UI_ERROR);

    public static final UserInteractionFaultDetectedEvent userInteractionFaultDetectedEventForUiCall = new UserInteractionFaultDetectedEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER, null,
            TP_UI_FAULT);

    public static final RecordMessageResEvent recordMessageResEvent = new RecordMessageResEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER,
            ASSIGNMENT_ID, TP_UI_REPORT, MESSAGE_ID);

    public static final RecordMessageErrEvent recordMessageErrEvent = new RecordMessageErrEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER,
            ASSIGNMENT_ID, TP_UI_ERROR);

    public static final DeleteMessageResEvent deleteMessageResEvent = new DeleteMessageResEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER,
            TP_UI_REPORT, ASSIGNMENT_ID);

    public static final DeleteMessageErrEvent deleteMessageErrEvent = new DeleteMessageErrEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER, TP_UI_ERROR,
            ASSIGNMENT_ID);

    public static final AbortActionResEvent abortActionResEvent = new AbortActionResEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER,
            ASSIGNMENT_ID);

    public static final AbortActionErrEvent abortActionErrEvent = new AbortActionErrEvent(
            SLEE_TP_SERVICE_IDENTIFIER, SLEE_TP_UICALL_IDENTIFIER,
            ASSIGNMENT_ID, TP_UI_ERROR);

 
    public static final TpUITargetObject SLEE_TP_UI_TARGET_OBJECT;
    static {
        SLEE_TP_UI_TARGET_OBJECT = new TpUITargetObject();
        SLEE_TP_UI_TARGET_OBJECT.setCallUITarget(new CallUITarget(UiTestData.SLEE_TP_SERVICE_IDENTIFIER, UiTestData.SLEE_TP_CALL_IDENTIFIER));
    }
    private static final TpAddressRange TP_ADDRESS_RANGE = new TpAddressRange(
            TpAddressPlan.P_ADDRESS_PLAN_E164, "*", "my name",
            "my sub address string");

    public static final TpUIEventCriteria TP_UI_EVENT_CRITERIA_DEL_RCPT = new TpUIEventCriteria(
            TP_ADDRESS_RANGE, TP_ADDRESS_RANGE, SERVICE_CODE_DEL_RCPT);

    public static final TpUIEventCriteriaResult TP_UI_EVENT_CRITERIA_RESULT = new TpUIEventCriteriaResult(
            TP_UI_EVENT_CRITERIA_DEL_RCPT, ASSIGNMENT_ID);

    public static final TpUIEventCriteriaResult[] TP_UI_EVENT_CRITERIA_RESULT_ARRAY = new TpUIEventCriteriaResult[] {
            TP_UI_EVENT_CRITERIA_RESULT, TP_UI_EVENT_CRITERIA_RESULT,
            TP_UI_EVENT_CRITERIA_RESULT };

    public static final int REPEAT_INDICATOR = 0;

    public static final int RESPONSE_REQUESTED = 0;

    public static final String LANGUAGE = "my language";
    
    public static final TpUIInfo TP_UI_INFO ;
    
    static {
        TP_UI_INFO = new TpUIInfo();
        TP_UI_INFO.InfoBinData(new byte[] {0x33, 0x34});        
    }
    
    public static final TpUIVariableInfo  TP_UI_VARIABLE_INFO;
    static {
        TP_UI_VARIABLE_INFO = new TpUIVariableInfo();
        TP_UI_VARIABLE_INFO.VariablePartAddress("my variable part address");
    }
    public static final TpUIVariableInfo[] TP_UI_VARIABLE_INFO_ARRAY = new TpUIVariableInfo[]{TP_UI_VARIABLE_INFO};



    public static final TpUICollectCriteria TP_UI_COLLECT_CRITERIA = new TpUICollectCriteria (1,2,"some end seq", 4, 99);

    public static final String ORIGIN = "my origin";

    public static final TpUIMessageCriteria TP_UI_MESSAGE_CRITERIA = new TpUIMessageCriteria();

    public static final UIGeneric UIGENERIC_ACTIVITY_STUB = new UIGenericStub();
    public static final UICall UICALL_ACTIVITY_STUB = new UICallStub();

    // Alway matcher will be used do valuse doesnt matter
    public static final org.csapi.ui.TpUITargetObject TP_UI_TARGET_OBJECT = new org.csapi.ui.TpUITargetObject();

    public static final TpCallIdentifier SLEE_TP_CALL_IDENTIFIER = new TpCallIdentifier(3, 5);
    public static final TpMultiPartyCallIdentifier  SLEE_TP_MULTIPARTYCALL_IDENTIFIER = new TpMultiPartyCallIdentifier(3, 5);
    public static final TpCallLegIdentifier SLEE_TP_CALLLEG_IDENTIFIER = new TpCallLegIdentifier(234, 36432);
    
    public static final CallLeg  MPCC_CALLLEG = new CallLegStub( );
    public static final MultiPartyCall  MPCC_CALL = new MultiPartyCallStub(MPCC_CALLLEG);
    public static final MultiPartyCallControlManager MPCC_SERVICE_SESSION = new MultiPartyCallControlManagerStub(MPCC_CALL);

    
    

    
    public static final Call  GCCS_CALL = new CallStub( );
    public static final CallControlManager GCCS_SERVICE_SESSION = new  CallControlManagerStub(GCCS_CALL);

    

 
    


}
