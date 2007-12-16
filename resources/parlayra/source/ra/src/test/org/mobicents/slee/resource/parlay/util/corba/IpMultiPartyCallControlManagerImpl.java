package org.mobicents.slee.resource.parlay.util.corba;

import org.csapi.IpInterface;
import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpAddressRange;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.TpCallLoadControlMechanism;
import org.csapi.cc.TpCallNotificationRequest;
import org.csapi.cc.TpCallTreatment;
import org.csapi.cc.TpNotificationRequested;
import org.csapi.cc.TpNotificationRequestedSetEntry;
import org.csapi.cc.mpccs.IpAppMultiPartyCall;
import org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager;
import org.csapi.cc.mpccs.IpMultiPartyCallControlManagerPOA;
import org.csapi.cc.mpccs.TpMultiPartyCallIdentifier;

/**
 *
 * Class Description for IpMultiPartyCallControlManagerImpl
 */
public class IpMultiPartyCallControlManagerImpl extends
        IpMultiPartyCallControlManagerPOA {

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#createCall(org.csapi.cc.mpccs.IpAppMultiPartyCall)
     */
    public TpMultiPartyCallIdentifier createCall(IpAppMultiPartyCall appCall)
            throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#createNotification(org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager, org.csapi.cc.TpCallNotificationRequest)
     */
    public int createNotification(
            IpAppMultiPartyCallControlManager appCallControlManager,
            TpCallNotificationRequest notificationRequest)
            throws P_INVALID_INTERFACE_TYPE, P_INVALID_EVENT_TYPE,
            TpCommonExceptions, P_INVALID_CRITERIA {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#destroyNotification(int)
     */
    public void destroyNotification(int assignmentID)
            throws P_INVALID_ASSIGNMENT_ID, TpCommonExceptions {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#changeNotification(int, org.csapi.cc.TpCallNotificationRequest)
     */
    public void changeNotification(int assignmentID,
            TpCallNotificationRequest notificationRequest)
            throws P_INVALID_ASSIGNMENT_ID, P_INVALID_EVENT_TYPE,
            TpCommonExceptions, P_INVALID_CRITERIA {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#getNotification()
     */
    public TpNotificationRequested[] getNotification()
            throws TpCommonExceptions {
        //  Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#setCallLoadControl(int, org.csapi.cc.TpCallLoadControlMechanism, org.csapi.cc.TpCallTreatment, org.csapi.TpAddressRange)
     */
    public int setCallLoadControl(int duration,
            TpCallLoadControlMechanism mechanism, TpCallTreatment treatment,
            TpAddressRange addressRange) throws TpCommonExceptions,
            P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#enableNotifications(org.csapi.cc.mpccs.IpAppMultiPartyCallControlManager)
     */
    public int enableNotifications(
            IpAppMultiPartyCallControlManager appCallControlManager)
            throws TpCommonExceptions {
        //  Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#disableNotifications()
     */
    public void disableNotifications() throws TpCommonExceptions {
        //  Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.csapi.cc.mpccs.IpMultiPartyCallControlManagerOperations#getNextNotification(boolean)
     */
    public TpNotificationRequestedSetEntry getNextNotification(boolean reset)
            throws TpCommonExceptions {
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
    public void setCallbackWithSessionID(IpInterface appInterface, int sessionID)
            throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions,
            P_INVALID_SESSION_ID {
        //  Auto-generated method stub

    }

}
