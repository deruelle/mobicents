/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.base;

import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.AbortSessionRequest;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer;
import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;
import net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;


/**
 * Factory to create instances of {@link DiameterAvp}, {@link DiameterMessage}.
 * <p/>
 * An implementation of this class should be returned by the
 * {@link DiameterProvider#getDiameterMessageFactory()} method.
 *
 * @author Open Cloud
 */
public interface DiameterMessageFactory {

    /**
     * Create a value for a Grouped AVP.  Type will always be
     * {@link DiameterAvpType#GROUPED}
     *
     * @param avpCode the code for the AVP
     * @param avps an array of DiameterAvp objects
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, DiameterAvp[] avps) throws NoSuchAvpException, AvpNotAllowedException;

    /**
     * Create a value for a vendor-specific Grouped AVP.  Type will always be
     * {@link DiameterAvpType#GROUPED}
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param avps an array of DiameterAvp objects
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, DiameterAvp[] avps) throws NoSuchAvpException, AvpNotAllowedException;

    /**
     * Create an AVP containing a DiameterAvpValue from the byte[] value provided.  The
     * AVP type will be determined from the AVP code.
     *
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, byte[] value) throws NoSuchAvpException;

    /**
     * Create a vendor-specific AVP containing a DiameterAvpValue from the byte[] value provided.
     * The AVP type will be determined from the AVP code.
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, byte[] value) throws NoSuchAvpException;

    /**
     * Create an AVP containing a DiameterAvpValue from the int value provided.  The
     * AVP type will be determined from the AVP code.
     *
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, int value) throws NoSuchAvpException;

    /**
     * Create a vendor-specific AVP containing a DiameterAvpValue from the int value provided.
     * The AVP type will be determined from the AVP code.
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, int value) throws NoSuchAvpException;

    /**
     * Create an AVP containing a DiameterAvpValue from the long value provided.  The
     * AVP type will be determined from the AVP code.
     *
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, long value) throws NoSuchAvpException;

    /**
     * Create a vendor-specific AVP containing a DiameterAvpValue from the long value provided.
     * The AVP type will be determined from the AVP code.
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, long value) throws NoSuchAvpException;

    /**
     * Create an AVP containing a DiameterAvpValue from the float value provided.  The
     * AVP type will be determined from the AVP code.
     *
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, float value) throws NoSuchAvpException;

    /**
     * Create a vendor-specific AVP containing a DiameterAvpValue from the float value provided.
     * The AVP type will be determined from the AVP code.
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, float value) throws NoSuchAvpException;

    /**
     * Create an AVP containing a DiameterAvpValue from the double value provided.  The
     * AVP type will be determined from the AVP code.
     *
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, double value) throws NoSuchAvpException;

    /**
     * Create a vendor-specific AVP containing a DiameterAvpValue from the double value provided.
     * The AVP type will be determined from the AVP code.
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, double value) throws NoSuchAvpException;

    /**
     * Create an AVP containing a DiameterAvpValue from the java.net.InetAddress value provided.  The
     * AVP type will be determined from the AVP code.
     *
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, java.net.InetAddress value) throws NoSuchAvpException;

    /**
     * Create a vendor-specific AVP containing a DiameterAvpValue from the java.net.InetAddress value provided.
     * The AVP type will be determined from the AVP code.
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, java.net.InetAddress value) throws NoSuchAvpException;

    /**
     * Create an AVP containing a DiameterAvpValue from the java.util.Date value provided.  The
     * AVP type will be determined from the AVP code.
     *
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, java.util.Date value) throws NoSuchAvpException;

    /**
     * Create a vendor-specific AVP containing a DiameterAvpValue from the java.util.Date value provided.
     * The AVP type will be determined from the AVP code.
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, java.util.Date value) throws NoSuchAvpException;

    /**
     * Create an AVP containing a DiameterAvpValue from the java.lang.String value provided.  The
     * AVP type will be determined from the AVP code.
     *
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, java.lang.String value) throws NoSuchAvpException;

    /**
     * Create a vendor-specific AVP containing a DiameterAvpValue from the java.lang.String value provided.
     * The AVP type will be determined from the AVP code.
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, java.lang.String value) throws NoSuchAvpException;

    /**
     * Create an AVP containing a DiameterAvpValue from the net.java.slee.resource.diameter.base.types.Enumerated value provided.  The
     * AVP type will be determined from the AVP code.
     *
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int avpCode, net.java.slee.resource.diameter.base.events.avp.Enumerated value) throws NoSuchAvpException;

    /**
     * Create a vendor-specific AVP containing a DiameterAvpValue from the net.java.slee.resource.diameter.base.types.Enumerated value provided.
     * The AVP type will be determined from the AVP code.
     *
     * @param vendorID the IANA-assigned enterprise number of the vendor that specified the AVP (eg. 10415 for 3GPP).
     * @param avpCode the code for the AVP
     * @param value the value itself
     * @return an implementation of the DiameterAvp interface
     */
    DiameterAvp createAvp(int vendorID, int avpCode, net.java.slee.resource.diameter.base.events.avp.Enumerated value) throws NoSuchAvpException;

