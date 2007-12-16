package org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui;

import javax.slee.resource.ActivityHandle;
import javax.slee.resource.ResourceException;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.TpCommonExceptions;
import org.csapi.ui.IpUI;
import org.csapi.ui.P_ID_NOT_FOUND;
import org.csapi.ui.P_ILLEGAL_ID;
import org.csapi.ui.P_ILLEGAL_RANGE;
import org.csapi.ui.P_INVALID_COLLECTION_CRITERIA;
import org.csapi.ui.TpUICollectCriteria;
import org.csapi.ui.TpUIError;
import org.csapi.ui.TpUIFault;
import org.csapi.ui.TpUIInfo;
import org.csapi.ui.TpUIReport;
import org.csapi.ui.TpUIVariableInfo;
import org.mobicents.csapi.jr.slee.ui.TpUIIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiTestData;

public class UIGenericStub implements UIGeneric {

    public TpUIIdentifier getTpUIIdentifier() {
        // Auto-generated method stub
        return UiTestData.SLEE_TP_UI_IDENTIFIER;
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

}
