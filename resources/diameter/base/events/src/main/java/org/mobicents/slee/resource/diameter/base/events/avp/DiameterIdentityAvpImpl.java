package org.mobicents.slee.resource.diameter.base.events.avp;



import net.java.slee.resource.diameter.base.events.avp.*;

public class DiameterIdentityAvpImpl implements DiameterIdentityAvp {

	
	private DiameterAvpType type=DiameterAvpType.OCTET_STRING;
	private String fqdn=null;
	private int avpCode=-1;
	private String name="no name";
	
	public DiameterIdentityAvpImpl(String fqdn,int avpCode, String name) {
		super();
		this.avpCode=avpCode;
		this.fqdn = fqdn;
		this.name=name;
	}

	public byte[] byteArrayValue() {
		throw new UnsupportedOperationException ("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
	}

	public double doubleValue() {
		throw new UnsupportedOperationException("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
	}

	public float floatValue() {
		throw new UnsupportedOperationException("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
	}

	public int getCode() {
		
		return avpCode;
	}

	public int getMandatoryRule() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() {
		
		return this.name;
	}

	public int getProtectedRule() {
		// TODO Auto-generated method stub
		return 0;
	}

	public DiameterAvpType getType() {
		
		return this.type;
	}

	public int getVendorID() {
		//Eric: This is always 1?
		return 1;
	}

	public int intValue() {
		throw new UnsupportedOperationException("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
	}

	public long longValue() {
		throw new UnsupportedOperationException("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
	}

	public String stringValue() {
		return this.fqdn;
	}

	public Object clone()
	{
		
		DiameterIdentityAvpImpl clone=new DiameterIdentityAvpImpl(this.fqdn,this.avpCode,this.name);
		return clone;
	}
}
