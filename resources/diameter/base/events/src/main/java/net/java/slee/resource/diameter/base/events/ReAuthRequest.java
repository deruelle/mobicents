package net.java.slee.resource.diameter.base.events;




import java.util.Iterator;


import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.ReAuthRequestTypeAvp;

/**
 * Defines an interface representing the Re-Auth-Request command.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 8.3.1.  Re-Auth-Request
 * 
 *    The Re-Auth-Request (RAR), indicated by the Command-Code set to 258
 *    and the message flags' 'R' bit set, may be sent by any server to the
 *    access device that is providing session service, to request that the
 *    user be re-authenticated and/or re-authorized.
 * 
 *    Message Format
 * 
 *       &lt;Re-Auth-Request&gt;  ::= &lt; Diameter Header: 258, REQ, PXY &gt;
 *                  &lt; Session-Id &gt;
 *                  { Origin-Host }
 *                  { Origin-Realm }
 *                  { Destination-Realm }
 *                  { Destination-Host }
 *                  { Auth-Application-Id }
 *                  { Re-Auth-Request-Type }
 *                  [ User-Name ]
 *                  [ Origin-State-Id ]
 *                * [ Proxy-Info ]
 *                * [ Route-Record ]
 *                * [ AVP ]
 * </pre>
 */
public interface ReAuthRequest extends DiameterMessage {

    int commandCode = 258;

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
     * Returns true if the Destination-Realm AVP is present in the message.
     */
    boolean hasDestinationRealm();

    /**
     * Returns the value of the Destination-Realm AVP, of type DiameterIdentity.
     * @return the value of the Destination-Realm AVP or null if it has not been set on this message
     */
    DiameterIdentityAvp getDestinationRealm();

    /**
     * Sets the value of the Destination-Realm AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setDestinationRealm has already been called
     */
    void setDestinationRealm(DiameterIdentityAvp destinationRealm);

    /**
     * Returns true if the Destination-Host AVP is present in the message.
     */
    boolean hasDestinationHost();

    /**
     * Returns the value of the Destination-Host AVP, of type DiameterIdentity.
     * @return the value of the Destination-Host AVP or null if it has not been set on this message
     */
    DiameterIdentityAvp getDestinationHost();

    /**
     * Sets the value of the Destination-Host AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setDestinationHost has already been called
     */
    void setDestinationHost(DiameterIdentityAvp destinationHost);

    /**
     * Returns true if the Auth-Application-Id AVP is present in the message.
     */
    boolean hasAuthApplicationId();

    /**
     * Returns the value of the Auth-Application-Id AVP, of type Unsigned32.
     * Use {@link #hasAuthApplicationId()} to check the existence of this AVP.  
     * @return the value of the Auth-Application-Id AVP
     * @throws IllegalStateException if the Auth-Application-Id AVP has not been set on this message
     */
    long getAuthApplicationId();

    /**
     * Sets the value of the Auth-Application-Id AVP, of type Unsigned32.
     * @throws IllegalStateException if setAuthApplicationId has already been called
     */
    void setAuthApplicationId(long authApplicationId);

    /**
     * Returns true if the Re-Auth-Request-Type AVP is present in the message.
     */
    boolean hasReAuthRequestType();

    /**
     * Returns the value of the Re-Auth-Request-Type AVP, of type Enumerated.
     * @return the value of the Re-Auth-Request-Type AVP or null if it has not been set on this message
     */
    ReAuthRequestTypeAvp getReAuthRequestType();

    /**
     * Sets the value of the Re-Auth-Request-Type AVP, of type Enumerated.
     * @throws IllegalStateException if setReAuthRequestType has already been called
     */
    void setReAuthRequestType(ReAuthRequestTypeAvp reAuthRequestType);

    /**
     * Returns true if the User-Name AVP is present in the message.
     */
    boolean hasUserName();

    /**
     * Returns the value of the User-Name AVP, of type UTF8String.
     * @return the value of the User-Name AVP or null if it has not been set on this message
     */
    String getUserName();

    /**
     * Sets the value of the User-Name AVP, of type UTF8String.
     * @throws IllegalStateException if setUserName has already been called
     */
    void setUserName(String userName);

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
     * Returns the set of Proxy-Info AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Proxy-Info AVPs have been set.
     * The elements in the given array are ProxyInfo objects.
     */
    ProxyInfoAvp[] getProxyInfos();

    /**
     * Sets a single Proxy-Info AVP in the message, of type Grouped.
     * @throws IllegalStateException if setProxyInfo or setProxyInfos
     *  has already been called
     */
    void setProxyInfo(ProxyInfoAvp proxyInfo);

    /**
     * Sets the set of Proxy-Info AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getProxyInfos() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setProxyInfo or setProxyInfos
     *  has already been called
     */
    void setProxyInfos(ProxyInfoAvp[] proxyInfos);

    /**
     * Returns the set of Route-Record AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Route-Record AVPs have been set.
     * The elements in the given array are DiameterIdentity objects.
     */
    DiameterIdentityAvp[] getRouteRecords();

    /**
     * Sets a single Route-Record AVP in the message, of type DiameterIdentity.
     * @throws IllegalStateException if setRouteRecord or setRouteRecords
     *  has already been called
     */
    void setRouteRecord(DiameterIdentityAvp routeRecord);

    /**
     * Sets the set of Route-Record AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getRouteRecords() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setRouteRecord or setRouteRecords
     *  has already been called
     */
    void setRouteRecords(DiameterIdentityAvp[] routeRecords);

    /**
     * Returns the set of extension AVPs. The returned array contains the extension AVPs
     * in the order they appear in the message.
     * A return value of null implies that no extensions AVPs have been set.
     */
    AvpList getExtensionAvps();

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
    void setExtensionAvps(AvpList avps) throws AvpNotAllowedException;

}
