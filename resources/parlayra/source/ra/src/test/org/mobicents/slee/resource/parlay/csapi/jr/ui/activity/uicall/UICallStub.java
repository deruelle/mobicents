package org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall;

import javax.slee.resource.ActivityHandle;
import javax.slee.resource.ResourceException;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.TpCommonExceptions;
import org.csapi.ui.IpUI;
import org.csapi.ui.IpUICall;
import org.csapi.ui.P_ID_NOT_FOUND;
import org.csapi.ui.P_ILLEGAL_ID;
import org.csapi.ui.P_ILLEGAL_RANGE;
import org.csapi.ui.P_INVALID_COLLECTION_CRITERIA;
import org.csapi.ui.TpUICollectCriteria;
import org.csapi.ui.TpUIError;
import org.csapi.ui.TpUIFault;
import org.csapi.ui.TpUIInfo;
import org.csapi.ui.TpUIMessageCriteria;
import org.csapi.ui.TpUIReport;
import org.csapi.ui.TpUIVariableInfo;
import org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier;

public class UICallStub implements UICall {

    public IpUICall getIpUICall() {
        // Auto-generated method stub
        return null;
    }

    public TpUICallIdentifier getTpUICallIdentifier() {
        // Auto-generated method stub
        return null;
    }

    public void init() {
        // Auto-generated method stub

    }

    public void dispose() {
        // Auto-generated method stub

    }

    public ActivityHandle getActivityHandle() {
        // Auto-generated method stub
        return null;
    }

    public IpUI getIpUI() {
        // Auto-generated method stub
        return null;
    }

    public int sendInfoReq(TpUIInfo info, String language,
            TpUIVariableInfo[] variableInfo, int repeatIndicator,
            int responseRequested) throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_ILLEGAL_ID, P_ID_NOT_FOUND,
            ResourceException {
        // Auto-generated method stub
        return 0;
    }

    public int sendInfoAndCollectReq(TpUIInfo info, String language,
            TpUIVariableInfo[] variableInfo, TpUICollectCriteria criteria,
            int responseRequested) throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_ILLEGAL_ID, P_ID_NOT_FOUND,
            P_ILLEGAL_RANGE, P_INVALID_COLLECTION_CRITERIA, ResourceException {
        // Auto-generated method stub
        return 0;
    }

    public void release() throws TpCommonExceptions, ResourceException {
        // Auto-generated method stub

    }

    public void setOriginatingAddress(String origin) throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, P_INVALID_ADDRESS, ResourceException {
        // Auto-generated method stub

    }

    public String getOriginatingAddress() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, ResourceException {
        // Auto-generated method stub
        return null;
    }

    public void closeConnection() throws ResourceException {
        // Auto-generated method stub

    }

    public void sendInfoRes(int userInteractionSessionID, int assignmentID,
            TpUIReport response) {
        // Auto-generated method stub

    }

    public void sendInfoErr(int userInteractionSessionID, int assignmentID,
            TpUIError error) {
        // Auto-generated method stub

    }

    public void sendInfoAndCollectRes(int userInteractionSessionID,
            int assignmentID, TpUIReport response, String collectedInfo) {
        // Auto-generated method stub

    }

    public void sendInfoAndCollectErr(int userInteractionSessionID,
            int assignmentID, TpUIError error) {
        // Auto-generated method stub

    }

    public void userInteractionFaultDetected(int userInteractionSessionID,
            TpUIFault fault) {
        // Auto-generated method stub

    }

    public int recordMessageReq(TpUIInfo info, TpUIMessageCriteria criteria)
            throws TpCommonExceptions, P_INVALID_NETWORK_STATE, P_ILLEGAL_ID,
            P_ID_NOT_FOUND, P_INVALID_CRITERIA, ResourceException {
        // Auto-generated method stub
        return 0;
    }

    public int deleteMessageReq(int messageID) throws TpCommonExceptions,
            P_ILLEGAL_ID, P_ID_NOT_FOUND, ResourceException {
        // Auto-generated method stub
        return 0;
    }

    public void abortActionReq(int assignmentID) throws TpCommonExceptions,
            P_INVALID_ASSIGNMENT_ID, ResourceException {
        // Auto-generated method stub

    }

    public void recordMessageRes(int userInteractionSessionID,
            int assignmentID, TpUIReport response, int messageID) {
        // Auto-generated method stub

    }

    public void recordMessageErr(int userInteractionSessionID,
            int assignmentID, TpUIError error) {
        // Auto-generated method stub

    }

    public void deleteMessageRes(int usrInteractionSessionID,
            TpUIReport response, int assignmentID) {
        // Auto-generated method stub

    }

    public void deleteMessageErr(int usrInteractionSessionID, TpUIError error,
            int assignmentID) {
        // Auto-generated method stub

    }

    public void abortActionRes(int userInteractionSessionID, int assignmentID) {
        // Auto-generated method stub

    }

    public void abortActionErr(int userInteractionSessionID, int assignmentID,
            TpUIError error) {
        // Auto-generated method stub

    }

}
