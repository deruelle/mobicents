package javax.megaco.pkg.NetworkPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgEventItem;

/**
 * The MEGACO Network Failure event class extends the PkgEventItem class. This
 * is a final class. This class defines Network Failure event of MEGACO Network
 * package. The methods shall define that this event item belongs to the Network
 * package.
 */
public final class NetworkNetfailEvt extends PkgEventItem {

	/**
	 * Identifies Network failure event of the MEGACO Network Package. Its value shall be set equal to 0x0005.
	 */
	public static final int NETWORK_NETFAIL_EVENT = 0x0005;

     
	
	/**
	 * Constructs a Jain MEGACO Object representing event Item of the MEGACO
	 * Package for event Network Failure and package as Network.
	 */
	public NetworkNetfailEvt() {
		super();
		super.eventId = NETWORK_NETFAIL_EVENT;
		super.itemId = NETWORK_NETFAIL_EVENT;
		super.packageId = new NetworkPkg();
	}

	/**
	 * This method is used to get the event identifier from an Event Item
	 * object. The implementations of this method in this class returns the id
	 * of the Network Failure event of Network Package.
	 * 
	 * @return It shall return {@link NETWORK_NETFAIL_EVENT}.
	 */
	public int getEventId() {
		return super.eventId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the
	 * Network Failure event of Network Package.
	 * 
	 * @return It shall return {@link NETWORK_NETFAIL_EVENT}.
	 */
	public int getItemId() {
		return super.itemId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the
	 * Network Failure event is defined in the Network Package of MEGACO
	 * protocol, this method returns the NetworkPkg class object.
	 * 
	 * @return The package is NetworkPkg.
	 */
	public MegacoPkg getItemsPkgId() {
		return super.packageId;
	}

}
