package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;

import org.csapi.IpInterface;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_AMOUNT;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_CURRENCY;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddress;
import org.csapi.TpAddressError;
import org.csapi.TpAddressPlan;
import org.csapi.TpAddressPresentation;
import org.csapi.TpAddressRange;
import org.csapi.TpAddressScreening;
import org.csapi.TpAoCInfo;
import org.csapi.TpAoCOrder;
import org.csapi.TpCAIElements;
import org.csapi.TpChargeAdviceInfo;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallAdditionalErrorInfo;
import org.csapi.cc.TpCallAdditionalEventInfo;
import org.csapi.cc.TpCallAdditionalTreatmentInfo;
import org.csapi.cc.TpCallAppInfo;
import org.csapi.cc.TpCallChargePlan;
import org.csapi.cc.TpCallEndedReport;
import org.csapi.cc.TpCallError;
import org.csapi.cc.TpCallErrorType;
import org.csapi.cc.TpCallEventInfo;
import org.csapi.cc.TpCallEventRequest;
import org.csapi.cc.TpCallEventType;
import org.csapi.cc.TpCallInfoReport;
import org.csapi.cc.TpCallLegAttachMechanism;
import org.csapi.cc.TpCallLegConnectionProperties;
import org.csapi.cc.TpCallLegInfoReport;
import org.csapi.cc.TpCallLoadControlMechanism;
import org.csapi.cc.TpCallMonitorMode;
import org.csapi.cc.TpCallNotificationInfo;
import org.csapi.cc.TpCallNotificationReportScope;
import org.csapi.cc.TpCallNotificationRequest;
import org.csapi.cc.TpCallNotificationScope;
import org.csapi.cc.TpCallTreatment;
import org.csapi.cc.TpCallTreatmentType;
import org.csapi.cc.TpNotificationRequested;
import org.csapi.cc.TpNotificationRequestedSetEntry;
import org.csapi.cc.TpReleaseCause;
import org.csapi.cc.mpccs.IpAppCallLeg;
import org.csapi.cc.mpccs.IpCallLeg;
import org.csapi.cc.mpccs.IpMultiPartyCall;
import org.csapi.cc.mpccs.TpCallLegIdentifier;
import org.csapi.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.AttachMediaErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.AttachMediaResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallAbortedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallEndedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallLegEndedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallOverloadCeasedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallOverloadEncounteredEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CreateAndRouteCallLegErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.DetachMediaErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.DetachMediaResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.EventReportErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.EventReportResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.GetInfoErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.GetInfoResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.ManagerInterruptedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.ManagerResumedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.ReportNotificationEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.RouteErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.SuperviseErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.SuperviseResEvent;
import org.mobicents.slee.resource.parlay.csapi.jr.ParlayServiceActivityHandle;
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
 * Class Description for MpccsTestData
 */
public class MpccsTestData {

    public static final int SESSION_ID = 101;

    public static final int CALL_SESSION_ID = 102;

    public static final int CALL_LEG_SESSION_ID = 103;

    public static final int CALL_ID = 1010;

    public static final int CALL_LEG_ID = 909098;

    public static final int ASSIGNMENT_ID = 10000000;

    public static final int REPORT = 11;

    public static final int DURATION = 15;

    public static final int NUM_QUEUES = 5;

    public static final int CALL_INFO_REQUESTED = 5;

    public static final int TARIFF_SWITCH = 5;

    public static final int TIME = 5;

    public static final int TREATMENT = 5;
    
