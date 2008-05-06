package net.java.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;




/**
 * Defines an interface representing the Extension-Diameter-Message command.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 9.7.0.  Extension-Diameter-Message
 * 
 *     An implementation of DiameterMessage for extension messages--those not defined by the
 *     Diameter RA being used.
 * 
 *     It follows the same pattern as the standard message types, but with the DiameterCommand supplied
 *     by the user.
 * 
 *     The AVPs are exposed as the set of 'extension AVP's', the same way as exposed for messages
 *     which define a "* [ AVP ]" line in the BNF definition of the message.
 * 
 *     Message Format
 * 
 *       &lt;Extension-Diameter-Message&gt; ::= &lt; Diameter Header: 0, PXY &gt;
 *                  &lt; Session-Id &gt;
 *                  { Origin-Host }
 *                  { Origin-Realm }
 *                  { Destination-Host }
 *                  { Destination-Realm }
 *                * [ AVP ]
 * </pre>
 */
public interface ExtensionDiameterMessage extends DiameterMessage {

    int commandCode = 0;

    /**
     * Returns the value of the Session-Id AVP, of type UTF8String.
     * A return value of null implies that the AVP has not been set.
     */
    String getSessionId();

    /**
     * Sets the value of the Session-Id AVP, of type UTF8String.
     * @throws IllegalStateException if setSessionId has already been called
     */
    void setSessionId(String sessionId);

    /**
     * Returns the value of the Origin-Host AVP, of type DiameterIdentity.
     * A return value of null implies that the AVP has not been set.
     */
    DiameterIdentityAvp getOriginHost();

    /**
     * Sets the value of the Origin-Host AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setOriginHost has already been called
     */
    void setOriginHost(DiameterIdentityAvp originHost);

    /**
     * Returns the value of the Origin-Realm AVP, of type DiameterIdentity.
     * A return value of null implies that the AVP has not been set.
     */
    DiameterIdentityAvp getOriginRealm();

    /**
     * Sets the value of the Origin-Realm AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setOriginRealm has already been called
     */
    void setOriginRealm(DiameterIdentityAvp originRealm);

    /**
     * Returns the value of the Destination-Host AVP, of type DiameterIdentity.
     * A return value of null implies that the AVP has not been set.
     */
    DiameterIdentityAvp getDestinationHost();

    /**
     * Sets the value of the Destination-Host AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setDestinationHost has already been called
     */
    void setDestinationHost(DiameterIdentityAvp destinationHost);

    /**
     * Returns the value of the Destination-Realm AVP, of type DiameterIdentity.
     * A return value of null implies that the AVP has not been set.
     */
    DiameterIdentityAvp getDestinationRealm();

    /**
     * Sets the value of the Destination-Realm AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setDestinationRealm has already been called
     */
    void setDestinationRealm(DiameterIdentityAvp destinationRealm);

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