    /**
     * Create an instance of a DiameterCommand concrete implementation using
     * the given arguments.
     *
     * @param commandCode the command code of the command
     * @param applicationId the application ID of the command
     * @param shortName   the short name of the command, e.g., "CER"
     * @param longName    the long name of the command, e.g., "Capabilities-Exchange-Request"
     * @param isRequest   true if this command represents a request (not answer)
     * @param isProxiable true if this command may be proxied
     * @return a complete and correct DiameterCommand object to be passed to
     *         {@link #createMessage(DiameterCommand, DiameterAvp[] avps)}.
     */
    DiameterCommand createCommand(int commandCode, int applicationId, String shortName, String longName, boolean isRequest, boolean isProxiable);

    /**
     * Create an instance of a DiameterMessage concrete implementation using
     * the given arguments and default AVPs required for the command (must be known
     * to the factory).
     *
     * @param command     the command for this message
     * @param avps        an array of DiameterAvp objects. AVPs will be added to the
     *                    message in the order they are in the array.
     * @return a complete and correct DiameterMessage object to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    ExtensionDiameterMessage createMessage(DiameterCommand command, DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     *
     */
    DiameterMessage createMessage(DiameterHeader header, DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create a AbortSessionRequest DiameterMessage for a ASR command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    AbortSessionRequest createAbortSessionRequest(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty AbortSessionRequest DiameterMessage for a ASR command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    AbortSessionRequest createAbortSessionRequest();

    /**
     * Create a AbortSessionAnswer DiameterMessage for a ASA command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    AbortSessionAnswer createAbortSessionAnswer(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty AbortSessionAnswer DiameterMessage for a ASA command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    AbortSessionAnswer createAbortSessionAnswer();

    /**
     * Create a AccountingRequest DiameterMessage for a ACR command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    AccountingRequest createAccountingRequest(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty AccountingRequest DiameterMessage for a ACR command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    AccountingRequest createAccountingRequest();

    /**
     * Create a AccountingAnswer DiameterMessage for a ACA command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    AccountingAnswer createAccountingAnswer(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty AccountingAnswer DiameterMessage for a ACA command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    AccountingAnswer createAccountingAnswer();

    /**
     * Create a CapabilitiesExchangeRequest DiameterMessage for a CER command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    CapabilitiesExchangeRequest createCapabilitiesExchangeRequest(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty CapabilitiesExchangeRequest DiameterMessage for a CER command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    CapabilitiesExchangeRequest createCapabilitiesExchangeRequest();

    /**
     * Create a CapabilitiesExchangeAnswer DiameterMessage for a CEA command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    CapabilitiesExchangeAnswer createCapabilitiesExchangeAnswer(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty CapabilitiesExchangeAnswer DiameterMessage for a CEA command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    CapabilitiesExchangeAnswer createCapabilitiesExchangeAnswer();

    /**
     * Create a DeviceWatchdogRequest DiameterMessage for a DWR command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    DeviceWatchdogRequest createDeviceWatchdogRequest(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty DeviceWatchdogRequest DiameterMessage for a DWR command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    DeviceWatchdogRequest createDeviceWatchdogRequest();

    /**
     * Create a DeviceWatchdogAnswer DiameterMessage for a DWA command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    DeviceWatchdogAnswer createDeviceWatchdogAnswer(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty DeviceWatchdogAnswer DiameterMessage for a DWA command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    DeviceWatchdogAnswer createDeviceWatchdogAnswer();

    /**
     * Create a DisconnectPeerRequest DiameterMessage for a DPR command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    DisconnectPeerRequest createDisconnectPeerRequest(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty DisconnectPeerRequest DiameterMessage for a DPR command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    DisconnectPeerRequest createDisconnectPeerRequest();

    /**
     * Create a DisconnectPeerAnswer DiameterMessage for a DPA command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    DisconnectPeerAnswer createDisconnectPeerAnswer(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty DisconnectPeerAnswer DiameterMessage for a DPA command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    DisconnectPeerAnswer createDisconnectPeerAnswer();

    /**
     * Create a ReAuthRequest DiameterMessage for a RAR command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    ReAuthRequest createReAuthRequest(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty ReAuthRequest DiameterMessage for a RAR command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    ReAuthRequest createReAuthRequest();

    /**
     * Create a ReAuthAnswer DiameterMessage for a RAA command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    ReAuthAnswer createReAuthAnswer(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty ReAuthAnswer DiameterMessage for a RAA command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    ReAuthAnswer createReAuthAnswer();

    /**
     * Create a SessionTerminationRequest DiameterMessage for a STR command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    SessionTerminationRequest createSessionTerminationRequest(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty SessionTerminationRequest DiameterMessage for a STR command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    SessionTerminationRequest createSessionTerminationRequest();

    /**
     * Create a SessionTerminationAnswer DiameterMessage for a STA command containing the given AVPs.
     *
     * @param avps an array of DiameterAvp objects. AVPs will be added to the
     *             message in the order they are in the array.  May be null or empty.
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    SessionTerminationAnswer createSessionTerminationAnswer(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty SessionTerminationAnswer DiameterMessage for a STA command.
     *
     * @return an implementation of DiameterMessage to be passed to
     *         {@link DiameterActivity#sendMessage(DiameterMessage)}.
     */
    SessionTerminationAnswer createSessionTerminationAnswer();


    /**
     * Create a ProxyInfo (Grouped AVP) instance using required AVP values.
     */
    ProxyInfoAvp createProxyInfo(
        DiameterIdentityAvp proxyHost
        , byte[] proxyState
    );

    /**
     * Create an empty ProxyInfo (Grouped AVP) instance.
     */
    ProxyInfoAvp createProxyInfo();
 
    /**
     * Create a ProxyInfo (Grouped AVP) instance, populating one AVP.
     */
    ProxyInfoAvp createProxyInfo(DiameterAvp avp);

    /**
     * Create a ProxyInfo (Grouped AVP) instance using the given array to populate the AVPs.
     */
    ProxyInfoAvp createProxyInfo(DiameterAvp[] avps);

    /**
     * Create a VendorSpecificApplicationId (Grouped AVP) instance using required AVP values.
     */
    VendorSpecificApplicationIdAvp createVendorSpecificApplicationId(
        long vendorId
    );

    /**
     * Create an empty VendorSpecificApplicationId (Grouped AVP) instance.
     */
    VendorSpecificApplicationIdAvp createVendorSpecificApplicationId();
 
    /**
     * Create a VendorSpecificApplicationId (Grouped AVP) instance, populating one AVP.
     */
    VendorSpecificApplicationIdAvp createVendorSpecificApplicationId(DiameterAvp avp) throws AvpNotAllowedException;

    /**
     * Create a VendorSpecificApplicationId (Grouped AVP) instance using the given array to populate the AVPs.
     */
    VendorSpecificApplicationIdAvp createVendorSpecificApplicationId(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Create an empty FailedAvp (Grouped AVP) instance.
     */
    FailedAvp createFailedAvp();
 
    /**
     * Create a FailedAvp (Grouped AVP) instance, populating one AVP.
     */
    FailedAvp createFailedAvp(DiameterAvp avp);

    /**
     * Create a FailedAvp (Grouped AVP) instance using the given array to populate the AVPs.
     */
    FailedAvp createFailedAvp(DiameterAvp[] avps);

    /**
     * Create a ExperimentalResult (Grouped AVP) instance using required AVP values.
     */
    ExperimentalResultAvp createExperimentalResult(
        long vendorId
        , long experimentalResultCode
    );

    /**
     * Create an empty ExperimentalResult (Grouped AVP) instance.
     */
    ExperimentalResultAvp createExperimentalResult();
 
    /**
     * Create a ExperimentalResult (Grouped AVP) instance, populating one AVP.
     */
    ExperimentalResultAvp createExperimentalResult(DiameterAvp avp) throws AvpNotAllowedException;

    /**
     * Create a ExperimentalResult (Grouped AVP) instance using the given array to populate the AVPs.
     */
    ExperimentalResultAvp createExperimentalResult(DiameterAvp[] avps) throws AvpNotAllowedException;
}