    public static final IpCallLeg IP_CALL_LEG = new IpCallLeg() {
        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#routeReq(int, org.csapi.TpAddress, org.csapi.TpAddress, org.csapi.cc.TpCallAppInfo[], org.csapi.cc.TpCallLegConnectionProperties)
         */
        public void routeReq(int callLegSessionID, TpAddress targetAddress,
                TpAddress originatingAddress, TpCallAppInfo[] appInfo,
                TpCallLegConnectionProperties connectionProperties)
                throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
                P_INVALID_ADDRESS, P_INVALID_SESSION_ID,
                P_UNSUPPORTED_ADDRESS_PLAN {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#eventReportReq(int, org.csapi.cc.TpCallEventRequest[])
         */
        public void eventReportReq(int callLegSessionID,
                TpCallEventRequest[] eventsRequested)
                throws P_INVALID_EVENT_TYPE, TpCommonExceptions,
                P_INVALID_SESSION_ID, P_INVALID_CRITERIA {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#release(int, org.csapi.cc.TpReleaseCause)
         */
        public void release(int callLegSessionID, TpReleaseCause cause)
                throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
                P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#getInfoReq(int, int)
         */
        public void getInfoReq(int callLegSessionID, int callLegInfoRequested)
                throws TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#getCall(int)
         */
        public TpMultiPartyCallIdentifier getCall(int callLegSessionID)
                throws TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#attachMediaReq(int)
         */
        public void attachMediaReq(int callLegSessionID)
                throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
                P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#detachMediaReq(int)
         */
        public void detachMediaReq(int callLegSessionID)
                throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
                P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#getCurrentDestinationAddress(int)
         */
        public TpAddress getCurrentDestinationAddress(int callLegSessionID)
                throws TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#continueProcessing(int)
         */
        public void continueProcessing(int callLegSessionID)
                throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
                P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#setChargePlan(int, org.csapi.cc.TpCallChargePlan)
         */
        public void setChargePlan(int callLegSessionID,
                TpCallChargePlan callChargePlan) throws TpCommonExceptions,
                P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#setAdviceOfCharge(int, org.csapi.TpAoCInfo, int)
         */
        public void setAdviceOfCharge(int callLegSessionID, TpAoCInfo aOCInfo,
                int tariffSwitch) throws P_INVALID_AMOUNT, P_INVALID_CURRENCY,
                TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#superviseReq(int, int, int)
         */
        public void superviseReq(int callLegSessionID, int time, int treatment)
                throws TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.cc.mpccs.IpCallLegOperations#deassign(int)
         */
        public void deassign(int callLegSessionID) throws TpCommonExceptions,
                P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.omg.CORBA.Object#_is_a(java.lang.String)
         */
        public boolean _is_a(String repositoryIdentifier) {
            //  Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
         */
        public boolean _is_equivalent(Object other) {
            //  Auto-generated method stub
            return false;
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
         * @see org.omg.CORBA.Object#_duplicate()
         */
        public Object _duplicate() {
            //  Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.omg.CORBA.Object#_release()
         */
        public void _release() {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.omg.CORBA.Object#_get_interface_def()
         */
        public Object _get_interface_def() {
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

        /* (non-Javadoc)
         * @see org.omg.CORBA.Object#_get_policy(int)
         */
        public Policy _get_policy(int policy_type) {
            //  Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.omg.CORBA.Object#_get_domain_managers()
         */
        public DomainManager[] _get_domain_managers() {
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
         * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
         */
        public void setCallback(IpInterface appInterface)
                throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
            //  Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.csapi.IpServiceOperations#setCallbackWithSessionID(org.csapi.IpInterface, int)
         */
        public void setCallbackWithSessionID(IpInterface appInterface,
                int sessionID) throws P_INVALID_INTERFACE_TYPE,
                TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }
    };

    public static final IpMultiPartyCall IP_MULTI_PARTY_CALL = new IpMultiPartyCall() {
        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallOperations#getCallLegs(int)
         */
        public TpCallLegIdentifier[] getCallLegs(int callSessionID)
                throws TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallOperations#createCallLeg(int,
         *      org.csapi.cc.mpccs.IpAppCallLeg)
         */
        public TpCallLegIdentifier createCallLeg(int callSessionID,
                IpAppCallLeg appCallLeg) throws P_INVALID_INTERFACE_TYPE,
                TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallOperations#createAndRouteCallLegReq(int,
         *      org.csapi.cc.TpCallEventRequest[], org.csapi.TpAddress,
         *      org.csapi.TpAddress, org.csapi.cc.TpCallAppInfo[],
         *      org.csapi.cc.mpccs.IpAppCallLeg)
         */
        public TpCallLegIdentifier createAndRouteCallLegReq(int callSessionID,
                TpCallEventRequest[] eventsRequested, TpAddress targetAddress,
                TpAddress originatingAddress, TpCallAppInfo[] appInfo,
                IpAppCallLeg appLegInterface) throws P_INVALID_INTERFACE_TYPE,
                P_INVALID_EVENT_TYPE, P_INVALID_NETWORK_STATE,
                TpCommonExceptions, P_INVALID_ADDRESS, P_INVALID_SESSION_ID,
                P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_CRITERIA {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallOperations#release(int,
         *      org.csapi.cc.TpReleaseCause)
         */
        public void release(int callSessionID, TpReleaseCause cause)
                throws P_INVALID_NETWORK_STATE, TpCommonExceptions,
                P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallOperations#deassignCall(int)
         */
        public void deassignCall(int callSessionID) throws TpCommonExceptions,
                P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallOperations#getInfoReq(int,
         *      int)
         */
        public void getInfoReq(int callSessionID, int callInfoRequested)
                throws TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallOperations#setChargePlan(int,
         *      org.csapi.cc.TpCallChargePlan)
         */
        public void setChargePlan(int callSessionID,
                TpCallChargePlan callChargePlan) throws TpCommonExceptions,
                P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallOperations#setAdviceOfCharge(int,
         *      org.csapi.TpAoCInfo, int)
         */
        public void setAdviceOfCharge(int callSessionID, TpAoCInfo aOCInfo,
                int tariffSwitch) throws P_INVALID_AMOUNT, P_INVALID_CURRENCY,
                TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.cc.mpccs.IpMultiPartyCallOperations#superviseReq(int,
         *      int, int)
         */
        public void superviseReq(int callSessionID, int time, int treatment)
                throws TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_release()
         */
        public void _release() {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_non_existent()
         */
        public boolean _non_existent() {
            //  Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_hash(int)
         */
        public int _hash(int arg0) {
            //  Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_is_a(java.lang.String)
         */
        public boolean _is_a(String arg0) {
            //  Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_get_domain_managers()
         */
        public DomainManager[] _get_domain_managers() {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_duplicate()
         */
        public Object _duplicate() {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_get_interface_def()
         */
        public Object _get_interface_def() {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
         */
        public boolean _is_equivalent(Object arg0) {
            //  Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_get_policy(int)
         */
        public Policy _get_policy(int arg0) {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_request(java.lang.String)
         */
        public Request _request(String arg0) {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[],
         *      org.omg.CORBA.SetOverrideType)
         */
        public Object _set_policy_override(Policy[] arg0, SetOverrideType arg1) {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context,
         *      java.lang.String, org.omg.CORBA.NVList,
         *      org.omg.CORBA.NamedValue)
         */
        public Request _create_request(Context arg0, String arg1, NVList arg2,
                NamedValue arg3) {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context,
         *      java.lang.String, org.omg.CORBA.NVList,
         *      org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList,
         *      org.omg.CORBA.ContextList)
         */
        public Request _create_request(Context arg0, String arg1, NVList arg2,
                NamedValue arg3, ExceptionList arg4, ContextList arg5) {
            //  Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.IpServiceOperations#setCallback(org.csapi.IpInterface)
         */
        public void setCallback(IpInterface appInterface)
                throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.csapi.IpServiceOperations#setCallbackWithSessionID(org.csapi.IpInterface,
         *      int)
         */
        public void setCallbackWithSessionID(IpInterface appInterface,
                int sessionID) throws P_INVALID_INTERFACE_TYPE,
                TpCommonExceptions, P_INVALID_SESSION_ID {
            //  Auto-generated method stub

        }
    };

    private static final TpAoCOrder TP_AOC_ORDER = new TpAoCOrder();
    static {
        TP_AOC_ORDER
                .ChargeAdviceInfo(new TpChargeAdviceInfo(new TpCAIElements(1,
                        2, 3, 4, 5, 6, 7), (new TpCAIElements(1, 2, 3, 4, 5, 6,
                        7))));
    }

    public static final TpAoCInfo TP_AOC_INFO = new TpAoCInfo(TP_AOC_ORDER,
            "GBP");

    public static final TpAddress TP_ADDRESS = new TpAddress(
            TpAddressPlan.P_ADDRESS_PLAN_E164, "44123", "",
            TpAddressPresentation.P_ADDRESS_PRESENTATION_UNDEFINED,
            TpAddressScreening.P_ADDRESS_SCREENING_UNDEFINED, "");

    public static final TpAddressRange TP_ADDRESS_RANGE = new TpAddressRange(
            TpAddressPlan.P_ADDRESS_PLAN_E164, "44123", "", "");

    public static final TpAddress[] TP_ADDRESSES = new TpAddress[] { TP_ADDRESS };

    public static TpCallAdditionalErrorInfo TP_CALL_ADDITIONAL_ERROR_INFO = new TpCallAdditionalErrorInfo();
    static {
        TP_CALL_ADDITIONAL_ERROR_INFO
                .CallErrorInvalidAddress(TpAddressError.P_ADDRESS_INVALID_MISSING);
    };

    public static final TpCallError TP_CALL_ERROR = new TpCallError("Error",
            TpCallErrorType.P_CALL_ERROR_INVALID_ADDRESS,
            TP_CALL_ADDITIONAL_ERROR_INFO);

    public static TpMultiPartyCallIdentifier TP_CALL_IDENTIFIER = new TpMultiPartyCallIdentifier(
            IP_MULTI_PARTY_CALL, CALL_SESSION_ID);

    public static TpCallLegIdentifier TP_CALL_LEG_IDENTIFIER = new TpCallLegIdentifier(
            IP_CALL_LEG, CALL_LEG_SESSION_ID);

    public static TpCallLegIdentifier[] TP_CALL_LEG_IDENTIFIER_ARRAY = new TpCallLegIdentifier[] { TP_CALL_LEG_IDENTIFIER };

    public static final TpCallEndedReport TP_CALL_ENDED_REPORT = new TpCallEndedReport(
            CALL_LEG_SESSION_ID, TpReleaseCause.P_NO_ANSWER);

    public static final TpCallInfoReport TP_CALL_INFO_REPORT = new TpCallInfoReport(
            CALL_SESSION_ID, "", "", "", "", TpReleaseCause.P_NO_ANSWER);

    public static final TpCallLegInfoReport TP_CALL_LEG_INFO_REPORT = new TpCallLegInfoReport(
            CALL_LEG_SESSION_ID, "", "", "", "", TP_ADDRESS,
            TpReleaseCause.P_BUSY, new TpCallAppInfo[0]);

    public static TpCallAdditionalEventInfo TP_CALL_ADDITIONAL_EVENT_INFO = new TpCallAdditionalEventInfo();
    static {
        TP_CALL_ADDITIONAL_EVENT_INFO.CalledAddress(TP_ADDRESS);
    };

    public static final TpCallEventInfo TP_CALL_EVENT_INFO = new TpCallEventInfo(
            TpCallEventType.P_CALL_EVENT_ANSWER, TP_CALL_ADDITIONAL_EVENT_INFO,
            TpCallMonitorMode.P_CALL_MONITOR_MODE_INTERRUPT, "String");

    public static final TpCallLegConnectionProperties TP_CALL_LEG_CONNECTION_PROPERTIES = new TpCallLegConnectionProperties(
            TpCallLegAttachMechanism.P_CALLLEG_ATTACH_IMPLICITLY);

    public static final TpCallAppInfo[] TP_CALL_APP_INFO_ARRAY = new TpCallAppInfo[] {};

    public static final TpCallChargePlan TP_CALL_CHARGE_PLAN = new TpCallChargePlan();

    public static final TpCallEventRequest[] TP_CALL_EVENT_REQUEST_ARRAY = new TpCallEventRequest[] {};

    public static final TpReleaseCause TP_RELEASE_CAUSE = TpReleaseCause.P_BUSY;

    private static final TpCallNotificationScope TP_CALL_NOTIFICATION_SCOPE = new TpCallNotificationScope(
            TP_ADDRESS_RANGE, TP_ADDRESS_RANGE);

    private static final TpCallNotificationReportScope TP_CALL_NOTIFICATION_REPORT_SCOPE = new TpCallNotificationReportScope(
            TP_ADDRESS, TP_ADDRESS);

    public static final TpCallNotificationRequest TP_CALL_NOTIFCATION_REQUEST = new TpCallNotificationRequest(
            TP_CALL_NOTIFICATION_SCOPE, TP_CALL_EVENT_REQUEST_ARRAY);

    public static final TpNotificationRequested TP_NOTIFCATION_REQUESTED = new TpNotificationRequested(
            TP_CALL_NOTIFCATION_REQUEST, ASSIGNMENT_ID);

    public static final TpNotificationRequested[] TP_NOTIFCATION_REQUESTED_ARRAY = new TpNotificationRequested[] { TP_NOTIFCATION_REQUESTED };

    public static final TpNotificationRequestedSetEntry TP_NOTIFICATION_REQUESTED_SET_ENTRY = new TpNotificationRequestedSetEntry(
            TP_NOTIFCATION_REQUESTED_ARRAY, true);

    public static final TpCallLoadControlMechanism TP_CALL_LOAD_CONTROL_MECHANISM = new TpCallLoadControlMechanism();

    static {
        TP_CALL_LOAD_CONTROL_MECHANISM.CallLoadControlPerInterval(1);
    }

    public static final TpCallAdditionalTreatmentInfo TP_CALL_ADDITIONAL_TREATMENT_INFO = new TpCallAdditionalTreatmentInfo();

    static {
        TP_CALL_ADDITIONAL_TREATMENT_INFO.InformationToSend(null);
    }

    public static final TpCallTreatment TP_CALL_TREATMENT = new TpCallTreatment(
            TpCallTreatmentType.P_CALL_TREATMENT_RELEASE, TP_RELEASE_CAUSE,
            TP_CALL_ADDITIONAL_TREATMENT_INFO);

    public static final TpNotificationRequestedSetEntry TP_NOTIFICATION_SET_ENTRY = new TpNotificationRequestedSetEntry(
            TP_NOTIFCATION_REQUESTED_ARRAY, true);

    public static final TpCallNotificationInfo TP_CALL_NOTIFICATION_INFO = new TpCallNotificationInfo(
            TP_CALL_NOTIFICATION_REPORT_SCOPE, TP_CALL_APP_INFO_ARRAY,
            TP_CALL_EVENT_INFO);

    public static final TpServiceIdentifier TP_SERVICE_IDENTIFIER = new TpServiceIdentifier(
            100);

    public static final ParlayServiceActivityHandle SERVICE_ACTIVITY_HANDLE = new ParlayServiceActivityHandle(
            TP_SERVICE_IDENTIFIER);

    public static final org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier TP_SLEE_MP_CALL_IDENTIFIER = new org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier(
            CALL_ID, CALL_SESSION_ID);

    public static final TpMultiPartyCallActivityHandle callActivityHandle = new TpMultiPartyCallActivityHandle(
            TP_SLEE_MP_CALL_IDENTIFIER);

    public static final org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier TP_SLEE_CALL_LEG_IDENTIFIER = new org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier(
            CALL_LEG_ID, CALL_LEG_SESSION_ID);

    public static final org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier[] TP_SLEE_CALL_LEG_IDENTIFIER_ARRAY = new org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier[] { TP_SLEE_CALL_LEG_IDENTIFIER };

    public static final TpCallLegActivityHandle callLegActivityHandle = new TpCallLegActivityHandle(
            TP_SLEE_CALL_LEG_IDENTIFIER);

    public static final AttachMediaErrEvent attachMediaErrEvent = new AttachMediaErrEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_CALL_ERROR);

    public static final AttachMediaResEvent attachMediaResEvent = new AttachMediaResEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER);

    public static final CallAbortedEvent callAbortedEvent = new CallAbortedEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER);

    public static final CallEndedEvent callEndedEvent = new CallEndedEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_CALL_ENDED_REPORT);

    public static final CallLegEndedEvent callLegEndedEvent = new CallLegEndedEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_RELEASE_CAUSE);

    public static final CallOverloadCeasedEvent callOverloadCeasedEvent = new CallOverloadCeasedEvent(
            TP_SERVICE_IDENTIFIER, ASSIGNMENT_ID);

    public static final CallOverloadEncounteredEvent callOverloadEncounteredEvent = new CallOverloadEncounteredEvent(
            TP_SERVICE_IDENTIFIER, ASSIGNMENT_ID);

    public static final CreateAndRouteCallLegErrEvent createAndRouteCallLegErrEvent = new CreateAndRouteCallLegErrEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_CALL_ERROR);

    public static final DetachMediaErrEvent detachMediaErrEvent = new DetachMediaErrEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_CALL_ERROR);

    public static final DetachMediaResEvent detachMediaResEvent = new DetachMediaResEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER);

    public static final EventReportErrEvent eventReportErrEvent = new EventReportErrEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_CALL_ERROR);

    public static final EventReportResEvent eventReportResEvent = new EventReportResEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_CALL_EVENT_INFO);

    public static final GetInfoErrEvent getInfoErrEvent = new GetInfoErrEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER, TP_CALL_ERROR);

