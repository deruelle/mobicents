package net.java.slee.resource.diameter.cca;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

public interface CreditControlProvider {

	public CreditControlClientSession createNewClientSession();

	public CreditControlClientSession createNewClientSession(
			DiameterIdentityAvp destinationHost,
			DiameterIdentityAvp destinationRealm);

	/**
	 * Provides with message factory.
	 * 
	 * @return
	 */
	public CreditControlMessageFactory getMessageFactory();

	/**
	 * Provides AVP factory.
	 * 
	 * @return
	 */
	public CreditControlAVPFactory getAVPFactory();
}
