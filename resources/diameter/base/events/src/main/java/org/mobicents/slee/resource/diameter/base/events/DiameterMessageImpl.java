package org.mobicents.slee.resource.diameter.base.events;

import java.util.List;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterIdentityAvpImpl;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

public abstract class DiameterMessageImpl implements DiameterMessage {

	protected Message message = null;
	protected boolean destinationHostMandatory=false;
	protected boolean destinationRealmMandatory=false;
	protected boolean originHostMandatory=false;
	protected boolean originRealmMandatory=false;
	
	public AvpList getAvps() {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterCommand getCommand() {

		return new DiameterCommandImpl(this.message, this.getLongName(), this
				.getShortName());
	}

	public DiameterIdentityAvp getDestinationHost(){

		Avp rawAvp = this.message.getAvps().getAvp(
				Avp.DESTINATION_HOST);
		if (rawAvp == null) {
			
			return null;
			
		} else {

			try {
				DiameterIdentityAvpImpl value=new DiameterIdentityAvpImpl(rawAvp.getDiameterIdentity(),Avp.DESTINATION_HOST,"Destination-Host");
				return value;
			} catch (AvpDataException e) {
	
				e.printStackTrace();
			}
			return null;
		}
	}

	public DiameterIdentityAvp getDestinationRealm() {
		Avp rawAvp = this.message.getAvps().getAvp(
				Avp.DESTINATION_REALM);
		if (rawAvp == null) {
			
			return null;
			
		} else {

			try {
				DiameterIdentityAvpImpl value=new DiameterIdentityAvpImpl(rawAvp.getDiameterIdentity(),Avp.DESTINATION_REALM,"Destination-Realm");
				return value;
			} catch (AvpDataException e) {
	
				e.printStackTrace();
			}
			return null;
		}
	}

	public DiameterHeader getHeader() {
		DiameterHeaderImpl dh=new DiameterHeaderImpl(message);
		
		return dh;
	}

	public DiameterIdentityAvp getOriginHost() {
		Avp rawAvp = this.message.getAvps().getAvp(
				Avp.ORIGIN_HOST);
		if (rawAvp == null) {
			
			return null;
			
		} else {

			try {
				DiameterIdentityAvpImpl value=new DiameterIdentityAvpImpl(rawAvp.getDiameterIdentity(),Avp.ORIGIN_HOST,"Origin-Host");
				return value;
			} catch (AvpDataException e) {
	
				e.printStackTrace();
			}
			return null;
		}
	}

	public DiameterIdentityAvp getOriginRealm() {
		Avp rawAvp = this.message.getAvps().getAvp(
				Avp.ORIGIN_REALM);
		if (rawAvp == null) {
			
			return null;
			
		} else {

			try {
				DiameterIdentityAvpImpl value=new DiameterIdentityAvpImpl(rawAvp.getDiameterIdentity(),Avp.ORIGIN_REALM,"Origin-Realm");
				return value;
			} catch (AvpDataException e) {
	
				e.printStackTrace();
			}
			return null;
		}
	}

	public String getSessionId() {
		
		return message.getSessionId();
	}

	public void setDestinationHost(DiameterIdentityAvp destinationHost) {
		
		Avp rawAvp = this.message.getAvps().getAvp(
				Avp.DESTINATION_HOST);
		if (rawAvp == null) {
			//FIXME: Ignoring madnatoryRules from avp and pFlag?
			this.message.getAvps().addAvp(Avp.DESTINATION_HOST,destinationHost.stringValue(),this.destinationHostMandatory,false,true);
			
		} else {

			throw new IllegalStateException("Cant set Desintation-Host AVP again!!!");
		}
	}

	public void setDestinationRealm(DiameterIdentityAvp destinationRealm) {

		Avp rawAvp = this.message.getAvps().getAvp(
				Avp.DESTINATION_REALM);
		if (rawAvp == null) {
			//FIXME: Ignoring madnatoryRules from avp and pFlag?
			this.message.getAvps().addAvp(Avp.DESTINATION_REALM,destinationRealm.stringValue(),this.destinationRealmMandatory,false,true);
			
		} else {

			throw new IllegalStateException("Cant set Desintation-Realm AVP again!!!");
		}

	}

	public void setOriginHost(DiameterIdentityAvp originHost) {
		Avp rawAvp = this.message.getAvps().getAvp(
				Avp.ORIGIN_HOST);
		if (rawAvp == null) {
			//FIXME: Ignoring madnatoryRules from avp and pFlag?
			this.message.getAvps().addAvp(Avp.ORIGIN_HOST,originHost.stringValue(),this.originHostMandatory,false,true);
			
		} else {

			throw new IllegalStateException("Cant set Desintation-Realm AVP again!!!");
		}

	}

	public void setOriginRealm(DiameterIdentityAvp originRealm) {
		Avp rawAvp = this.message.getAvps().getAvp(
				Avp.ORIGIN_REALM);
		if (rawAvp == null) {
			//FIXME: Ignoring madnatoryRules from avp and pFlag?
			this.message.getAvps().addAvp(Avp.ORIGIN_REALM,originRealm.stringValue(),this.originHostMandatory,false,true);
			
		} else {

			throw new IllegalStateException("Cant set Desintation-Realm AVP again!!!");
		}

	}

	public void setSessionId(String sessionId) {

		Avp rawAvp = this.message.getAvps().getAvp(
				Avp.SESSION_ID);
		if (rawAvp == null) {
			//FIXME: Ignoring madnatoryRules from avp and pFlag?
			rawAvp=this.message.getAvps().addAvp(Avp.ORIGIN_REALM,sessionId,true,false,true);

		} else {

			throw new IllegalStateException("Cant set Desintation-Realm AVP again!!!");
		}

	}

	public Object clone() {
		//TODO
		return null;
	}

	/**
	 * This method return short name of this message type - for instance DWR,DWA
	 * for DeviceWatchdog message
	 * 
	 * @return
	 */
	public abstract String getShortName();

	/**
	 * This method returns long name of this message type - Like
	 * Device-Watchdog-Request
	 * 
	 * @return
	 */
	public abstract String getLongName();

}
