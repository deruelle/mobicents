package net.java.slee.resource.diameter.base.events;


import java.util.Iterator;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;

/**
 * Defines an interface representing the Error-Answer command.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 7.2.  Error Bit
 * 
 *    The 'E' (Error Bit) in the Diameter header is set when the request
 *    caused a protocol-related error (see Section 7.1.3).  A message with
 *    the 'E' bit MUST NOT be sent as a response to an answer message.
 *    Note that a message with the 'E' bit set is still subjected to the
 *    processing rules defined in Section 6.2.  When set, the answer
 *    message will not conform to the ABNF specification for the command,
 *    and will instead conform to the following ABNF:
 * 
 *    Message Format
 * 
 *    &lt;Error-Answer&gt; ::= &lt; Diameter Header: 0, ERR [PXY] &gt;
 *                      0*1&lt; Session-Id &gt;
 *                         { Origin-Host }
 *                         { Origin-Realm }
 *                         { Result-Code }
 *                         [ Origin-State-Id ]
 *                         [ Error-Reporting-Host ]
 *                         [ Proxy-Info ]
 *                       * [ AVP ]
 *    Note that the code used in the header is the same than the one found
 *    in the request message, but with the 'R' bit cleared and the 'E' bit
 *    set.  The 'P' bit in the header is set to the same value as the one
 *    found in the request message.
 * 
 * </pre>
 */
public interface ErrorAnswer extends DiameterMessage {

    int commandCode = 0;

    /**
     * Returns true if the Session-Id AVP is present in the message.
     */
    boolean hasSessionId();

    /**
     * Returns the value of the Session-Id AVP, of type UTF8String.
     * @return the value of the Session-Id AVP or null if it has not been set on this message
     */
    String getSessionId();

    /**
     * Sets the value of the Session-Id AVP, of type UTF8String.
     * @throws IllegalStateException if setSessionId has already been called
     */
    void setSessionId(String sessionId);

    /**
     * Returns true if the Origin-Host AVP is present in the message.
     */
    boolean hasOriginHost();

    /**
     * Returns the value of the Origin-Host AVP, of type DiameterIdentity.
     * @return the value of the Origin-Host AVP or null if it has not been set on this message
     */
    DiameterIdentityAvp getOriginHost();

    /**
     * Sets the value of the Origin-Host AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setOriginHost has already been called
     */
    void setOriginHost(DiameterIdentityAvp originHost);

    /**
     * Returns true if the Origin-Realm AVP is present in the message.
     */
    boolean hasOriginRealm();

    /**
     * Returns the value of the Origin-Realm AVP, of type DiameterIdentity.
     * @return the value of the Origin-Realm AVP or null if it has not been set on this message
     */
    DiameterIdentityAvp getOriginRealm();

    /**
     * Sets the value of the Origin-Realm AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setOriginRealm has already been called
     */
    void setOriginRealm(DiameterIdentityAvp originRealm);

    /**
     * Returns true if the Result-Code AVP is present in the message.
     */
    boolean hasResultCode();

    /**
     * Returns the value of the Result-Code AVP, of type Unsigned32.
     * Use {@link #hasResultCode()} to check the existence of this AVP.  
     * @return the value of the Result-Code AVP
     * @throws IllegalStateException if the Result-Code AVP has not been set on this message
     */
    long getResultCode();

    /**
     * Sets the value of the Result-Code AVP, of type Unsigned32.
     * @throws IllegalStateException if setResultCode has already been called
     */
    void setResultCode(long resultCode);

    /**
     * Returns true if the Origin-State-Id AVP is present in the message.
     */
    boolean hasOriginStateId();

    /**
     * Returns the value of the Origin-State-Id AVP, of type Unsigned32.
     * Use {@link #hasOriginStateId()} to check the existence of this AVP.  
     * @return the value of the Origin-State-Id AVP
     * @throws IllegalStateException if the Origin-State-Id AVP has not been set on this message
     */
    long getOriginStateId();

    /**
     * Sets the value of the Origin-State-Id AVP, of type Unsigned32.
     * @throws IllegalStateException if setOriginStateId has already been called
     */
    void setOriginStateId(long originStateId);

    /**
     * Returns true if the Error-Reporting-Host AVP is present in the message.
     */
    boolean hasErrorReportingHost();

    /**
     * Returns the value of the Error-Reporting-Host AVP, of type DiameterIdentity.
     * @return the value of the Error-Reporting-Host AVP or null if it has not been set on this message
     */
    DiameterIdentityAvp getErrorReportingHost();

    /**
     * Sets the value of the Error-Reporting-Host AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setErrorReportingHost has already been called
     */
    void setErrorReportingHost(DiameterIdentityAvp errorReportingHost);

    /**
     * Returns true if the Proxy-Info AVP is present in the message.
     */
    boolean hasProxyInfo();

    /**
     * Returns the value of the Proxy-Info AVP, of type Grouped.
     * @return the value of the Proxy-Info AVP or null if it has not been set on this message
     */
    ProxyInfoAvp getProxyInfo();

    /**
     * Sets the value of the Proxy-Info AVP, of type Grouped.
     * @throws IllegalStateException if setProxyInfo has already been called
     */
    void setProxyInfo(ProxyInfoAvp proxyInfo);

    /**
     * Returns the set of extension AVPs. The returned array contains the extension AVPs
     * in the order they appear in the message.
     * A return value of null implies that no extensions AVPs have been set.
     */
    DiameterAvp[] getExtensionAvps();

    /**
     * Sets the set of extension AVPs with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getExtensionAvps() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws AvpNotAllowedException if an AVP is encountered of a type already known to this class
     *   (i.e. an AVP for which get/set methods already appear in this class)
     * @throws IllegalStateException if setExtensionAvps has already been called
     */
    void setExtensionAvps(DiameterAvp[] avps) throws AvpNotAllowedException;

}