    public static final GetInfoErrEvent getInfoErrEvent2 = new GetInfoErrEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_CALL_ERROR);

    public static final GetInfoResEvent getInfoResEvent = new GetInfoResEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_CALL_INFO_REPORT);

    public static final GetInfoResEvent getInfoResEvent2 = new GetInfoResEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_CALL_LEG_INFO_REPORT);

    public static final ManagerInterruptedEvent managerInterruptedEvent = new ManagerInterruptedEvent(
            TP_SERVICE_IDENTIFIER);

    public static final ManagerResumedEvent managerResumedEvent = new ManagerResumedEvent(
            TP_SERVICE_IDENTIFIER);

    public static final ReportNotificationEvent reportNotificationEvent = new ReportNotificationEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER_ARRAY, TP_CALL_NOTIFICATION_INFO,
            ASSIGNMENT_ID);

    public static final RouteErrEvent routeErrEvent = new RouteErrEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_CALL_ERROR);

    public static final SuperviseErrEvent superviseErrEvent = new SuperviseErrEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER, TP_CALL_ERROR);

    public static final SuperviseErrEvent superviseErrEvent2 = new SuperviseErrEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, TP_CALL_ERROR);

    public static final SuperviseResEvent superviseResEvent = new SuperviseResEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER, REPORT, TIME);

    public static final SuperviseResEvent superviseResEvent2 = new SuperviseResEvent(
            TP_SERVICE_IDENTIFIER, TP_SLEE_MP_CALL_IDENTIFIER,
            TP_SLEE_CALL_LEG_IDENTIFIER, REPORT, TIME);

}